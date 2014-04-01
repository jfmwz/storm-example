package com.packtpub.twitter;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TweetEmitter implements Emitter<Long>, Serializable {
    private static final long serialVersionUID = 1L;
    public static AtomicInteger successfulTransactions = new AtomicInteger(0);
    public static AtomicInteger uids = new AtomicInteger(0);
    public static String SEARCH_PHRASE = "apple jobs";
    private QueryResult result = null;
    private Twitter twitter = null;
    private Query query = null;
    private long lastFetch = -1;
    private static final long FETCH_PERIODICITY = 15 * 1000; // 15 seconds.

    public TweetEmitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey("DJSv02qbCiCVY6Sy8uz8Cg")
                .setOAuthConsumerSecret("iVa3GGSl9NrHvu3AAXm6zBV7Z2XTvtuDkAMiArHlGM")
                .setOAuthAccessToken("1353264175-5EWRtEHoaJ6zfmAG5BEn7Uc7E8qg7P531oddm08")
                .setOAuthAccessTokenSecret("ss7YCprReDBEpm4AGnguqB1xNCUwyRGli5Q33yImf0");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        query = new Query(SEARCH_PHRASE);
        query.setLang("en");
        long now = System.currentTimeMillis();
        if (now - lastFetch > FETCH_PERIODICITY) {
            try {
                result = twitter.search(query);
            } catch (TwitterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        for (Status status : result.getTweets()) {
            List<Object> tweets = new ArrayList<Object>();
            tweets.add(SEARCH_PHRASE);
            tweets.add(status.getText());
            collector.emit(tweets);
            System.out.println("Emitted [" + status.getText() + "]");
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
