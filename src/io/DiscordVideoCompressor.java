package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class DiscordVideoCompressor {

    public boolean compressVideo(String commandString, String videoFilePath, int targetFileSize, int retries, boolean backupVideo) throws IOException, InterruptedException {

        double aspectRatio = getAspectRatioFromFile();

        File toolsFolder = new File("tools");
        String toolsFolderPath = toolsFolder.getAbsolutePath();
        String outputFolderPath = toolsFolderPath.substring(0, toolsFolderPath.lastIndexOf("tools")) + "output";
        String backupFolderPath = toolsFolderPath.substring(0, toolsFolderPath.lastIndexOf("tools")) + "backup";

        createFolder(outputFolderPath);

        if (backupVideo) {
            createFolder(backupFolderPath);
        }

        String fileSeparator = toolsFolderPath.substring(0, toolsFolderPath.lastIndexOf("tools"));
        fileSeparator = fileSeparator.substring(fileSeparator.length() - 1);

        File videoFile = new File(videoFilePath);
        File backupVideoFile = new File(backupFolderPath + fileSeparator + videoFile.getName());
        File outputVideoFile = new File(outputFolderPath + fileSeparator + getVideoFileName(videoFile) + "_compressed.mp4");
        if (outputVideoFile.exists()) {
            outputVideoFile.delete();
        }

        long videoFileSizeInBytes = videoFile.length();

        if (!isVideoSmallEnoughForTargetFileSize(videoFileSizeInBytes, targetFileSize)) {
            if (!backupVideoFile.exists() && backupVideo) {
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

        if (!isVideoSmallEnoughForTargetFileSize(outputVideoFileSizeInBytes, targetFileSize) && retries+1 < FileSizeConstants.HEIGHTS.length) {
            int oldWidth = (int)Math.ceil(FileSizeConstants.HEIGHTS[retries] * aspectRatio);
            int oldHeight = FileSizeConstants.HEIGHTS[retries];

            retries++;

            int newWidth;
            int newHeight;

            newWidth = (int)Math.ceil(FileSizeConstants.HEIGHTS[retries] * aspectRatio);
            newHeight = FileSizeConstants.HEIGHTS[retries];

            if (newWidth % 2 != 0) {
                newWidth++;
            }

            String newCommandString = commandString;
            String partToReplace = "-vf scale=" + oldWidth + ":" + oldHeight;
            String replacement = "-vf scale=" + newWidth + ":" + newHeight;
            newCommandString = newCommandString.replaceAll(partToReplace, replacement);
            return compressVideo(newCommandString, videoFilePath, targetFileSize, retries, false);
        }
        else {
            return true;
        }
    }

    private double getAspectRatioFromFile() {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileInputStream("aspect_ratio.txt"));
        } catch (FileNotFoundException e) {
            return 4.0/3.0;
        }

        while (inputStream.hasNextLine()) {
            String line = inputStream.nextLine();
            inputStream.close();
            return Double.parseDouble(line);
        }

        inputStream.close();
        return 4.0/3.0; //if the text file somehow doesn't have an aspect ratio, then use 4:3
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