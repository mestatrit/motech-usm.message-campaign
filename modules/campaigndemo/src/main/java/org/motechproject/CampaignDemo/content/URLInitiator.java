package org.motechproject.CampaignDemo.content;

import java.util.List;
import java.util.ArrayList;

public class URLInitiator {

	private List<Message> messages;

	public URLInitiator() {
	}

	public List<Message> initiateURLS() {
		String cmsLiteBaseUrl = "http://130.111.132.59:8080/motech-platform-server/module/cmsliteapi/";
		if (messages == null)
			messages = new ArrayList<Message>();
		for (int i = 5; i <= 40; i++) {
			Message message = new Message();
			message.setAudioFileUrl(cmsLiteBaseUrl + "ghanaPregnancyWeek" + i);
			messages.add(message);
		}
		Message cronMessage = new Message();
		cronMessage.setAudioFileUrl(cmsLiteBaseUrl + "test");
		messages.add(cronMessage);
		return messages;
	}

	public List<Message> getMessages() {
		return messages;
	}

}
