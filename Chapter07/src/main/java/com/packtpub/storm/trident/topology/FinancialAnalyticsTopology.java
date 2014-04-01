package com.packtpub.storm.trident.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.metamx.druid.log.LogLevelAdjuster;
import com.packtpub.storm.trident.operator.MessageTypeFilter;
import com.packtpub.storm.trident.spout.FixEventSpout;
import com.packtpub.storm.trident.state.DruidStateFactory;
import com.packtpub.storm.trident.state.DruidStateUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentTopology;

public class FinancialAnalyticsTopology {
    private static final Logger LOG = LoggerFactory.getLogger(FinancialAnalyticsTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();
        FixEventSpout spout = new FixEventSpout();
        Stream inputStream = topology.newStream("message", spout);

        inputStream.each(new Fields("message"), new MessageTypeFilter())
                .partitionPersist(new DruidStateFactory(), new Fields("message"), new DruidStateUpdater());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        LogLevelAdjuster.register();

        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");

        cluster.submitTopology("financial", conf, buildTopology());
        LOG.info("Topology submitted.");

        Thread.sleep(600000);
    }
}
