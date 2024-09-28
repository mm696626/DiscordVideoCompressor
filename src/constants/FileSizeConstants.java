package constants;

public class FileSizeConstants {

    //will use the file sizes
    //8MB = older free limit
    //10MB = the free limit as of making this
    //25MB = the old free limit
    //50MB = Nitro Basic limit
    //500MB = Nitro file size limit

    public static final String[] FILE_SIZES_DISPLAY = {"8 MB", "10 MB", "25 MB", "50 MB", "500 MB"};
    public static final int[] FILE_SIZES = {8, 10, 25, 50, 500};
    public static final int BYTES_PER_MB = 1000000;

    //this will calculate the width for the desired aspect ratio
    //will use 4K, 1440p, 1080p, 720p, 480p, 360p, 240p, and 144p and after that divide by 2 for 2 more retries
    public static final int[] HEIGHTS = {2160, 1440, 1080, 720, 480, 360, 240, 144, 72, 36};

    public static final String[] ASPECT_RATIOS_DISPLAY = {"4:3", "16:9", "16:10", "1:1", "21:9"};
    public static final double[] ASPECT_RATIOS = {4.0/3.0, 16.0/9.0, 16.0/10.0, 1.0, 21.0/9.0};

    //frame rate of output video (optional)
    public static final int[] FRAME_RATES = {120, 60, 30, 25, 24, 20, 15, 10, 5, 2};
}
