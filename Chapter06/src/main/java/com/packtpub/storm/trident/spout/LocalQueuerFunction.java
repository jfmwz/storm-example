package com.packtpub.storm.trident.spout;

import com.esotericsoftware.minlog.Log;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class LocalQueuerFunction<T> extends BaseFunction {
    private static final long serialVersionUID = 1L;
    LocalQueueEmitter<T> emitter;

    public LocalQueuerFunction(LocalQueueEmitter<T> emitter) {
        this.emitter = emitter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        T object = (T) tuple.get(0);
        Log.debug("Queueing [" + object + "]");
        this.emitter.enqueue(object);
    }
}
