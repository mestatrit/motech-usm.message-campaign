package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.service.CouchMRSService;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * A Spring controller for starting and stopping campaigns based on an external
 * ID. The PatientDAO is used only to display the list of registered users
 */
@Controller
public class CampaignController extends MultiActionController {

    @Autowired
    private CouchMRSService couchMRSService;

    @Autowired
    private MessageCampaignService service;

    public CampaignController(MessageCampaignService service, CouchMRSService couchMRSService) {
        this.couchMRSService = couchMRSService;
        this.service = service;
    }

    public CampaignController() {

    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {

        String externalId = request.getParameter("externalId");
        String campaignName = request.getParameter("campaignName");
        String startoffset = request.getParameter("offset");

        int offsetValue = 0;

        try {
            offsetValue = Integer.parseInt(startoffset);
        } catch (NumberFormatException e) {
            offsetValue = 0;
        }

        /**
         * The campaign name in the campaign request references the
         * simple-message-campaign.json file found in the campaign demo's
         * resource folder. The required name of this file is determined by the
         * messageCampaign.properties file found in the motech-messagecampaign
         * platform module's resource folder.
         */
        CampaignRequest campaignRequest = new CampaignRequest();
        campaignRequest.setCampaignName(campaignName);
        campaignRequest.setExternalId(externalId);
        LocalTime now = new LocalTime();
        now = now.now().plusMinutes(2);
        Time time = new Time(now);
        campaignRequest.setStartTime(time);
        LocalDate date = new LocalDate();
        campaignRequest.setReferenceDate(date.now());

        service.startFor(campaignRequest);

        List<CouchMRSPerson> patientList = couchMRSService.findAllCouchMRSPersons();
        ;

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = null;

        if (campaignName.equals("Cron based SMS Program") || campaignName.equals("Cron based IVR Program")) {
            mv = new ModelAndView("cronFormPage", modelMap);
        } else {
            mv = new ModelAndView("formPage", modelMap);
        }

        return mv;
    }

    // register anyone get a message ... enroll a person by selecting from a
    // list of persons
    // angular UI for person object
    // commcare to motech to openmrs (one direction) nurses get notified when
    // someone is out of compliance...in demo both patients
    // and nurses will be notified. Use OpenMRS (don't need to define couch
    // people)

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) {

        String externalId = request.getParameter("externalId");
        String campaignName = request.getParameter("campaignName");

        CampaignRequest campaignRequest = new CampaignRequest();
        campaignRequest.setCampaignName(campaignName);
        campaignRequest.setExternalId(externalId);

        /**
         * See comment for service.startFor(campaignRequest) in above method for
         * a more detailed description. When stopping a campaign, an event is
         * not raised, the job is simply removed from the Quartz scheduler and
         * no more events are raised. stopAll stops ALL messages associated with
         * the specific campaign and specific external id. To stop a specific
         * message, instead call service.stopFor(campaignRequest, messageKey)
         * with the provided message key as a parameter
         */
        try {
            service.stopAll(campaignRequest);
        } catch (Exception e) {
            if (campaignName.equals("Cron based SMS Program") || campaignName.equals("Cron based IVR Program")) {
                return new ModelAndView("cronFormPage");
            } else {
                return new ModelAndView("formPage");
            }
        }

        List<CouchMRSPerson> patientList = couchMRSService.findAllCouchMRSPersons();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = null;

        if (campaignName.equals("Cron based SMS Program") || campaignName.equals("Cron based IVR Program")) {
            mv = new ModelAndView("cronFormPage", modelMap);
        } else {
            mv = new ModelAndView("formPage", modelMap);
        }

        return mv;
    }

}
