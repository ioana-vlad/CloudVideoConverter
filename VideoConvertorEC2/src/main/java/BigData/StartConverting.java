package BigData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StartConverting {
	static String myodlFile;
	static String mynewFile;

	/**
	 * Cycles endlessly retrieving messages from a SQS-queue and processes them.
	 * If the message returned is NULL (the queue has no visible messages), it
	 * waits a bit and looks again for a message. If a message is found, it gets
	 * the name of the video-file and the command from the message body,
	 * downloads the video-file from S3 (input file), creates name of the final
	 * video-file, and the file itself (output file), calls the
	 * ConvertFile.ConvertVideoFFMPEG method using the path of the input and
	 * output files, uploads the output file to the S3-bucket, writes the name
	 * of the output file in another SQS-queue, deletes the input file from the
	 * S3-bucket and deletes the two local video-files created.
	 * 
	 * @author IoanaVlad
	 */

	public static void main(String[] args) {

		while (true) {
			FileFromS3 deSus = new FileFromS3();
			String element = deSus.getElementFromSQS();
			String cmd = "";
			if (element != null) {
				System.out.println("SQS element: " + element);
				if (element.contains("+")) {
					cmd = element.substring(element.indexOf("+") + 1);
					element = element.substring(0, element.indexOf("+"));
				}
				File file = deSus.getFirstFileFromS3(element);
				System.out.println("Old file downloaded from S3");
				String filename = file.getAbsolutePath().substring(0,
						file.getAbsoluteFile().toString().lastIndexOf('.'))
						+ "-terminat";
				switch (cmd) {
				case "ipodIphone":
					filename = filename + ".mp4";
					break;
				case "PSP":
					filename = filename + ".mp4";
					break;
				case "mp3":
					filename = filename + ".mp3";
					break;
				case "mpg":
					filename = filename + ".mpg";
					break;
				case "avi":
					filename = filename + ".avi";
					break;
				default:
					filename = filename
							+ file.getAbsolutePath().substring(
									file.getAbsoluteFile().toString()
											.lastIndexOf('.'));
				}
				File newFile = new File(filename);

				System.out.println("Converting " + element + " to " + filename);
				String pathInputFile = file.getAbsolutePath();
				String pathOutputFile = newFile.getAbsolutePath();
				myodlFile = pathInputFile;
				mynewFile = pathOutputFile;
				String flagOpt = "gray";
				File f = new File(System.getProperty("java.class.path"));
				File dir = f.getAbsoluteFile().getParentFile();
				int exitVal = ConvertFile.ConvertVideoFFMPEG(pathInputFile,
						pathOutputFile, flagOpt, cmd, dir.getAbsolutePath());
				if (exitVal != 0) {
					System.out.println("Conversion ERROR");
					clearLocalFiles();
				} else {
					UploadFileS3.upload(pathOutputFile);
					UploadFileS3.addMessageToSQS(newFile.getName());
					FileFromS3.deleteFromS3(file);
					clearLocalFiles();
				}

			} else
				try {
					String timeStamp = new SimpleDateFormat(
							"yyyy-MM-dd_HH:mm:ss").format(Calendar
							.getInstance().getTime());
					System.out.println(timeStamp
							+ "  -- SQS is empty. Waiting...");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	private static void clearLocalFiles() {
		new File(myodlFile).delete();
		new File(mynewFile).delete();
		System.out.println("Local files deleted");

	}
}
