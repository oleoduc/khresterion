/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.khresterion.due.Section;
import com.khresterion.due.services.SectionService;
import com.khresterion.kengine.core.KType;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.jpa.model.EnvironmentEntity;
import com.khresterion.web.jpa.model.InstanceEntity;

/**
 * @author khresterion
 *
 */
@Controller
@RequestMapping("/section")
public class SectionController {

  @Autowired
  SectionService service;

  /**
   * 
   */
  public SectionController() {}

  /**
   * @param response
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.GET)
  public @ResponseBody List<Section> listSection(HttpServletResponse response) {
    //configcache(response);
    return service.listAll();
  }

  /**
   * @param typeId
   * @param path
   * @param response
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public @ResponseBody boolean createSection(@RequestParam(value = "type_id") String typeId,HttpServletResponse response) {
    configcache(response);
    return service.create(typeId);
  }
    
  /**
   * @param id
   * @param envkey
   * @param response
   * @return
   */
  @RequestMapping(value = "/{section_id}", method = RequestMethod.GET)
  public @ResponseBody InstanceEntity getSection(@PathVariable(value = "section_id") String id,
      HttpServletResponse response) {
    configcache(response);
    return service.display(id);
  }
  
  /**
   * @param id
   * @param envkey
   * @param response
   * @return
   */
  @RequestMapping(value = "/{section_id}", method = RequestMethod.DELETE)
  public @ResponseBody boolean deleteSection(@PathVariable(value = "section_id") String id,
      HttpServletResponse response) {
    configcache(response);
    return service.remove(id);
  }

  /**
   * @param id
   * @param typeId
   * @param envkey
   * @param response
   * @return
   */
  @RequestMapping(value = "/{section_id}/load", method = RequestMethod.GET)
  public @ResponseBody InstanceEntity loadSection(@PathVariable(value = "section_id") String id,
      @RequestParam(value = "type_id") String typeId, HttpServletResponse response) {
    configcache(response);
    return service.display(id);
  }

  /**
   * @param id
   * @param envkey
   * @param response
   * @return
   */
  @RequestMapping(value = "/{section_id}/update", method = RequestMethod.POST)
  public @ResponseBody Integer updateSection(@PathVariable(value = "section_id") String id,
      @RequestParam(value = "text", required=true) String text, HttpServletResponse response) {
    configcache(response);
    return service.update(id, text);
  }
  
  @RequestMapping(value = "/names", method = RequestMethod.GET)
  public @ResponseBody List<String> getSectionNames(HttpServletResponse response) {
    configcache(response);
    
    return service.listSectionNames();
  }
  
  /**
   * return the envkey for the Plan
   * @param id
   * @param response
   * @return
   */
  @RequestMapping(value = "/plan", method = RequestMethod.POST)
  public @ResponseBody String createNewPlan(HttpServletResponse response){
    configcache(response);
    return service.createPlan();
  }
  
  /**
   * @param planId
   * @param response
   * @return
   */
  @RequestMapping(value = "/plan", method = RequestMethod.GET)
  public @ResponseBody List<EnvironmentEntity> listExistingPlan(HttpServletResponse response){
    
    return service.listPlan();  
  }
  
  /**
   * @param planId
   * @param response
   * @return
   */
  @RequestMapping(value = "/plan/{plan_id}", method = RequestMethod.DELETE)
  public @ResponseBody String deleteSavedPlan(@PathVariable(value = "plan_id") String planId,HttpServletResponse response){
    configcache(response);
    return service.removePlan(planId);
  }
  
  @RequestMapping(value = "/plan/{plan_id}", method = RequestMethod.GET)
  public @ResponseBody String loadExistingPlan(@PathVariable(value = "plan_id") String planId,HttpServletResponse response){
    configcache(response);
    return service.loadPlan(planId);
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
