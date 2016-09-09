/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.khresterion.due.services.DBSessionManager;
import com.khresterion.due.services.RuntimeInstanceService;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.jpa.Session;

/**
 * @author khresterion
 * APIV2 for properties
 */
@Controller
@RequestMapping("/v2/property")
public class PropertyController {

    @Autowired
    RuntimeInstanceService ris;
    
    @Autowired
    DBSessionManager dsm;
    
    /**
     * 
     */
    public PropertyController() {
        super();
    }
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody com.khresterion.kengine.bo.Property getProperty(@RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,@RequestParam("envkey") String envkey, HttpServletResponse response){
        configcache(response);
        Session session = dsm.resolve(envkey);
        return ris.getProperty(instanceId,propertypath,session);
    }
    
    /**
     * @param instanceId
     * @param propertypath
     * @param value
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper updateProperty(
            @RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,
            @RequestParam("value") String value,@RequestParam("envkey") String envkey,HttpServletResponse response) {
        
        configcache(response);
        Session session = dsm.resolve(envkey);
        try {
            ris.updateProperty(instanceId, propertypath, value, session);
            return new EntityWrapper(ris.getInstance(instanceId, session),envkey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public @ResponseBody EntityWrapper deleteProperty(
            @RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,@RequestParam("envkey") String envkey, HttpServletResponse response) {
        configcache(response);
        Session session = dsm.resolve(envkey);
        try {
            ris.removeProperty(instanceId, propertypath, session);
            return new EntityWrapper(ris.getInstance(instanceId, session),envkey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }        
    }
    
    /**
     * @param instanceId
     * @param propertypath
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "/include", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper includeProperty(@RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,@RequestParam("envkey") String envkey,HttpServletResponse response) {
        
        configcache(response);
        Session session = dsm.resolve(envkey);
        try {
            ris.includeProperty(instanceId, propertypath, session);
            return new EntityWrapper(ris.getInstance(instanceId, session),envkey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }        
    }
    
    /**
     * @param instanceId
     * @param propertypath
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "/notinclude", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper notIncludeProperty(@RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,@RequestParam("envkey") String envkey,HttpServletResponse response) {
        
        configcache(response);
        Session session = dsm.resolve(envkey);
        try {
            ris.notIncludeProperty(instanceId, propertypath, session);
            return new EntityWrapper(ris.getInstance(instanceId, session),envkey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @param instanceId
     * @param propertypath
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "/exclude", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper excludeProperty(@RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,@RequestParam("envkey") String envkey,HttpServletResponse response) {
        
        configcache(response);
        Session session = dsm.resolve(envkey);
        try {
            ris.excludeProperty(instanceId, propertypath, session);
            return new EntityWrapper(ris.getInstance(instanceId, session),envkey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @param instanceId
     * @param propertypath
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "/notexclude", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper notExcludeProperty(@RequestParam("instance_id") String instanceId,
            @RequestParam("propertypath") String propertypath,@RequestParam("envkey") String envkey,HttpServletResponse response) {
        
        configcache(response);
        Session session = dsm.resolve(envkey);
        try {
            ris.notExcludeProperty(instanceId, propertypath, session);
            return new EntityWrapper(ris.getInstance(instanceId, session),envkey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Force no cache for Internet Explorer
     * 
     * @param response
     */
    private void configcache(HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
