package edu.goldenhammer.mongoStuff;

/**
 * Created by devonkinghorn on 4/12/17.
 */
public class MongoUser {
    private String username;
    private String password;
    private String token;

    public MongoUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
