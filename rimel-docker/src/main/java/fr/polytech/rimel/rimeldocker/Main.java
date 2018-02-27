package fr.polytech.rimel.rimeldocker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubClientFactory;
import fr.polytech.rimel.rimeldocker.model.MongoRepository;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.persistance.MongoConnection;
import fr.polytech.rimel.rimeldocker.transforms.*;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws APIException, IOException {

        if (args.length < 1) {
            System.out.println("Error: Need at least one oAuth token as argument");
            System.exit(1);
        }

        for(int i=0; i<args.length;i++) {
            GitHub gh = GitHub.connectUsingOAuth(args[i]);
            GithubClientFactory.addGitHub(gh);
        }


        // Step 1 : Get all the repositories
        PagedSearchIterable<GHRepository> ghRepositories = GithubClientFactory.getOne().searchRepositories().q("topic:docker").q("is:public").list();

        List<Repository> inputRepositories = new ArrayList<>();
        for (GHRepository ghRepository : ghRepositories) {
            Repository repository = new Repository();
            repository.setGhRepository(ghRepository);
            inputRepositories.add(repository);
            // Pour debuguer qu'une petite partie des résultats plutôt que tout
            if (inputRepositories.size() > 4) {
                break;
            }
        }
        LOGGER.info("Got "+inputRepositories.size()+" repositories in the sample");
        /**Persistance**/
        Gson gson = new Gson();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data.json")));
        writer.write("[");
        List<Repository> outputRepositories = new ArrayList<>();
        inputRepositories.parallelStream().forEach((repository -> {
            try {
                int newCount = count.incrementAndGet();
                LOGGER.info("Now processing repository #" + newCount + " " + repository.getGhRepository().getFullName());
                // Step 2 : Retrieve the number of contributor in the project
                repository = ContributorProcessor.processElement(repository);

                // Step 3 : Retrieve the number of commits made
                //repository = CommitProcessor.processElement(repository);

                // Step 3 : Retrieve the path of all the Docker compose files
                repository = HasDockerCompose.processElement(repository);
                if (repository == null) {
                    return;
                }

                // Step 4 : Retrieve the docker compose change
                repository = TraceDockerCompose.processElement(repository);

                // Step 5 : Compare the Docker Compose versions
                repository = new CompareDCVersion().processElement(repository);

                // Step 6 : Final Check before adding the repository to the output
                if (repository != null) {
                    outputRepositories.add(repository);
                    /**Persistance**/
                    MongoRepository mongoRepository = MongoRepository.fromRepository(repository);
                    String json = gson.toJson(mongoRepository);
                    synchronized (writer) {
                        writer.write(json+",");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }));

        writer.write("]");
        writer.close();
        System.out.println("END");
    }
}
