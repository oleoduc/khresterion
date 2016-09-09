/**
 * &copy; Khresterion 2015
 */
package com.khresterion.due.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.khresterion.due.DueConstants;
import com.khresterion.due.services.EtudeService;
import com.khresterion.kengine.core.KConstants;
import com.khresterion.util.ResourceManager;
import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.kbuilder.KbuilderWebProxy;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author khresterion khresterion
 *
 */
public class KengineContextLoaderListener extends ContextLoaderListener {

    private static final KhresterionLogger LOG = KhresterionLogger
            .getLogger(com.khresterion.due.config.KengineContextLoaderListener.class);
    
    private static final String sep = "-";
    
    private KbuilderWebProxy kbp;

    /**
     * 
     */
    public KengineContextLoaderListener() {
        super();
    }

    /**
     * @param context
     */
    public KengineContextLoaderListener(WebApplicationContext context) {
        super(context);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.ContextLoaderListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        
        super.contextInitialized(event);        
        startKbuilder(event.getServletContext());
        loadPlan(event.getServletContext());
    }

    private void startKbuilder(ServletContext context) {
        try {            
            
            ResourceManager rm = new ResourceManager("META-INF.kbuilder.booter.app");
            String[] array = new String[4];
            String appname = rm.getValue("app.name");
            String appversion = rm.getValue("app.version");
            String appusage = rm.getValue("app.usage");
            array[0] = appname;
            array[1] = "/META-INF/" + appname + "/" + appname + ".xml";
            array[2] = "/META-INF/" + appname + "/" + KConstants.KENGINE_NAME + sep
                    + KConstants.KENGINE_VERSION + sep + appname + sep + appversion
                    + ".lic";
            array[3] = appusage;
            LOG.trace("Starting Kbuilderweb for app " + appname + " version " + appversion);
            WebApplicationContext appContext =  WebApplicationContextUtils.getWebApplicationContext(context);
            kbp = (KbuilderWebProxy) appContext.getBean("kbuilderWeb");
            kbp.init(array);
            kbp.showTooltipTitle(true);
            String[] metainfArray = {"liste_des_idcc_decembre_2015.xlsx"};
            String[] booksArray = {"liste_des_idcc_decembre_2015.xlsx"};
            kbp.loadWorkbooks(metainfArray,booksArray);
            LOG.trace("Deploying on " + context.getContextPath() + ",name  " + context.getServletContextName());            
            
        } catch (Exception e) {
            LOG.warning("Kbuilderweb startup failed " + e.getMessage());
            e.printStackTrace();
        }
    }    

    private void loadPlan(ServletContext context){
      try{
      WebApplicationContext appContext =  WebApplicationContextUtils.getWebApplicationContext(context);
      EtudeService etudeService = (EtudeService) appContext.getBean("etudeService");
      String envkey = etudeService.initPlan();
      System.setProperty(DueConstants.SESSION_PLAN, envkey);
      LOG.trace("DUE default plan loaded");
      } catch(Exception e){
        LOG.warning("DUE default plan error " + e.getMessage());
      }
    }
}
