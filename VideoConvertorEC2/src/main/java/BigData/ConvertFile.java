package BigData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConvertFile {
	/**
	 * @author IoanaVlad 
	 *         This function converts an
	 *         existing video-file using specific parameters and creates a new
	 *         file;
	 * @param pathInputFile
	 *            : the path of the file that is going to be converted;
	 * @param pathOutputFile
	 *            : the path of the final converted file;
	 * @param flagOpt
	 *            : additional parameters for the ffmpeg command;
	 * @param CMD
	 *            : determines which type of command string gets created for the
	 *            ffmpeg process;
	 * @param ffmpegPath
	 * @return
	 */
	static int ConvertVideoFFMPEG(String pathInputFile, String pathOutputFile,
			String flagOpt, String CMD, String ffmpegPath) {
		try {
			String command = "";
			switch (CMD) {
			case "ipodIphone":
				command += "ffmpeg -i " + pathInputFile
						+ " -s 320x180 -vcodec mpeg4 " + pathOutputFile;
				break;
			case "PSP":
				command += "ffmpeg -i " + pathInputFile
						+ " -s 320x240 -vcodec mpeg4 " + pathOutputFile;
				break;
			case "mp3":
				command += "ffmpeg -i " + pathInputFile
						+ " -vn -ar 44100 -ac 2 -ab 192 -f mp3 "
						+ pathOutputFile;
				break;
			case "mpg":
				command += "ffmpeg -i " + pathInputFile + " " + pathOutputFile;
				break;
			case "avi":
				command += "ffmpeg -i " + pathInputFile + " " + pathOutputFile;
				break;
			default:
				command += "ffmpeg -i " + pathInputFile + " -flags " + flagOpt
						+ " " + pathOutputFile;
			}
			System.out.println("FFMPEG command: " + command);
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));
			String line = null;

			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);
			if (exitVal == 1)
				return -1;
			return 0;

		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
			return 1;
		} catch (InterruptedException e) {
			System.out.println(e);
			e.printStackTrace();
			return 1;
		}
	}
}
