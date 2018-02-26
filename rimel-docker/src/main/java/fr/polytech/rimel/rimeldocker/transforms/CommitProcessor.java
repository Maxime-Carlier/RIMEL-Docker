package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.List;


public class CommitProcessor {
    public static Repository processElement(Repository repository) throws APIException, IOException {

        GHRepository githubRepo = repository.getGhRepository();
        List<GHCommit> commits =githubRepo.listCommits().asList();
        int nbCommit = commits.size();
        Repository result = repository.clone();
        result.setNbOfCommits(nbCommit);
        return result;
    }
}
