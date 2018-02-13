package fr.polytech.rimel.rimeldocker.model.tracer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;


@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {
    @JsonProperty("filename")
    private String fileName;
    @JsonProperty("status")
    private String status;
    @JsonProperty("additions")
    private String additions;
    @JsonProperty("deletions")
    private String deletions;
    @JsonProperty("changes")
    private String changes;
    @JsonProperty("raw_url")
    private String rawUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdditions() {
        return additions;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    public String getDeletions() {
        return deletions;
    }

    public void setDeletions(String deletions) {
        this.deletions = deletions;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    @Override
    public String toString() {
        return "File{" +
                "fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                ", additions='" + additions + '\'' +
                ", deletions='" + deletions + '\'' +
                ", changes='" + changes + '\'' +
                ", rawUrl='" + rawUrl + '\'' +
                '}';
    }
}
