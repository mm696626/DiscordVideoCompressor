package ui;

import constants.FileSizeConstants;
import io.CommandStringBuilder;
import io.DiscordVideoCompressor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DiscordVideoCompressorUI extends JFrame implements ActionListener {

    private JButton videoFilePicker, compressVideo;
    private JLabel targetFileSizeLabel, videoFileNameLabel, targetFileSizeValueLabel;
    private JSlider targetFileSizeSlider;
    private JTextField videoFileTextField;
    private String videoFilePath;

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

        addComponent(compressVideo, 2, 2);
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
            CommandStringBuilder commandStringBuilder = new CommandStringBuilder();
            DiscordVideoCompressor discordVideoCompressor = new DiscordVideoCompressor();
            try {
                String commandString = commandStringBuilder.buildCommandString(videoFilePath);
                boolean isCompressed = discordVideoCompressor.compressVideo(commandString, videoFilePath, FileSizeConstants.FILE_SIZES[targetFileSizeSlider.getValue()], 0);
                if (!isCompressed) {
                    JOptionPane.showMessageDialog(this, "Video already is at or smaller than the target file size!");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Your video has been compressed!");
                }
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    private void addComponent(JComponent component, int gridX, int gridY) {
        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;
        add(component, gridBagConstraints);
    }
}