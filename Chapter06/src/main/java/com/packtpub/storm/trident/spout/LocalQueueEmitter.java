package com.packtpub.storm.trident.spout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalQueueEmitter<T> implements Emitter<Long>, Serializable {
    private static final long serialVersionUID = 1L;
    public static final int MAX_BATCH_SIZE = 1000;
    public static AtomicInteger successfulTransactions = new AtomicInteger(0);
    private static Map<String, BlockingQueue<Object>> queues = new HashMap<String, BlockingQueue<Object>>();
    // private static BlockingQueue<Object> q = new
    // LinkedBlockingQueue<Object>();
    private static final Logger LOG = LoggerFactory.getLogger(LocalQueueEmitter.class);
    private String queueName;

    public LocalQueueEmitter(String queueName) {
        queues.put(queueName, new LinkedBlockingQueue<Object>());
        this.queueName = queueName;
    }

    @Override
    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        int size = 0;
        LOG.debug("Getting batch for [" + tx.getTransactionId() + "]");
        while (size <= MAX_BATCH_SIZE) {
            List<Object> values = new ArrayList<Object>();
            try {
                LOG.debug("Waiting on work from [" + this.queueName + "]:[" + getQueue().size() + "]");
                values.add(getQueue().take());
                LOG.debug("Got work from [" + this.queueName + "]:[" + getQueue().size() + "]");
            } catch (InterruptedException ex) {
                // do something smart
            }
            collector.emit(values);
            size++;
        }
        LOG.info("Emitted [" + size + "] elements in [" + tx.getTransactionId() + "], [" + getQueue().size() + "] remain in queue.");
    }

    @Override
    public void success(TransactionAttempt tx) {
        successfulTransactions.incrementAndGet();
    }

    public void enqueue(T work) {
        //LOG.debug("Adding work to [" + this.queueName + "]:[" + getQueue().size() + "]");
        if (getQueue().size() % 1000 == 0)
            LOG.info("[" + this.queueName + "] size = [" + getQueue().size() + "].");
        this.getQueue().add(work);
    }

    public BlockingQueue<Object> getQueue() {
        return LocalQueueEmitter.queues.get(this.queueName);
    }

    @Override
    public void close() {
    }
}
