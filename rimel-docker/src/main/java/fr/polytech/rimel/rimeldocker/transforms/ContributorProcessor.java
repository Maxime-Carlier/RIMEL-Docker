package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import java.io.IOException;
import java.util.List;


public class ContributorProcessor extends DoFn<Repository, Repository> {

    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {

        /*Repository repository = context.element();
        GitHub github = GitHub.connectUsingOAuth(OAuthToken);
        GHRepository githubRepo = github.getRepository(repository.getName());

        List<GHUser> users = githubRepo.listCollaborators().asList();
        int nbUsers = users.size();
        Repository result = repository.clone();
        result.setNbOfContributors(nbUsers);
        context.output(repository);*/
    }
}
