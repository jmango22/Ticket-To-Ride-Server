package edu.goldenhammer.data_types;

import java.io.Serializable;

/**
 * Created by seanjib on 2/3/2017.
 */
public class ServerPlayer implements Player {
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

    private String id;
    private String username;
    private String password;
    private String accessToken;
}
