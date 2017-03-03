package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/3/2017.
 */
public class DatabaseGame implements IDatabaseGame, Serializable {

    public static final String ID = "game_id";
    public static final String GAME_NAME = "name";
    public static final String STARTED = "started";
    public static final String PLAYER_TURN = "player_turn";
    public static final String TABLE_NAME = "game";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE IF NOT EXISTS %1$s (\n" +
                    "    %2$s SERIAL UNIQUE,\n" +
                    "    %3$s VARCHAR(20) UNIQUE NOT NULL,\n" +
                    "    %4$s BOOLEAN NOT NULL,\n" +
                    "    %5$s INTEGER," +
                    "    PRIMARY KEY(%2$s)" +
                    ");"
            , TABLE_NAME, ID, GAME_NAME, STARTED, PLAYER_TURN);

    public DatabaseGame(String id, String name, Boolean started, int playerTurn){
        this.id = id;
        this.name = name;
        this.started = started;
        this.playerTurn = playerTurn;
    }

    public String getID() {
        return id;
    }


    public String getName() {
        return name;
    }


    public Boolean isStarted() {
        return started;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public static String columnNames() {
        return String.join(",", ID, GAME_NAME, STARTED, PLAYER_TURN);
    }

    private String id;
    private String name;
    private Boolean started;
    private int playerTurn;

}
