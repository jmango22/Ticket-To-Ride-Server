package edu.goldenhammer.mongoStuff;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSON;
import edu.goldenhammer.model.GameModel;
import edu.goldenhammer.server.Serializer;
import edu.goldenhammer.server.commands.BaseCommand;
import org.bson.Document;

import java.net.UnknownHostException;

public class MongoDriver {
    private static MongoClient mongoClient;
    private static MongoClient getClient() throws UnknownHostException{
        if(mongoClient == null) {
            mongoClient = new MongoClient("localhost");
//            DBCollection coll = mongoClient.getDB("ticket_to_ride").getCollection("games");
//
        }
        return mongoClient;
    }
    public MongoDriver() {

    }

    private DBCollection getCollection(String collectionName) throws UnknownHostException{
        return getClient().getDB("ticket_to_ride").getCollection(collectionName);
    }

    private DBCollection getGameCollection() throws UnknownHostException {
        return getCollection("games");
    }

    private DBCollection getUserCollection() throws UnknownHostException {
        return getCollection("user");
    }

    private DBObject getOne(Cursor cursor) {
        if(cursor.hasNext())
            return cursor.next();
        return null;
    }

    /**
     *
     * @param gameName
     * @return the json what is contained in mongo
     * @throws UnknownHostException
     */
    public MongoGame getGame(String gameName) throws UnknownHostException{
        DBCollection coll = getGameCollection();
        BasicDBObject query = new BasicDBObject("gameName", gameName);
        Cursor cursor = coll.find(query);
        DBObject object = getOne(cursor);
        if(object != null) {
            return MongoGame.deserialize(object.toString());
        } else {
            return null;
        }
    }
    //games player not in
    //games player is in
    //get checkpoint
    //addSingleCommand

    public boolean setGame(MongoGame game) throws UnknownHostException {
        DBCollection coll = getGameCollection();
        DBObject json = (DBObject) JSON.parse(Serializer.serialize(game));
        DBObject query = new BasicDBObject("gameName", new BasicDBObject("$eq",game.gameName));
        DBObject update = new BasicDBObject();
        update.put("$set", json);
        return coll.update(query, update, true, false).getN() == 1;
    }

    public boolean setUser(MongoUser user) throws UnknownHostException {
        DBCollection coll = getGameCollection();
        DBObject json = (DBObject) JSON.parse(Serializer.serialize(user));
        DBObject query = new BasicDBObject("username", new BasicDBObject("$eq",user.getUsername()));
        DBObject update = new BasicDBObject();
        update.put("$set", json);
        return coll.update(query, update, true, false).getN() == 1;
    }

    public MongoUser getUser(String username) throws UnknownHostException {
        DBCollection coll = getUserCollection();
        BasicDBObject query = new BasicDBObject("username", username);
        Cursor cursor = coll.find(query);
        DBObject object = getOne(cursor);
        if(object != null) {
            return MongoUser.deserialize(object.toString());
        } else {
            return null;
        }
    }

    public boolean addSingleCommand(BaseCommand command, String gameName) throws UnknownHostException{
        DBObject json = (DBObject) JSON.parse(Serializer.serialize(command));
        DBCollection coll = getGameCollection();
        DBObject query1 = new BasicDBObject("gameName", new BasicDBObject("$eq",gameName));
        DBObject query2 = new BasicDBObject("commands", new BasicDBObject("$size",command.getCommandNumber()));
        BasicDBList and = new BasicDBList();
        and.add(query1);
        and.add(query2);
        DBObject query = new BasicDBObject("$and", and);
        DBObject push = new BasicDBObject("$push", new BasicDBObject("commands",json));
        return coll.update(query, push).getN() == 1;
    }





}
