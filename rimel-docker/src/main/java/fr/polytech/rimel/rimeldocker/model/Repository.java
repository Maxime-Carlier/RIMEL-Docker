package fr.polytech.rimel.rimeldocker.model;

import fr.polytech.rimel.rimeldocker.model.tracer.UpdateTimeStamp;
import org.kohsuke.github.GHRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Repository {

    private GHRepository ghRepository;
    private boolean hasDockerCompose;
    private int nbOfContributors;
    private int nbOfCommits;
    private List<String> dockerPaths;
    private Map<String, List<CommitHistory>> commitHistoryMap;
    private Map<String, Map<String, UpdateTimeStamp>> versionEvolutionMap;

    public Repository() {
        ghRepository =null;
        hasDockerCompose = false;
        dockerPaths = new ArrayList<>();
        commitHistoryMap = new HashMap<>();
        versionEvolutionMap = new HashMap<>();
    }

    public GHRepository getGhRepository() {
        return ghRepository;
    }

    public void setGhRepository(GHRepository ghRepository) {
        this.ghRepository = ghRepository;
    }

    public boolean hasDockerCompose() {
        return hasDockerCompose;
    }

    public void setHasDockerCompose(boolean hasDockerCompose) {
        this.hasDockerCompose = hasDockerCompose;
    }

    public int getNbOfContributors() {
        return nbOfContributors;
    }

    public void setNbOfContributors(int nbOfContributors) {
        nbOfContributors = nbOfContributors;
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
        r.setGhRepository(this.ghRepository);
        r.setHasDockerCompose(this.hasDockerCompose);
        r.setDockerPaths(this.dockerPaths);
        r.setCommitHistoryMap(this.commitHistoryMap);
        r.setVersionEvolutionMap(this.versionEvolutionMap);
        return r;
    }
}
