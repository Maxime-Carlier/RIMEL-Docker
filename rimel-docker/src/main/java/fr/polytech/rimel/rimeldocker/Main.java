package fr.polytech.rimel.rimeldocker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubClientFactory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.transforms.CompareDCVersion;
import fr.polytech.rimel.rimeldocker.transforms.ContributorProcessor;
import fr.polytech.rimel.rimeldocker.transforms.HasDockerCompose;
import fr.polytech.rimel.rimeldocker.transforms.TraceDockerCompose;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class.getName());

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

        List<Repository> repositories = new ArrayList<>();
        for (GHRepository ghRepository : ghRepositories) {
            Repository repository = new Repository();
            repository.setGhRepository(ghRepository);
            repositories.add(repository);
            // Pour debuguer qu'une petite partie des résultats plutôt que tout
            if (repositories.size() > 7) {
                break;
            }
        }
        LOGGER.info("Got "+repositories.size()+" repositories in the sample");

        // Step 2 : Retrieve the number of contributor in the project
        List<Repository> repositories2 = new ArrayList<>();
        for (Repository r : repositories) {
            Repository result = ContributorProcessor.processElement(r);
            repositories2.add(result);
        }

        // Step 3 : Retrieve the path of the Docker compose file
        List<Repository> repositories3 = new ArrayList<>();
        for (Repository r : repositories2) {
            Repository result = HasDockerCompose.processElement(r);
            if (result != null) {
                repositories3.add(result);
            }
        }

        // Step 4 : Retrieve the docker compose change
        List<Repository> repositories4 = new ArrayList<>();
        for (Repository r : repositories3) {
            Repository result = TraceDockerCompose.processElement(r);
            repositories4.add(result);
        }

        // Step 5 : Compare the Docker Compose versions
        List<Repository> repositories5 = new ArrayList<>();
        for (Repository r : repositories4) {
            Repository result = new CompareDCVersion().processElement(r);
            repositories5.add(result);
        }
    }
}
