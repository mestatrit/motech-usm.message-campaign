package org.motechproject.CampaignDemo.listeners;

import java.util.ArrayList;
import java.util.List;

import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.service.CouchMRSService;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A listener class used to listen on fired campaign message events. This class
 * demonstrates how to listen in on events and take action based upon their
 * payload. Payloads are stored as a String-Object mapping pair, where the
 * String is found in an appropriate EventKey class and the Object is the
 * relevant data or information associated with the key. The payload information
 * should be known ahead of time.
 * 
 * AllMessageCampaigns accesses the simple-message-campaign.json file found in
 * the resource package in the demo. The json file defines the characteristics
 * of a campaign.
 */
public class MessageListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CMSLiteService cmsliteService;

    @Autowired
    private CouchMRSService couchMRSService;

    // Defined in the voxeo module
    @Autowired
    private IVRService ivrService;

    @Autowired
    private MessageCampaignService service;

    @Autowired
    private SmsService smsService;

    public MessageListener() {

    }

    public MessageListener(CMSLiteService cmsliteService, CouchMRSService couchMRSService, IVRService ivrService,
            MessageCampaignService service) {
        this.cmsliteService = cmsliteService;
        this.couchMRSService = couchMRSService;
        this.ivrService = ivrService;
        this.service = service;
    }

    /**
     * Methods are registered as listeners on specific motech events. All motech
     * events have an associated subject, which is found in an appropriate
     * EventKeys class. When an event with that particular subject is relayed,
     * this method will be invoked. The payload parameters, in this case,
     * campaign name, message key and external id, must be known ahead of time.
     * 
     * @param event
     *            The Motech event relayed by the EventRelay
     * @throws ContentNotFoundException
     */
    @MotechListener(subjects = { EventKeys.SEND_MESSAGE })
    public void execute(MotechEvent event) throws ContentNotFoundException {

        String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);
        String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        @SuppressWarnings("unchecked")
        String language = "en";
        List<String> formats = new ArrayList<String>();
        formats.add("IVR");
        formats.add("SMS");
        String format = "";
        if (campaignName.contains("IVR")) {
            format = formats.get(0);
        }
        if (campaignName.contains("SMS")) {
            format = formats.get(1);
        }
        List<CouchMRSPerson> patientList = (List<CouchMRSPerson>) couchMRSService.findByExternalId(externalId);

        if (patientList.size() == 0) { // In the event no patient was found, the
                                       // campaign is unscheduled
            CampaignRequest toRemove = new CampaignRequest();
            toRemove.setCampaignName(campaignName);
            toRemove.setExternalId(externalId);

            service.stopAll(toRemove);
            return;
        } else {

            String phoneNum = patientList.get(0).attrValue("phone number");

            if (format.equals("IVR")) {
                if (cmsliteService.isStringContentAvailable(language, messageKey)) {
                    StringContent content = cmsliteService.getStringContent(language, messageKey);

                    CallRequest request = new CallRequest(phoneNum, 119, content.getValue());

                    String urlEnd = "";
                    if (messageKey.matches("cron-message")) {
                        urlEnd = "cron";
                    } else {
                        urlEnd = messageKey;
                    }
                    request.setVxml("http://webhosting.voxeo.net/92594/www/" + urlEnd + ".xml");
                    // request.setVxml("http://130.111.132.59:8080/motech-platform-server/module/cmsliteapi/stream/en/"
                    // + urlEnd);
                    request.setMotechId(patientList.get(0).getExternalId());

                    request.getPayload().put("applicationName", "CampaignDemo");

                    request.setOnBusyEvent(new MotechEvent("CALL_BUSY"));
                    request.setOnFailureEvent(new MotechEvent("CALL_FAIL"));
                    request.setOnNoAnswerEvent(new MotechEvent("CALL_NO_ANSWER"));
                    request.setOnSuccessEvent(new MotechEvent("CALL_SUCCESS"));

                    try {
                        ivrService.initiateCall(request);
                    } catch (CallInitiationException e) {
                        log.error("Unable to place the call. ", e);
                    }
                } else { // no content, don't place IVR call
                    log.error("No content available");
                }
            }

            if (format.equals("SMS")) {
                if (cmsliteService.isStringContentAvailable(language, messageKey)) { // SMS
                    StringContent content = cmsliteService.getStringContent(language, messageKey); // sms
                    smsService.sendSMS(phoneNum, content.getValue());

                } else { // no content, don't send SMS
                    log.error("No content available");
                }
            }
        }
    }
}
