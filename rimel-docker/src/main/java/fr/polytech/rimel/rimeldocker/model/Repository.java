package fr.polytech.rimel.rimeldocker.model;

import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.joda.time.DateTime;


@DefaultCoder(AvroCoder.class)
public class Repository {

    private long        id;
    private String      name;
    private String      url;
    private boolean     fork;
    private boolean     hasDockerCompose;
    private int         nbOfContributors;
    private int         nbOfCommits;
    private String      path;

    public Repository() {
        hasDockerCompose = false;
        this.path =" ";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Repository clone() {
        Repository r = new Repository();
        r.setId(this.id);
        r.setName(this.name);
        r.setUrl(this.url);
        r.setFork(this.fork);
        r.setHasDockerCompose(this.hasDockerCompose);
        r.setNbOfContributors(this.nbOfContributors);
        r.setNbOfCommits(this.nbOfCommits);
        r.setPath(this.path);

        return r;
    }

    @Override
    public String toString() {


        return "Repository{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", fork=" + fork +
                ", hasDockerCompose=" + hasDockerCompose +
                ", DockerComposePath='" + this.path + '\'' +
                '}';
    }
}
