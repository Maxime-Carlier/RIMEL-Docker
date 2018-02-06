package fr.polytech.rimel.rimeldocker.model;

import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.joda.time.DateTime;

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
    private List<String> dockerPaths;
    private boolean     fork;
    private boolean     hasDockerCompose;

    public Repository() {
        hasDockerCompose = false;
        dockerPaths = new ArrayList<>();
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getDockerPaths() {
        return dockerPaths;
    }

    public void setDockerPaths(List<String> dockerPaths) {
        this.dockerPaths = dockerPaths;
    }

    public Repository clone() {
        Repository r = new Repository();
        r.setId(this.id);
        r.setName(this.name);
        r.setUrl(this.url);
        r.setFork(this.fork);
        r.setHasDockerCompose(this.hasDockerCompose);
        return r;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", dockerPaths=" + dockerPaths +
                ", fork=" + fork +
                ", hasDockerCompose=" + hasDockerCompose +
                '}';
    }
}
