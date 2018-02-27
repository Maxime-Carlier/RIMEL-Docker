package fr.polytech.rimel.rimeldocker.model;

import fr.polytech.rimel.rimeldocker.model.tracer.UpdateTimeStamp;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.IOException;
import java.util.*;

public class MongoRepository {
    private String repositoryName;
    private String ownerName;
    private List<String> pathToAllDockerCompose;
    private Date repoCreationDate;
    private String languages;
    private int numberOfCollaborators;
    private int numberOfCommits;
    private int numberOfLOC;
    private int ageInDays;
    private Map<String, Map<String,UpdateTimeStamp>> changeDelaysByVersionByDockerFile;

    public MongoRepository() {
        pathToAllDockerCompose = new ArrayList<>();
        changeDelaysByVersionByDockerFile = new HashMap<>();
    }

    public static MongoRepository fromRepository(Repository repository) {
        MongoRepository mongoRepository = new MongoRepository();
        mongoRepository.setRepositoryName(repository.getGhRepository().getFullName());
        mongoRepository.setOwnerName(repository.getGhRepository().getOwnerName());
        mongoRepository.setPathToAllDockerCompose(repository.getDockerPaths());
        try {
            mongoRepository.setRepoCreationDate(repository.getGhRepository().getCreatedAt());
            DateTime now = new DateTime();
            DateTime creationDateTime = new DateTime(mongoRepository.getRepoCreationDate());
            int nbOfDays = Days.daysBetween(creationDateTime, now).getDays();
            mongoRepository.setAgeInDays(nbOfDays);
        } catch (IOException ioe) {
            mongoRepository.setRepoCreationDate(null);
        }
        mongoRepository.setLanguages(repository.getGhRepository().getLanguage());
        mongoRepository.setNumberOfCollaborators(repository.getNbOfContributors());
        //TODO Addnumber of commit
        //mongoRepository.setNumberOfCommits(repository.getNumberofCommit());
        //TODO Add number of LOC
        //mongoRepository.setNumberOfLOC();
        mongoRepository.setChangeDelaysByVersionByDockerFile(repository.getVersionEvolutionMap());
        return mongoRepository;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<String> getPathToAllDockerCompose() {
        return pathToAllDockerCompose;
    }

    public void setPathToAllDockerCompose(List<String> pathToAllDockerCompose) {
        this.pathToAllDockerCompose = pathToAllDockerCompose;
    }

    public Date getRepoCreationDate() {
        return repoCreationDate;
    }

    public void setRepoCreationDate(Date repoCreationDate) {
        this.repoCreationDate = repoCreationDate;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public int getNumberOfCollaborators() {
        return numberOfCollaborators;
    }

    public void setNumberOfCollaborators(int numberOfCollaborators) {
        this.numberOfCollaborators = numberOfCollaborators;
    }

    public int getNumberOfCommits() {
        return numberOfCommits;
    }

    public void setNumberOfCommits(int numberOfCommits) {
        this.numberOfCommits = numberOfCommits;
    }

    public int getNumberOfLOC() {
        return numberOfLOC;
    }

    public void setNumberOfLOC(int numberOfLOC) {
        this.numberOfLOC = numberOfLOC;
    }

    public int getAgeInDays() {
        return ageInDays;
    }

    public void setAgeInDays(int ageInDays) {
        this.ageInDays = ageInDays;
    }

    public Map<String, Map<String, UpdateTimeStamp>> getChangeDelaysByVersionByDockerFile() {
        return changeDelaysByVersionByDockerFile;
    }

    public void setChangeDelaysByVersionByDockerFile(Map<String, Map<String, UpdateTimeStamp>> changeDelaysByVersionByDockerFile) {
        this.changeDelaysByVersionByDockerFile = changeDelaysByVersionByDockerFile;
    }
}
