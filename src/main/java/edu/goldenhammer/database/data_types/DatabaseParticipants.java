package edu.goldenhammer.database.data_types;

import java.io.Serializable;
import java.util.List;

/**
 * Created by seanjib on 2/3/2017.
 */
public class DatabaseParticipants implements IDatabaseParticipants, Serializable {
    public static final String TABLE_NAME = "participants";
    public static final String USER_ID = "user_id";
    public static final String GAME_ID = "game_id";
    public static final String PLAYER_NUMBER = "player_number";
    public static final String POINTS = "points";
    public static final String TRAINS_LEFT = "trains_left";

    public static final String CREATE_STMT = String.format(
                    "CREATE TABLE if not exists %1$s (\n" +
                    "    %2$s INTEGER NOT NULL,\n" +
                    "    %3$s INTEGER NOT NULL,\n" +
                    "    %4$s INTEGER,\n" +
                    "    %5$s INTEGER,\n" +
                    "    %6$s INTEGER,\n" +
                    "    PRIMARY KEY(%2$s, %3$s),\n" +
                    "    FOREIGN KEY(%2$s)\n" +
                    "      references %7$s\n" +
                    "      on delete CASCADE,\n" +
                    "    FOREIGN KEY(%3$s)\n" +
                    "      references %8$s\n" +
                    "      on delete CASCADE\n" +
                    ")"
            , TABLE_NAME,
            DatabasePlayer.ID,
            DatabaseGame.ID,
            PLAYER_NUMBER,
            POINTS,
            TRAINS_LEFT,
            DatabasePlayer.TABLE_NAME,
            DatabaseGame.TABLE_NAME);

    public DatabaseParticipants(String id, String name, boolean started, int points, int trainsLeft, List<String> players) {
        this.id = id;
        this.name = name;
        this.started = started;
        this.players = players;
        this.points = points;
        this.trainsLeft = trainsLeft;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public List<String> getPlayers() {
        return players;
    }


    @Override
    public void setPlayers(List<String> players) { this.players = players; }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public int getTrainsLeft() {
        return trainsLeft;
    }

    public static String columnNames() {
        return String.join(",", USER_ID, GAME_ID, PLAYER_NUMBER);
    }


    private String id;
    private String name;
    private boolean started;
    private int points;
    private int trainsLeft;
    private List<String> players;
}
