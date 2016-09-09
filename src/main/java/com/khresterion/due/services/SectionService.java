/**
 * 
 */
package com.khresterion.due.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.khresterion.due.DueConstants;
import com.khresterion.due.Section;
import com.khresterion.due.model.DefaultSection;
import com.khresterion.kengine.bo.BoPredicates;
import com.khresterion.kengine.bo.BoUtilities;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.bo.Property;
import com.khresterion.kengine.calk.runtime.Calk;
import com.khresterion.kengine.core.Category;
import com.khresterion.kengine.core.KType;
import com.khresterion.kengine.core.RuleManager;
import com.khresterion.kengine.environment.Institution;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.jpa.Session;
import com.khresterion.web.jpa.model.EnvironmentEntity;
import com.khresterion.web.jpa.model.InstanceEntity;
import com.khresterion.web.jpa.services.EnvironmentService;
import com.khresterion.web.jpa.services.InstanceService;
import com.khresterion.web.settings.services.SettingsService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ndriama
 *
 */
@Service
@Transactional
@Scope("prototype")
public class SectionService {

  @Autowired
  DatabaseService dbService;

  @Autowired
  RuntimeInstanceService instanceService;

  @Autowired
  DBSessionManager dsm;

  @Autowired
  SettingsService settings;

  @Autowired
  EnvironmentService envService;

  @Autowired
  InstanceService entityService;

  /**
   * @return
   */
  public List<Section> listAll() {

    List<InstanceEntity> allEntities = entityService.findByPathNative(DueConstants.PATH_CRITERIA);
    List<Section> sectionList = Lists.newArrayList();
    for (InstanceEntity entity : allEntities) {
      Section newSection = new DefaultSection();
      newSection.setId(Integer.toString(entity.getId()));
      newSection.setName(entity.getTypeId());
      newSection.setLink("etude/edit/section?type_id=" + newSection.getName() + "&section_id="
          + newSection.getId());
      sectionList.add(newSection);
    }
    Collections.sort(sectionList);

    return ImmutableList.copyOf(sectionList);

  }

  /**
   * All Ontologie1_Section00 types
   * 
   * @return
   */
  public List<String> listSectionNames() {
    KType[] types = instanceService.getKBManager().getAllEntityTypes();
    List<String> idList = Lists.newArrayList();
    for (KType ktype : types) {
      idList.add(ktype.getId());
    }
    return idList;
  }

  /**
   * @param id
   * @return
   */
  public InstanceEntity display(final String id) {

    return dbService.getInstanceEntity(id);
  }

  /**
   * @param id
   * @return
   */
  public boolean remove(final String id) {

    return dbService.removeInstanceEntity(id);
  }

  /**
   * @param id
   * @param typeId
   * @return
   */
  public EntityWrapper load(final String id, final String typeId) {
    boolean isValidType = false;
    for (String types : DueConstants.SECTION_TYPES) {
      if (types.equals(typeId)) {
        isValidType = true;
      }
    }
    if (isValidType) {
      EntityWrapper wrapper = dbService.loadKbuilder(id, typeId);
      // clearRedaction(wrapper.getBo());
      return wrapper;
    } else {
      return null;
    }
  }

  /**
   * 
   * @param id
   * @param envkey
   * @return
   */
  public Integer update(final String id, final String value) {
    try {

      InstanceEntity entity = dbService.getInstanceEntity(id);
      //System.out.println(value);      
      return entityService.rawUpdate(entity.getId(),
          DueConstants.SECTION_TEXTE.replace(DueConstants.TYPE_SECTION + "/", StringUtils.EMPTY),
          value);

    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }


  /**
   * @param typeId
   * @return
   */
  public boolean create(final String typeId) {

    boolean isValidType = false;
    for (String types : DueConstants.SECTION_TYPES) {
      if (types.equals(typeId)) {
        isValidType = true;
      }
    }
    if (isValidType) {
      // create only if new section
      Page<InstanceEntity> pagedSearch = dbService.getPagedInstanceEntitiesByType(typeId, 0, 5);
      if (pagedSearch.getContent().isEmpty()) {
        Session session = dsm.createStandardSession(typeId);
        Instance section = instanceService.createInstance(typeId, "1", session);
        BoUtilities.addValue(section.findProperty(DueConstants.SECTION_TEXTE), "TODO");
        dbService.saveSession(dsm.envkey(session));
        dbService.closeSession(dsm.envkey(session));
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * @param id
   * @return
   */
  public String createPlan() {

    EnvironmentEntity env = new EnvironmentEntity(DueConstants.TYPE_DOCUMENTDUE);
    Session session = dsm.createStandardSession(env);
    Instance docDue =
        session.getInstanceManager().createInstance(DueConstants.TYPE_DOCUMENTDUE, "1");
    // create an instance for each sections
    List<InstanceEntity> entityList = entityService.findByPathNative(DueConstants.PATH_CRITERIA);
    for (InstanceEntity entity : entityList) {
      session.importEnvironment(findEnvironment(entity));
    }
    Instance[] headSection =
        session.getInstanceManager().findInstances(BoPredicates.isA(DueConstants.SECTION_TYPES[0]));
    try {
      instanceService.linkExistingInstance(docDue.getId(), DueConstants.LINK_PLAN_SECTION,
          headSection[0].getId(), session);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (!session.getCalk().isRunning()) {
      session.getCalk().start();
    }
    return dsm.envkey(session);
  }

  /**
   * @param id
   * @return
   */
  public String loadPlan(final String id) {
    EnvironmentEntity env = envService.findEnvironment(Integer.parseInt(id));
    if (env == null) {
      return null;
    } else {
      Session session = dsm.createStandardSession(env);      
      startCalk(session);      
      /*
       * check if a section is missing from environment - then add missing section
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
        /*System.out.println("Loading section " + section.getTypeId());*/
      }
           
      return dsm.envkey(session);
    }
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
   * @return
   */
  public List<EnvironmentEntity> listPlan() {

    Page<EnvironmentEntity> page =
        envService.findByName(DueConstants.TYPE_DOCUMENTDUE, new PageRequest(0, 50));

    return page.getContent();
  }

  /**
   * @param planId
   * @return
   */
  public String removePlan(final String planId) {
    try {
      EnvironmentEntity arg0 = envService.findEnvironment(Integer.parseInt(planId));
      envService.removeEnvironment(arg0);
      return "Plan removed";
    } catch (Exception e) {
      return "Plan not removed " + e.getMessage();
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
