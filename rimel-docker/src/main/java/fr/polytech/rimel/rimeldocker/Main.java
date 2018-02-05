package fr.polytech.rimel.rimeldocker;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws APIException, IOException {
        if (args.length != 2) {
            System.out.println("Il faut lancer: $ java -jar polling.jar \"VOTRE_TOKEN\" \"VOTRE_PSEUDO_GITHUB\"");
            System.exit(1);
        }

        GithubAPI githubAPI = new GithubAPI(args[0], args[1]);

        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        Pipeline pipeline = Pipeline.create(pipelineOptions);

        List<Repository> repositories = githubAPI.getRepositoriesBySearchItem("topic:docker");
        for (Repository r : repositories) {
            System.out.println(r.toString());
        }

    }
}
