package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.IOException;

public class CommandStringBuilder {

    public String buildCommandString (String videoFilePath) throws IOException {
        String commandString = "";


        File videoFile = new File(videoFilePath);
        String videoFileName = getVideoFileName(videoFile);
        commandString = "ffmpeg.exe " + "-i " + "\"" + videoFilePath + "\"" + " -vf scale=" + FileSizeConstants.INITIAL_WIDTH + ":" + FileSizeConstants.INITIAL_HEIGHT + " -c:v libx264 -preset fast -c:a aac " + "\"" + "output.mp4" + "\"";

        return commandString;
    }

    private String getVideoFileName(File videoFile) {
        String videoFileName = videoFile.getName();
        videoFileName = videoFileName.substring(0, videoFileName.indexOf("."));
        videoFileName = videoFileName.replaceAll("[^a-zA-Z0-9]", "");
        return videoFileName;
    }
}