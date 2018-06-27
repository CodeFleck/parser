package com.ef;

import java.time.LocalDateTime;
import java.util.List;

import com.ef.models.Log;
import com.ef.services.LogService;
import com.ef.util.WalletHubUtil;

public class Parser {

    public static void main(String[] args) {

        final LogService logService = new LogService();

        String duration = null;
        int threshold = 0;
        String filePathInput = null;
        LocalDateTime startDate = LocalDateTime.now();

        if (args.length > 0) {
            for (String arg : args) {
                if (arg != null) {
                    if (arg.startsWith(WalletHubUtil.getStartDateParam())) {
                        startDate = logService.removeStartDatePrefix(arg);
                    } else if (arg.startsWith(WalletHubUtil.getDurationParam())) {
                        duration = logService.removeDurationPrefix(arg);
                        if (duration.equalsIgnoreCase(WalletHubUtil.getHourly()) || duration.equalsIgnoreCase(WalletHubUtil.getDaily())){
                            continue;
                        } else {
                            System.out.println("Duration must be hourly or daily");
                            System.exit(1);
                        }
                    } else if (arg.startsWith(WalletHubUtil.getThresholdParam())) {
                        try {
                            threshold = logService.removeThresholdPrefix(arg);
                        } catch (NumberFormatException e) {
                            System.err.println("Threshold must be an integer.");
                            System.exit(1);
                        }
                    } else if (arg.startsWith(WalletHubUtil.getAccessLogParam())) {
                        filePathInput = logService.removeFilePathPrefix(arg);
                    }
                }
            }

            boolean isParamsPopulated = validateParams(duration, threshold);

            if (filePathInput == null) {
                filePathInput = WalletHubUtil.getFilePath();
            }

            List<Log> logList = logService.readLogFile(filePathInput);
            logService.saveAll(logList);
            if (isParamsPopulated){
                logService.findIPs(startDate, duration, threshold, logList);
            } else {
                System.exit(1);
            }
        } else {
            System.out.println("Please follow the following example as input params: --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
            System.exit(1);
        }

        System.out.println("Task finished.");
    }

    private static boolean validateParams(String duration, int threshold) {

        boolean isParamsPopulated = true;

        if (threshold <= 0){
            System.out.println("Please insert " + WalletHubUtil.getThresholdParam());
            return isParamsPopulated = false;
        }

        if (duration == null){
            System.out.println("Please insert " + WalletHubUtil.getDurationParam());
            return isParamsPopulated = false;
        }

        return isParamsPopulated;
    }
}


