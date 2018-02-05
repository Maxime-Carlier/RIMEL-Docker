package fr.polytech.rimel.rimeldocker.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.polytech.rimel.rimeldocker.model.Repository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GithubAPI {

    private static final String API =               "https://api.github.com";
    private static final String SEARCH_TOPIC =      API + "/search/repositories";
    private static final String GET_REPOSITORY =    API + "/repos";
    private static final String ID = "id";
    private static final String FULL_NAME = "full_name";
    private static final String HTML_URL = "html_url";
    private static final String FORK = "fork";
    private static final String CREATED_AT = "created_at";

    private static String AUTHORIZATION = "Authorization";
    private static String TOKEN = "Bearer 1a3ea623f809bef78d84c747fa757849a19e499a";
    private static String USER_AGENT = "User-agent";
    private static String AGENT = "AjroudRami";


    private static Logger LOGGER = Logger.getLogger(GithubAPI.class.getName());

    private static final DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();

    private OkHttpClient client;


    private int remainingRequest;
    private long timeToReset;

    private int searchRemaining;
    private long timeToSearchReset;

    public GithubAPI(String token, String username) {
        client = new OkHttpClient();
        TOKEN = token;
        AGENT = username;
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
        updateLimits(remaining, reset);
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
            DateTime createdDateTime = parser.parseDateTime(createdAt);
            repository.setId(id);
            repository.setFork(fork);
            repository.setName(name);
            repository.setUrl(url);
            repository.setCreationDate(createdDateTime);
            repos.add(repository);
        }
        return repos;
    }

    private void updateLimits(String remaining, String reset) {
        this.searchRemaining = (remaining == null) ? -1 : Integer.parseInt(remaining);
        this.timeToSearchReset = (reset == null) ? this.timeToSearchReset : Long.parseLong(reset);
    }

    private boolean canSendRequest() {
        if (remainingRequest > 0 | remainingRequest == -1) return true;
        else return timeToReset - (System.currentTimeMillis() / 1000) < 0;
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
}
