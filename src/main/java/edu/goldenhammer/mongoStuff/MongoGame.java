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
}
