package com.packtpub.druid.firehose;

import com.google.common.collect.Maps;
import com.metamx.druid.input.InputRow;
import com.metamx.druid.input.MapBasedInputRow;
import com.metamx.druid.realtime.firehose.Firehose;
import com.packtpub.storm.trident.state.DruidBatchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.tuple.TridentTuple;

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
    private static BlockingQueue<TridentTuple> BLOCKING_QUEUE;
    public static final DruidBatchStatus STATUS = new DruidBatchStatus();
    private static Long TRANSACTION_ID = null;
    private static BlockingQueue<Long> LIMBO_TRANSACTIONS = new ArrayBlockingQueue<Long>(99999);

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
            TridentTuple tuple = null;
            tuple = BLOCKING_QUEUE.poll();
            if (tuple != null) {
                String phrase = (String) tuple.getValue(0);
                String word = (String) tuple.getValue(2);
                Long baseline = (Long) tuple.getValue(3);
                theMap.put("searchphrase", phrase);
                theMap.put("word", word);
                theMap.put("baseline", baseline);
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
        dimensions.add("searchphrase");
        dimensions.add("word");
        return new MapBasedInputRow(System.currentTimeMillis(), dimensions, theMap);
    }

    @Override
    public Runnable commit() {
        List<Long> limboTransactions = new ArrayList<Long>();
        LIMBO_TRANSACTIONS.drainTo(limboTransactions);
        return new StormCommitRunnable(limboTransactions);
    }

    public synchronized void sendBatch(Long txId, List<TridentTuple> tuples) {
        BLOCKING_QUEUE = new ArrayBlockingQueue<TridentTuple>(tuples.size(), false, tuples);
        TRANSACTION_ID = txId;
        LOG.error("Beginning commit to Druid. [" + tuples.size() + "] messages, unlocking [START]");
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
