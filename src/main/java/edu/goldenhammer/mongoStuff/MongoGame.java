package edu.goldenhammer.mongoStuff;

import edu.goldenhammer.model.GameModel;
import edu.goldenhammer.model.Message;
import edu.goldenhammer.server.commands.BaseCommand;

import java.io.Serializable;
import java.util.List;

/**
 * Created by devonkinghorn on 4/10/17.
 */
public class MongoGame implements Serializable {
    GameModel checkpoint;
    int checkpointIndex;
    List<Message> chatMessages;
    List<BaseCommand> commands;
    List<String> players;
    String gameName;

    public MongoGame(GameModel checkpoint, int checkpointIndex, List<Message> chatMessages, List<BaseCommand> commands, List<String> players, String gameName) {
        this.checkpoint = checkpoint;
        this.checkpointIndex = checkpointIndex;
        this.chatMessages = chatMessages;
        this.commands = commands;
        this.players = players;
        this.gameName = gameName;
    }

    public GameModel getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(GameModel checkpoint) {
        this.checkpoint = checkpoint;
    }

    public int getCheckpointIndex() {
        return checkpointIndex;
    }

    public void setCheckpointIndex(int checkpointIndex) {
        this.checkpointIndex = checkpointIndex;
    }

    public List<Message> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<Message> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public List<BaseCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<BaseCommand> commands) {
        this.commands = commands;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public static MongoGame deserialize(String Json) {
        return null;
    }
}
