package com.ef;

import java.time.LocalDateTime;
import java.util.List;

import com.ef.models.Log;
import com.ef.services.LogService;

public class Parser {

    public static void main(String[] args) throws Exception {

        final LogService logService = new LogService();

        final String FILEPATH = "/access.log";

        String duration = null;
        int threshold = 0;
        String filePathInput = null;
        LocalDateTime startDate = LocalDateTime.now();

        if (args.length > 0) {
            for (String arg : args) {
                if (arg != null) {
                    if (arg.startsWith("--startDate")) {
                        startDate = logService.validateStartDate(arg);
                    } else if (arg.startsWith("--duration")) {
                        duration = logService.validateDuration(arg);
                        if (duration.equalsIgnoreCase("hourly") || duration.equalsIgnoreCase("daily")){
                            continue;
                        } else {
                            System.out.println("Duration must be hourly or daily");
                            System.exit(1);
                        }
                    } else if (arg.startsWith("--threshold")) {
                        try {
                            threshold = logService.validateThreshold(arg);
                        } catch (NumberFormatException e) {
                            System.err.println("Threshold must be an integer.");
                            System.exit(1);
                        }
                    } else if (arg.startsWith("--accesslog")) {
                        filePathInput = logService.validateFilePath(arg);
                    } else {
                        System.out.println("Please follow the following example as input: --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
                    }
                }
            }
            if (filePathInput == null) {
                filePathInput = FILEPATH;
            }
        }

        List<Log> logList = logService.readLogFile(filePathInput);

        logService.saveAll(logList);

        logService.findIPs(startDate, duration, threshold, logList);

    }
}


