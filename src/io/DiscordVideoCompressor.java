package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DiscordVideoCompressor {

    public boolean compressVideo(String commandString, String videoFilePath, int targetFileSize, int retries) throws IOException, InterruptedException {

        File toolsFolder = new File("tools");
        String toolsFolderPath = toolsFolder.getAbsolutePath();
        String outputFolderPath = toolsFolderPath.substring(0, toolsFolderPath.lastIndexOf("tools")) + "output";
        String backupFolderPath = toolsFolderPath.substring(0, toolsFolderPath.lastIndexOf("tools")) + "backup";

        createFolder(outputFolderPath);
        createFolder(backupFolderPath);

        String fileSeparator = toolsFolderPath.substring(0, toolsFolderPath.lastIndexOf("tools"));
        fileSeparator = fileSeparator.substring(fileSeparator.length() - 1);

        File videoFile = new File(videoFilePath);
        File backupVideoFile = new File(backupFolderPath + fileSeparator + videoFile.getName());
        File outputVideoFile = new File(outputFolderPath + fileSeparator + getVideoFileName(videoFile) + "_c.mp4");
        if (outputVideoFile.exists()) {
            outputVideoFile.delete();
        }

        long videoFileSizeInBytes = videoFile.length();

        if (!isVideoSmallEnoughForTargetFileSize(videoFileSizeInBytes, targetFileSize)) {
            if (!backupVideoFile.exists()) {
                Files.copy(videoFile.toPath(), backupVideoFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
            String[] commands = {"cmd.exe", "/c", "start", "cmd.exe", "/c", "cd tools && " + commandString};
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.start();
        }
        else {
            return false;
        }

        while (!outputVideoFile.exists()) {
            //stall
        }


        long outputVideoFileSizeInBytes = outputVideoFile.length();

        if (!isVideoSmallEnoughForTargetFileSize(outputVideoFileSizeInBytes, targetFileSize) && retries+1 < FileSizeConstants.WIDTHS.length) {
            int oldWidth = FileSizeConstants.WIDTHS[retries];
            int oldHeight = FileSizeConstants.HEIGHTS[retries];

            retries++;

            int newWidth;
            int newHeight;

            newWidth = FileSizeConstants.WIDTHS[retries];
            newHeight = FileSizeConstants.HEIGHTS[retries];

            String newCommandString = commandString;
            String partToReplace = "-vf scale=" + oldWidth + ":" + oldHeight;
            String replacement = "-vf scale=" + newWidth + ":" + newHeight;
            newCommandString = newCommandString.replaceAll(partToReplace, replacement);
            return compressVideo(newCommandString, videoFilePath, targetFileSize, retries);
        }
        else {
            return true;
        }
    }

    private void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            folder.delete();
        }
        folder.mkdirs();
    }

    private boolean isVideoSmallEnoughForTargetFileSize(long videoFileSizeInBytes, int targetFileSize) {
        double videoFileSizeInMegabytes = (double)videoFileSizeInBytes/FileSizeConstants.BYTES_PER_MB;
        return videoFileSizeInMegabytes <= (double)targetFileSize;
    }

    private String getVideoFileName(File videoFile) {
        String videoFileName = videoFile.getName();
        videoFileName = videoFileName.substring(0, videoFileName.lastIndexOf("."));
        videoFileName = videoFileName.replaceAll("[^a-zA-Z0-9]", "");
        return videoFileName;
    }
}