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
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.DoFn;
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
@DefaultCoder(AvroCoder.class)
public class TraceDockerCompose extends DoFn<Repository, Repository> {

    private static Logger LOGGER = Logger.getLogger(TraceDockerCompose.class.getName());

    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {
        Repository repository = context.element().clone();
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
        context.output(repository);
    }

    private void retrieveFileHistory(String path, GHCommit ghCommit) throws IOException {
        String dockerComposeContent = retrieveFile(path, ghCommit.getFiles());
    }

    private String retrieveFile(String path, List<GHCommit.File> files) {
        String output = "";
        for (GHCommit.File ghFile : files) {
            if (ghFile.getFileName().equals(path)) {
                LOGGER.info("Raw URL: "+ghFile.getRawUrl());
            }
        }
        return output;
    }
}
