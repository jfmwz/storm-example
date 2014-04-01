package com.packtpub.storm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private Board board;
    private List<Board> history;
    private String player;
    static private Random generator = new Random();

    public GameState(Board board, List<Board> history, String player) {
        this.board = board;
        this.history = history;
        this.player = player;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("GAME [");
        sb.append(board.toKey()).append("]");
        sb.append(": player(").append(player).append(")\n");
        sb.append("   history [");
        for (Board b : history) {
            sb.append(b.toKey()).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public Board getBoard() {
        return board;
    }

    public List<Board> getHistory() {
        return history;
    }

    public String getPlayer() {
        return player;
    }

    public static GameState playAtRandom(Board currentBoard, String player) {
        List<Board> history = new ArrayList<Board>();
        while (!currentBoard.isEndState()) {
            List<Board> boards = currentBoard.nextBoards(player);
            int move = generator.nextInt(boards.size());

            history.add(currentBoard);
            player = Player.next(player);
            currentBoard = boards.get(move);
        }
        return new GameState(currentBoard, history, player);
    }

    public int score() {
        return this.board.score(this.player);
    }

}
