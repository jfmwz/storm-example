package com.packtpub.storm.trident.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordFrequencyFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(WordFrequencyFunction.class);
    public static final long DEFAULT_BASELINE = 10000;
    private Map<String, Long> wordLikelihoods = new HashMap<String, Long>();

    public WordFrequencyFunction() throws IOException {
        File file = new File("src/main/resources/en.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int i = 0;
        while ((line = br.readLine()) != null && i < 10000) {
            String[] pair = line.split(" ");
            long baseline = Long.parseLong(pair[1]);
            LOG.debug("[" + pair[0] + "]=>[" + baseline + "]");
            wordLikelihoods.put(pair[0].toLowerCase(), baseline);
            i++;
        }
        br.close();
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String word = (String) tuple.getValue(2);
        Long baseline = this.getLikelihood(word);
        List<Object> newTuple = new ArrayList<Object>();
        newTuple.add(baseline);
        collector.emit(newTuple);
    }

    public long getLikelihood(String word) {
        Long baseline = this.wordLikelihoods.get(word);
        if (baseline == null)
            return DEFAULT_BASELINE;
        else
            return baseline;
    }
}