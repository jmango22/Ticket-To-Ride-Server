package edu.goldenhammer.data_types;

/**
 * Created by seanjib on 2/3/2017.
 */
public class ServerPlayer implements IServerPlayer {
    public static final String ID = "user_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TABLE_NAME = "player";
    public static final String CREATE_STMT = String.format(
            "CREATE TABLE %1$s if not exists (\n" +
                    "    user_id SERIAL INTEGER not null,\n" +
                    "    username VARCHAR(20) UNIQUE NOT NULL,\n" +
                    "    password VARCHAR(20) NOT NULL,\n" +
                    "    access_token VARCHAR(20) UNIQUE,\n" +
                    "    PRIMARY KEY(user_id)\n" +
                    ")"
            , TABLE_NAME);


    public ServerPlayer(String id, String username, String password, String accessToken) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accessToken = accessToken;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public static String columnNames() {
        return String.join(",", ID, USERNAME, PASSWORD, ACCESS_TOKEN);
    }
    private String id;
    private String username;
    private String password;
    private String accessToken;
}
