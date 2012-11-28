package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.couch.mrs.model.CouchMRSPerson;
import org.motechproject.couch.mrs.service.CouchMRSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

//Spring controller for displaying the initial demo form page

@Controller
public class FormController extends MultiActionController {

    @Autowired
    private CouchMRSService couchMRSService;

    @RequestMapping(value = "/form/cron", method = RequestMethod.GET)
    public ModelAndView cronCampaign(HttpServletRequest request, HttpServletResponse response) {
        List<CouchMRSPerson> patientList = null;
        patientList = couchMRSService.findAllCouchMRSPersons();
        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView("cronFormPage", modelMap);
        return mv;
    }

    @RequestMapping(value = "/form/offset", method = RequestMethod.GET)
    public ModelAndView offsetCampaign(HttpServletRequest request, HttpServletResponse response) {

        List<CouchMRSPerson> patientList = null;
        patientList = couchMRSService.findAllCouchMRSPersons();
        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView("formPage", modelMap);
        return mv;
    }

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView main(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("main");
    }

}
