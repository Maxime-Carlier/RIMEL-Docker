package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import java.io.IOException;
import java.util.List;


public class ContributorProcessor {

    public static Repository processElement(Repository repository) throws APIException, IOException {

        GHRepository githubRepo = repository.getGhRepository();
        List<GHUser> users = githubRepo.listCollaborators().asList();
        int nbUsers = users.size();
        Repository result = repository.clone();
        result.setNbOfContributors(nbUsers);
        return result;
    }
}
