/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.khresterion.due.DueConstants;
import com.khresterion.due.services.DatabaseService;
import com.khresterion.due.services.EtudeService;
import com.khresterion.web.bo.EntityWrapper;

/**
 * @author khresterion
 *
 */
@Controller
@RequestMapping("/etude")
public class EtudeController {

  public static final String FORMAT_WORD = "application/msword";

  public static final String FORMAT_WORDML =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

  public static final String FORMAT_ODT = "application/vnd.oasis.opendocument.text";

  public static final String FORMAT_PDF = "application/pdf";

  public static final String FORMAT_XML = "application/xml";

  @Autowired
  EtudeService service;

  @Autowired
  DatabaseService dbService;

  /**
   * 
   */
  public EtudeController() {
    super();
  }

  /**
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.GET)
  public String defaultResponse(HttpServletResponse response) {
    configcache(response);
    return "index";
  }

  /**
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public @ResponseBody EntityWrapper createEtude(HttpServletResponse response) {
    configcache(response);
    return service.create("0", DueConstants.TYPE_ETUDE, null);
  }

  /**
   * @param envkey
   * @return
   */
  @RequestMapping(value = "/display/{id}", method = RequestMethod.GET)
  public @ResponseBody EntityWrapper getEtude(@PathVariable(value = "id") String id,
      @RequestParam(value = "envkey") String envkey, HttpServletResponse response) {
    configcache(response);
    return service.display(DueConstants.TYPE_ETUDE, id, envkey);
  }

  /**
   * @param envkey
   * @param response
   * @return
   */
  @RequestMapping(value = "/save", method = RequestMethod.GET)
  public @ResponseBody boolean saveEtude(
      @RequestParam(value = "envkey", required = true) String envkey,
      @RequestParam(value = "entity_id", required = false) String entityId,
      HttpServletResponse response) {
    configcache(response);
    return service.save(envkey, StringUtils.trimToNull(entityId));
  }

  /**
   * @param envkey
   * @param response
   * @return
   */
  @RequestMapping(value = "/close", method = RequestMethod.GET)
  public @ResponseBody boolean closeEtude(@RequestParam(value = "envkey") String envkey,
      HttpServletResponse response) {
    configcache(response);
    return service.close(envkey);
  }

  /**
   * @param id
   * @param model
   * @return
   */
  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String openDUE(@RequestParam(value = "id", required = false, defaultValue = "") String id,
      @RequestParam(value = "envkey", required = false, defaultValue = "") String envkey,
      ModelMap model, HttpServletResponse response) {
    configcache(response);

    if ((envkey.isEmpty()) || (id.isEmpty())) {
      EntityWrapper wrapper = service.create(id, DueConstants.TYPE_ETUDE, null);
      if (wrapper == null) {
        return getList(model, response, 0, 50);
      } else {
        model.put("instance_id", wrapper.getId());
        model.put("type_id", DueConstants.TYPE_ETUDE);
        model.put("envkey", wrapper.getEnvkey());
        model.put("entity_id", StringUtils.EMPTY);
        return "etude_view";
      }
    } else {
      EntityWrapper wrapper = service.display(DueConstants.TYPE_ETUDE, id, envkey);
      if (wrapper == null) {
        return getList(model, response, 0, 50);
      } else {
        model.put("instance_id", wrapper.getId());
        model.put("type_id", DueConstants.TYPE_ETUDE);
        model.put("envkey", envkey);
        model.put("entity_id", StringUtils.EMPTY);
        return "etude_view";
      }
    }
  }

  /**
   * @param entityId
   * @param model
   * @param response
   * @return
   * @throws Exception
   * @throws NumberFormatException
   */
  @RequestMapping(value = "/load", method = RequestMethod.GET)
  public String loadFromDB(@RequestParam(value = "entity_id", required = true) String entityId,
      @RequestParam(value = "plan_id", required = false) String planIndex, ModelMap model,
      HttpServletResponse response) throws NumberFormatException, Exception {

    EntityWrapper wrapper = service.load(entityId, DueConstants.TYPE_ETUDE);
    if (wrapper == null) {
      return getList(model, response, 0, 50);
    } else {
      model.put("instance_id", wrapper.getId());
      model.put("type_id", DueConstants.TYPE_ETUDE);
      model.put("envkey", wrapper.getEnvkey());
      model.put("entity_id", entityId);

      return "etude_view";
    }   
  }
  
  @RequestMapping(value = "/treeview", method = RequestMethod.GET)
  public String getTreeview(@RequestParam(value = "envkey", required = true) String envkey,
          @RequestParam(value = "plan_id", required = true) String planId, 
          ModelMap model,HttpServletResponse response) {
              
    model.put("envkey", envkey);      
    model.put("plan_id", planId);
    configcache(response);
    
    return "treeview";
  }


  /**
   * edit section
   * 
   * @param id
   * @param model
   * @return
   */
  @RequestMapping("/edit/section")
  public String openSection(
      @RequestParam(value = "section_id", required = false, defaultValue = "0") String sectionId,
      @RequestParam(value = "type_id", required = false) String typeId, ModelMap model) {

    if (typeId == null) {
      model.put("type_id", DueConstants.SECTION_TYPES[0]);
    } else {
      model.put("type_id", typeId);
    }

    if (sectionId.equals("0")) {
      model.put("section_id", null);
    } else {
      model.put("section_id", sectionId);
    }

    return "section_view";
  }

  /**
   * @param model
   * @param page_number
   * @param page_size
   * @return
   */
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String getList(ModelMap model, HttpServletResponse response,
      @RequestParam("page") int page_number, @RequestParam("size") int page_size) {
    configcache(response);
    Page<com.khresterion.web.jpa.model.InstanceEntity> instanceList =
        service.getPagedInstanceEntitiesByType(DueConstants.TYPE_ETUDE, page_number, page_size);
    model.put("type_id", DueConstants.TYPE_ETUDE);
    model.put("instanceList", instanceList.getContent());
    model.put("totalElements", instanceList.getTotalElements());
    model.put("totalPages", instanceList.getTotalPages());
    model.put("nextPage", Math.min(page_number + 1, instanceList.getTotalPages()));
    model.put("prevPage", Math.max(page_number - 1, 0));
    model.put("currentPage", instanceList.getNumber() + 1);
    model.put("page_size", page_size);

    Map<String, Object> params = Maps.newHashMap();
    params.put("CalendarCrea", Calendar.getInstance());
    params.put("CalendarMod", Calendar.getInstance());
    params.put("DateFormat", DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE));
    model.put("params", params);
    return "etude_list";
  }

  @RequestMapping("/list/section")
  public String getSectionList(ModelMap model, HttpServletResponse response) {

    configcache(response);
    return "section_list";
  }

  @RequestMapping("/list/plan")
  public String getPlanList(ModelMap model, HttpServletResponse response) {

    configcache(response);
    return "modeplan_list";
  }

  /**
   * edit plan/summary
   * 
   * @param id
   * @param model
   * @return
   */
  @RequestMapping(value = "/edit/plan", method = RequestMethod.GET)
  public String openPlan(@RequestParam(value = "envkey", required = true) String envkey,
      @RequestParam(value = "plan_id", required = true) String planId, ModelMap model) {

    model.put("envkey", envkey);
    model.put("plan_id", planId);

    return "modeplan_view";
  }

  /**
   * @param envkey
   * @param planId
   * @param model
   * @return
   */
  @RequestMapping(value = "/save/plan", method = RequestMethod.GET)
  public @ResponseBody String savePlan(
      @RequestParam(value = "envkey", required = true) String envkey, ModelMap model) {

    return service.reloadPlan(envkey);
  }

  /**
   * @param entityId
   * @param response
   * @param model
   * @return
   */
  @RequestMapping(value = "/remove", method = RequestMethod.GET)
  public String removeInstance(
      @RequestParam(value = "entity_id", required = true) final String entityId,
      HttpServletResponse response, ModelMap model) {

    configcache(response);
    service.removeInstanceEntity(entityId);
    return getList(model, response, 0, 50);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET,
      produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, FORMAT_WORDML})
  public void exportContractDOCX(@RequestParam(value = "envkey", required = true) String envkey,
      HttpServletResponse response, HttpServletRequest request) {
    configcache(response);

    try {

      File finalFile = null;
      finalFile = service.generateDoc(envkey);

      // send response
      if (finalFile == null) {
        finalFile = new File("error.docx");
      }
      request.getSession().setAttribute("exportedfile", finalFile.getName());
      response.setContentType(FORMAT_WORDML);
      response.setHeader("Content-Disposition",
          "attachment; filename=\"" + finalFile.getName() + "\"");
      response.setContentLength((int) finalFile.length());
      OutputStream out;

      out = response.getOutputStream();

      FileSystemResource fsr = new FileSystemResource(finalFile);
      InputStream in = fsr.getInputStream();
      byte[] b = new byte[4096];
      while (in.read(b) > 0) {
        out.write(b);
      }
      in.close();
      out.flush();
      out.close();
      finalFile.delete();

    } catch (IOException e) {
      e.printStackTrace();
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
