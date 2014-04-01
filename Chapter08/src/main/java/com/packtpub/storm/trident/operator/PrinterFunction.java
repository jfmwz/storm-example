package com.packtpub.storm.trident.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class PrinterFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PrinterFunction.class);

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String terms = (String) tuple.getValue(0);
        String tweet = (String) tuple.getValue(1);
        String word = (String) tuple.getValue(2);
        LOG.error("[" + terms + "],[" + tweet + "],[" + word + "]");
    }
}
