package edu.goldenhammer.database.data_types;

/**
 * Created by seanjib on 2/22/2017.
 */
public interface IDatabaseCommand {
    String getCommandID();
    String getGameID();
    String getUserID();
    String getMetadata();
    boolean isVisibleToSelf();
    boolean isVisibleToAll();
}
