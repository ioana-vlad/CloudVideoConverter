package BigData;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "BigData.AppWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		Label title = new Label();
		title.setCaption("Video converter");
		title.setStyleName("Didi");
		setContent(layout);
		VideoUploader receiver = new VideoUploader();
		Upload uploader = new Upload("Upload your video here!", receiver);
		uploader.addSucceededListener(receiver);
		
		final String[] formats = new String[] {
				"Encode a video sequence for the iPpod/iPhone",
				"Encode video for the PSP",
				"Extracting sound from a video, and save it as Mp3",
				"Convert to .mpg",
				"Convert to .avi"};
		OptionGroup option = new OptionGroup(); 
		option.setImmediate(true);
		option.addValueChangeListener(receiver);
		for(String st : formats){
			option.addItem(st);
		}
		layout.addComponent(title);
		layout.addComponent(option);
		layout.addComponent(uploader);
	}

}
