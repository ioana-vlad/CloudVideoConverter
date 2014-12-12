package BigData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;

public class VideoUploader implements Receiver, SucceededListener,
		Property.ValueChangeListener {

	private static final long serialVersionUID = -8390092564386198111L;
	File file;
	private String option = "";

	@Override
	public void uploadSucceeded(SucceededEvent event) {

		String fileName = FileToS3.putInS3File(file.getName(),
				file.getAbsolutePath());
		if (option.length() > 0){
			FileToS3.putInSQS(fileName + "+" + option);
			if (option.equals("PSP") || option.equals("ipodIphone")){
				fileName = fileName.substring(0, fileName.lastIndexOf('.'))
						+ "-terminat.mp4";
			}
			else {
				fileName = fileName.substring(0, fileName.lastIndexOf('.'))
						+ "-terminat." + option;
			}
			
		}
			
		else {
			FileToS3.putInSQS(fileName);
			fileName = fileName.substring(0, fileName.lastIndexOf('.'))
					+ "-terminat" + fileName.substring(fileName.lastIndexOf('.'));
		}		
		System.out.println("upload succeded");
		System.out.println(fileName);
		File download = DownloadFile.getFile(fileName);
		System.out.println("converting..");
		while (download == null) {
			try {
				Thread.sleep(1000);
				download = DownloadFile.getFile(fileName);
			} catch (InterruptedException e) { //
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Downloaded");
		Link downloadLink = new Link();
		downloadLink.setCaption("download");

		FileDownloader fileDownloader = new FileDownloader(new FileResource(
				download));
		fileDownloader.extend(downloadLink);
		Layout layout = (Layout) event.getComponent().getParent();
		layout.addComponent(downloadLink);

	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		if (!mimeType.contains("video")) {
			MessageBox.showPlain(Icon.WARN, "", filename
					+ " could not be converted! ", ButtonId.OK);
			return null;
		}
		FileOutputStream fos = null;
		try {
			// Open the file for writing.
			file = File.createTempFile(
					UUID.randomUUID().toString().replaceAll("-", ""),
					filename.substring(filename.lastIndexOf(".")));
			file.deleteOnExit();
			fos = new FileOutputStream(file);
			new Notification("Upload started...",
					Notification.Type.HUMANIZED_MESSAGE);
		} catch (final java.io.FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			return null;
		} catch (IOException e) {
			new Notification("Could not create tempFile<br/>", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			return null;
		}
		return fos; // Return the output stream to write to
	}

	@Override
	public void valueChange(ValueChangeEvent event) {

		String val = event.getProperty().getValue().toString();
		if (val.equals("Encode a video sequence for the iPpod/iPhone")) {
			option = "ipodIphone";
		} else if (val.equals("Encode video for the PSP")) {
			option = "PSP";
		} else if (val
				.equals("Extracting sound from a video, and save it as Mp3")) {
			option = "mp3";
		} else if (val.equals("Convert to .mpg")) {
			option = "mpg";
		} else if (val.equals("Convert to .avi")) {
			option = "avi";
		}

	}

}
