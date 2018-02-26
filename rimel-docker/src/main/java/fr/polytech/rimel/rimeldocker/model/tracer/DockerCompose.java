package fr.polytech.rimel.rimeldocker.model.tracer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerCompose {

    private String version;
    private String srcCode;

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

    @Override
    public String toString() {
        return "DockerCompose{" +
                "version='" + version + '\'' +
                ", srcCode='" + srcCode + '\'' +
                '}';
    }
}
