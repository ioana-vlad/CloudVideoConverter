package BigData;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;


public class FileToS3 {
	/**
	 * @author IoanaVlad
	 * Creates the credentials needed to connect to AWS and implements methods that upload files to  
	 * S3 and write messages to SQS. 
	 * 		putInS3File(String keyName, String filePath) : connects to a S3-bucket and uploads a 
	 * file from a specific path; 
	 * 		putInSQS(String fileName) : connects to a SQS-queue and adds a specific message.
	 */
	
	static String existingBucketName = "dis-bucket";
	private static AWSCredentials cred;

	static {
		try {
			cred = new PropertiesCredentials(
					FileToS3.class
							.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String putInS3File(String keyName, String filePath) {
		File file = new File(filePath);
		try {
			PutObjectRequest obj = new PutObjectRequest(existingBucketName,
					keyName, file);
			AmazonS3 s3Client = new AmazonS3Client(cred);
			s3Client.putObject(obj);
			System.out.println(keyName + " uploaded cu succes");
		} catch (AmazonServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keyName;
	}

	public static void putInSQS(String fileName) {
		AmazonSQS sqsClient = new AmazonSQSClient(cred);
		SendMessageRequest msg = new SendMessageRequest(
				"https://sqs.us-east-1.amazonaws.com/675072056297/dis-sqs",
				fileName);
		sqsClient.sendMessage(msg);
		System.out.println(fileName + " adaugat in SQS cu succes");
	}

	public void isConverted(String fileName) {
		// TODO ask there is fileName+terminat in S3

	}
}
