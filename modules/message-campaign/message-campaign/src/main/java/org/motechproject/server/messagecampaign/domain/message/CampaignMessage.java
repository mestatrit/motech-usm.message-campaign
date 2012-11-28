package org.motechproject.server.messagecampaign.domain.message;

//import org.motechproject.model.Time;

import java.util.List;

public class CampaignMessage {
    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;
    //private Time startTime;

    public String name() {
        return name;
    }

    public List<String> formats() {
        return formats;
    }

    public List<String> languages() {
        return languages;
    }

    public String messageKey() {
        return messageKey;
    }

    public CampaignMessage name(String name) {
        this.name = name;
        return this;
    }

    public CampaignMessage formats(List<String> formats) {
        this.formats = formats;
        return this;
    }

    public CampaignMessage languages(List<String> languages) {
        this.languages = languages;
        return this;
    }

    public CampaignMessage messageKey(String messageKey) {
        this.messageKey = messageKey;
        return this;
    }

  /*  public Time getStartTime() {
        return startTime;
    }

    public CampaignMessage setStartTime(Time startTime) {
        this.startTime = startTime;
        return this;
    }*/
}
