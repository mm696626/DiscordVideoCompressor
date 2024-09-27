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

    //will use 480p, 360p, 240p, and 144p and after that divide by 2 for 2 more retries
    public static final int[] WIDTHS = {640, 480, 320, 192, 96, 48};
    public static final int[] HEIGHTS = {480, 360, 240, 144, 72, 36};
}
