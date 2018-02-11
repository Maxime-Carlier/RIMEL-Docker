package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.List;

import static fr.polytech.rimel.rimeldocker.api.GithubAPI.OAuthToken;

public class CommitProcessor extends DoFn<Repository, Repository> {
    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {

        Repository repository = context.element();
        GitHub github = GitHub.connectUsingOAuth(OAuthToken);
        GHRepository githubRepo = github.getRepository(repository.getName());

        List<GHCommit> commits =githubRepo.listCommits().asList();
        int nbCommit = commits.size();
        Repository result = repository.clone();
        result.setNbOfCommits(nbCommit);
        context.output(repository);
    }
}
