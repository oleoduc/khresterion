/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.khresterion.due.DueConstants;
import com.khresterion.due.services.EtudeService;

/**
 * @author khresterion
 *
 */
@Controller
@RequestMapping("/")
public class IndexController {

  @Autowired
  EtudeService service;

  /**
   * index page
   * 
   * @param model
   * @return
   */
  @RequestMapping(value = "")
  public String getIndex(ModelMap model) {

    Page<com.khresterion.web.jpa.model.InstanceEntity> instanceList =
        service.getPagedInstanceEntitiesByType(DueConstants.TYPE_ETUDE, 0, 50);
    model.put("type_id", DueConstants.TYPE_ETUDE);
    model.put("instanceList", instanceList.getContent());
    model.put("totalElements", instanceList.getTotalElements());
    model.put("totalPages", instanceList.getTotalPages());
    model.put("nextPage", Math.min(1, instanceList.getTotalPages()));
    model.put("prevPage", 0);
    model.put("currentPage", instanceList.getNumber() + 1);
    model.put("page_size", 50);

    Map<String, Object> params = Maps.newHashMap();
    params.put("CalendarCrea", Calendar.getInstance());
    params.put("CalendarMod", Calendar.getInstance());
    params.put("DateFormat", DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE));
    model.put("params", params);

    return "index";
  }

  /**
   * index page
   * 
   * @return
   */
  @RequestMapping("/404")
  public String getNotFoundPage(ModelMap model) {
    return "404";
  }

  /**
   * index page
   * 
   * @return
   */
  @RequestMapping("/500")
  public String getErrorPage(ModelMap model) {
    return "404";
  }

  /**
   * @param error
   * @param logout
   * @param model
   * @param response
   * @return
   */
  @RequestMapping(value = "/connect", method = RequestMethod.GET)
  public String login(@RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "logout", required = false) String logout, ModelMap model,
      HttpServletResponse response) {

    if (error != null) {
      model.addAttribute("errormsg", "Identifiant incorrect ou connexion refus&eacute;e.");
    } else {
      model.addAttribute("errormsg", null);
    }
    if (logout != null) {
      model.addAttribute("successmsg",
          "Vous avez &eacute;t&eacute; deconnect&eacute; avec succ&egrave;s.");
    } else {
      model.addAttribute("successmsg", null);
    }

    return "connect_page";
  }

  /* custom pages */


  /**
   * liste des documents
   * 
   * @param model
   * @return
   */
  @RequestMapping("/list/plan")
  public String getDocumentList(ModelMap model) {
    return "plan_list";
  }

  /**
   * @param model
   * @return
   */
  @RequestMapping("/list/section")
  public String getSectionList(ModelMap model) {
    return "section_list";
  }

  /**
   * @param model
   * @return
   */
  @RequestMapping("/edit/accounts")
  public String openAccountManagement(ModelMap model) {
    return "admin_console";
  }

  /**
   * display a warning before removing the document
   * 
   * @param id
   * @param model
   * @return
   */
  @RequestMapping("/remove")
  public String removePlan(@RequestParam(value = "plan_id", required = true) String id,
      ModelMap model) {
    return "remove_view";
  }

  /**
   * @param path
   * @return
   */
  @RequestMapping("/views/partials/{remainder}")
  public String getLayoutFolder(@PathVariable("remainder") String path,
      HttpServletResponse response) {

    return "partials/" + path;
  }

  /**
   * add a second mapping for subfolders this is dumb but Spring does not play nice with wildcards
   * 
   * @param folder
   * @param path
   * @return
   */
  @RequestMapping("/views/partials/{folder}/{remainder}")
  public String getLayoutFolder(@PathVariable("folder") String folder,
      @PathVariable("remainder") String path, HttpServletResponse response) {

    return "partials/" + folder + "/" + path;
  }
}
