package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;

public class ToString extends DoFn<Repository, Repository> {
    @ProcessElement
    public void processElement(ProcessContext context) {
        System.out.println(context.element().toString());
    }
}
