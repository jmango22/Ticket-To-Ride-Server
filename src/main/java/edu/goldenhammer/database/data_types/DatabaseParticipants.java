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

    public static final String CREATE_STMT = String.format(
                    "CREATE TABLE if not exists 1$s (\n" +
                    "    user_id INTEGER NOT NULL,\n" +
                    "    game_id INTEGER NOT NULL,\n" +
                    "    player_number INTEGER NOT NULL,\n" +
                    "    PRIMARY Key(user_id, game_id),\n" +
                    "    FOREIGN KEY(user_id)\n" +
                    "      references player\n" +
                    "      on delete CASCADE,\n" +
                    "    FOREIGN KEY(game_id)\n" +
                    "      references game\n" +
                    "      on delete CASCADE\n" +
                    ")"
            , TABLE_NAME);

    public DatabaseParticipants(String id, String name, boolean started, List<String> players) {
        this.id = id;
        this.name = name;
        this.started = started;
        this.players = players;
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


    public static String columnNames() {
        return String.join(",", USER_ID, GAME_ID, PLAYER_NUMBER);
    }


    private String id;
    private String name;
    private boolean started;
    private List<String> players;
}
