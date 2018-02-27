package fr.polytech.rimel.rimeldocker.persistance;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.polytech.rimel.rimeldocker.model.MongoRepository;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoConnection {

    public static final String MONGO_REPOSITORIES = "MongoRepositories";
    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DATABASE = "rimel";


    private Logger LOGGER = Logger.getLogger(MongoConnection.class.getName());
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoConnection() {
        LOGGER.log(Level.INFO, "Attempting connection to DB on " + HOST);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = new MongoClient(HOST, MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        LOGGER.log(Level.INFO, "Connection to db on" + HOST + " successful");
        database = mongoClient.getDatabase(DATABASE);
    }

    public void insert(MongoRepository mongoRepository) {
        MongoCollection<MongoRepository> collection = database.getCollection(MONGO_REPOSITORIES, MongoRepository.class);
        collection.insertOne(mongoRepository);
    }

}