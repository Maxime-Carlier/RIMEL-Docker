package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


@DefaultCoder(AvroCoder.class)
public class ToJson extends DoFn<Repository, Repository> {
    @ProcessElement
    public void processElement(ProcessContext context) {
        try {
            System.out.println(new ObjectMapper().writeValueAsString(context.element()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
