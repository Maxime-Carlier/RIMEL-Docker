package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * For a given String input as a URL, Extract the repository base info
 * Retrieve all docker-compose.yml path in a repository
 * and return a Repository object
 */
public final class HasDockerCompose extends DoFn<Repository, Repository> {

    private static Logger LOGGER = Logger.getLogger(HasDockerCompose.class.getName());
    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {
        Repository repository = context.element();
        List<String> dockerPaths = GithubAPI.getInstance().retrieveFilePath(repository.getName(), repository.getOwner());
        if (!dockerPaths.isEmpty()) {
            LOGGER.info("Found " + dockerPaths.size() +" docker compose file in repository " + repository.getName());
            Repository result = repository.clone();
            result.setHasDockerCompose(true);
            result.setDockerPaths(dockerPaths);
            context.output(result);
        }
    }
}
