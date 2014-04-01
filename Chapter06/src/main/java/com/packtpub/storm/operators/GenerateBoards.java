package com.packtpub.storm.operators;

import com.esotericsoftware.minlog.Log;
import com.packtpub.storm.model.Board;
import com.packtpub.storm.model.GameState;
import com.packtpub.storm.model.Player;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

public class GenerateBoards extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        GameState gameState = (GameState) tuple.get(0);
        Board currentBoard = gameState.getBoard();
        List<Board> history = new ArrayList<Board>();
        history.addAll(gameState.getHistory());
        history.add(currentBoard);

        if (!currentBoard.isEndState()) {
            String nextPlayer = Player.next(gameState.getPlayer());
            List<Board> boards = gameState.getBoard().nextBoards(nextPlayer);
            Log.debug("Generated [" + boards.size() + "] children boards for [" + gameState.toString() + "]");
            for (Board b : boards) {
                GameState newGameState = new GameState(b, history, nextPlayer);
                List<Object> values = new ArrayList<Object>();
                values.add(newGameState);
                collector.emit(values);
            }
        } else {
            Log.debug("End game found! [" + currentBoard + "]");
        }
    }
}