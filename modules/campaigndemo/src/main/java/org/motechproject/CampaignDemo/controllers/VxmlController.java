package org.motechproject.CampaignDemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.motechproject.CampaignDemo.content.URLInitiator;

@Controller
public class VxmlController {
    
    private Logger logger = LoggerFactory.getLogger((this.getClass()));
    
    @Autowired
    private URLInitiator urlInitiator;

    @RequestMapping(value = "/vxml", method = RequestMethod.GET)
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generate VXML");

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        
        ModelAndView mav = new ModelAndView();

        mav.addObject("messages", urlInitiator.initiateURLS());
        mav.setViewName("messages-voxeo");

        return mav;
    }

}
