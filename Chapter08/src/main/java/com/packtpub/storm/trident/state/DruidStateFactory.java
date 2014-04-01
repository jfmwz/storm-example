package com.packtpub.storm.trident.state;

import backtype.storm.task.IMetricsContext;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.druid.realtime.RealtimeNode;
import com.packtpub.druid.firehose.StormFirehoseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class DruidStateFactory implements StateFactory {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DruidStateFactory.class);
    private static RealtimeNode rn = null;

    private static synchronized void startRealtime() {
        if (rn == null) {
            final Lifecycle lifecycle = new Lifecycle();
            rn = RealtimeNode.builder().build();
            lifecycle.addManagedInstance(rn);
            rn.registerJacksonSubtype(new NamedType(StormFirehoseFactory.class, "storm"));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    LOG.info("Running shutdown hook");
                    lifecycle.stop();
                }
            }));

            try {
                lifecycle.start();
            } catch (Throwable t) {
                LOG.info("Throwable caught at startup, committing seppuku", t);
                t.printStackTrace();
                System.exit(2);
            }
        }
    }

    @Override
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
        DruidStateFactory.startRealtime();
        return new DruidState();
    }
}
