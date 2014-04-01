package com.packtpub.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.packtpub.storm.model.Board;
import com.packtpub.storm.model.GameState;
import com.packtpub.storm.operators.ScoreFunction;
import com.packtpub.storm.operators.ScoreUpdater;
import com.packtpub.storm.operators.isEndGame;
import com.packtpub.storm.trident.spout.LocalQueueEmitter;
import com.packtpub.storm.trident.spout.LocalQueueSpout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentTopology;

public class ScoringTopology {
    private static final Logger LOG = LoggerFactory.getLogger(ScoringTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();

        GameState exampleRecursiveState = GameState.playAtRandom(new Board(), "X");
        LOG.info("SIMULATED LEAF NODE : [" + exampleRecursiveState.getBoard() + "] w/ state [" + exampleRecursiveState + "]");

        // Scoring Queue / Spout
        LocalQueueEmitter<GameState> scoringSpoutEmitter = new LocalQueueEmitter<GameState>("ScoringQueue");
        scoringSpoutEmitter.enqueue(exampleRecursiveState);
        LocalQueueSpout<GameState> scoringSpout = new LocalQueueSpout<GameState>(scoringSpoutEmitter);

        Stream inputStream = topology.newStream("scoring", scoringSpout);

        inputStream.each(new Fields("gamestate"), new isEndGame())
                .each(new Fields("gamestate"),
                        new ScoreFunction(),
                        new Fields("board", "score", "player"))
                .each(new Fields("board", "score", "player"), new ScoreUpdater(), new Fields());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");
        cluster.submitTopology("scoringTopology", conf, ScoringTopology.buildTopology());
        LOG.info("Topology submitted.");
        Thread.sleep(600000);
    }
}
