package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.model.tracer.DockerCompose;
import fr.polytech.rimel.rimeldocker.model.tracer.UpdateTimeStamp;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CompareDCVersion {

    private Map<String, Date> dcRelease;

    public CompareDCVersion(){
        dcRelease = new HashMap<>();
        // V1
        dcRelease.put("1", new GregorianCalendar(2013, Calendar.DECEMBER, 20).getTime());
        //V2
        dcRelease.put("2", new GregorianCalendar(2016, Calendar.JANUARY, 15).getTime());
        dcRelease.put("2.0", new GregorianCalendar(2016, Calendar.JANUARY, 15).getTime());
        dcRelease.put("2.1", new GregorianCalendar(2016, Calendar.NOVEMBER, 16).getTime());
        dcRelease.put("2.2", new GregorianCalendar(2017, Calendar.MAY, 2).getTime());
        dcRelease.put("2.3", new GregorianCalendar(2017, Calendar.AUGUST, 31).getTime());
        // V3
        dcRelease.put("3", new GregorianCalendar(2017, Calendar.JANUARY, 18).getTime());
        dcRelease.put("3.0", new GregorianCalendar(2017, Calendar.JANUARY, 18).getTime());
        dcRelease.put("3.1", new GregorianCalendar(2017, Calendar.FEBRUARY, 8).getTime());
        dcRelease.put("3.2", new GregorianCalendar(2017, Calendar.APRIL, 4).getTime());
        dcRelease.put("3.3", new GregorianCalendar(2017, Calendar.JUNE, 19).getTime());
        dcRelease.put("3.4", new GregorianCalendar(2017, Calendar.NOVEMBER, 1).getTime());
        dcRelease.put("3.5", new GregorianCalendar(2017, Calendar.DECEMBER, 18).getTime());
    }

    public Repository processElement(Repository repository) throws APIException, IOException {
        repository.setVersionEvolutionMap(sortDockerVersionToEarliestDate(repository));
        return repository;
    }

    private Map<String, Map<String,UpdateTimeStamp>> sortDockerVersionToEarliestDate(Repository repository){
        Map<String , List<DockerCompose>> dockerComposesMap = repository.getDockerComposes();
        Map<String, Map<String,UpdateTimeStamp>> evolutionMap = new HashMap<>();

        for (String key1 : dockerComposesMap.keySet()){
            List<DockerCompose> dockerComposes = dockerComposesMap.get(key1);
            Map<String, Date> versionUpdate = new HashMap<>();
            Map<String, UpdateTimeStamp> evolution = new HashMap<>();
            for (DockerCompose dc : dockerComposes){
                if (versionUpdate.containsKey(dc.getVersion())) {
                    Date tmp = versionUpdate.get(dc.getVersion());
                    versionUpdate.put(dc.getVersion(),
                            tmp.getTime() > dc.getCommitDate().getTime() ?
                                    dc.getCommitDate() : tmp);
                } else {
                    versionUpdate.put(dc.getVersion(), dc.getCommitDate());
                }
            }

            for (String key2: versionUpdate.keySet()){
                if (versionUpdate.get(key2) != null) {
                    UpdateTimeStamp uts = new UpdateTimeStamp();
                    uts.setDateUpdated(versionUpdate.get(key2).toString());
                    uts.setDelay(getDateDiff(versionUpdate.get(key2), dcRelease.get(key2), TimeUnit.DAYS));
                    evolution.put(key2, uts);
                }
            }
            evolutionMap.put(key1, evolution);
        }

        return evolutionMap;
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
