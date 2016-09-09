/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author khresterion
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleError(HttpServletRequest req, Exception exception) {
      StringBuilder sb = new StringBuilder("{request:" + req.getRequestURI() + "?" + req.getQueryString());
      sb.append(",message:"+ exception.getMessage());
      StackTraceElement stack = exception.getStackTrace()[0];      
      if(stack!=null) sb.append(",method:"+stack.toString());
      sb.append("}");
      exception.printStackTrace();

      return sb.toString();
    }
}
