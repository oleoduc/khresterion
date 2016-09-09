package com.khresterion.due.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.khresterion.web.docbase.config.DocbasePersistenceConfig;
import com.khresterion.web.docbase.config.DocbaseWebConfig;

/**
 * application initialization: replace most part of web.xml.
 * 
 * @author nmdev
 *
 */
public class WebInit implements WebApplicationInitializer {

    private static final String DISPATCHER_SERVLET_NAME = "dueservlet";
    private static final String DISPATCHER_SERVLET_MAPPING = "/";
    private static final String UTF_8 = "UTF-8";

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();        
          
        appContext.setDisplayName("due");
        appContext.getEnvironment().setActiveProfiles(ProfileConstants.MYSQL_DB); //change profile here
        appContext.register(AppConfig.class);

        AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
        mvcContext.register(WebConfig.class);
        mvcContext.register(DocbaseWebConfig.class);
        
      
        ServletRegistration.Dynamic springmvc = servletContext.addServlet(
                DISPATCHER_SERVLET_NAME, new DispatcherServlet(mvcContext));
        springmvc.setLoadOnStartup(1);
        springmvc.addMapping(DISPATCHER_SERVLET_MAPPING);
        springmvc.setInitParameter("readonly", "false");
        springmvc.setInitParameter("throwExceptionIfNoHandlerFound", "true");      

        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(
                DispatcherType.REQUEST, DispatcherType.FORWARD);

        /* entity manager view filter for kengine */
        OpenEntityManagerInViewFilter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
        openEntityManagerInViewFilter.setPersistenceUnitName("kengine");
        openEntityManagerInViewFilter.setEntityManagerFactoryBeanName("entityManagerFactory");
        
        FilterRegistration.Dynamic openEntityManager = servletContext
                .addFilter("openEntityManagerInViewFilter",
                        openEntityManagerInViewFilter);
        openEntityManager.addMappingForUrlPatterns(dispatcherTypes, true,
                "/etude/*","/v2/*","/member/*", "/role/*","/v1/admin/*","/variable/*", "/document/*");
        openEntityManager.setInitParameter("singleSession", "false");
        openEntityManager.setInitParameter("flushMode", "AUTO");

        FilterRegistration.Dynamic corsfilter = servletContext.addFilter("corsFilter", CORSFilter.class);
        corsfilter.addMappingForUrlPatterns(dispatcherTypes, false, "/v2/*");
        
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(UTF_8);
        characterEncodingFilter.setForceEncoding(true);

        FilterRegistration.Dynamic characterEncoding = servletContext
                .addFilter("characterEncoding", characterEncodingFilter);
        characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        FilterRegistration.Dynamic security = servletContext.addFilter(
                "springSecurityFilterChain", new DelegatingFilterProxy());
        security.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        //servletContext.addListener(HttpSessionEventPublisher.class);
        servletContext.addListener(new KengineContextLoaderListener(appContext));
        servletContext.addListener(new KengineContextCleanupListener());
    }

}
