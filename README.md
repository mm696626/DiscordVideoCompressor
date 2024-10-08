# DiscordVideoCompressor

### Discord Video Compressor
* A tool to compress videos to be able to upload videos to Discord due to the file size limit

### Setup to get this tool working
* Open the tool (you'll need the latest JDK installed for that)
    * Link to JDK: https://www.oracle.com/java/technologies/downloads/
* Choose your video
* Once a video is picked, pick the desired file size options
* Once you're done, then press Compress Video to compress your video
* Enjoy uploading your clips to Discord!

### Disclaimers about this tool
* **This tool is Windows only due to me only knowing how to open Command Prompt at this moment**
* This tool also uses the ffmpeg suite, so credit to them as well.

### Notes about how this tool works
* This tool starts compression to 640x480 (or whatever resolution you select) and works it's way down and will use the highest quality that fits the target file size
  * In specific, this tool will use 4K, 1440p, 1080p, 720p, 480p, 360p, 240p, and 144p and after that divide by 2 for 2 more retries
  * This tool will do nothing if the video is less than the target file size
* Optionally, you can pick the frame rate of the output video to reduce file size while having better quality
* You can also trim your video as well to not have the whole video compressed as well
* H.265 codec only works on desktop at the moment of me making this, but it compresses better (H.264 is selected by default)