/**
 * 
 */
package com.khresterion.due.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.khresterion.due.services.FileUploadService;

/**
 * @author khresterion
 *
 */
@Controller
@RequestMapping("/upload")
public class FileUploadController {

  @Autowired
  private FileUploadService uploadService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String getDefault() {
    return "file_upload";
  }

  @RequestMapping(value = "", method = RequestMethod.POST)
  public @ResponseBody String saveFile(@RequestParam("userfile") MultipartFile file,
      HttpServletResponse response, HttpServletRequest request) {

    configcache(response);
    try {

      int status = uploadService.save(file);
      request.getSession().setAttribute("userfile", file.getOriginalFilename());

      response.setStatus(status);
      return Integer.toString(response.getStatus());
    } catch (IOException e) {
      response.setStatus(430);
      return Integer.toString(response.getStatus());
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
