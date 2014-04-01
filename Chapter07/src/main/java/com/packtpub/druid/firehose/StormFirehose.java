package com.packtpub.druid.firehose;

import com.google.common.collect.Maps;
import com.metamx.druid.input.InputRow;
import com.metamx.druid.input.MapBasedInputRow;
import com.metamx.druid.realtime.firehose.Firehose;
import com.packtpub.storm.model.FixMessageDto;
import com.packtpub.storm.trident.state.DruidPartitionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StormFirehose implements Firehose {
    private static final Logger LOG = LoggerFactory.getLogger(StormFirehose.class);
    private static final Object START = new Object();
    private static final Object FINISHED = new Object();
    private static BlockingQueue<FixMessageDto> BLOCKING_QUEUE;
    public static final DruidPartitionStatus STATUS = new DruidPartitionStatus();
    private static String TRANSACTION_ID = null;
    private static BlockingQueue<String> LIMBO_TRANSACTIONS = new ArrayBlockingQueue<String>(99999);

    @Override
    public boolean hasMore() {
        if (BLOCKING_QUEUE != null && !BLOCKING_QUEUE.isEmpty())
            return true;
        try {
            synchronized (START) {
                START.wait();
            }
        } catch (InterruptedException e) {
            LOG.error("hasMore() blocking was interrupted.", e);
        }
        return true;
    }

    @Override
    public InputRow nextRow() {
        final Map<String, Object> theMap = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
        try {
            FixMessageDto message;
            message = BLOCKING_QUEUE.poll();
            if (message != null) {
                // LOG.info("[" + message.symbol + "] @ [" + message.price + "] for [" + message.uid + "]");
                theMap.put("symbol", message.symbol);
                theMap.put("price", message.price);
            }

            if (BLOCKING_QUEUE.isEmpty()) {
                STATUS.putInLimbo(TRANSACTION_ID);
                LIMBO_TRANSACTIONS.add(TRANSACTION_ID);
                LOG.info("Batch is fully consumed by Druid. Unlocking [FINISH]");
                synchronized (FINISHED) {
                    FINISHED.notify();
                }
            }
        } catch (Exception e) {
            LOG.error("Error occurred in nextRow.", e);
        }
        final LinkedList<String> dimensions = new LinkedList<String>();
        dimensions.add("symbol");
        dimensions.add("price");
        return new MapBasedInputRow(System.currentTimeMillis(), dimensions, theMap);
    }

    @Override
    public Runnable commit() {
        List<String> limboTransactions = new ArrayList<String>();
        LIMBO_TRANSACTIONS.drainTo(limboTransactions);
        return new StormCommitRunnable(limboTransactions);
    }

    public synchronized void sendMessages(String partitionId, List<FixMessageDto> messages) {
        BLOCKING_QUEUE = new ArrayBlockingQueue<FixMessageDto>(messages.size(), false, messages);
        TRANSACTION_ID = partitionId;
        LOG.info("Beginning commit to Druid. [" + messages.size() + "] messages, unlocking [START]");
        synchronized (START) {
            START.notify();
        }
        try {
            synchronized (FINISHED) {
                FINISHED.wait();
            }
        } catch (InterruptedException e) {
            LOG.error("Commit to Druid interrupted.");
        }
        LOG.info("Returning control to Storm.");
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
