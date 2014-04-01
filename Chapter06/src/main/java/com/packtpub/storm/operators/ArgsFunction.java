package com.packtpub.storm.operators;

import com.esotericsoftware.minlog.Log;
import com.packtpub.storm.model.Board;
import com.packtpub.storm.model.GameState;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

public class ArgsFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String args = tuple.getString(0);
        Log.info("Executing DRPC w/ args = [" + args + "]");
        Board board = new Board(args);
        GameState gameState = new GameState(board, new ArrayList<Board>(), "X");
        Log.info("Emitting [" + gameState + "]");

        List<Object> values = new ArrayList<Object>();
        values.add(gameState);
        collector.emit(values);
    }
}