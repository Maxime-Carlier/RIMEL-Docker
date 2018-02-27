package fr.polytech.rimel.rimeldocker.persistance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import fr.polytech.rimel.rimeldocker.model.MongoRepository;
import fr.polytech.rimel.rimeldocker.model.Repository;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoConnection {

    public static final String MONGO_REPOSITORIES = "mongo-repositories";
    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DATABASE = "rimel_final";
    private Logger LOGGER = Logger.getLogger(MongoConnection.class.getName());
    private MongoClient mongoClient;

    public MongoConnection() {
        LOGGER.log(Level.INFO, "Attempting connection to DB on " + HOST);
        mongoClient = new MongoClient(HOST, PORT);
        LOGGER.log(Level.INFO, "Connection to db on" + HOST + " successful");
    }


    public void insert(MongoRepository mongoRepository) throws JsonProcessingException {
        insertInCollection(mongoRepository);
    }


    public void insertInCollection(MongoRepository mongoRepository) throws JsonProcessingException {
        MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE).getCollection(MONGO_REPOSITORIES);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(mongoRepository);
        LOGGER.log(Level.INFO, "Attempting insertion of json: " + json);
        try {
            collection.insertOne(Document.parse(json));
        } catch (MongoException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        LOGGER.log(Level.INFO, "Insertion success");
    }

    public MongoCursor<Document> findCursor(String colName) {
        MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE).getCollection(colName);
        return collection.find().iterator();
    }

    public void replaceOne(MongoRepository mongoRepository) throws JsonProcessingException {
        MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE).getCollection(MONGO_REPOSITORIES);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(mongoRepository);
        JsonParser parser = new JsonParser();
        double id = parser.parse(json).getAsJsonObject().get("_id").getAsDouble();
        LOGGER.log(Level.INFO, "Attempting insertion of json: " + json);
        try {
            BsonDocument query = new BsonDocument();
            query.put("_id", new BsonDouble(id));
            collection.replaceOne(query, Document.parse(json));
        } catch (MongoException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        LOGGER.log(Level.INFO, "Replace succes");
    }
}