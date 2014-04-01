package com.packtpub.storm.trident.operator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PersistenceFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        writeToLog(tuple);
        collector.emit(tuple);
    }

    synchronized public void writeToLog(TridentTuple tuple) {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        StringBuffer sb = new StringBuffer("{ ");
        sb.append(String.format("\"utcdt\":\"%s\",", fmt.print(dt)));
        sb.append(String.format("\"searchphrase\":\"%s\",", tuple.getValue(0)));
        sb.append(String.format("\"word\":\"%s\",", tuple.getValue(2)));
        sb.append(String.format("\"baseline\":%s", tuple.getValue(3)));
        sb.append("}");
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter("nlp.json", true));
            bw.write(sb.toString());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
