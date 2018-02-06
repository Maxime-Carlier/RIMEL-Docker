package fr.polytech.rimel.rimeldocker.model.tracer;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

@DefaultCoder(AvroCoder.class)
public class UpdateTimeStamp {

    private String dateUpdated;
    private long delay; // in days

    public UpdateTimeStamp(){
        dateUpdated = new String();
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "UpdateTimeStamp{" +
                "dateUpdated=" + dateUpdated +
                ", delay=" + delay +
                '}';
    }
}
