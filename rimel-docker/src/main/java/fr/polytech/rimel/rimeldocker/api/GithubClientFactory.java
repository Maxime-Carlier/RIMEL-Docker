package fr.polytech.rimel.rimeldocker.api;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GithubClientFactory {
    private static List<GitHub> gitHubs = new ArrayList<>();

    public static GitHub getOne() {
        GitHub result = null;
            for (GitHub gh : gitHubs) {
                try {
                    GHRateLimit limit = gh.rateLimit();
                    if (limit.remaining > 0) {
                        return gh;
                    }
                    Date d = limit.getResetDate();
                    if (result == null || d.before(result.getRateLimit().getResetDate())) {
                        result = gh;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
    }

    public static void addGitHub(GitHub gh){
        gitHubs.add(gh);
    }
}
