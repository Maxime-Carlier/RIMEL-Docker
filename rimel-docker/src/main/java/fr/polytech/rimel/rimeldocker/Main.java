package fr.polytech.rimel.rimeldocker;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubClientFactory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.transforms.CompareDCVersion;
import fr.polytech.rimel.rimeldocker.transforms.HasDockerCompose;
import fr.polytech.rimel.rimeldocker.transforms.ToString;
import fr.polytech.rimel.rimeldocker.transforms.TraceDockerCompose;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws APIException, IOException {

        if (args.length < 1) {
            System.out.println("Need at least one oAuth token as argument");
            System.exit(1);
        }

        GitHub gh = GitHub.connectUsingOAuth(args[0]);
        GithubClientFactory.addGitHub(gh);


        PagedSearchIterable<GHRepository> ghRepositories = GithubClientFactory.getOne().searchRepositories().q("topic:docker").q("is:public").list();

        List<Repository> repositories = new ArrayList<>();
        for (GHRepository ghRepository : ghRepositories) {
            Repository repository = new Repository();
            repository.setGhRepository(ghRepository);
            repositories.add(repository);
        }

        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        Pipeline pipeline = Pipeline.create(pipelineOptions);


        pipeline
                .apply(Create.of(repositories))
                // Retrieve docker-compose.yml path
                //.apply(ParDo.of(new HasDockerCompose()))
                // Trace each docker-compose.yml history (commits)
                //.apply(ParDo.of(new TraceDockerCompose()));
                // Base on the file history, detect changes in the version
                //.apply(ParDo.of(new CompareDCVersion()))
                // convert to String
                .apply(ParDo.of(new ToString()));

        pipeline.run().waitUntilFinish();
    }
}
