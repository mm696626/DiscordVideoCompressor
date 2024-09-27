package io;

import constants.FileSizeConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class CommandStringBuilder {

    public String buildCommandString (String videoFilePath, int startingResolutionIndex, int frameRateIndex) throws IOException {
        String commandString = "";

        File videoFile = new File(videoFilePath);
        String videoFileName = getVideoFileName(videoFile);
        String compressedVideoFileName = videoFileName + "_compressed.mp4";
        commandString = "ffmpeg.exe " + "-i " + "\"" + videoFilePath + "\"" + " -vf scale=" + FileSizeConstants.WIDTHS[startingResolutionIndex] + ":" + FileSizeConstants.HEIGHTS[startingResolutionIndex] + " -c:v libx264 -preset fast -c:a aac " + getVideoWithFrameRate(compressedVideoFileName, frameRateIndex, videoFilePath) + " && move " + compressedVideoFileName + " ../output/" + compressedVideoFileName;

        return commandString;
    }

    private String getVideoFileName(File videoFile) {
        String videoFileName = videoFile.getName();
        videoFileName = videoFileName.substring(0, videoFileName.lastIndexOf("."));
        videoFileName = videoFileName.replaceAll("[^a-zA-Z0-9]", "");
        return videoFileName;
    }

    private String getVideoWithFrameRate(String compressedVideoFileName, int frameRateIndex, String videoFilePath) throws IOException {
        if (frameRateIndex == 0) {
            return compressedVideoFileName;
        }
        else if (FileSizeConstants.FRAME_RATES[frameRateIndex-1] > getVideoFrameRate(videoFilePath)) {
            return compressedVideoFileName;
        }
        else {
            return "-r " + FileSizeConstants.FRAME_RATES[frameRateIndex-1] + " " + compressedVideoFileName;
        }
    }

    private double getVideoFrameRate(String videoFilePath) throws IOException {
        String ffmpegGetFrameRateCommand = "ffmpeg.exe -i" + " \"" + videoFilePath + "\"" + " 2>&1 | findstr /R \"fps\" > frame_rate.txt";
        String[] commands = {"cmd.exe", "/c", "start", "cmd.exe", "/c", "cd tools && " + ffmpegGetFrameRateCommand + " && move frame_rate.txt ../frame_rate.txt"};
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.start();

        while (!new File("frame_rate.txt").exists()) {
            //stall until file is created
        }

        try {
            return Double.parseDouble(getFrameRateFromTextFile());
        }
        catch (Exception e) {
            //this is a last resort if this somehow still throws an exception to use 30fps
            return 30;
        }
    }

    private String getFrameRateFromTextFile() {
        Scanner inputStream = null;
        String frameRateString = "";
        try {
            inputStream = new Scanner(new FileInputStream("frame_rate.txt"));
        } catch (FileNotFoundException e) {
            return "";
        }

        while (inputStream.hasNextLine()) {
            String line = inputStream.nextLine();
            String[] videoInformationLine = line.split(",");
            //check if it starts with Stream to address edge case of if the video file name contains "fps"
            if (line.trim().startsWith("Stream")) {
                for (int i=0; i<videoInformationLine.length; i++) {
                    if (videoInformationLine[i].toLowerCase().contains("fps")) {
                        frameRateString = videoInformationLine[i].replaceAll("[^Z0-9.]", "");
                        frameRateString = frameRateString.trim();
                        inputStream.close();
                        return frameRateString;
                    }
                }
            }
        }

        inputStream.close();
        return "30"; //if the text file somehow doesn't have a fps value, then use 30
    }
}