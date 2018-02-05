package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;

/**
 * For a given String input as a URL, Extract the repository base info
 * and return a Repository object
 */
public final class ExtractBaseInfo extends DoFn<String, Repository> {
    @ProcessElement
    public void processElement(ProcessContext context) {
        String repoURL = context.element();

    }
}
