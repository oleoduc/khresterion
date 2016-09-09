package com.khresterion.due.util;

import static org.junit.Assert.*;

import com.khresterion.kengine.bo.BoPredicates;
import com.khresterion.kengine.bo.BoUtilities;
import com.khresterion.kengine.bo.CoherenceObject;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.bo.InstanceManager;
import com.khresterion.kengine.environment.Environment;
import com.khresterion.web.kbuilder.KbuilderWebProxy;

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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import com.khresterion.kengine.core.KConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.khresterion.due.config.AppConfig.class,
    com.khresterion.due.config.WebConfig.class,
    com.khresterion.web.docbase.config.DocbaseWebConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class,
    WithSecurityContextTestExecutionListener.class})
@WebAppConfiguration
@ActiveProfiles(com.khresterion.due.config.ProfileConstants.HSQL_DB)
public class PlanLoaderTest {

  @Autowired
  KbuilderWebProxy kbp;

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

  @After
  public void tearDown() throws Exception {}

  public void testCopyFrom() {

    Environment env0 = new Environment(kbp.getKDataConfiguration().getKbManager());
    Environment env1 = new Environment(kbp.getKDataConfiguration().getKbManager());
    InstanceManager origin = new InstanceManager(env0);
    InstanceManager destination = new InstanceManager(env1);
    assertNotNull(origin);
    assertNotNull(destination);

    /* create mock instances */
    Instance docDue = origin.createInstance("Ontologie1_DocumentDUE0", "1");
    Instance entete = origin.createInstance("Ontologie1_00Entete", "2");
    Instance section = origin.createInstance("Ontologie1_01bPreambuleModificationDuRegime", "3");

    BoUtilities.addValue(
        docDue.findProperty("Ontologie1_Document/Ontologie1_estComposeDe/Ontologie1_Section0"),
        entete);
    BoUtilities.addValue(
        entete.findProperty("Ontologie1_Section0/Ontologie1_estSuiviDe/Ontologie1_Section0"),
        section);
    PlanLoader.copyFrom(origin, destination);
    assertTrue((origin.size() == destination.size()) && (destination.size() == 3));

    Instance destDocDue = destination.findInstances(BoPredicates.isA("Ontologie1_DocumentDUE0"))[0];
    Instance destEntete = destination.findInstances(BoPredicates.isA("Ontologie1_00Entete"))[0];
    assertNotNull(
        destDocDue.findProperty("Ontologie1_Document/Ontologie1_estComposeDe/Ontologie1_Section0")
            .getValue());
    assertTrue(
        destDocDue.findProperty("Ontologie1_Document/Ontologie1_estComposeDe/Ontologie1_Section0")
            .getValue().equals(destEntete));

    System.out.println(
        destDocDue.findProperty("Ontologie1_Document/Ontologie1_estComposeDe/Ontologie1_Section0")
            .getValue());

    Instance destSection = destination
        .findInstances(BoPredicates.isA("Ontologie1_01bPreambuleModificationDuRegime"))[0];
    System.out.println(destSection);
    assertNotNull(destEntete
        .findProperty("Ontologie1_Section0/Ontologie1_estSuiviDe/Ontologie1_Section0").getValue());
    System.out.println(destEntete
        .findProperty("Ontologie1_Section0/Ontologie1_estSuiviDe/Ontologie1_Section0").getValue());
    assertTrue(
        destEntete.findProperty("Ontologie1_Section0/Ontologie1_estSuiviDe/Ontologie1_Section0")
            .getValue().equals(destSection));
  }

  @Test
  public void testDirectCopy() {
    Environment env0 = new Environment(kbp.getKDataConfiguration().getKbManager());
    Environment env1 = new Environment(kbp.getKDataConfiguration().getKbManager());
    InstanceManager origin = new InstanceManager(env0);
    InstanceManager destination = new InstanceManager(env1);
    assertNotNull(origin);
    assertNotNull(destination);

    /* create mock instances */
    Instance sourceInstance = origin.createInstance("Ontologie1_DUE", "10");
    Instance targetInstance = destination.createInstance("Ontologie1_DUE", "20");
    BoUtilities.addValue(
        sourceInstance.findProperty(
            "Ontologie1_DUE/Ontologie1_estDecritPar/Ontologie1_Societe/Ontologie1_estDecritPar/Ontologie1_DenominationSociale"),
        "Societe");
    CoherenceObject coherence =
        BoUtilities.getIncludedOrAdvisedCoherenceObject(sourceInstance.findProperty(
            "Ontologie1_DUE/Ontologie1_estDecritPar/Ontologie1_Societe/Ontologie1_estDecritPar/Ontologie1_DenominationSociale"));
    assertNotNull(coherence);
    PlanLoader.directCopyInstance(sourceInstance, targetInstance);
    coherence = BoUtilities.getIncludedOrAdvisedCoherenceObject(targetInstance.findProperty(
        "Ontologie1_DUE/Ontologie1_estDecritPar/Ontologie1_Societe/Ontologie1_estDecritPar/Ontologie1_DenominationSociale"));
    assertNotNull(coherence);
    assertTrue(coherence.getValue().equals("Societe"));
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
