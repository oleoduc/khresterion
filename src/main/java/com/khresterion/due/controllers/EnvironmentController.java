/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.khresterion.due.services.DatabaseService;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.jpa.model.InstanceEntity;

/**
 * @author khresterion
 *
 */
@Controller
@RequestMapping("/v2/environment")
public class EnvironmentController {

    /**
     * 
     */
    private static final String KHRESTERION_API_V2 = "Khresterion API V2";

    @Autowired
    ServletContext context;

    @Autowired
    DatabaseService dbService;

    /**
     * @return nothing by default
     */
    @RequestMapping
    public @ResponseBody String defaultResponse() {
        return KHRESTERION_API_V2;
    }
            
    /**
     * Get entity without loading a session
     * 
     * @param entityId
     * @return
     */
    @RequestMapping(value = "/entity/{entity_id}", method = RequestMethod.GET)
    public @ResponseBody InstanceEntity getInstanceEntity(@PathVariable("entity_id") String entityId,
            HttpServletResponse response) {

        return dbService.getInstanceEntity(entityId);
    }
    
    /**
     * remove entity
     * 
     * @param entityId
     * @return
     */
    @RequestMapping(value = "/entity/{entity_id}", method = RequestMethod.DELETE)
    public @ResponseBody Boolean removeInstanceEntity(@PathVariable("entity_id") String entityId,
            HttpServletResponse response) {

        configcache(response);

        return dbService.removeInstanceEntity(entityId);
    }
    
    @RequestMapping(value = "/entity/{entity_id}/load", method = RequestMethod.GET)
    public @ResponseBody EntityWrapper loadInstance(@PathVariable("entity_id") String entityId,@RequestParam("type_id") String typeId){
        
        return dbService.loadKbuilder(entityId, typeId);
    }
    
    /**
     * @param entityId
     * @param typeId
     * @param envkey
     * @return
     */
    @RequestMapping(value = "/entity/{entity_id}/import", method = RequestMethod.GET)
    public @ResponseBody EntityWrapper importInstance(@PathVariable("entity_id") String entityId,@RequestParam("type_id") String typeId,
        @RequestParam("envkey") String envkey){
        
        return dbService.importSingle(entityId, typeId, envkey);
    }
    
    /**
     * @param envkey
     * @param response
     * @return
     */
    @RequestMapping(value = "/size", method = RequestMethod.GET)
    public @ResponseBody int getSessionSize(
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {
        configcache(response);
        return dbService.getSessionSize(envkey);
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public @ResponseBody Boolean saveSession(
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);
        boolean respcode = dbService.saveSession(envkey);
        if(!respcode) {response.setStatus(500);}
        return respcode;
    }

    @RequestMapping(value = "/close", method = RequestMethod.GET)
    public @ResponseBody Boolean closeSession(
            @RequestParam(value = "envkey", defaultValue = StringUtils.EMPTY) String envkey,
            HttpServletResponse response) {

        configcache(response);
        boolean respcode = dbService.closeSession(envkey);
        if(!respcode) {response.setStatus(500);}
        return respcode;

    }

    /**
     * return all instance by page with page limit = page_size
     * 
     * @param typeId
     * @param page
     * @param page_size
     * @param response
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public @ResponseBody Iterable<InstanceEntity> getPagedEntitiesByType(@RequestParam("type_id") String typeId,
            @RequestParam(value="page_number", required=false, defaultValue="0") int page_number, 
            @RequestParam(value="page_size", required=false, defaultValue="20") int page_size,
            HttpServletResponse response) {

        response.setHeader("Cache-Control", "public,max-age=3600");
        response.setDateHeader("Expires", 3600);
        if (page_number < 0)
            page_number = 0;
        if (page_size < 1)
            page_size = 1;
        return dbService.getPagedInstanceEntitiesByType(typeId, page_number, page_size);
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
