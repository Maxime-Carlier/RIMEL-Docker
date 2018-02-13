package fr.polytech.rimel.rimeldocker.transforms;

import fr.polytech.rimel.rimeldocker.api.APIException;
import fr.polytech.rimel.rimeldocker.model.CommitHistory;
import fr.polytech.rimel.rimeldocker.model.Repository;
import fr.polytech.rimel.rimeldocker.model.tracer.UpdateTimeStamp;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DefaultCoder(AvroCoder.class)
public class CompareDCVersion extends DoFn<Repository, Repository> {

    private Map<String, Date> dcRelease;

    public CompareDCVersion(){
        dcRelease = new HashMap<>();
        // V1
        dcRelease.put("1", new GregorianCalendar(2013, Calendar.DECEMBER, 20).getTime());
        //V2
        dcRelease.put("2", new GregorianCalendar(2016, Calendar.JANUARY, 15).getTime());
        dcRelease.put("2.1", new GregorianCalendar(2016, Calendar.NOVEMBER, 16).getTime());
        dcRelease.put("2.2", new GregorianCalendar(2017, Calendar.MAY, 2).getTime());
        dcRelease.put("2.3", new GregorianCalendar(2017, Calendar.AUGUST, 31).getTime());
        // V3
        dcRelease.put("3", new GregorianCalendar(2017, Calendar.JANUARY, 18).getTime());
        dcRelease.put("3.1", new GregorianCalendar(2017, Calendar.FEBRUARY, 8).getTime());
        dcRelease.put("3.2", new GregorianCalendar(2017, Calendar.APRIL, 4).getTime());
        dcRelease.put("3.3", new GregorianCalendar(2017, Calendar.JUNE, 19).getTime());
        dcRelease.put("3.4", new GregorianCalendar(2017, Calendar.NOVEMBER, 1).getTime());
        dcRelease.put("3.5", new GregorianCalendar(2017, Calendar.DECEMBER, 18).getTime());
    }

    @ProcessElement
    public void processElement(ProcessContext context) throws APIException, IOException {
        Repository repository = context.element().clone();
        repository.setVersionEvolutionMap(sortDockerVersionToEarliestDate(repository));
        context.output(repository);
    }

    private Map<String, Map<String, UpdateTimeStamp>> sortDockerVersionToEarliestDate(Repository repository){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Map<String, Map<String, UpdateTimeStamp>> evolutionMap = new HashMap<>();

        for (String key : repository.getCommitHistoryMap().keySet()){

            Map<String, UpdateTimeStamp> versionEvolution = new HashMap<>();

            // group element by version
            Map<String, List<CommitHistory>> commitHistoryMap
                    = repository.getCommitHistoryMap().get(key)
                    .stream()
                    .collect(Collectors.groupingBy(
                            cm->cm.getDockerFile().getVersion()));


            // format commit date
            for (String version : commitHistoryMap.keySet()){
                List<Date> dates = new ArrayList<>();
                commitHistoryMap.get(version).forEach(
                        cm->{
                            try {
                                dates.add(formatter.parse(cm.getCommit().getCommitter().getDate().replaceAll("Z$", "+0000")));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        });
                // Retrieve oldest date
                UpdateTimeStamp uts  = new UpdateTimeStamp();
                uts.setDateUpdated( Collections.min(dates).toString());
                uts.setDelay(getDateDiff(Collections.min(dates), dcRelease.get(version), TimeUnit.DAYS));
                versionEvolution.put(version, uts);
            }
            evolutionMap.put(key, versionEvolution);
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
