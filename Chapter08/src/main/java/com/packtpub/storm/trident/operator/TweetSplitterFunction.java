package com.packtpub.storm.trident.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetSplitterFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TweetSplitterFunction.class);

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String tweet = (String) tuple.getValue(0);
        LOG.error("SPLITTING TWEET [" + tweet + "]");
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(tweet);
        List<String> result = new ArrayList<String>();
        while (m.find()) {
            String word = m.group();
            if (word.length() > 0) {
                List<Object> newTuple = new ArrayList<Object>();
                newTuple.add(word);
                collector.emit(newTuple);
            }
        }
    }
}