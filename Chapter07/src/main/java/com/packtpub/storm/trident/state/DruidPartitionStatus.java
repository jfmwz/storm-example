package com.packtpub.storm.trident.state;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class DruidPartitionStatus {
    private static final Logger LOG = LoggerFactory.getLogger(DruidPartitionStatus.class);
    final String COMPLETED_PATH = "completed";
    final String LIMBO_PATH = "limbo";
    final String CURRENT_PATH = "current";
    private CuratorFramework curatorFramework;

    public DruidPartitionStatus() {
        try {
            curatorFramework = CuratorFrameworkFactory.builder().namespace("stormdruid")
                    .connectString("localhost:2181").retryPolicy(new RetryNTimes(1, 1000)).connectionTimeoutMs(5000)
                    .build();

            curatorFramework.start();

            if (curatorFramework.checkExists().forPath(COMPLETED_PATH) == null) {
                curatorFramework.create().forPath(COMPLETED_PATH);
            }

            if (curatorFramework.checkExists().forPath(CURRENT_PATH) == null) {
                curatorFramework.create().forPath(CURRENT_PATH);
            }

            if (curatorFramework.checkExists().forPath(LIMBO_PATH) == null) {
                curatorFramework.create().forPath(LIMBO_PATH);
            }
        } catch (Exception e) {
            LOG.error("Could not establish conneciton to Zookeeper", e);
        }
    }

    public boolean isCompleted(String partitionId) throws Exception {
        return (curatorFramework.checkExists().forPath(COMPLETED_PATH + "/" + partitionId) != null);
    }

    public boolean isInLimbo(String partitionId) throws Exception {
        return (curatorFramework.checkExists().forPath(LIMBO_PATH + "/" + partitionId) != null);
    }

    public boolean isInProgress(String partitionId) throws Exception {
        return (curatorFramework.checkExists().forPath(CURRENT_PATH + "/" + partitionId) != null);
    }

    public void putInProgress(String partitionId) throws Exception {
        curatorFramework.create().forPath(CURRENT_PATH + "/" + partitionId);
    }

    public void putInLimbo(String partitionId) throws Exception {
        curatorFramework.inTransaction().
                delete().forPath(CURRENT_PATH + "/" + partitionId)
                .and().create().forPath(LIMBO_PATH + "/" + partitionId).and().commit();
    }

    public void complete(List<String> partitionIds) throws Exception {
        Iterator<String> iterator = partitionIds.iterator();
        CuratorTransaction transaction = curatorFramework.inTransaction();
        while (iterator.hasNext()) {
            String partitionId = iterator.next();
            transaction = transaction.delete().forPath(LIMBO_PATH + "/" + partitionId)
                    .and().create().forPath(COMPLETED_PATH + "/" + partitionId).and();
        }
        CuratorTransactionFinal tx = (CuratorTransactionFinal) transaction;
        tx.commit();
    }
}
