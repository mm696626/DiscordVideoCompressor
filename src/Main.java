// Discord Video Compressor by Matt McCullough
// This is to compress video files to be able to upload onto Discord

import ui.DiscordVideoCompressorUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("windows")) {
            DiscordVideoCompressorUI discordVideoCompressorUI = new DiscordVideoCompressorUI();
            discordVideoCompressorUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            discordVideoCompressorUI.pack();
            discordVideoCompressorUI.setVisible(true);
        }
        else {
            System.exit(0);
        }
    }
}