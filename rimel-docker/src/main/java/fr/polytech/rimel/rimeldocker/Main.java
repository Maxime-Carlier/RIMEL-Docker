package fr.polytech.rimel.rimeldocker;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubClientFactory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.transforms.HasDockerCompose;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
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
        }

        // Step 2 : Retrieve the path of the Docker compose file
        List<Repository> repositories2 = new ArrayList<>();
        for (Repository r : repositories) {
            Repository result = HasDockerCompose.processElement(r);
            if (result != null) {
                repositories2.add(result);
            }
        }

        System.out.println("holup");
    }
}
