package fr.polytech.rimel.rimeldocker.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.polytech.rimel.rimeldocker.model.tracer.Commit;
import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
import fr.polytech.rimel.rimeldocker.model.tracer.Parent;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitHistory {


    @JsonProperty("sha")
    private String sha;
    @JsonProperty("url")
    private String url;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("commit")
    private Commit commit;
    @JsonProperty("parents")
    private List<Parent> parents;
    private DockerCompose dockerFile;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public List<Parent> getParents() {
        return parents;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }

    public DockerCompose getDockerFile() {
        return dockerFile;
    }

    public void setDockerFile(DockerCompose dockerFile) {
        this.dockerFile = dockerFile;
    }

    @Override
    public String toString() {
        return "CommitHistory{" +
                "sha='" + sha + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", commit=" + commit +
                ", parents=" + parents +
                ", dockerFile=" + dockerFile +
                '}';
    }
}
