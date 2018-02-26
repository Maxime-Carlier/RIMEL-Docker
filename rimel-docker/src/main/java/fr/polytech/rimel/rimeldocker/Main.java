package fr.polytech.rimel.rimeldocker;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubClientFactory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.transforms.CompareDCVersion;
import fr.polytech.rimel.rimeldocker.transforms.HasDockerCompose;
import fr.polytech.rimel.rimeldocker.transforms.TraceDockerCompose;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
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
            if (repositories.size() > 10) {
                break;
            }
        }
        LOGGER.info("Got "+repositories.size()+" repositories in the sample");

        // Step 2 : Retrieve the path of the Docker compose file
        List<Repository> repositories2 = new ArrayList<>();
        for (Repository r : repositories) {
            Repository result = HasDockerCompose.processElement(r);
            if (result != null) {
                repositories2.add(result);
            }
        }

        // Step 3 : Retrieve the docker compose change
        List<Repository> repositories3 = new ArrayList<>();
        for (Repository r : repositories2) {
            Repository result = TraceDockerCompose.processElement(r);
            repositories3.add(result);
        }

        // Step 4 : Compare the Docker Compose versions
        List<Repository> repositories4 = new ArrayList<>();
        for (Repository r : repositories3) {
            Repository result = new CompareDCVersion().processElement(r);
            repositories4.add(result);
        }

        // Print result
        for (Repository r : repositories4) {
            System.out.println(r.toString());
        }
    }
}
