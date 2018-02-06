package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;

@DefaultCoder(AvroCoder.class)
public class CompareDCVersion extends DoFn<Repository, Repository> {

    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {


    }
}
