package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.IOException;

public class CommandStringBuilder {

    public String buildCommandString (String videoFilePath) throws IOException {
        String commandString = "";

        File videoFile = new File(videoFilePath);
        String videoFileName = getVideoFileName(videoFile);
        String compressedVideoFileName = videoFileName + "_c.mp4";
        commandString = "ffmpeg.exe " + "-i " + "\"" + videoFilePath + "\"" + " -vf scale=" + FileSizeConstants.WIDTHS[0] + ":" + FileSizeConstants.HEIGHTS[0] + " -c:v libx264 -preset fast -c:a aac " + compressedVideoFileName + " && move " + compressedVideoFileName + " ../output/" + compressedVideoFileName;

        return commandString;
    }

    private String getVideoFileName(File videoFile) {
        String videoFileName = videoFile.getName();
        videoFileName = videoFileName.substring(0, videoFileName.lastIndexOf("."));
        videoFileName = videoFileName.replaceAll("[^a-zA-Z0-9]", "");
        return videoFileName;
    }
}