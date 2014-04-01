package com.packtpub.storm.trident.spout;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickThruEmitter implements Emitter<Long>, Serializable {
    private static final long serialVersionUID = 1L;
    public static AtomicInteger successfulTransactions = new AtomicInteger(0);
    public static AtomicInteger uids = new AtomicInteger(0);

    @Override
    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        File file = new File("click_thru_data.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");
                List<Object> tuple = new ArrayList<Object>();
                tuple.add(data[0]); // username
                tuple.add(data[1]); // campaign
                tuple.add(data[2]); // product
                tuple.add(data[3]); // click
                collector.emit(tuple);
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void success(TransactionAttempt tx) {
        successfulTransactions.incrementAndGet();
    }

    @Override
    public void close() {
    }

}
