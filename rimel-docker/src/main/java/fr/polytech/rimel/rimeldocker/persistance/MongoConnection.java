package fr.polytech.rimel.rimeldocker.persistance;

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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoConnection {

    public static final String REPOSITORIES = "repositories";
    private static final String HOST = "novagen.fr";
    private static final int PORT = 27017;
    private static final String DATABASE = "rimel_final";
    private Logger LOGGER = Logger.getLogger(MongoConnection.class.getName());
    private MongoClient mongoClient;

    public MongoConnection() {
        LOGGER.log(Level.INFO, "Attempting connection to DB on " + HOST);
        mongoClient = new MongoClient(HOST, PORT);
        LOGGER.log(Level.INFO, "Connection to db on" + HOST + " successful");
    }


    /**
     * Insert a repository in the rimel.randomRepositories collection
     *
     * @param repository
     * @return
     */
    public void insert(Repository repository) {
        insertInCollection(repository);
    }


    public void insertInCollection(Repository repository) {
        MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE).getCollection(REPOSITORIES);
        Gson gson = new Gson();
        String json = toJson(repository);
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

    public void replaceOne(Repository repository) {
        MongoCollection<Document> collection = mongoClient.getDatabase(DATABASE).getCollection(REPOSITORIES);
        String json = toJson(repository);
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


    private String toJson(Repository repository) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", repository.getGhRepository().getFullName());
        map.put("hasDockerCompose", repository.hasDockerCompose());
        map.put("nbOfContributors", repository.getNbOfContributors());
        map.put("nbOfCommits", repository.getNbOfCommits());
        map.put("dockerPaths", repository.getDockerPaths().toArray());
        map.put("versionEvolutionMap", repository.getVersionEvolutionMap());
        map.put("dockerComposes", repository.getDockerComposes());

        return gson.toJson(map);
    }
}