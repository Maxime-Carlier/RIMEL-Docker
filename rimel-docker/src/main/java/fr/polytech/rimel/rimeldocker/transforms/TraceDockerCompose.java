package fr.polytech.rimel.rimeldocker.transforms;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
import org.kohsuke.github.GHCommit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For a given repository, we extract the history of the docker compose file
 */
public class TraceDockerCompose {

    private static Logger LOGGER = Logger.getLogger(TraceDockerCompose.class.getName());
    private static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static Repository processElement(Repository repository) {
        for (String path : repository.getDockerPaths()) {
            LOGGER.log(Level.INFO, "Processing commit history from file " + path + " at " + repository.getGhRepository().getName());
            List<GHCommit> commitHistories = GithubAPI.getCommitsForFile(repository.getGhRepository(), path);
            List<DockerCompose> dockerComposeList = new ArrayList<>();
            for (GHCommit ghCommit : commitHistories) {
                try {
                    dockerComposeList.add(retrieveFileHistory(path, ghCommit));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            repository.getDockerComposes().put(path, dockerComposeList);
        }
        return repository;
    }

    private static DockerCompose retrieveFileHistory(String path, GHCommit ghCommit) throws IOException , MismatchedInputException {
        String dockerComposeContent = retrieveFile(path, ghCommit.getFiles());

        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        DockerCompose dockerCompose = objectMapper.readValue(dockerComposeContent, DockerCompose.class);

        if (dockerCompose.getVersion() == null) {
            dockerCompose.setVersion("1");
        }
        dockerCompose.setSrcCode(dockerComposeContent);
        dockerCompose.setCommitDate(ghCommit.getCommitDate());
        return dockerCompose;
    }

    private static String retrieveFile(String path, List<GHCommit.File> files) throws IOException {
        StringBuilder output = new StringBuilder();
        for (GHCommit.File ghFile : files) {
            if (ghFile.getFileName().equals(path)) {
                LOGGER.info("Raw URL: "+ghFile.getRawUrl());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(ghFile.getRawUrl().openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    output.append(inputLine).append("\n");
                }
                in.close();
            }
        }
        return output.toString();
    }
}
