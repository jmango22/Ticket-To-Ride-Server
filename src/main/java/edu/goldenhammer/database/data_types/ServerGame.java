package edu.goldenhammer.database.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/3/2017.
 */
public class ServerGame implements IServerGame, Serializable {

    public static final String ID = "game_id";
    public static final String GAME_NAME = "name";
    public static final String STARTED = "started";
    public static final String TABLE_NAME = "game";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE if not exists %1$s (\n" +
                    "    game_id SERIAL unique,\n" +
                    "    name VARCHAR(20) NOT NULL,\n" +
                    "    started BOOLEAN NOT NULL,\n" +
                    "    PRIMARY KEY(game_id)" +
                    ")"
            , TABLE_NAME);

    public ServerGame(String id, String name, Boolean started){
        this.id = id;
        this.name = name;
        this.started = started;
    }


    public String getID() {
        return id;
    }


    public String getName() {
        return name;
    }


    public Boolean getStarted() {
        return started;
    }

    private String id;
    private String name;
    private Boolean started;

}
