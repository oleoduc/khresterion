/**
 * 
 */
package com.khresterion.due.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author ndriama
 *
 */
@Component
public class CORSFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String origin = null;

        if(request.getHeader("Origin") != null){
            origin = request.getHeader("Origin");
        } else if(request.getHeader("X-Real-IP") != null) {
            origin = request.getHeader("X-Real-IP");
        } else {
            origin = "*";
        }
        response.addHeader("Access-Control-Allow-Origin", origin);
        response.addHeader("Access-Control-Max-Age", "10");
        
        // CORS "pre-flight" request
        if(request.getMethod().equals("OPTIONS")){
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers",
                "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,Pragma,Cache-Control,Expires");
        }

        filterChain.doFilter(request, response);
    }

}
