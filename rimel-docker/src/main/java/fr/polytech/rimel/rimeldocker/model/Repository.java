package fr.polytech.rimel.rimeldocker.model;

import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
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
    private Map<String, Map<String,UpdateTimeStamp>>  versionEvolutionMap;
    private Map<String, List<DockerCompose>> dockerComposes;


    public Repository() {
        ghRepository =null;
        hasDockerCompose = false;
        dockerPaths = new ArrayList<>();
        versionEvolutionMap = new HashMap<>();
        dockerComposes = new HashMap<>();
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

    public Map<String, Map<String,UpdateTimeStamp>>  getVersionEvolutionMap() {
        return versionEvolutionMap;
    }

    public void setVersionEvolutionMap(Map<String, Map<String,UpdateTimeStamp>>  versionEvolutionMap) {
        this.versionEvolutionMap = versionEvolutionMap;
    }

    public Map<String, List<DockerCompose>> getDockerComposes() {
        return dockerComposes;
    }

    public void setDockerComposes(Map<String, List<DockerCompose>> dockerComposes) {
        this.dockerComposes = dockerComposes;
    }

    public Repository clone() {
        Repository r = new Repository();
        r.setGhRepository(this.ghRepository);
        r.setHasDockerCompose(this.hasDockerCompose);
        r.setDockerPaths(this.dockerPaths);
        r.setVersionEvolutionMap(this.versionEvolutionMap);
        r.setDockerComposes(this.dockerComposes);
        return r;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "ghRepository=" + ghRepository +
                ", hasDockerCompose=" + hasDockerCompose +
                ", nbOfContributors=" + nbOfContributors +
                ", nbOfCommits=" + nbOfCommits +
                ", dockerPaths=" + dockerPaths +
                ", dockerComposes=" + dockerComposes +
                ", versionEvolutionMap=" + versionEvolutionMap +
                '}';
    }
}
