package fr.polytech.rimel.rimeldocker.transforms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.api.GithubClientFactory;
import fr.polytech.rimel.rimeldocker.model.CommitHistory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
import fr.polytech.rimel.rimeldocker.model.tracer.File;
import fr.polytech.rimel.rimeldocker.model.tracer.FileTracer;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For a given repository, we extract the history of the docker compose file
 */
public class TraceDockerCompose {

    private static Logger LOGGER = Logger.getLogger(TraceDockerCompose.class.getName());

    public static Repository processElement(Repository repository) throws APIException, IOException {
        Map<String , List<CommitHistory>> commitMap = new HashMap<>();
        for (String path : repository.getDockerPaths()) {
            LOGGER.log(Level.INFO, "Processing commit history from file " + path + " at " + repository.getGhRepository().getName());
            List<GHCommit> commitHistories = GithubAPI.getCommitsForFile(repository.getGhRepository(), path);
            for (GHCommit ghCommit : commitHistories) {
                try {
                    retrieveFileHistory(path, ghCommit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        repository.setCommitHistoryMap(commitMap);
        return repository;
    }

    private static void retrieveFileHistory(String path, GHCommit ghCommit) throws IOException {
        String dockerComposeContent = retrieveFile(path, ghCommit.getFiles());
    }

    private static String retrieveFile(String path, List<GHCommit.File> files) {
        String output = "";
        for (GHCommit.File ghFile : files) {
            if (ghFile.getFileName().equals(path)) {
                LOGGER.info("Raw URL: "+ghFile.getRawUrl());
            }
        }
        return output;
    }
}
