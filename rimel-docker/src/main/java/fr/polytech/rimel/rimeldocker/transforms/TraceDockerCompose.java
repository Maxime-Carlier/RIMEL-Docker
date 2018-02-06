package fr.polytech.rimel.rimeldocker.transforms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.CommitHistory;
import fr.polytech.rimel.rimeldocker.model.Metrics;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
import fr.polytech.rimel.rimeldocker.model.tracer.File;
import fr.polytech.rimel.rimeldocker.model.tracer.FileTracer;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@DefaultCoder(AvroCoder.class)
public class TraceDockerCompose extends DoFn<Repository, Metrics> {

    private static Logger LOGGER = Logger.getLogger(TraceDockerCompose.class.getName());

    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {
        Repository repository = context.element();
        Map<String , List<CommitHistory>> commitMap = new HashMap<>();
        for (String path : repository.getDockerPaths()) {
            LOGGER.log(Level.INFO, "Processing commit history from file " + path
                    + " at " + repository.getName());
            List<CommitHistory> commitHistories =
                    processCommitHistory(repository.getOwner(), repository.getName(), path);
            commitMap.put(path, commitHistories);
        }
        processFileHistory(commitMap);
        Metrics metrics = new Metrics();
        metrics.setCommitHistories(commitMap);
        metrics.setRepository(repository);
        context.output(metrics);
    }


    /*
        File history
     */

    private void processFileHistory(Map<String, List<CommitHistory>> commitMap){
        for (String key : commitMap.keySet()){
            commitMap.get(key).forEach(commitHistory -> {
                LOGGER.log(Level.INFO, "Processing file history for file " + key);
                retrieveFileHistory(key, commitHistory);
            });
        }
    }

    private void retrieveFileHistory(String path, CommitHistory commitHistory) {
        try {
            String output = GithubAPI.getInstance().retrieveCommit(commitHistory.getUrl());
            String dockerFile = retrieveFile(path, new ObjectMapper().readValue(output,FileTracer.class));
            DockerCompose dockerCompose = new ObjectMapper(new YAMLFactory()).readValue(dockerFile, DockerCompose.class);
            if (dockerCompose.getVersion() == null) {
                dockerCompose.setVersion("1");
            }
            dockerCompose.setSrcCode(dockerFile);
            commitHistory.setDockerFile(dockerCompose);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String retrieveFile(String path, FileTracer fileTracer) {
        String output = "";
        for (File file : fileTracer.getFiles()) {
            if (file.getFileName().equals(path)) {
                try {
                    output = GithubAPI.getInstance().retrieveFile(file.getRawUrl());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return output;
    }

    /*
            Commit history
     */

    private List<CommitHistory> processCommitHistory(String owner, String name, String dockerPath){
        List<CommitHistory> commitHistories = new ArrayList<>();
        String output;
        try {
            output = GithubAPI.getInstance().retrieveCommits(owner, name, dockerPath);
            commitHistories.addAll( new ObjectMapper().readValue(output, new TypeReference<List<CommitHistory>>() {}));
            continueRetrieveCommits(owner, name, dockerPath,commitHistories);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return commitHistories;
    }

    /**
     * Continue to retrieve commits that doesnt fit the first request
     * @param owner
     * @param repository
     * @param filePath
     * @param commitHistoryList
     */
    private void continueRetrieveCommits(String owner, String repository, String filePath, List<CommitHistory> commitHistoryList) {
        try {
            String output = GithubAPI.getInstance().retrieveCommits(owner, repository, filePath, commitHistoryList.get(commitHistoryList.size() - 1).getSha());
            List<CommitHistory> tmp = new ObjectMapper().readValue(output, new TypeReference<List<CommitHistory>>() {});
            if (tmp.size() <= 1 && tmp.get(0).getSha().equals(commitHistoryList.get(commitHistoryList.size() - 1).getSha())) {
                return;
            }
            commitHistoryList.addAll(tmp);
            continueRetrieveCommits(owner, repository, filePath, commitHistoryList);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
