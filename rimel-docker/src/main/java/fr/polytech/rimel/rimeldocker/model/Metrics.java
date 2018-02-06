package fr.polytech.rimel.rimeldocker.model;

import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DefaultCoder(AvroCoder.class)
public class Metrics {

    Repository repository;
    Map<String , List<CommitHistory>> commitHistories;

    public Metrics(){
        commitHistories = new HashMap<>();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Map<String , List<CommitHistory>>  getCommitHistories() {
        return commitHistories;
    }

    public void setCommitHistories(Map<String , List<CommitHistory>> commitHistories) {
        this.commitHistories = commitHistories;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "repository=" + repository +
                ", commitHistories=" + commitHistories +
                '}';
    }
}
