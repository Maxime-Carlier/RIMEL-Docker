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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GithubAPI {

    private static final    String API =               "https://api.github.com";
    private static final    String SEARCH_TOPIC =      API + "/search/repositories";
    private static final    String SEARCH_CODE =       API + "/search/code";

    private static final    String ID =                "id";
    private static final    String FULL_NAME =         "full_name";
    private static final    String HTML_URL =          "html_url";
    private static final    String FORK =              "fork";
    private static final    String CREATED_AT =        "created_at";

    private static          String AUTHORIZATION =      "Authorization";
    private static          String TOKEN =              "Bearer 1a3ea623f809bef78d84c747fa757849a19e499a";
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

    public static void setTOKEN(String TOKEN) {
        GithubAPI.TOKEN = TOKEN;
    }

    public static void setAGENT(String AGENT) {
        GithubAPI.AGENT = AGENT;
    }
}
