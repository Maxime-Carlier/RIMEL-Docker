package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public class ContributorProcessor {

    public static Repository processElement(Repository repository) throws IOException {
        int nbUsers = repository.getGhRepository().listContributors().asSet().size();
        Repository result = repository.clone();
        result.setNbOfContributors(nbUsers);
        return result;
    }
}
