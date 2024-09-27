package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.IOException;

public class CommandStringBuilder {

    public String buildCommandString (String videoFilePath, int startingResolutionIndex, int frameRateIndex) throws IOException {
        String commandString = "";

        File videoFile = new File(videoFilePath);
        String videoFileName = getVideoFileName(videoFile);
        String compressedVideoFileName = videoFileName + "_compressed.mp4";
        commandString = "ffmpeg.exe " + "-i " + "\"" + videoFilePath + "\"" + " -vf scale=" + FileSizeConstants.WIDTHS[startingResolutionIndex] + ":" + FileSizeConstants.HEIGHTS[startingResolutionIndex] + " -c:v libx264 -preset fast -c:a aac " + getVideoWithFrameRate(compressedVideoFileName, frameRateIndex) + " && move " + compressedVideoFileName + " ../output/" + compressedVideoFileName;

        return commandString;
    }

    private String getVideoFileName(File videoFile) {
        String videoFileName = videoFile.getName();
        videoFileName = videoFileName.substring(0, videoFileName.lastIndexOf("."));
        videoFileName = videoFileName.replaceAll("[^a-zA-Z0-9]", "");
        return videoFileName;
    }

    private String getVideoWithFrameRate(String compressedVideoFileName, int frameRateIndex) {
        if (frameRateIndex == 0) {
            return compressedVideoFileName;
        }
        else {
            return "-r " + FileSizeConstants.FRAME_RATES[frameRateIndex-1] + " " + compressedVideoFileName;
        }
    }
}