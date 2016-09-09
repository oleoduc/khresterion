/**
 * &copy; Khresterion 2015
 */
package com.khresterion.due.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextCleanupListener;

import com.khresterion.util.log.KhresterionLogger;

/**
 * @author khresterion khresterion
 *
 */
public class KengineContextCleanupListener extends ContextCleanupListener {

    
    private static final KhresterionLogger LOG = KhresterionLogger
            .getLogger(com.khresterion.due.config.KengineContextCleanupListener.class);
    
    /**
     * 
     */
    public KengineContextCleanupListener() {
        super();
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.ContextCleanupListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        
        unregisterDriver(event.getServletContext());
        super.contextDestroyed(event);                
    }

    private void unregisterDriver(ServletContext context) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if(driver.getClass().getClassLoader() == cl){
                    DriverManager.deregisterDriver(driver);
                    LOG.warning(String.format("deregistering jdbc driver: %s", driver));
                } else {
                    LOG.warning(String.format("not touching jdbc driver: %s", driver));
                }
            }
        } catch (SQLException e) {
            LOG.warning(String.format("Error deregistering driver %s", e));
        }
        LOG.warning(context.getServerInfo() + " Context destroyed " + context.getServletContextName());
    }  
}
