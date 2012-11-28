package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.couch.mrs.service.CouchMRSService;
import org.motechproject.couch.mrs.model.Attribute;
import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/* Spring controller for adding and removing users from a patient database using
 * Couch Patients minimally need a phone number and external id in order to make
 * calls from campaign messages.
 */

@Controller
public class UserController extends MultiActionController {

    @Autowired
    private CouchMRSService couchMRSService;

    public UserController() {
    }

    public UserController(CouchMRSService couchMRSService) {
        this.couchMRSService = couchMRSService;
    }

    @RequestMapping(value = "user/add", method = RequestMethod.POST)
    private ModelAndView add(String returnPage, HttpServletRequest request) {
        List<CouchMRSPerson> patientList = null;

        String phoneNum = request.getParameter("phoneNum");
        String externalID = request.getParameter("externalId");

        if (externalID.length() == 0 || externalID.equals("") || externalID.trim().length() == 0) {
        } else {
            /*
             * Only one patient should be returned if ID is unique, but it is
             * still returned as a list
             */
            patientList = (List<CouchMRSPerson>) couchMRSService.findByExternalId(externalID);

            if (patientList.size() > 0) { // Patient already exists, so it is
                                          // updated
                CouchMRSPerson thePatient = patientList.get(0);
                thePatient.addAttribute(new Attribute("phone number", phoneNum));
                couchMRSService.updatePerson(thePatient);
            } else {
                CouchMRSPerson newPatient = new CouchMRSPerson();
                newPatient.setExternalId(externalID);
                newPatient.setId(externalID);
                newPatient.addAttribute(new Attribute("phone number", phoneNum));
                couchMRSService.addPerson(newPatient);
            }
        }

        patientList = couchMRSService.findAllCouchMRSPersons();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView(returnPage, modelMap);

        return mv;
    }

    @RequestMapping(value = "user/remove", method = RequestMethod.POST)
    private ModelAndView remove(String returnPage, HttpServletRequest request) {

        try {
            String externalID = request.getParameter("externalId");
            List<CouchMRSPerson> patientsToRemove = couchMRSService.findByExternalId(externalID);
            couchMRSService.removePerson(patientsToRemove.get(0));

            List<CouchMRSPerson> patientList = couchMRSService.findAllCouchMRSPersons();

            Map<String, Object> modelMap = new TreeMap<String, Object>();
            modelMap.put("patients", patientList); // List of patients is for
                                                   // display purposes only

            ModelAndView mv = new ModelAndView(returnPage, modelMap);
            return mv;
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView(returnPage);
            return mv;
        }

    }

    @RequestMapping(value = "user/addCronUser", method = RequestMethod.POST)
    public ModelAndView addCronUser(HttpServletRequest request, HttpServletResponse response) {
        return add("cronFormPage", request);
    }

    @RequestMapping(value = "user/removeCronUser", method = RequestMethod.POST)
    public ModelAndView removeCronUser(HttpServletRequest request, HttpServletResponse response) {
        return remove("cronFormPage", request);
    }

    @RequestMapping(value = "user/addOffsetUser", method = RequestMethod.POST)
    public ModelAndView addOffsetUser(HttpServletRequest request, HttpServletResponse response) {
        return add("formPage", request);
    }

    @RequestMapping(value = "user/removeOffsetUser", method = RequestMethod.POST)
    public ModelAndView removeOffsetUser(HttpServletRequest request, HttpServletResponse response) {
        return remove("formPage", request);
    }

}
