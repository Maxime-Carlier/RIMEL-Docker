package fr.polytech.rimel.rimeldocker.model.tracer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerCompose {

    private String version;
    private String srcCode;
    private Date commitDate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSrcCode() {
        return srcCode;
    }

    public void setSrcCode(String srcCode) {
        this.srcCode = srcCode;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    @Override
    public String toString() {
        return "DockerCompose{" +
                "version='" + version + '\'' +
                ", srcCode='" + srcCode + '\'' +
                ", commitDate=" + commitDate +
                '}';
    }
}
