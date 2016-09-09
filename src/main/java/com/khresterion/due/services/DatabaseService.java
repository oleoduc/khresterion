/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khresterion.kengine.bo.BoPredicates;
import com.khresterion.kengine.calk.runtime.Calk;
import com.khresterion.web.bo.EntityWrapper;
import com.khresterion.web.jpa.Session;
import com.khresterion.web.jpa.model.EnvironmentEntity;
import com.khresterion.web.jpa.model.InstanceEntity;
import com.khresterion.web.jpa.services.EnvironmentService;
import com.khresterion.web.jpa.services.InstanceService;
import com.khresterion.web.kbuilder.KbuilderWebProxy;

/**
 * @author khresterion
 *
 */
@Service
@Transactional
@Scope("prototype")
public class DatabaseService {

  @Autowired
  KbuilderWebProxy kbp;

  @Autowired
  DBSessionManager dsm;

  @Autowired
  InstanceService instanceService;

  @Autowired
  EnvironmentService envService;

  /**
   * @param entityId
   * @param currentTypeId
   * @return
   */
  public EntityWrapper loadKbuilder(final String entityId, final String typeId) {

    InstanceEntity instanceEntity = instanceService.findInstance(Integer.parseInt(entityId));

    Collection<EnvironmentEntity> envList = instanceEntity.findEnvironmentsByName(typeId);
    EnvironmentEntity env = null;

    if (envList.size() > 0) {      
      env = envList.iterator().next();
    } else {      
      env = new EnvironmentEntity(typeId);
      env.addInstance(instanceEntity);
    }
    Session locSession =
        kbp.getSessionFactory().createSession(kbp.getKDataConfiguration().getKbManager(), env);
    startCalk(locSession);
    dsm.register(locSession);

    return new EntityWrapper(locSession.findInstance(instanceEntity), dsm.envkey(locSession));
  }

  /**
   * import this instance in kbuilderType environement
   * @param entityId
   * @param kbuilderType
   * @param envkey
   * @return
   */
  public EntityWrapper importSingle(final String entityId, final String kbuilderType, final String envkey) {

    Session session = dsm.resolve(envkey);
    if (session == null) {
      return null;
    } else {      
      com.khresterion.kengine.bo.Instance[] kbuilderInstance = session.getInstanceManager().findInstances(BoPredicates.isA(kbuilderType));
      InstanceEntity kbEntity = session.findInstanceEntity(kbuilderInstance[0]);
      InstanceEntity instanceEntity = instanceService.findInstance(Integer.parseInt(entityId));
      
      Collection<EnvironmentEntity> envList = kbEntity.findEnvironmentsByName(kbuilderType);
      EnvironmentEntity env = envList.iterator().next();
      env.addInstance(instanceEntity);
      session.importEnvironment(env);
      
      return new EntityWrapper(session.findInstance(instanceEntity), envkey);
    }
  }

  /**
   * save current session with all entities
   * 
   * @param contextId
   * @return
   */
  public boolean saveSession(final String envkey) {

    try {
      Session session = dsm.resolve(envkey);
      if (session != null)
        session.save();
      return (session != null);
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  /**
   * @param envkey
   * @return
   */
  public int getSessionSize(String envkey) {
    Session session = dsm.resolve(envkey);

    return (session == null) ? 0 : session.getInstanceManager().size();
  }

  /**
   * @param envkey
   * @return
   */
  public boolean closeSession(final String envkey) {

    Session session = dsm.resolve(envkey);
    if (session != null) {
      dsm.cleanup(session);
      session = null;
    }

    return (session == null);
  }

  /**
   * get a specific entity
   * 
   * @param entityId
   * @return
   */
  public InstanceEntity getInstanceEntity(String entityId) {

    InstanceEntity ie = null;

    final int index = Integer.parseInt(entityId);
    if (index > 0) {

      ie = instanceService.findInstance(index);
    }
    return ie;
  }

  /**
   * will delete everything related to the entity remove entity directly
   * 
   * @param entityId
   * @param session
   * @return
   */
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

  private void startCalk(Session session) {

    Calk calk = session.getCalk();
    if (!calk.isRunning())
      calk.start();
  }
}
