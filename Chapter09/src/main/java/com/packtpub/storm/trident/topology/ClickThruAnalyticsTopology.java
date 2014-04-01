package com.packtpub.storm.trident.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.packtpub.storm.trident.operator.CampaignEffectiveness;
import com.packtpub.storm.trident.operator.Distinct;
import com.packtpub.storm.trident.operator.Filter;
import com.packtpub.storm.trident.spout.ClickThruSpout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.MapGet;
import storm.trident.state.StateFactory;
import storm.trident.testing.MemoryMapState;

public class ClickThruAnalyticsTopology {
    private static final Logger LOG = LoggerFactory.getLogger(ClickThruAnalyticsTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();
        StateFactory clickThruMemory = new MemoryMapState.Factory();
        ClickThruSpout spout = new ClickThruSpout();
        Stream inputStream = topology.newStream("clithru", spout);
        TridentState clickThruState = inputStream.each(new Fields("username", "campaign", "product", "click"), new Filter("click", "true"))
                .each(new Fields("username", "campaign", "product", "click"), new Distinct())
                .groupBy(new Fields("campaign"))
                .persistentAggregate(clickThruMemory, new Count(), new Fields("click_thru_count"));

        inputStream.groupBy(new Fields("campaign"))
                .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("impression_count"))
                .newValuesStream()
                .stateQuery(clickThruState, new Fields("campaign"), new MapGet(), new Fields("click_thru_count"))
                .each(new Fields("campaign", "impression_count", "click_thru_count"), new CampaignEffectiveness(), new Fields(""));

        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();
        LOG.info("Submitting topology.");
        cluster.submitTopology("financial", conf, buildTopology());
        LOG.info("Topology submitted.");
        Thread.sleep(600000);
    }
}
