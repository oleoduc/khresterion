/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.khresterion.due.DueConstants;
import com.khresterion.due.util.DocxUtility;
import com.khresterion.due.util.PlanLoader;
import com.khresterion.due.util.RuntimeVariableResolver;
import com.khresterion.kengine.bo.BoPredicates;
import com.khresterion.kengine.bo.BoUtilities;
import com.khresterion.kengine.bo.CoherenceObject;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.bo.Property;
import com.khresterion.kengine.bo.Reifications;
import com.khresterion.kengine.calk.runtime.Calk;
import com.khresterion.kengine.core.Category;
import com.khresterion.kengine.core.RuleManager;
import com.khresterion.kengine.environment.Institution;
import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.docbase.TParser;
import com.khresterion.web.docbase.services.ParserService;
import com.khresterion.web.docbase.services.VariableService;
import com.khresterion.web.docbase.util.RandomIdGenerator;
import com.khresterion.web.jpa.Session;
import com.khresterion.web.jpa.model.EnvironmentEntity;
import com.khresterion.web.jpa.model.InstanceEntity;
import com.khresterion.web.jpa.services.EnvironmentService;
import com.khresterion.web.jpa.services.InstanceService;
import com.khresterion.web.kbuilder.KbuilderWebProxy;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author khresterion
 *
 */
@Service
@Transactional
@Scope("prototype")
public class EtudeService { 

  private static final String ALIGN_CENTER = "center";

  private static final String ALIGN_LEFT = "left";

  private static final String DOCX = ".docx";

  private static final String HTML = ".html";

  private static final String UNDERSCORE = "_";

  private static final KhresterionLogger LOG = KhresterionLogger.getLogger(EtudeService.class);

  @Autowired
  RuntimeInstanceService ris;

  @Autowired
  DatabaseService dbService;

  @Autowired
  KbuilderWebProxy kbp;

  @Autowired
  DBSessionManager dsm;

  @Autowired
  InstanceService instanceService;

  @Autowired
  EnvironmentService envService;

  @Autowired
  ParserService parserService;

  @Autowired
  VariableService varService;

  @Autowired
  InstanceService entityService;

  private TParser parser;

  /**
   * 
   */
  public EtudeService() {}


  /**
   * @param id
   * @param typeId
   * @param planIndex
   * @return
   */
  public EntityWrapper create(final String id, final String typeId, final String planIndex) {
    try {
      LOG.trace("Create environment");
      Session session = dsm.createStandardSession(typeId);
      importPlan(dsm.envkey(session));      
      /* 
       * startCalk(session); ris.createInstance already start the Calk runtime
       * */
      LOG.trace("Create instance");
      EntityWrapper wrapper = new EntityWrapper(
          ris.createInstance(typeId, DueConstants.DEFAULT_DUE_ID, session), dsm.envkey(session));
      /* import sections from saved plan */      

      linkDocumentDUE(session, DueConstants.DEFAULT_DUE_ID);
      LOG.trace("DocumentDUE0 linked");
      return wrapper;
    } catch (Exception e) {
      e.printStackTrace();
      LOG.warning(e.getMessage());
      return null;
    }
  }

  /**
   * @param entityId
   * @param typeId
   * @return
   * @throws Exception
   */
  public EntityWrapper load(final String entityId, final String typeId) throws Exception {
    LOG.trace("Create environment");
    Session session = dsm.createStandardSession(typeId);
    startCalk(session);
    /*
     * import sections from saved plan 
     * */
    importPlan(dsm.envkey(session));
    
    /* load entityId */
    InstanceEntity instanceEntity = instanceService.findInstance(Integer.parseInt(entityId));

    Collection<EnvironmentEntity> envList = instanceEntity.findEnvironmentsByName(typeId);
    EnvironmentEntity env = null;

    if (envList.size() > 0) {

      env = envList.iterator().next();
    } else {

      env = new EnvironmentEntity(typeId);
      env.addInstance(instanceEntity);
    }
    LOG.trace("Load Instance");
    /* import env into current session */
    session.importEnvironment(env);
    
    Instance dueInstance = session.findInstance(instanceEntity);
    linkDocumentDUE(session, dueInstance.getId());
    LOG.trace("DocumentDUE0 linked");
    
    return new EntityWrapper(dueInstance, dsm.envkey(session));
  }

  /**
   * @return
   */
  public String initPlan() {
    LOG.trace("initialize Plan...");
    Page<EnvironmentEntity> envPage =
        envService.findByName(DueConstants.TYPE_DOCUMENTDUE, new PageRequest(0, 2));
    EnvironmentEntity env = envPage.getContent().get(0);
    if (env == null) {
      return null;
    } else {
      Session session = dsm.createStandardSession(env);
      /*
       * - check if a section is missing from environment - then add missing section
       */
      List<InstanceEntity> sectionList = entityService.findByPathNative(DueConstants.PATH_CRITERIA);

      Instance[] memoryList =
          session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_SECTION));
      StringBuilder sbMeme = new StringBuilder();
      for (Instance memInstance : memoryList) {
        // clearRedaction(memInstance);
        sbMeme.append(memInstance.getType().getId()).append(":");
      }

      for (InstanceEntity section : sectionList) {
        if (sbMeme.indexOf(section.getTypeId()) < 0) {
          session.importEnvironment(findEnvironment(section));
        }
      }
      LOG.trace("initialize Plan OK, instances created " + session.getInstanceManager().size());
      return dsm.envkey(session);
    }
  }

  /**
   * @param envkey
   * @return
   */
  public String reloadPlan(final String envkey) {

    /* remove former enviromnent */
    String realKey = System.getProperty(DueConstants.SESSION_PLAN);
    if(dbService.closeSession(realKey)) {
      LOG.trace("Closed old PLAN " + realKey);
      /* save and remove current runtime environment */
      if(dbService.saveSession(envkey)){        
      dbService.closeSession(envkey);
      LOG.trace("Closing current PLAN " + envkey);          
      /* load a new plan */
      String newKey = initPlan();
      System.setProperty(DueConstants.SESSION_PLAN, newKey);
      LOG.trace("new PLAN created " + newKey);
      
      return newKey;
      } else {
        return realKey;
      }
    } else {
      return envkey;
    }
  }


  /**
   * @param docId
   * @param planIndex
   * @param session
   * @throws Exception
   */
  private void importPlan(String envkey) throws Exception {
    LOG.trace("Starting import PLAN for " + envkey);
    Session session = dsm.resolve(envkey);

    Page<EnvironmentEntity> envPlan =
        envService.findByName(DueConstants.TYPE_DOCUMENTDUE, new PageRequest(0, 2));
    if ((envPlan == null) ? false : envPlan.hasContent()) {
      /*
       * former version too heavy EnvironmentEntity firstPlan = envPlan.getContent().get(planIndex);
       * session.importEnvironment(firstPlan);
       */

      Session planSession = dsm.resolve(System.getProperty(DueConstants.SESSION_PLAN));
      PlanLoader.copyFrom(planSession.getInstanceManager(), session.getInstanceManager());
      /*
       * Instance[] instanceList = session.getInstanceManager()
       * .findInstances(BoPredicates.isA(DueConstants.TYPE_DOCUMENTDUE)); Instance docDue =
       * instanceList[0];
       * 
       * link DUE to DocumentDUE0 moved outside this
       * ris.linkExistingInstance(DueConstants.DEFAULT_DUE_ID, DueConstants.LINK_ETUDE_PLAN,
       * docDue.getId(), session);
       */
      LOG.trace("Imported PLAN in runtime " + dsm.envkey(planSession));
    } else {
      /*
       * create a dummy plan with only the first section
       * ris.linkExistingInstance(DueConstants.DEFAULT_DUE_ID, DueConstants.LINK_ETUDE_PLAN,
       * docDue.getId(), session);
       */
      Instance docDue = ris.createInstance(DueConstants.TYPE_DOCUMENTDUE, "2", session);

      String newId = Integer.toString(Integer.parseInt(docDue.getId()) + 2);
      ris.createInstance(DueConstants.SECTION_TYPES[0], newId, session);
      try {
        ris.linkExistingInstance(docDue.getId(), DueConstants.LINK_PLAN_SECTION, newId, session);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @param session
   * @throws Exception
   */
  private void linkDocumentDUE(Session session, final String dueId) throws Exception {
    Instance[] instanceList =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_DOCUMENTDUE));
    ris.linkExistingInstance(dueId, DueConstants.LINK_ETUDE_PLAN, instanceList[0].getId(), session);
  }

  /**
   * @param typeSection
   * @param planId
   * @param envkey
   * @return
   */
  public EntityWrapper createSection(final String typeSection, final String parentId,
      final String path, final String envkey) {

    Session session = dsm.resolve(envkey);
    if (session == null) {
      return new EntityWrapper(null, envkey);
    } else {
      Instance parent = ris.getInstance(parentId, session);

      Property target = parent.findProperty(path);
      Instance targetInstance = (target == null) ? null : (Instance) target.getValue();
      if (targetInstance == null) {
        try {
          targetInstance = ris.createInstance(typeSection,
              Integer.toString(session.getInstanceManager().size() + 1), session);
          ris.linkExistingInstance(parent.getId(), path, targetInstance.getId(), session);
        } catch (Exception e) {
          LOG.warning(e.getMessage());
          // e.printStackTrace();
        }
      } else {
        LOG.trace("Section found " + targetInstance.getId());
      }

      return new EntityWrapper(targetInstance, envkey);
    }
  }

  /**
   * @param envkey
   * @return
   */
  public String getHeadSectionId(String envkey) {
    List<Instance> docdue =
        ris.getInstancesByType(DueConstants.TYPE_DOCUMENTDUE, dsm.resolve(envkey));
    if (docdue != null) {
      Instance headSection =
          (Instance) docdue.get(0).findProperty(DueConstants.LINK_PLAN_SECTION).getValue();

      return (headSection == null) ? null : headSection.getId();
    } else {
      return null;
    }
  }

  /**
   * @param id
   * @param planId
   * @param envkey
   * @return
   */
  public EntityWrapper removeSection(final String instanceId, final String planId,
      final String envkey) {
    Session session = dsm.resolve(envkey);
    if (session == null || instanceId == null) {
      return null;
    } else {
      ris.removeInstance(instanceId, session);
      return new EntityWrapper(null, envkey);
    }
  }

  /**
   * @param typeEtude
   * @param envkey
   * @return
   */
  public EntityWrapper display(final String typeId, final String instanceId, final String envkey) {
    Session session = dsm.resolve(envkey);

    if (session == null) {
      return null;
    } else {
      return new EntityWrapper(ris.getInstance(instanceId, session), envkey);
    }
  }

  public Boolean removeInstanceEntity(final String entityId) {

    InstanceEntity instanceEntity = instanceService.findInstance(Integer.parseInt(entityId));

    if (instanceEntity != null) {
      envService.removeEnvironmentsOfInstance(instanceEntity);

      return true;
    } else {
      return false;
    }
  }

  /**
   * Pageable query
   * 
   * @param typeId
   * @param page
   * @param page_size
   * @return
   */
  public Page<InstanceEntity> getPagedInstanceEntitiesByType(final String typeId,
      final int page_number, final int page_size) {

    return instanceService.findAllByType(typeId, new PageRequest(page_number, page_size));
  }


  /**
   * @param envkey
   * @return
   */
  public boolean close(final String envkey) {
    Session session = dsm.resolve(envkey);
    /* beware if session has already been closed */
    if (session == null) {
      return false;
    }
    Instance[] sectionList =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_SECTION));
    for (Instance section : sectionList) {
      session.getInstanceManager().removeInstance(section);
    }
    Instance[] docList =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_DOCUMENTDUE));
    for (Instance doc : docList) {
      session.getInstanceManager().removeInstance(doc);
    }
    return dbService.closeSession(envkey);
  }

  /**
   * @param envkey
   * @return
   */
  public boolean save(final String envkey, final String entityId) {
    Session session = dsm.resolve(envkey);
    /* beware if session has already been closed */
    if (session == null) {
      return false;
    }
    Instance[] sectionList =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_SECTION));
    for (Instance section : sectionList) {
      session.getInstanceManager().removeInstance(section);
    }
    Instance[] docList =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_DOCUMENTDUE));
    for (Instance doc : docList) {
      session.getInstanceManager().removeInstance(doc);
    }
    /*
     * synchronise BO and JPA to avoid persistence issue if there is an entityId
     */
    if (entityId == null) {
      dbService.saveSession(envkey);
      LOG.trace("Entity ID is NULL");
    } else {
      String newKey = synchroniseEntity(entityId,
          session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_ETUDE)));
      if (dbService.saveSession(newKey)) {
        dbService.closeSession(newKey);
        LOG.trace("synchro session saved and closed");
      } else {
        dbService.closeSession(newKey); // to force synchro
        LOG.trace("synchro session closed without saving");
      }
    }
    return dbService.closeSession(envkey);
  }

  /**
   * open the real environment and copy current DUE fields
   * 
   * @param entityId
   * @param dueInstances
   * @return
   */
  private String synchroniseEntity(String entityId, Instance[] dueInstances) {

    /* load entityId */
    InstanceEntity instanceEntity = instanceService.findInstance(Integer.parseInt(entityId));

    Collection<EnvironmentEntity> envList =
        instanceEntity.findEnvironmentsByName(DueConstants.TYPE_ETUDE);
    EnvironmentEntity env = envList.iterator().next();
    Session locSession =
        kbp.getSessionFactory().createSession(kbp.getKDataConfiguration().getKbManager(), env);
    dsm.register(locSession);
    startCalk(locSession);
    /* copy value */
    PlanLoader.directCopyInstance(dueInstances[0], locSession.findInstance(instanceEntity));
    LOG.trace("synchronise entity " + entityId + " and due Instance " + dueInstances[0].getLabel());

    return dsm.envkey(locSession);
  }

  /**
   * @param envkey
   * @param string
   * @return
   * @throws IOException
   */
  public File generateDoc(final String envkey) throws IOException {

    Session session = dsm.resolve(envkey);
    Instance[] docDueList =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_DOCUMENTDUE));
    Instance contract = docDueList[0];
    if (contract == null) {
      return null;
    } else {
      File file = new File(System.getProperty("java.io.tmpdir") + "/"
          + randomName(DueConstants.TYPE_DOCUMENTDUE) + HTML);      
      if (!file.exists()) {
        file.createNewFile();
      }

      assembleDocV2(contract, file, session);
      File outFile = new File(file.getAbsolutePath().replace(HTML, DOCX));
      try {

        /* images */
        DocxUtility docxUtil = new DocxUtility();
        docxUtil.htmlToWord(file, outFile, true);
        docxUtil = null;
      } catch (Docx4JException e) {
        LOG.warning(e.getMessage());
        outFile = new File(file.getAbsolutePath().replace(HTML, DOCX));
        outFile.createNewFile();
      }

      file.delete();

      return outFile;
    }
  }

  /**
   * @param instance
   * @param file
   * @throws IOException
   */
  private void assembleDoc(Instance instance, File file, Session session) throws IOException {
    parser = parserService.create("{{", "}}");
    Map<String, String> resolvedVariables =
        RuntimeVariableResolver.resolve(varService.toMap(), session.getInstanceManager());

    Property redactionField = instance.findProperty(DueConstants.PLAN_REDACTION);
    FileOutputStream fos = new FileOutputStream(file);
    CoherenceObject coherence = BoUtilities.getIncludedOrAdvisedCoherenceObject(redactionField);

    if (coherence != null) {

      File tempFile = File.createTempFile("wxyz11", HTML);

      byte[] tmpContent = nestHTML(Reifications.value().linearizeForSystem(coherence));
      parserService.execute(parser, tmpContent, fos, resolvedVariables);
      fos.write(toBytes(tempFile));

      tempFile.delete();
    }
    fos.close();
  }
  
  /**
   * @throws IOException
   */
  private void assembleDocV2(Instance instance, File file, Session session) throws IOException {
    parser = parserService.create("{{", "}}");
    Map<String, String> resolvedVariables =
        RuntimeVariableResolver.resolve(varService.toMap(), session.getInstanceManager());
    
    /*assemble everything until we finalize the tree walking algorithm*/
    StringBuilder sb = new StringBuilder("TEST");
    
    Instance[] instanceList = session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.TYPE_SECTION));
    for(Instance section: instanceList){
      LOG.trace("Parsing section " + section.getLabel());
      Property textField = section.findProperty(DueConstants.SECTION_TEXTE);
      Property includeField = section.findProperty(DueConstants.SECTION_EST_INCLUSE);
      Property presentField = section.findProperty(DueConstants.SECTION__EST_PRESENTE);
      CoherenceObject objectText = BoUtilities.getIncludedOrAdvisedCoherenceObject(textField);
      CoherenceObject isIncluded = BoUtilities.getIncludedOrAdvisedCoherenceObject(includeField);
      CoherenceObject isPresent = BoUtilities.getIncludedOrAdvisedCoherenceObject(presentField);
      if(((isIncluded != null)?isIncluded.getValue().equals("OUI"):false) && 
          ((isPresent != null)?isPresent.getValue().equals("OUI"):false)){
        LOG.trace("Text present " + section.getLabel());
        sb.append((String) objectText.getValue());
      } else {
        LOG.trace("Text ignored " + section.getLabel());
      }
    }
    /*write final file*/
    FileOutputStream fos = new FileOutputStream(file);
    
    BoUtilities.addValue(instance.findProperty(DueConstants.PLAN_REDACTION), sb.toString());
    CoherenceObject coherence = BoUtilities.getIncludedOrAdvisedCoherenceObject(instance.findProperty(DueConstants.PLAN_REDACTION));

    if (coherence != null) {

      File tempFile = File.createTempFile("wxyz11", HTML);

      byte[] tmpContent = nestHTML(Reifications.value().linearizeForSystem(coherence));
      parserService.execute(parser, tmpContent, fos, resolvedVariables);
      fos.write(toBytes(tempFile));

      tempFile.delete();
    }
    fos.close();
  }

  /**
   * Nesting html tags before and after conditions
   * 
   * @param content
   * @return
   */
  private byte[] nestHTML(String strContent) throws FileNotFoundException, IOException {
    StringBuilder content = new StringBuilder(strContent);

    Pattern pattern = Pattern.compile("<p>#end</p>");
    Matcher matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#end"));
    }
    matcher.reset();

    pattern = Pattern.compile("<strong>#end</strong>");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#end"));
    }
    matcher.reset();

    pattern = Pattern.compile("#end</p>");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#end"));
    }
    matcher.reset();

    pattern = Pattern.compile("</p>#end");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#end"));
    }
    matcher.reset();

    pattern = Pattern.compile("<p>#if\\((.*?)\\)");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#if\\($1\\)"));
    }
    matcher.reset();

    pattern = Pattern.compile("#if\\((.*?)\\)</p>");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#if\\($1\\)"));
    }
    matcher.reset();

    pattern = Pattern.compile("<p>#else</p>");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("#else"));
    }
    matcher.reset();

    pattern = Pattern.compile("</p>");
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      content = new StringBuilder(matcher.replaceAll("</p>\n"));
    }
    matcher.reset();

    String finalhtml = content.toString().replaceAll("<em>", StringUtils.EMPTY).replaceAll("</em>",
        StringUtils.EMPTY);

    return finalhtml.getBytes(Charsets.UTF_8);

  }

  /**
   * @param number
   * @return
   */
  private String formatNumber(double number) {
    NumberFormat nformat = NumberFormat.getInstance(Locale.FRANCE);
    DecimalFormat df = (DecimalFormat) nformat;
    df.applyPattern("###,###.00");

    return df.format(number);
  }

  /**
   * @param format
   * @return
   */
  private String randomName(final String typeId) {
    StringBuilder randN = new StringBuilder(typeId);

    RandomIdGenerator rig = new RandomIdGenerator();
    randN.append(UNDERSCORE + rig.generateKey(6));
    Calendar calendar = Calendar.getInstance();
    randN.append(Long.toString(calendar.getTimeInMillis()));

    return randN.toString();
  }

  /**
   * @param inputFile
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  private byte[] toBytes(File inputFile) throws FileNotFoundException, IOException {
    return IOUtils.toByteArray(new FileInputStream(inputFile));
  }

  /**
   * clear Redaction field that has been saved in DB
   * 
   * @param memInstance
   */
  private void clearRedaction(Instance section) {
    synchronized (section) {
      Property redacProperty = section.findProperty(DueConstants.SECTION_REDACTION);
      redacProperty.removeValue(redacProperty.getValue());
    }
  }

  /**
   * @param entity
   * @return
   */
  private EnvironmentEntity findEnvironment(InstanceEntity entity) {
    Collection<EnvironmentEntity> coll = entity.findEnvironmentsByName(entity.getTypeId());
    Iterator<EnvironmentEntity> iter = coll.iterator();

    return (iter.hasNext()) ? iter.next() : null;
  }

  private void startCalk(Session session) {

    Calk calk = session.getCalk();
    if (!calk.isRunning()){
      
      RuleManager ruleManager = calk.getEnvironment().getKbManager().findRuleManagerByName("FormulaManager1");
      Institution formulaManager = calk.getEnvironment().findInstitution(ruleManager );      
       if(formulaManager.getLabel().equals("InstitutionDocument")){
         Category category = formulaManager.getRuleManager().findCategoryByName("Redaction");
        formulaManager.setEnabled(category, false);
       }
      
      calk.start();
    }
  }
}
