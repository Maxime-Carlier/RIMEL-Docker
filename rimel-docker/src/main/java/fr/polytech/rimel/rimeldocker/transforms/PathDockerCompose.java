package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.api.GithubAPI;
import fr.polytech.rimel.rimeldocker.model.Repository;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;

import java.util.concurrent.TimeoutException;
import java.io.*;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.text.ParseException;
import com.google.gson.Gson;
import org.json.simple.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * For a given String input, search the dockercompose and retrun the path
 */
public final class PathDockerCompose extends DoFn<Repository, Repository> {




    public static boolean isDockerCompose(String s){
        String[] tokens = s.split("/");
        for (int j = 0; j < tokens.length; j++){
            if(tokens[j].equals("docker-compose.yml")){
                return  true;
            }
        }
        return false;
    }


    public static String alldockercompose(String ch){
        ArrayList<String> dockercomposelist = new ArrayList<String>();
        String url = "https://api.github.com/repos/"+ch+"/git/trees/master?recursive=1&access_token=e8d1d643d6653bcf728986aa972f2f2e974b1445";
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jo = jelement.getAsJsonObject();
            if(jo.get("tree")!= null) {
                String tree = jo.get("tree").toString();
                JsonElement element = new JsonParser().parse(tree);
                JsonArray jarr = element.getAsJsonArray();
                for (int i = 0; i < jarr.size(); i++) {
                    JsonObject joo = (JsonObject) jarr.get(i);
                    String fullName = joo.get("path").toString();
                    fullName = fullName.substring(1, fullName.length() - 1);
                    if (isDockerCompose(fullName)) {
                        //dockercomposelist.add("https://github.com/"+ch+"/"+fullName);
                        //System.out.println(fullName);
                        return ("https://raw.githubusercontent.com/"+ch+"/master/"+fullName);
                    }


                }
            }

        }  catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
        return  null;

    }


    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {
        Repository repository = context.element();
        //ArrayList<String> PathListDockerCompose = alldockercompose(repository.getName());
        String p = alldockercompose(repository.getName());

        Repository result = repository.clone();
        if(p != null) {
            result.setPath(p);
            result.setHasDockerCompose(true);
        }
        //System.out.println(result.toString());

        context.output(repository);
    }
}
