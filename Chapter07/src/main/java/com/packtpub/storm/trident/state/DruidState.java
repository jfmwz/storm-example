package com.packtpub.storm.trident.state;

import com.packtpub.druid.firehose.StormFirehose;
import com.packtpub.druid.firehose.StormFirehoseFactory;
import com.packtpub.storm.model.FixMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.state.State;

import java.util.Vector;

public class DruidState implements State {
    private static final Logger LOG = LoggerFactory.getLogger(DruidState.class);
    private Vector<FixMessageDto> messages = new Vector<FixMessageDto>();
    private int partitionIndex;

    public DruidState(int partitionIndex){
        this.partitionIndex = partitionIndex;
    }

    @Override
    public void beginCommit(Long batchId) {
    }

    @Override
    public void commit(Long batchId) {
        String partitionId = batchId.toString() + "-" + partitionIndex;
        LOG.info("Committing partition [" + partitionIndex + "] of batch [" + batchId + "]");
        try {
            if (StormFirehose.STATUS.isCompleted(partitionId)) {
                LOG.warn("Encountered completed partition [" + partitionIndex + "] of batch [" + batchId + "]");
                return;
            } else if (StormFirehose.STATUS.isInLimbo(partitionId)) {
                LOG.warn("Encountered limbo partition [" + partitionIndex + "] of batch [" + batchId + "] : NOTIFY THE AUTHORITIES!");
                return;
            } else if (StormFirehose.STATUS.isInProgress(partitionId)) {
                LOG.warn("Encountered in-progress partition [\" + partitionIndex + \"] of batch [" + batchId + "] : NOTIFY THE AUTHORITIES!");
                return;
            }
            StormFirehose.STATUS.putInProgress(partitionId);
            StormFirehoseFactory.getFirehose().sendMessages(partitionId, messages);
        } catch (Exception e) {
            LOG.error("Could not start firehose for [" + partitionIndex + "] of batch [" + batchId + "]", e);
        }
    }

    public void aggregateMessage(FixMessageDto message) {
        // LOG.info("Aggregating [" + message + "]");
        messages.add(message);
    }
}
