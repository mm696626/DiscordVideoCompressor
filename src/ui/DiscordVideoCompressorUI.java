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
    private JLabel targetFileSizeLabel, videoFileNameLabel, targetFileSizeValueLabel, startingResolutionsLabel;
    private JSlider targetFileSizeSlider;
    private JTextField videoFileTextField;
    private String videoFilePath;

    private JComboBox startingResolutions;


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


        startingResolutionsLabel = new JLabel("Starting Resolution to Compress To");
        startingResolutions = new JComboBox<>(getStartingResolutions());

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

        addComponent(compressVideo, 2, 3);
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
                    String commandString = commandStringBuilder.buildCommandString(videoFilePath, startingResolutions.getSelectedIndex());
                    boolean isCompressed = discordVideoCompressor.compressVideo(commandString, videoFilePath, FileSizeConstants.FILE_SIZES[targetFileSizeSlider.getValue()], startingResolutions.getSelectedIndex());
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
        }
    }

    private void addComponent(JComponent component, int gridX, int gridY) {
        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;
        add(component, gridBagConstraints);
    }

    private String[] getStartingResolutions() {
        String[] startingResolutions = new String[FileSizeConstants.WIDTHS.length];

        for (int i=0; i<FileSizeConstants.WIDTHS.length; i++) {
            startingResolutions[i] = FileSizeConstants.WIDTHS[i] + "x" + FileSizeConstants.HEIGHTS[i];
        }

        return startingResolutions;
    }
}