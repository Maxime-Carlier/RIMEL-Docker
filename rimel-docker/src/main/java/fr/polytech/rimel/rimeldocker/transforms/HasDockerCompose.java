package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.kohsuke.github.GHException;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * For a given String input as a URL, Extract the repository base info
 * Retrieve all docker-compose.yml path in a repository
 * and return a Repository object
 */
public final class HasDockerCompose {

    private static Logger LOGGER = Logger.getLogger(HasDockerCompose.class.getName());

    public static Repository processElement(Repository repository) throws APIException, IOException {
        try {
            List<String> dockerPaths = GithubAPI.retrieveFilePath(repository.getGhRepository().getName(), repository.getGhRepository().getOwner().getName());
            if (!dockerPaths.isEmpty()) {
                LOGGER.info("Found " + dockerPaths.size() +" docker compose file in repository " + repository.getGhRepository().getName());
                Repository result = repository.clone();
                result.setHasDockerCompose(true);
                result.setDockerPaths(dockerPaths);
                return result;
            } else {
                return null;
            }
        } catch (GHException ghe) {
            return null;
        }

    }
}
