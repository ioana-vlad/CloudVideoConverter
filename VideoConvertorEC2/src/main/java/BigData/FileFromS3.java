package BigData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class FileFromS3 {
	/**
	 * @author IoanaVlad
	 * Creates the credentials needed to connect to AWS and implements methods that download and delete files 
	 * from S3 and retrieve messages from SQS.
	 * 		getFirstFileFromS3(String fileName) : connects to a S3-bucket and downloads a specified file,
	 * returns the file;
	 *  	getElementFromSQS() : connects to a SQS-queue, gets the body of the first available message,
	 *  which is the name of an object in the S3-bucket, and tries to download the object. If the object 
	 *  is NULL, it means that it could not be found because it has already been processed successfully 
	 *  and deleted, and deletes the message. Otherwise returns the message;
	 *  	SQSHasMessage() : connects to a SQS-queue and checks if it is empty;
	 *  	deleteFromS3(File file) : connects to a S3-bucket and deletes a specific object. 
	 */
	static String existingBucketName = "dis-bucket";
	String keyName;
	String filePath;
	private static AWSCredentials cred;
	static {
		try {
			cred = new PropertiesCredentials(FileFromS3.class.getClassLoader()
					.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFirstFileFromS3(String fileName) {
		AmazonS3 s3Client = new AmazonS3Client(cred);
		File temp = null;
		S3Object obj = s3Client.getObject("dis-bucket", fileName);
		InputStream is = obj.getObjectContent();
		byte[] buffer = new byte[1024];
		int read = -1;
		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String jarPath = dir.toString();
			temp = new File(jarPath + File.separator + fileName);
			temp.createNewFile();
			FileOutputStream fos = new FileOutputStream(temp);

			while ((read = is.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	public String getElementFromSQS() {
		AmazonSQS sqsClient = new AmazonSQSClient(cred);
		ReceiveMessageRequest mesaj = new ReceiveMessageRequest(
				"https://sqs.us-east-1.amazonaws.com/675072056297/dis-sqs")
				.withMaxNumberOfMessages(1);
		List<Message> listaDeMesaje = sqsClient.receiveMessage(mesaj)
				.getMessages();
		if (listaDeMesaje.isEmpty())
			return null;
		Message mesag = listaDeMesaje.get(0);
		AmazonS3 s3Client = new AmazonS3Client(cred);
		S3Object obj = null;
		try {
			if (mesag.getBody().contains("+")) {
				obj = s3Client.getObject("dis-bucket", mesag.getBody()
						.substring(0, mesag.getBody().indexOf("+")));
			} else {
				obj = s3Client.getObject("dis-bucket", mesag.getBody());
			}
		} catch (AmazonServiceException e) {
			System.out.println("Delete: " + mesaj.toString());
			sqsClient.deleteMessage(new DeleteMessageRequest(
					"https://sqs.us-east-1.amazonaws.com/675072056297/dis-sqs",
					mesag.getReceiptHandle()));
			System.out.println("Old message deleted from SQS");
			return getElementFromSQS();

		} catch (AmazonClientException e) {
			System.out.println("delete: " + mesaj.toString());
			sqsClient.deleteMessage(new DeleteMessageRequest(
					"https://sqs.us-east-1.amazonaws.com/675072056297/dis-sqs",
					mesag.getReceiptHandle()));
			return getElementFromSQS();
		}

		String msg = mesag.getBody();
		return msg;
	}

	public static boolean SQSHasMessage() {
		AmazonSQS sqsClient = new AmazonSQSClient(cred);
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				"https://sqs.us-east-1.amazonaws.com/675072056297/dis-sqs");
		return !sqsClient.receiveMessage(receiveMessageRequest).getMessages()
				.isEmpty();
	}

	public static void deleteFromS3(File file) {
		AmazonS3 s3Client = new AmazonS3Client(cred);
		try {
			s3Client.deleteObject(new DeleteObjectRequest(existingBucketName,
					file.getName()));
			System.out.println("Old file deleted from S3");
		} catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
}
