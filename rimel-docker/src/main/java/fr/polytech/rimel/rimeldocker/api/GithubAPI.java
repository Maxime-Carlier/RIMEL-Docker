package fr.polytech.rimel.rimeldocker.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.polytech.rimel.rimeldocker.model.Repository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GithubAPI {

    public static final     String OAuthToken = "";
    private static final    String API =               "https://api.github.com";
    private static final    String SEARCH_TOPIC =      API + "/search/repositories";
    private static final    String SEARCH_CODE =       API + "/search/code";

    private static final    String ID =                "id";
    private static final    String FULL_NAME =         "full_name";
    private static final    String HTML_URL =          "html_url";
    private static final    String FORK =              "fork";
    private static final    String CREATED_AT =        "created_at";

    private static          String AUTHORIZATION =      "Authorization";
    private static          String TOKEN =              "Bearer 60f5d5e864a0ec5684120a896b377f14a1ddd2bb";
    private static          String RAW_TOKEN =          "10f2c7b194656ca08b024363106d2b7cda88695d";
    private static          String USER_AGENT =         "User-agent";
    private static          String AGENT =              "AjroudRami";

    private static GithubAPI instance = null;

    private static Logger LOGGER = Logger.getLogger(GithubAPI.class.getName());

    private OkHttpClient client;

    // Search and other request don't share the same rate limit
    private int remainingRequest;
    private long timeToReset;

    private int searchRemaining;
    private long timeToSearchReset;

    private GithubAPI() {
        client = new OkHttpClient();
    }

    public static GithubAPI getInstance() {
        if (instance == null) {
            instance = new GithubAPI();
        }
        return instance;
    }

    /**
     * Search repository with the given search Item
     * @see <a href="https://developer.github.com/v3/search/#search-repositories">https://developer.github.com/v3/search/#search-repositories</a>
     * @param searchItem The search items to use
     * @return A List of the repositories matching the search items
     */
    public List<Repository> getRepositoriesBySearchItem(String searchItem) throws APIException, IOException {
        List<Repository> results = new ArrayList<>();
        if (!canSendSearchRequest()) throw new APIException("Cannot send request");
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, TOKEN)
                .addHeader(USER_AGENT, AGENT)
                .url(SEARCH_TOPIC + "?q=" + searchItem)
                .build();
        Response response = client.newCall(request).execute();
        String remaining = response.header("x-ratelimit-remaining");
        String reset = response.header("x-ratelimit-reset");
        updateSearchLimits(remaining, reset);
        if (response.code() != 200) {
            throw new APIException("Response code: " + response.code() + " " + response.message());
        }
        String json = response.body().string();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        if (jsonObject.get("total_count").getAsInt() > 0) {
            JsonArray items = jsonObject.get("items").getAsJsonArray();
            results = parseRepositories(items);
        }
        return results;
    }

    private List<Repository> parseRepositories(JsonArray jsonArray) {
        List<Repository> repos = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Repository repository = new Repository();
            long id = jsonArray.get(i).getAsJsonObject().get(ID).getAsLong();
            String name = jsonArray.get(i).getAsJsonObject().get(FULL_NAME).getAsString();
            String url = jsonArray.get(i).getAsJsonObject().get(HTML_URL).getAsString();
            boolean fork = jsonArray.get(i).getAsJsonObject().get(FORK).getAsBoolean();
            String createdAt = jsonArray.get(i).getAsJsonObject().get(CREATED_AT).getAsString();
            DateTime createdDateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(createdAt);
            repository.setId(id);
            repository.setFork(fork);
            repository.setName(name);
            repository.setUrl(url);
            repos.add(repository);
        }
        return repos;
    }

    public boolean searchDockerCompose(String repositoryName) throws APIException, IOException {
        if (!canSendSearchRequest()) throw new APIException("Cannot send request");
        Request request = new Request.Builder()
                .addHeader(AUTHORIZATION, TOKEN)
                .addHeader(USER_AGENT, AGENT)
                .url(SEARCH_CODE + "?q=filename:docker-compose.yml+repo:" + repositoryName)
                .build();
        Response response = client.newCall(request).execute();
        String remaining = response.header("x-ratelimit-remaining");
        String reset = response.header("x-ratelimit-reset");
        this.searchRemaining = (remaining == null) ? -1 : Integer.parseInt(remaining);
        this.timeToSearchReset = (reset == null) ? this.timeToSearchReset : Long.parseLong(reset);
        //LOGGER.log(Level.INFO, "Remaining search: " + searchRemaining);
        if (response.code() != 200)
            throw new APIException("Response code: " + response.code() + " " + response.message()+"\n"+response.body().string());
        String json = response.body().string();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        int nbDockerCompose = jsonObject.get("total_count").getAsInt();
        return nbDockerCompose != 0;
    }

    public List<String> retrieveFilePath(String repoName, String owner) throws IOException {
        GitHub gitHub = GitHub.connectUsingOAuth(RAW_TOKEN);
        List<String> dockerPaths = new ArrayList<>();
        PagedSearchIterable<GHContent> contents = gitHub.searchContent().filename("docker-compose.yml").repo(repoName).user(owner).list();
        contents.forEach(ghContent -> {
            dockerPaths.add(ghContent.getPath());
        });
        return new ArrayList<>(new HashSet<>(dockerPaths));
    }

    private void updateSearchLimits(String remaining, String reset) {
        this.searchRemaining = (remaining == null) ? -1 : Integer.parseInt(remaining);
        this.timeToSearchReset = (reset == null) ? this.timeToSearchReset : Long.parseLong(reset);
    }

    private boolean canSendSearchRequest() {
        if (searchRemaining > 0 | searchRemaining == -1) return true;
        else if (timeToSearchReset - (System.currentTimeMillis() / 1000) < 0) {
            return true;
        } else {
            LOGGER.log(Level.SEVERE, "time :" + (timeToSearchReset - (System.currentTimeMillis() / 1000)) + " search remaining : " + searchRemaining);
            return false;
        }
    }


    /*
            Docker compose history tracing
            @author danial
     */

    private static final String BASE_URL = "https://api.github.com/repos/";
    private static final String CLIENT_ID = "9e0352ec72f276dfc007";
    private static final String CLIENT_SECRET = "7aa5bf7de2646291e1e1fbd4c2439857659ec7e0";
    private static final String GIT_CRED = "client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET;
    private static final String PER_PAGE = "per_page=100";
    private static final String SHA = "sha=";
    private static final int SLEEP_DURATION = 60000;

    public String retrieveCommits(String owner, String repository, String filePath) throws IOException, InterruptedException {
        return establishConnection(BASE_URL + owner + "/"
                + repository + "/commits?path=" + filePath
                + "&" + PER_PAGE + "&" + GIT_CRED);
    }

    public String retrieveCommits(String owner, String repository, String filePath, String sha) throws IOException, InterruptedException {
        return establishConnection(BASE_URL + owner + "/"
                + repository + "/commits?path=" + filePath
                + "&" + PER_PAGE + "&" + SHA
                + sha + "&" + GIT_CRED);
    }

    public String retrieveCommit(String stringURL) throws IOException,InterruptedException {
        return establishConnection(stringURL + "?" + GIT_CRED);
    }

    public String retrieveFile(String stringURL) throws IOException, InterruptedException {
        return establishConnection(stringURL);
    }

    private String establishConnection(String url) throws IOException, InterruptedException {

        com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .get()
                .addHeader("content-type", "application/javascript")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "4d2b8b25-2b29-fed6-f125-434710ae15e1")
                .build();

        LOGGER.log(Level.INFO, "Sending out the following request : " + request.toString());
        com.squareup.okhttp.Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            if (response.code() == 403) {
                // Sleep for 1 minute if our session is revoke
                Thread.sleep(SLEEP_DURATION);
                return establishConnection(url);
            } else {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.code());
            }
        }
        return response.body().string();
    }

    public static void setTOKEN(String TOKEN) {
        GithubAPI.TOKEN = TOKEN;
    }

    public static void setAGENT(String AGENT) {
        GithubAPI.AGENT = AGENT;
    }
}
