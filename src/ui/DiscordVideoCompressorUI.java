package ui;

import constants.FileSizeConstants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiscordVideoCompressorUI extends JFrame implements ActionListener {

    private JButton videoFilePicker, compressVideo;
    private JLabel targetFileSizeLabel, videoFileNameLabel, fileSizeValueLabel;
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

        targetFileSizeLabel = new JLabel("Video Quality (higher values mean lower quality)");
        targetFileSizeSlider = new JSlider(0, 4, 1);
        targetFileSizeSlider.setPaintTicks(true);
        targetFileSizeSlider.setSnapToTicks(true);
        targetFileSizeSlider.setMajorTickSpacing(1);
        targetFileSizeSlider.setMinorTickSpacing(0);

        fileSizeValueLabel = new JLabel("10 MB");

        compressVideo = new JButton("Compress Video");
        compressVideo.addActionListener(this);

        targetFileSizeSlider.addChangeListener(e -> fileSizeValueLabel.setText(FileSizeConstants.FILE_SIZES_DISPLAY[targetFileSizeSlider.getValue()]));

        setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();

        addComponent(videoFileNameLabel, 0, 0);
        addComponent(videoFileTextField, 1, 0);
        addComponent(videoFilePicker, 2, 0);

        addComponent(targetFileSizeLabel, 0, 1);
        addComponent(targetFileSizeSlider, 1, 1);
        addComponent(fileSizeValueLabel, 2, 1);

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

        }

    }

    private void addComponent(JComponent component, int gridX, int gridY) {
        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;
        add(component, gridBagConstraints);
    }
}