package fr.polytech.rimel.rimeldocker.model;

import fr.polytech.rimel.rimeldocker.model.tracer.UpdateTimeStamp;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@DefaultCoder(AvroCoder.class)
public class Repository {

    private long        id;
    private String      owner;
    private String      name;
    private String      url;
    private boolean     fork;
    private boolean     hasDockerCompose;
    private int         nbOfContributors;
    private int         nbOfCommits;
    private List<String> dockerPaths;
    private Map<String, List<CommitHistory>> commitHistoryMap;
    private Map<String, Map<String, UpdateTimeStamp>> versionEvolutionMap;

    public Repository() {
        hasDockerCompose = false;
        name = "";
        owner = "";
        url = "";
        dockerPaths = new ArrayList<>();
        commitHistoryMap = new HashMap<>();
        versionEvolutionMap = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public boolean isHasDockerCompose() {
        return hasDockerCompose;
    }

    public void setHasDockerCompose(boolean hasDockerCompose) {
        this.hasDockerCompose = hasDockerCompose;
    }

    public int getNbOfContributors() {
        return nbOfContributors;
    }

    public void setNbOfContributors(int nbOfContributors) {
        this.nbOfContributors = nbOfContributors;
    }

    public int getNbOfCommits() {
        return nbOfCommits;
    }

    public void setNbOfCommits(int nbOfCommits) {
        this.nbOfCommits = nbOfCommits;
    }

    public List<String> getDockerPaths() {
        return dockerPaths;
    }

    public void setDockerPaths(List<String> dockerPaths) {
        this.dockerPaths = dockerPaths;
    }

    public Map<String, List<CommitHistory>> getCommitHistoryMap() {
        return commitHistoryMap;
    }

    public void setCommitHistoryMap(Map<String, List<CommitHistory>> commitHistoryMap) {
        this.commitHistoryMap = commitHistoryMap;
    }

    public Map<String, Map<String, UpdateTimeStamp>> getVersionEvolutionMap() {
        return versionEvolutionMap;
    }

    public void setVersionEvolutionMap(Map<String, Map<String, UpdateTimeStamp>> versionEvolutionMap) {
        this.versionEvolutionMap = versionEvolutionMap;
    }

    public Repository clone() {
        Repository r = new Repository();
        r.setId(this.id);
        r.setOwner(this.owner);
        r.setName(this.name);
        r.setUrl(this.url);
        r.setFork(this.fork);
        r.setHasDockerCompose(this.hasDockerCompose);
        r.setNbOfContributors(this.nbOfContributors);
        r.setNbOfCommits(this.nbOfCommits);
        r.setDockerPaths(this.dockerPaths);
        r.setCommitHistoryMap(this.commitHistoryMap);
        r.setVersionEvolutionMap(this.versionEvolutionMap);
        return r;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", fork=" + fork +
                ", hasDockerCompose=" + hasDockerCompose +
                ", nbOfContributors=" + nbOfContributors +
                ", nbOfCommits=" + nbOfCommits +
                ", dockerPaths=" + dockerPaths +
                ", commitHistoryMap=" + commitHistoryMap +
                ", versionEvolutionMap=" + versionEvolutionMap +
                '}';
    }
}
