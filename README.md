# RIMEL-Docker

### Informations

This is a Java based project which serve as support for a Docker study.
The aim of this study, is to try to correlate users actions regarding the deployment of
new Docker Compose update with metric regarding a project size.

### How to run the project

```bash
cd rimel-docker
mvn clean package assembly:single
cd target
java -jar rimel-docker-jar-with-dependencies.jar <YOUR GITHUB OAUTH TOKEN> <YOUR SECOND GITHUB OAUTH TOKEN>...
```

This will launch the mining process and produce a data.json file in the current working directory containing the raw data.
Be warned that the total execution time took several hours to complete.

If you want to check this for a proof of work, you should clone this repo  and uncomment this part right there.
Main.java : ligne 45

```java
List<Repository> inputRepositories = new ArrayList<>();
        for (GHRepository ghRepository : ghRepositories) {
            Repository repository = new Repository();
            repository.setGhRepository(ghRepository);
            inputRepositories.add(repository);
            // Pour debuguer qu'une petite partie des résultats plutôt que tout
            /*if (inputRepositories.size() > 9) {
                break;
            }*/
        }
```

### Raw result

A JSON file containing the raw result of run is available in the `rawResutls/` folder