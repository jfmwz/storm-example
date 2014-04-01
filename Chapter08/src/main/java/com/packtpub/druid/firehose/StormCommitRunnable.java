package com.packtpub.druid.firehose;

import com.esotericsoftware.minlog.Log;

import java.util.List;

public class StormCommitRunnable implements Runnable {
    private List<Long> txIds = null;

    public StormCommitRunnable(List<Long> txIds) {
        this.txIds = txIds;
    }

    @Override
    public void run() {
        try {
            StormFirehose.STATUS.complete(txIds);
        } catch (Exception e) {
            Log.error("Could not complete transactions.", e);
        }
    }
}
