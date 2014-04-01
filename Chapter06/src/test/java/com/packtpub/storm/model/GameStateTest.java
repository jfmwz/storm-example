package com.packtpub.storm.model;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameStateTest {
    private static final Logger LOG = LoggerFactory.getLogger(GameStateTest.class);

    @Test
    public void testToString() {
        Board board = new Board();
        List<Board> boards = board.nextBoards("X");
        Board child = boards.get(0);
        Board grandChild = child.nextBoards("O").get(0);
        List<Board> parents = new ArrayList<Board>();
        parents.add(child);
        parents.add(board);
        GameState gameState = new GameState(grandChild, parents, "X");
        LOG.debug(gameState.toString());
    }
}
