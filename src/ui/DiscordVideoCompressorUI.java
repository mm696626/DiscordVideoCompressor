package ui;

import constants.FileSizeConstants;
import io.CommandStringBuilder;
import io.DiscordVideoCompressor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class DiscordVideoCompressorUI extends JFrame implements ActionListener {

    private JButton videoFilePicker, compressVideo;
    private JLabel targetFileSizeLabel, videoFileNameLabel, targetFileSizeValueLabel, startingResolutionsLabel, outputFrameRateLabel, aspectRatioLabel, backupVideoLabel;
    private JSlider targetFileSizeSlider;
    private JCheckBox backUpVideo;
    private JTextField videoFileTextField;
    private String videoFilePath;

    private JComboBox startingResolutions, outputFrameRates, aspectRatios;

    GridBagConstraints gridBagConstraints;


    public DiscordVideoCompressorUI()
    {
        setTitle("Discord Video Compressor");
        generateUI();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.exit(0);
        }
    }

    private void generateUI() {
        videoFileNameLabel = new JLabel("Input Video File");
        videoFileTextField = new JTextField();
        videoFileTextField.setColumns(10);
        videoFileTextField.setEditable(false);
        videoFilePicker = new JButton("Browse for Video File");
        videoFilePicker.addActionListener(this);

        targetFileSizeLabel = new JLabel("Target File Size");
        targetFileSizeSlider = new JSlider(0, 4, 1);
        targetFileSizeSlider.setPaintTicks(true);
        targetFileSizeSlider.setSnapToTicks(true);
        targetFileSizeSlider.setMajorTickSpacing(1);
        targetFileSizeSlider.setMinorTickSpacing(0);

        targetFileSizeValueLabel = new JLabel("10 MB");


        startingResolutionsLabel = new JLabel("Starting Resolution to Compress To (Width will be adjusted to the aspect ratio chosen)");
        startingResolutions = new JComboBox<>(getStartingResolutions());
        startingResolutions.setSelectedIndex(2);

        outputFrameRateLabel = new JLabel("Output Video Frame Rate");
        outputFrameRates = new JComboBox<>(getOutputFrameRates());

        aspectRatioLabel = new JLabel("Output Video Aspect Ratio");
        aspectRatios = new JComboBox<>(getAspectRatios());

        backupVideoLabel = new JLabel("Backup Original Video");
        backUpVideo = new JCheckBox();
        backUpVideo.setSelected(true);

        compressVideo = new JButton("Compress Video");
        compressVideo.addActionListener(this);

        targetFileSizeSlider.addChangeListener(e -> targetFileSizeValueLabel.setText(FileSizeConstants.FILE_SIZES_DISPLAY[targetFileSizeSlider.getValue()]));

        setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();

        addComponent(videoFileNameLabel, 0, 0);
        addComponent(videoFileTextField, 1, 0);
        addComponent(videoFilePicker, 2, 0);

        addComponent(targetFileSizeLabel, 0, 1);
        addComponent(targetFileSizeSlider, 1, 1);
        addComponent(targetFileSizeValueLabel, 2, 1);

        addComponent(startingResolutionsLabel, 0, 2);
        addComponent(startingResolutions, 1, 2);

        addComponent(outputFrameRateLabel, 0, 3);
        addComponent(outputFrameRates, 1, 3);

        addComponent(aspectRatioLabel, 0, 4);
        addComponent(aspectRatios, 1, 4);

        addComponent(backupVideoLabel, 0, 5);
        addComponent(backUpVideo, 1, 5);

        addComponent(compressVideo, 2, 6);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == videoFilePicker) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter videoFileFilter = new FileNameExtensionFilter("Video File","avi", "mkv", "mp4", "mov", "AVI", "MKV", "MP4", "MOV");
            fileChooser.setFileFilter(videoFileFilter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                videoFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                videoFileTextField.setText(videoFilePath);
            } else {
                return;
            }
        }

        if (e.getSource() == compressVideo) {

            if (videoFileTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No video was chosen!");
                return;
            }

            CommandStringBuilder commandStringBuilder = new CommandStringBuilder();
            DiscordVideoCompressor discordVideoCompressor = new DiscordVideoCompressor();
            File videoFile = new File(videoFilePath);

            if (videoFile.exists()) {
                try {
                    String commandString = commandStringBuilder.buildCommandString(videoFilePath, startingResolutions.getSelectedIndex(), outputFrameRates.getSelectedIndex(), aspectRatios.getSelectedIndex());
                    boolean isCompressed = discordVideoCompressor.compressVideo(commandString, videoFilePath, FileSizeConstants.FILE_SIZES[targetFileSizeSlider.getValue()], startingResolutions.getSelectedIndex(), backUpVideo.isSelected());
                    if (!isCompressed) {
                        JOptionPane.showMessageDialog(this, "Video already is at or smaller than the target file size!");
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Your video has been compressed!");
                        videoFile.delete();
                    }
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "The video file you tried to compress doesn't exist!");
            }

            videoFileTextField.setText("");
            File aspectRatioFile = new File("aspect_ratio.txt");
            if (aspectRatioFile.exists()) {
                aspectRatioFile.delete();
            }
        }
    }

    private void addComponent(JComponent component, int gridX, int gridY) {
        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;
        add(component, gridBagConstraints);
    }

    private String[] getStartingResolutions() {
        String[] startingResolutions = new String[FileSizeConstants.HEIGHTS.length];

        for (int i=0; i<FileSizeConstants.HEIGHTS.length; i++) {
            startingResolutions[i] = FileSizeConstants.HEIGHTS[i] + "p";
        }

        return startingResolutions;
    }

    private String[] getOutputFrameRates() {
        String[] outputFrameRates = new String[FileSizeConstants.FRAME_RATES.length + 1];

        outputFrameRates[0] = "Don't Change Frame Rate";
        for (int i=1; i<=FileSizeConstants.FRAME_RATES.length; i++) {
            outputFrameRates[i] = FileSizeConstants.FRAME_RATES[i-1] + " FPS";
        }

        return outputFrameRates;
    }

    private String[] getAspectRatios() {
        String[] aspectRatios = new String[FileSizeConstants.ASPECT_RATIOS_DISPLAY.length + 1];

        aspectRatios[0] = "Don't Change Aspect Ratio";
        for (int i=1; i<=FileSizeConstants.ASPECT_RATIOS_DISPLAY.length; i++) {
            aspectRatios[i] = FileSizeConstants.ASPECT_RATIOS_DISPLAY[i-1];
        }

        return aspectRatios;
    }
}