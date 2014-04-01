package com.packtpub.storm.model;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

public class BoardTest {
    private static final Logger LOG = LoggerFactory.getLogger(BoardTest.class);

    @Test
    public void testToString() {
        Board board = new Board();
        LOG.debug(board.toString());
    }

    @Test
    public void testMoves() {
        Board board = new Board();
        List<Board> boards = board.nextBoards("X");
        for (Board b : boards) {
            LOG.debug(b.toString());
        }
    }

    @Test
    public void testWinningGame() {
        Board board = new Board();
        board.board[0][0] = "X";
        board.board[0][1] = "X";
        assertEquals(10000, board.scoreLine("X", "X", "X", "X"));
        assertEquals(false, board.isEndState());
        assertEquals(130, board.scoreLines("X"), Integer.MAX_VALUE);

        board.board[0][2] = "X";
        assertEquals(Integer.MAX_VALUE, board.scoreLines("X"), Integer.MAX_VALUE);
        assertEquals(true, board.isEndState());


    }

}
