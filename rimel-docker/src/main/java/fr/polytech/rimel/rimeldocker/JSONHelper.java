package fr.polytech.rimel.rimeldocker;

import com.google.gson.*;
import fr.polytech.rimel.rimeldocker.model.RepoData;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class JSONHelper {


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
        printHeaders(writer);
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader("D:\\Data\\Projects\\RIMEL-Docker\\rimel-docker\\data.json"));
        JsonArray repoDataList = jsonElement.getAsJsonArray();

        for (int i = 0; i < repoDataList.size(); i++) {
            RepoData data = new RepoData();
            JsonObject jsonRepoData = repoDataList.get(i).getAsJsonObject();
            data.setName(jsonRepoData.get("repositoryName").getAsString());
            data.setAge(jsonRepoData.get("ageInDays").getAsInt());
            data.setLastCommitDate(jsonRepoData.get("lastCommitDate").getAsString());
            data.setNbCollaborators(jsonRepoData.get("numberOfCollaborators").getAsInt());
            data.setAverageTimeToUpdate(getAverageTimeToUpdate(jsonRepoData));
            data.setVarianceOverComposes(getVarianceOverComposes(jsonRepoData, data.getAverageTimeToUpdate()));
            writeRepoData(data, writer);
        }
        writer.close();
    }


    private static void printHeaders(PrintWriter writer){
        writer.println("name;age;lastCommitDate;nbCollaborators;averageTimeToUpdate;UpdateTimeVariance");
    }

    private static void writeRepoData(RepoData data, PrintWriter writer) {
        writer.println(
                data.getName() + ";" +
                data.getAge() + ";" +
                data.getLastCommitDate() + ";" +
                data.getNbCollaborators() + ";" +
                data.getAverageTimeToUpdate() + ";" +
                data.getVarianceOverComposes());
    }

    private static double getAverageTimeToUpdate(JsonObject jsonObject) {
        double sum = 0;
        double count = 0;
        JsonObject changeDelays = jsonObject.getAsJsonObject("changeDelaysByVersionByDockerFile");
        Iterator<String> composesIt = changeDelays.keySet().iterator();
        while(composesIt.hasNext()) {
            String compose = composesIt.next();
            JsonObject jsonCompose = changeDelays.getAsJsonObject(compose);
            Iterator<String> versions = jsonCompose.keySet().iterator();
            while(versions.hasNext()) {
                String version = versions.next();
                int time = jsonCompose.getAsJsonObject(version).get("delay").getAsInt();
                sum += time;
                count += 1;
            }
        }
        if(count == 0) return -1.0;
        double average = sum / count;
        System.out.println(average);
        return average;
    }

    private static double getVarianceOverComposes(JsonObject jsonObject, double average) {
        double sum = 0;
        double count = 0;
        JsonObject changeDelays = jsonObject.getAsJsonObject("changeDelaysByVersionByDockerFile");
        Iterator<String> composesIt = changeDelays.keySet().iterator();
        while(composesIt.hasNext()) {
            String compose = composesIt.next();
            JsonObject jsonCompose = changeDelays.getAsJsonObject(compose);
            Iterator<String> versions = jsonCompose.keySet().iterator();
            while(versions.hasNext()) {
                String version = versions.next();
                int time = jsonCompose.getAsJsonObject(version).get("delay").getAsInt();
                sum += Math.pow(time - average, 2);
                count += 1;
            }
        }
        if(count == 0) return -1.0;
        double variance = sum / count;
        return variance;
    }


}
