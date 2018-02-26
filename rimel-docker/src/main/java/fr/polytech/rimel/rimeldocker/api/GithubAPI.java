package fr.polytech.rimel.rimeldocker.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.polytech.rimel.rimeldocker.model.CommitHistory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
import fr.polytech.rimel.rimeldocker.model.tracer.File;
import fr.polytech.rimel.rimeldocker.model.tracer.FileTracer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GithubAPI {

    private static Logger LOGGER = Logger.getLogger(GithubAPI.class.getName());

    public static boolean searchDockerCompose(String repositoryName) throws APIException, IOException {
        PagedSearchIterable<GHContent> result = GithubClientFactory.getOne().searchContent().q("filename:docker-compose.yml+repo:" + repositoryName).list();
        return result.getTotalCount() != 0;
    }

    public static List<String> retrieveFilePath(String repoName, String owner) throws GHException {
        List<String> dockerPaths = new ArrayList<>();
        PagedSearchIterable<GHContent> contents = GithubClientFactory.getOne().searchContent().filename("docker-compose.yml").repo(repoName).user(owner).list();
        contents.forEach(ghContent -> {
            dockerPaths.add(ghContent.getPath());
        });
        return new ArrayList<>(new HashSet<>(dockerPaths));
    }

    public static List<GHCommit> getCommitsForFile(GHRepository ghRepository, String dockerFilepath){
        List<GHCommit> commitHistories = new ArrayList<>();
        PagedIterable<GHCommit> commits = ghRepository.queryCommits().path(dockerFilepath).list();
        for (GHCommit commit : commits) {
            commitHistories.add(commit);
        }
        return commitHistories;
    }

}
