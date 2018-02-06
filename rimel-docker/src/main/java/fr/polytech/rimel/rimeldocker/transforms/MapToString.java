package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.model.CommitHistory;
import fr.polytech.rimel.rimeldocker.model.Metrics;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.DoFn;

import java.util.List;
import java.util.Map;


@DefaultCoder(AvroCoder.class)
public class MapToString extends DoFn<Metrics, Metrics> {
    @ProcessElement
    public void processElement(ProcessContext context) {
        System.out.println(context.element().toString());
    }
}
