package com.packtpub.storm.trident.state;

import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.state.StateUpdater;
import storm.trident.tuple.TridentTuple;

import java.util.List;
import java.util.Map;

public class DruidStateUpdater implements StateUpdater<DruidState> {
    private static final long serialVersionUID = 1L;

    // private static final Logger LOG =
    // LoggerFactory.getLogger(DruidStateUpdater.class);

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map conf, TridentOperationContext context) {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void updateState(DruidState state, List<TridentTuple> tuples, TridentCollector collector) {
        state.aggregateTuples(tuples);
    }
}
