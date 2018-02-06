package fr.polytech.rimel.rimeldocker;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.transforms.ToJson;
import fr.polytech.rimel.rimeldocker.transforms.TraceDockerCompose;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws APIException, IOException {
//        if (args.length != 2) {
//            System.out.println("Il faut lancer: $ java -jar polling.jar \"VOTRE_TOKEN\" \"VOTRE_PSEUDO_GITHUB\"");
//            System.exit(1);
//        }
//
//        GithubAPI.setTOKEN(args[0]);
//        GithubAPI.setAGENT(args[1]);
//
//        List<Repository> repositories = GithubAPI.getInstance().getRepositoriesBySearchItem("topic:docker+is:public");
//
//        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
//        Pipeline pipeline = Pipeline.create(pipelineOptions);
//
//        pipeline
//                .apply(Create.of(repositories))
//                .apply(ParDo.of(new HasDockerCompose()))
//                .apply(ParDo.of(new ToString()));
//
//        pipeline.run().waitUntilFinish();
        Repository repository = new Repository();
        repository.setOwner("scipio3000");
        repository.setName("polytech-soa");
        repository.setUrl("https://github.com/scipio3000/polytech-soa");
        repository.setDockerPaths(new ArrayList<>(Arrays.asList("integration/docker-compose.yml")));
        List<Repository> repositories = new ArrayList<>();
        repositories.add(repository);
        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        Pipeline pipeline = Pipeline.create(pipelineOptions);

        pipeline
                .apply(Create.of(repositories))
                .apply(ParDo.of(new TraceDockerCompose()))
                .apply(ParDo.of(new ToJson()));

        pipeline.run().waitUntilFinish();
    }
}
