package BigData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;

public class DownloadFile {
	/**
	 * @author IoanaVlad
	 * Creates the credentials needed to connect to AWS and implements a method that downloads a 
	 * specific file from a S3-bucket.
	 */
	static boolean notCompleted = true;
	static String existingBucketName = "dis-bucket";
	static private AWSCredentials cred;
	
	static{
		try {
			cred = new PropertiesCredentials(DownloadFile.class
					.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static File getFile(String fileName) {
		AmazonS3 s3Client = new AmazonS3Client(cred);
		File temp = null;
		try {
			S3Object obj = s3Client.getObject("dis-bucket", fileName);
			InputStream is = obj.getObjectContent();
			byte[] buffer = new byte[1024];
			int read = -1;
			temp = new File(fileName);
			temp.createNewFile();
			FileOutputStream fos = new FileOutputStream(temp);

			while ((read = is.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.close();
		} catch (Exception e) {
			return null;
		}
		return temp;
	}

}
