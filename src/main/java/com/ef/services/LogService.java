package com.ef.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ef.Parser;
import com.ef.models.BlockedIP;
import com.ef.models.Log;
import com.ef.util.HibernateUtil;
import com.ef.util.WalletHubUtil;

public class LogService {

    public LogService() { }

    private Log createLog(String date, String ip, String request, String status, String userAgent){
        LocalDateTime dateTime = LocalDateTime.parse(date.substring(0,23), WalletHubUtil.getLogFormatter());
        return new Log(dateTime, ip, request, status, userAgent);

    }

    public List<Log> readLogFile(String filePath){

        List<Log> logList = new ArrayList<>();

        System.out.println("Reading file...");

        try{
            InputStream in = Parser.class.getResourceAsStream(filePath);
            if ( in == null )
                throw new Exception("resource not found: " + filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\|");
                Log log = createLog(values[0], values[1], values[2], values[3], values[4]);
                logList.add(log);
            }
            br.close();
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return logList;
    }

    public void saveAll(List<Log> list) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            System.out.println("Importing file...");

            list.stream().forEach(log -> {session.save(log);});

            transaction.commit();
            System.out.println("Finished importing file");

        } catch (HibernateException ex) {
            assert transaction != null;
            transaction.rollback();
            ex.printStackTrace();
        }
    }

    private void saveBlockedIP(BlockedIP blockedIP) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(blockedIP);
            transaction.commit();
        } catch (HibernateException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
    }

    public String removeDurationPrefix(String duration) {
        return duration.replace(WalletHubUtil.getDurationParam(), "");
    }

    public LocalDateTime removeStartDatePrefix(String startDate) {

        if (startDate != null){
            String dateAsString = startDate.replace(WalletHubUtil.getStartDateParam(), "").replace(".", " ");
            return LocalDateTime.parse(dateAsString, WalletHubUtil.getInputFormatter());
        } else {
            System.out.println("Please check the startDate");
            return null;
        }
    }

    public String removeFilePathPrefix(String arg) {
        return arg.replace(WalletHubUtil.getAccessLogParam(), "");
    }

    public int removeThresholdPrefix(String arg) {
        return Integer.parseInt(arg.replace(WalletHubUtil.getThresholdParam(), ""));
    }

    public void findIPs(LocalDateTime startDate, String durationInput, int threshold, List<Log> logList) {

        System.out.println("Searching for IPs...");
        LocalDateTime endDate = getEndDateByStartDate(startDate, durationInput);
        List<String> ipList = new ArrayList<>();
        logList.stream().filter(log -> log.getDate().isAfter(startDate) && log.getDate().isBefore(endDate)).forEach(log -> {
            ipList.add(log.getIp());
        });

        saveBlockedIPs(ipList, threshold);
    }

    private void saveBlockedIPs(List<String> ipList, int threshold) {
        //Create Map with IP, # of requests
        Map<String, Long> requestsCountResult = toMap(ipList);
        //Display results over threshold
        requestsCountResult.forEach((k,v)->{
            if (v > threshold){
                System.out.println("IP: " + k);
                String comment = String.format("Blocked due to %o requests", v);
                saveBlockedIP(new BlockedIP(k, comment));
            }
        });
    }

    private Map<String, Long> toMap(List<String> lst){
        return lst.stream().collect(Collectors.groupingBy(s -> s,
            Collectors.counting()));
    }

    private LocalDateTime getEndDateByStartDate(LocalDateTime startDate, String durationInput) {
        if (durationInput.equalsIgnoreCase(WalletHubUtil.getHourly())) {
            return startDate.plusHours(1);
        }
        return startDate.plusHours(24);
    }
}
