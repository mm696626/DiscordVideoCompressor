package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DiscordVideoCompressor {

    public boolean compressVideo(String commandString, String videoFilePath, int targetFileSize) throws IOException, InterruptedException {

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

        long videoFileSizeInBytes = videoFile.length();

        if (!isVideoSmallEnoughForTargetFileSize(videoFileSizeInBytes, targetFileSize)) {
            Files.copy(videoFile.toPath(), backupVideoFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            String[] commands = {"cmd.exe", "/c", "start", "cmd.exe", "/k", "cd tools && " + commandString};
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.start();
            return true;
        }
        else {
            return false;
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
}