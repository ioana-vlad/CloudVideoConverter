package BigData;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class UploadFileS3 {
	/**
	 * @author IoanaVlad
	 * Creates the credentials needed to connect to AWS and implements methods that upload files to  
	 * S3 and write messages to SQS.
	 * 		upload(String filep) : connects to a S3-bucket and uploads a file from a specific path;
	 * 		addMessageToSQS(String message) : connects to a SQS-queue and adds a specific message.
	 */

	static String existingBucketName = "dis-bucket";
	static private AWSCredentials cred;

	static {
		try {
			cred = new PropertiesCredentials(UploadFileS3.class
					.getClassLoader().getResourceAsStream(
							"AwsCredentials.properties"));
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public static void upload(String filep) {
		String filePath = filep;
		File file;
		try {
			System.out.println(filePath);
			file = new File(filePath);
			PutObjectRequest obj = new PutObjectRequest(existingBucketName,
					file.getName(), file);
			AmazonS3 s3Client = new AmazonS3Client(cred);
			s3Client.putObject(obj);
			System.out.println("New file uploaded to S3");
		} catch (Exception e) {
			System.out.println("NO INPUT FILE");
			e.printStackTrace();
		}
	}

	public static void addMessageToSQS(String message) {
		AmazonSQS sqsClient = new AmazonSQSClient(cred);
		SendMessageRequest msg = new SendMessageRequest(
				"https://sqs.us-east-1.amazonaws.com/675072056297/dis-sqs-terminat",
				message);
		sqsClient.sendMessage(msg);
		System.out.println("New message added to SQS");
	}
}
