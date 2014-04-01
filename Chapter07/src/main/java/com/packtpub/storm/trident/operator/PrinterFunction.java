package com.packtpub.storm.trident.operator;

import com.esotericsoftware.minlog.Log;
import com.packtpub.storm.model.FixMessageDto;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

public class PrinterFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        FixMessageDto message = (FixMessageDto) tuple.getValue(0);
        Log.error("MESSAGE RECEIVED [" + message + "]");
        List<Object> values = new ArrayList<Object>();
        values.add(message);
        collector.emit(values);
    }
}
