package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;

/**
 * For a given String input as a URL, Extract the repository base info
 * and return a Repository object
 */
public final class HasDockerCompose extends DoFn<Repository, Repository> {
    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {
        Repository repository = context.element();
        boolean projectHasComposeFile = GithubAPI.getInstance().searchDockerCompose(repository.getName());
        if (projectHasComposeFile) {
            Repository result = repository.clone();
            result.setHasDockerCompose(true);
            context.output(repository);
        }
    }
}
