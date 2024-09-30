package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class VideoTrimmerUI extends JFrame implements ActionListener {

    private JButton previewVideo, saveTrimSettings;
    private JLabel startTimeSliderLabel, endTimeSliderLabel, startTimeSliderValueLabel, endTimeSliderValueLabel;
    private JSlider startTimeSlider, endTimeSlider;
    private String videoFilePath;

    private int videoDuration;

    private boolean invalidParameters = true;

    GridBagConstraints gridBagConstraints;


    public VideoTrimmerUI(String videoFilePath) throws IOException {
        this.videoFilePath = videoFilePath;
        videoDuration = getVideoDuration();

        setTitle("Video Trimmer");
        generateUI();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.exit(0);
        }
    }

    private void generateUI() {
        startTimeSliderLabel = new JLabel("Starting Time in Video (in seconds)");
        startTimeSlider = new JSlider(0, videoDuration, 0);
        startTimeSlider.setPaintTicks(true);
        startTimeSlider.setSnapToTicks(true);
        startTimeSlider.setMajorTickSpacing(1);
        startTimeSlider.setMinorTickSpacing(0);

        endTimeSliderLabel = new JLabel("Ending Time in Video (in seconds)");
        endTimeSlider = new JSlider(0, videoDuration, videoDuration);
        endTimeSlider.setPaintTicks(true);
        endTimeSlider.setSnapToTicks(true);
        endTimeSlider.setMajorTickSpacing(1);
        endTimeSlider.setMinorTickSpacing(0);

        startTimeSliderValueLabel = new JLabel("0");
        endTimeSliderValueLabel = new JLabel(String.valueOf(videoDuration));

        startTimeSlider.addChangeListener(e -> setStartText());
        endTimeSlider.addChangeListener(e -> setEndText());

        previewVideo = new JButton("Preview Video");
        previewVideo.addActionListener(this);

        saveTrimSettings = new JButton("Save Trim Settings");
        saveTrimSettings.addActionListener(this);

        setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();

        addComponent(startTimeSliderLabel, 0, 0);
        addComponent(startTimeSlider, 1, 0);
        addComponent(startTimeSliderValueLabel, 2, 0);

        addComponent(endTimeSliderLabel, 0, 1);
        addComponent(endTimeSlider, 1, 1);
        addComponent(endTimeSliderValueLabel, 2, 1);

        addComponent(previewVideo, 0, 2);
        addComponent(saveTrimSettings, 1, 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previewVideo) {

            int startingTime = startTimeSlider.getValue();
            int endingTime = endTimeSlider.getValue();

            int durationOfPlayback = endingTime - startingTime;
            if (durationOfPlayback <= 0) {
                invalidParameters = true;
                JOptionPane.showMessageDialog(this, "Invalid Preview Parameters. Please try again!");
                return;
            }

            invalidParameters = false;
            String previewVideoCommand = "ffplay.exe -fs -ss " + startingTime + " -t " + durationOfPlayback + " \"" + videoFilePath + "\"";
            String[] commands = {"cmd.exe", "/c", "start", "cmd.exe", "/c", "cd tools && " + previewVideoCommand};
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            try {
                processBuilder.start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (e.getSource() == saveTrimSettings) {

            if (invalidParameters) {
                JOptionPane.showMessageDialog(this, "Invalid Preview Parameters. Please try again!");
                return;
            }

            PrintWriter outputStream = null;

            try {
                outputStream = new PrintWriter( new FileOutputStream("trim_settings.txt"));
            }
            catch (FileNotFoundException f) {
                System.out.println("File does not exist");
                return;
            }

            outputStream.println(startTimeSlider.getValue());
            outputStream.println(endTimeSlider.getValue());
            outputStream.close();

            setVisible(false);
        }
    }

    private void addComponent(JComponent component, int gridX, int gridY) {
        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;
        add(component, gridBagConstraints);
    }

    private int getVideoDuration() throws IOException {
        String getDurationCommand = "ffprobe.exe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1" + " \"" + videoFilePath + "\"" + " > duration.txt";
        String[] commands = {"cmd.exe", "/c", "start", "cmd.exe", "/c", "cd tools && " + getDurationCommand + " && move duration.txt ../duration.txt"};
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.start();

        while (!new File("duration.txt").exists()) {
            //stall until file is created
        }

        try {
            return getDurationFromTextFile();
        }
        catch (Exception e) {
            //this is a last resort if this somehow still throws an exception
            return 0;
        }
    }

    private int getDurationFromTextFile() {
        Scanner inputStream = null;
        String durationString;

        try {
            inputStream = new Scanner(new FileInputStream("duration.txt"));
        } catch (FileNotFoundException e) {
            return 0;
        }

        durationString = inputStream.nextLine();
        inputStream.close();
        double duration = Math.ceil(Double.parseDouble(durationString));
        return (int)duration;
    }

    private void setStartText() {
        startTimeSliderValueLabel.setText(String.valueOf(startTimeSlider.getValue()));
        if (endTimeSlider.getValue() - startTimeSlider.getValue() <= 0) {
            invalidParameters = true;
        }
        else {
            invalidParameters = false;
        }
    }

    private void setEndText() {
        endTimeSliderValueLabel.setText(String.valueOf(endTimeSlider.getValue()));
        if (endTimeSlider.getValue() - startTimeSlider.getValue() <= 0) {
            invalidParameters = true;
        }
        else {
            invalidParameters = false;
        }
    }
}