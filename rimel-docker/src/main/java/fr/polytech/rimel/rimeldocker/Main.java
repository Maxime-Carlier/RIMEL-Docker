package fr.polytech.rimel.rimeldocker;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.transforms.HasDockerCompose;
import fr.polytech.rimel.rimeldocker.transforms.ToString;
import fr.polytech.rimel.rimeldocker.transforms.PathDockerCompose;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws APIException, IOException {
        if (args.length != 2) {
            System.out.println("Il faut lancer: $ java -jar polling.jar \"VOTRE_TOKEN\" \"VOTRE_PSEUDO_GITHUB\"");
            System.exit(1);
        }

        GithubAPI.setTOKEN(args[0]);
        GithubAPI.setAGENT(args[1]);

        List<Repository> repositories = GithubAPI.getInstance().getRepositoriesBySearchItem("topic:docker+is:public");

        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        Pipeline pipeline = Pipeline.create(pipelineOptions);

        pipeline
                .apply(Create.of(repositories))
                //.apply(ParDo.of(new HasDockerCompose()))
                .apply(ParDo.of(new PathDockerCompose()))
                .apply(ParDo.of(new ToString()));

        pipeline.run().waitUntilFinish();
    }
}
