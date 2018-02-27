package fr.polytech.rimel.rimeldocker.model;

public class RepoData {

    private String name;
    private int age;
    private String lastCommitDate;
    private int nbCollaborators;
    private double averageTimeToUpdate;
    private double varianceOverComposes;

    public RepoData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    public void setLastCommitDate(String lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    public int getNbCollaborators() {
        return nbCollaborators;
    }

    public void setNbCollaborators(int nbCollaborators) {
        this.nbCollaborators = nbCollaborators;
    }


    public double getAverageTimeToUpdate() {
        return averageTimeToUpdate;
    }

    public void setAverageTimeToUpdate(double averageTimeToUpdate) {
        this.averageTimeToUpdate = averageTimeToUpdate;
    }

    public double getVarianceOverComposes() {
        return varianceOverComposes;
    }

    public void setVarianceOverComposes(double varianceOverComposes) {
        this.varianceOverComposes = varianceOverComposes;
    }
}
