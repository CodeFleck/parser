package com.ef.services;

import static java.time.OffsetDateTime.now;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ef.Parser;
import com.ef.models.BlockedIP;
import com.ef.models.Log;
import com.ef.util.HibernateUtil;

public class LogService {

    public LogService() { }

    DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Log createLog(String date, String ip, String request, String status, String userAgent){

        LocalDateTime dateTime = LocalDateTime.parse(date.substring(0,23), logFormatter);

        Log log = new Log();
        log.setDate(dateTime);
        log.setIp(ip);
        log.setRequest(request);
        log.setStatus(status);
        log.setUserAgent(userAgent);

        return log;
    }

    public List<Log> readLogFile(String filePath){

        List<Log> logList = new ArrayList<>();

        System.out.println("Reading file...");

        try{
            String respath = filePath;
            InputStream in = Parser.class.getResourceAsStream(respath);
            if ( in == null )
                throw new Exception("resource not found: " + respath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            int cont = 0;
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

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            System.out.println("Importing file...");

            list.stream().forEach(log -> {
                session.save(log);
            });

            transaction.commit();
            System.out.println("Finished importing file");

        } catch (Exception ex) {
            transaction.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void saveBlockedIP(BlockedIP blockedIP) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Log> result = new ArrayList<>();

        try {
            transaction = session.beginTransaction();
            session.save(blockedIP);
            transaction.commit();

        } catch (Exception ex) {
            transaction.rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    public String validateDuration(String duration) {
        return duration.replace("--duration=", "");
    }

    public LocalDateTime validateStartDate(String startDate) {

        if (startDate != null){
            String dateAsString = startDate.replace("--startDate=", "").replace(".", " ");
            LocalDateTime localDateTime = LocalDateTime.parse(dateAsString, inputFormatter);
            return localDateTime;
        } else {
            System.out.println("Please check the startDate");
            return null;
        }
    }

    public String validateFilePath(String arg) {
        return arg.replace("--accesslog=", "");
    }

    public int validateThreshold(String arg) {
        return Integer.parseInt(arg.replace("--threshold=", ""));
    }

    public Map<String, Long> findIPs(LocalDateTime startDate, String durationInput, int threshold, List<Log> logList) {

        System.out.println("Searching for IPs...");

        List<String> ipList = new ArrayList<>();

        LocalDateTime endDate;

        if (durationInput.equalsIgnoreCase("hourly")) {
            endDate = startDate.plusHours(1);
        } else {
            endDate = startDate.plusHours(24);
        }

        List<Log> logsWithinTimePeriod = logList.stream().filter(log -> log.getDate().isAfter(startDate) && log.getDate().isBefore(endDate)).collect(Collectors.toList());

        for (Log log : logsWithinTimePeriod) {
            ipList.add(log.getIp());
        }

        //Create Map with IP, # of requests
        Map<String, Long> requestsCountResult = toMap(ipList);
        //Display results over threshold
        requestsCountResult.forEach((k,v)->{
            if (v > threshold){
                System.out.println("IP: " + k + " Number of request: " + v);
                String comment = String.format("Blocked due to %o requests", v);
                BlockedIP blockedIP = new BlockedIP(k, comment);
                saveBlockedIP(blockedIP);
            }
        });

        return requestsCountResult;
    }


    public Map<String, Long> toMap(List<String> lst){
        return lst.stream().collect(Collectors.groupingBy(s -> s,
            Collectors.counting()));
    }
}
