/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.services;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.khresterion.due.DueConstants;
import com.khresterion.due.config.ProfileConstants;
import com.khresterion.kengine.core.KConstants;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.kbuilder.KbuilderWebProxy;

/**
 * @author khresterion
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.khresterion.due.config.AppConfig.class,
    com.khresterion.due.config.WebConfig.class,
    com.khresterion.web.docbase.config.DocbaseWebConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class})
@WebAppConfiguration
@ActiveProfiles(ProfileConstants.MYSQL_DB)
public class DatabaseServiceTest {

    @Autowired
    KbuilderWebProxy kbp;
    
    @Autowired
    EtudeService etudeService;
    
    @Autowired
    DatabaseService dbService;
    
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
          .apply(SecurityMockMvcConfigurers.springSecurity()).build();
      MockHttpSession session = new MockHttpSession();
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.setSession(session);
      ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

      RequestContextHolder.setRequestAttributes(requestAttributes);
      MockHttpServletResponse response = new MockHttpServletResponse();
      initKB();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.khresterion.due.services.DatabaseService#saveSession(java.lang.String)}.
     */    
    public void testSaveSession() {
        EntityWrapper ew = etudeService.create("1", DueConstants.TYPE_ETUDE,null);
        assertNotNull(ew);
        dbService.saveSession(ew.getEnvkey());
    }
    
    @Test
    public void testLoadSaveSession() {
        EntityWrapper ew  = dbService.loadKbuilder("541", DueConstants.TYPE_ETUDE);
        assertNotNull(ew);
        dbService.saveSession(ew.getEnvkey());
    }

    private void initKB() throws Exception {
        String[] array = new String[4];
        String appname = "due";
        String appversion = "0.5";
        String appusage = "TestContrat";
        array[0] = appname;
        array[1] = "/META-INF/" + appname + "/" + appname + ".xml";
        array[2] = "/META-INF/" + appname + "/" + KConstants.KENGINE_NAME + "-"
            + KConstants.KENGINE_VERSION + "-" + appname + "-" + appversion + ".lic";
        array[3] = appusage;
        kbp.init(array);
      }    
}
