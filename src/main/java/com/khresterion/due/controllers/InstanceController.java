/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.khresterion.due.services.DBSessionManager;
import com.khresterion.due.services.RuntimeInstanceService;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.core.KType;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.jpa.Session;

/**
 * @author khresterion
 *
 */
@Controller
@RequestMapping("/v2/instance")
public class InstanceController {
    
    private static final String KHRESTERION_API_V2 = "Khresterion API V2";
    
    @Autowired
    ServletContext context;

    @Autowired
    DBSessionManager jsm;
    
    @Autowired
    RuntimeInstanceService rts;   
    
    /**
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody String defaultResponse() {
        return KHRESTERION_API_V2;
    }

    /**
     * create an instance for the given Kengine {@link KType}
     * 
     * @param envkey
     * @param typeid
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody EntityWrapper createInstance(
            @RequestParam("type_id") String typeId,
            @RequestParam("instance_id") String instanceId,
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);

        Session session = jsm.resolve(envkey);
        if (session == null)
            session = jsm.createStandardSession(typeId);

        return new EntityWrapper(rts.createInstance(typeId,
                instanceId, session),jsm.envkey(session));
    }

    /**
     * return all instances of a given {@value typeId}.
     * 
     * @param typeiId
     * @param envkey
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public @ResponseBody Collection<EntityWrapper> getInstancesByType(
            @RequestParam(value = "type_id", required=true) String typeId,
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);


        Session session = jsm.resolve(envkey);
        if (session == null) {
            return Lists.newArrayList();
        } else {

            Collection<Instance> instanceList = rts.getInstancesByType(typeId,
                    session);
            Collection<EntityWrapper> wrapperList = Lists.newArrayList();

            for (Instance instance : instanceList) {
                wrapperList.add(new EntityWrapper(instance,envkey));
            }
            return wrapperList;
        }
    }

    /**
     * return a specific instance given his {@value instanceId}. The id is given
     * during creation.
     * 
     * @param instanceId
     * @param envkey
     * @return
     */
    @RequestMapping(value = "/{instance_id}", method = RequestMethod.GET)
    public @ResponseBody EntityWrapper getInstance(
            @PathVariable("instance_id") String instanceId,
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);


        Session session = jsm.resolve(envkey);
        if (session == null) {
            return new EntityWrapper(null,envkey);
        } else {
            Instance instance = rts.getInstance(instanceId, session);

            return new EntityWrapper(instance, envkey);
        }
    }

    /**
     * remove a specific instance given his {@value instanceId}. The id is given
     * during creation. it is a runtime action
     * 
     * @param envkey
     * @param id
     * @return
     */
    @RequestMapping(value = "/{instance_id}", method = RequestMethod.DELETE)
    public @ResponseBody EntityWrapper removeInstance(
            @PathVariable("instance_id") String instanceId,
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);

        Session session = jsm.resolve(envkey);
        if (session == null) {
            return new EntityWrapper(null,envkey);
        } else {
            return rts.removeInstance(instanceId, session) ? new EntityWrapper(null,envkey) : null;
        }
    }

    /**
     * link an existing instance to a main instance. Equivalent of
     * updateProperty for {@link com.khresterion.kengine.core.Entity}, because
     * updateProprety accepts only primitive values To use when the main
     * instance holds the relation.
     * 
     * @param instanceId
     * @param propertytype
     * @param propertypath
     * @param linkedinstanceId
     * @return
     * @throws WebpanelException
     */
    @RequestMapping(value = "/{instance_id}/link/existing", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper linkExistingInstance(
            @PathVariable("instance_id") String instanceId,
            @RequestParam(value="linked_id", required=true) String linkedinstanceId,
            @RequestParam(value="propertypath", required=true) String propertypath,
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);

        Session session = jsm.resolve(envkey);
        if (session == null) {
            return new EntityWrapper(null,envkey);
        } else {
            Instance instance;
            try {
                instance = rts.linkExistingInstance(instanceId,
                        propertypath, linkedinstanceId, session);
                return new EntityWrapper(instance, envkey);
            } catch (Exception e) {
                
                e.printStackTrace();
                return new EntityWrapper(null,envkey);
            }            
        }
    }

    /**
     * shortcut for creating a child instance and link to a parent instance this
     * is the mirror operation of {@link linkInstance}. To use when the child
     * instance holds the relation.
     * 
     * @param instanceId
     * @param typeId
     * @param propertypath
     * @param envkey
     * @return
     * @throws WebpanelException
     */
    @RequestMapping(value = "/{instance_id}/link/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody EntityWrapper linkNewInstance(
            @PathVariable("instance_id") String instanceId,
            @RequestParam(value="type_id", required=true) String typeId,
            @RequestParam(value="propertypath", required=true) String propertypath,
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response){

        configcache(response);


        Session session = jsm.resolve(envkey);
        if (session == null) {
            return new EntityWrapper(null,envkey);
        } else {

            Instance instance = null;
            try {
                instance = rts.linkNewInstance(typeId, propertypath,
                        instanceId, session);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return new EntityWrapper(instance,envkey);
        }
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
