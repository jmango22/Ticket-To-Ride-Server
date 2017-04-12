package edu.goldenhammer.mongoStuff;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSON;
import edu.goldenhammer.model.GameModel;
import edu.goldenhammer.server.Serializer;
import org.bson.Document;

import java.net.UnknownHostException;

public class MongoDriver {
    private static MongoClient mongoClient;
    private static MongoClient getClient() throws UnknownHostException{
        if(mongoClient == null)
            mongoClient = new MongoClient( "localhost" );
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
        UpdateOptions options = new UpdateOptions().upsert(true);
        return coll.update(query, update, true, false).getN() > 0;
//        coll.findAndModify(query, update);
    }





}
