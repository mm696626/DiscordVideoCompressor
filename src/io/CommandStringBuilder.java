package io;

import constants.FileSizeConstants;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandStringBuilder {

    public String buildCommandString (String videoFilePath, int startingResolutionIndex, int frameRateIndex, int aspectRatioIndex) throws IOException {
        String commandString = "";

        File videoFile = new File(videoFilePath);
        String videoFileName = getVideoFileName(videoFile);
        String compressedVideoFileName = videoFileName + "_compressed.mp4";

        double aspectRatio;
        if (aspectRatioIndex == 0) {
            String resolution = getVideoResolution(videoFilePath);
            String[] resolutionStats = resolution.split("x");
            aspectRatio = Double.parseDouble(resolutionStats[0])/Double.parseDouble(resolutionStats[1]);
        }
        else {
            aspectRatio = FileSizeConstants.ASPECT_RATIOS[aspectRatioIndex-1];
        }

        writeAspectRatioToFile(String.valueOf(aspectRatio));

        int width = (int) Math.ceil(FileSizeConstants.HEIGHTS[startingResolutionIndex] * aspectRatio);

        if (width % 2 != 0) {
            width++;
        }

        commandString = "ffmpeg.exe " + "-i " + "\"" + videoFilePath + "\"" + " -vf scale=" + width + ":" + FileSizeConstants.HEIGHTS[startingResolutionIndex] + " -c:v libx264 -preset fast -c:a aac " + getVideoWithFrameRate(compressedVideoFileName, frameRateIndex, videoFilePath) + " && move " + compressedVideoFileName + " ../output/" + compressedVideoFileName;

        deleteFrameRateFile();
        deleteResolutionFile();

        return commandString;
    }

    private void deleteFrameRateFile() {
        File frameRateFile = new File("frame_rate.txt");
        if (frameRateFile.exists()) {
            frameRateFile.delete();
        }
    }

    private void deleteResolutionFile() {
        File resolutionFile = new File("resolution.txt");
        if (resolutionFile.exists()) {
            resolutionFile.delete();
        }
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

    private String getVideoResolution(String videoFilePath) throws IOException {
        String getResolutionCommand = "ffmpeg.exe -i" + " \"" + videoFilePath + "\"" + " 2>&1 | findstr /R \"fps\" > resolution.txt";
        String[] commands = {"cmd.exe", "/c", "start", "cmd.exe", "/c", "cd tools && " + getResolutionCommand + " && move resolution.txt ../resolution.txt"};
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.start();

        while (!new File("resolution.txt").exists()) {
            //stall until file is created
        }

        try {
            return getResolutionFromTextFile();
        }
        catch (Exception e) {
            //this is a last resort if this somehow still throws an exception to use 480p
            return "640x480";
        }
    }

    private String getResolutionFromTextFile() {
        Scanner inputStream = null;
        String resolutionString = "";
        try {
            inputStream = new Scanner(new FileInputStream("resolution.txt"));
        } catch (FileNotFoundException e) {
            return "";
        }

        while (inputStream.hasNextLine()) {
            String line = inputStream.nextLine();
            String[] videoInformationLine = line.split(",");
            //check if it starts with Stream
            if (line.trim().startsWith("Stream")) {
                for (int i=1; i<videoInformationLine.length; i++) {

                    String regex = "\\b\\d+x\\d+\\b"; // Pattern to find resolution
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(videoInformationLine[i]);

                    if (matcher.find()) {
                        resolutionString = videoInformationLine[i].replaceAll("[^a-zA-Z0-9]", "");
                        resolutionString = resolutionString.trim();
                        inputStream.close();
                        return resolutionString;
                    }
                }
            }
        }

        inputStream.close();
        return "640x480"; //if the text file somehow doesn't have a fps value, then use 30
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

    private void writeAspectRatioToFile(String aspectRatio) {
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter("aspect_ratio.txt");
        }
        catch (FileNotFoundException f) {
            return;
        }

        outputStream.println(aspectRatio);
        outputStream.close();
    }
}