package com.packtpub.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.packtpub.storm.model.Board;
import com.packtpub.storm.model.GameState;
import com.packtpub.storm.operators.GenerateBoards;
import com.packtpub.storm.operators.isEndGame;
import com.packtpub.storm.trident.spout.LocalQueueEmitter;
import com.packtpub.storm.trident.spout.LocalQueueSpout;
import com.packtpub.storm.trident.spout.LocalQueuerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import java.util.ArrayList;

public class RecursiveTopology {
    private static final Logger LOG = LoggerFactory.getLogger(RecursiveTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();

        // Work Queue / Spout
        LocalQueueEmitter<GameState> workSpoutEmitter = new LocalQueueEmitter<GameState>("WorkQueue");
        LocalQueueSpout<GameState> workSpout = new LocalQueueSpout<GameState>(workSpoutEmitter);
        GameState initialState = new GameState(new Board(), new ArrayList<Board>(), "X");
        workSpoutEmitter.enqueue(initialState);

        // Scoring Queue / Spout
        LocalQueueEmitter<GameState> scoringSpoutEmitter = new LocalQueueEmitter<GameState>("ScoringQueue");

        Stream inputStream = topology.newStream("gamestate", workSpout);

        inputStream.each(new Fields("gamestate"), new isEndGame())
                .each(new Fields("gamestate"),
                        new LocalQueuerFunction<GameState>(scoringSpoutEmitter),
                        new Fields(""));

        inputStream.each(new Fields("gamestate"), new GenerateBoards(), new Fields("children"))
                .each(new Fields("children"),
                        new LocalQueuerFunction<GameState>(workSpoutEmitter),
                        new Fields());

        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");
        cluster.submitTopology("recursiveTopology", conf, RecursiveTopology.buildTopology());
        LOG.info("Topology submitted.");
        Thread.sleep(600000);
    }
}
