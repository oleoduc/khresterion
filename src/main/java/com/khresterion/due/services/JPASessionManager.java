package com.khresterion.due.services;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashBiMap;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.jpa.Session;
import com.khresterion.web.jpa.model.EnvironmentEntity;
import com.khresterion.web.kbuilder.KbuilderWebProxy;

/**
 * replacing the old {@Link SessionContext} handling manage {@Link kengine.spring.jpa.Session} for
 * web clients by giving an unique id
 *
 */
@Component
// @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class JPASessionManager implements DBSessionManager {

  private static final KhresterionLogger LOG =
      KhresterionLogger.getLogger(com.khresterion.due.services.JPASessionManager.class);

  private class SessionKeyGenerator {

    private char[] chars;

    public SessionKeyGenerator() {

      chars = "0123456789ABCDEF".toCharArray();
    }

    public String generateKey() {
      try {
        return RandomStringUtils.random(8, 0, 16, false, false, chars,
            SecureRandom.getInstance("SHA1PRNG"));
      } catch (NoSuchAlgorithmException e) {
        for (Provider provider : Security.getProviders()) {
          LOG.warning(provider.getInfo());
        }

        return null;
      }
    }
  }

  @Autowired
  KbuilderWebProxy kbp;

  SessionKeyGenerator keygen;

  HashBiMap<String, Session> lookup;

  /**
   * default
   */
  public JPASessionManager() {
    super();
    keygen = new SessionKeyGenerator();
    lookup = HashBiMap.create();
  }

  @Override
  public Session createReferentSession(String referentType) {
    Session session = kbp.getSessionFactory().createSession(
        kbp.getKDataConfiguration().getKbManager(), new EnvironmentEntity(referentType));

    register(session);

    return session;
  }

  @Override
  public Session createKbuilderSession(String kbuilderType) {
    Session session = kbp.getSessionFactory().createSession(
        kbp.getKDataConfiguration().getKbManager(), new EnvironmentEntity(kbuilderType));

    if (session == null) {
      LOG.warning("Could not create session");
    } else {
      register(session);
    }

    return session;
  }

  @Override
  public Session createStandardSession(String typeId) {
    Session session = kbp.getSessionFactory()
        .createSession(kbp.getKDataConfiguration().getKbManager(), new EnvironmentEntity(typeId));
    if (session == null) {
      LOG.warning("Could not create session");
    } else {
      register(session);
    }

    return session;
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * com.khresterion.due.services.DBSessionManager#createStandardSession(com.khresterion.web.jpa.
   * model.EnvironmentEntity)
   */
  @Override
  public Session createStandardSession(EnvironmentEntity env) {
    Session session =
        kbp.getSessionFactory().createSession(kbp.getKDataConfiguration().getKbManager(), env);

    if (session == null) {
      LOG.warning("Could not create session");
    } else {
      register(session);
    }

    return session;
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.web.panel.services.DBSessionManager#envkey(com.khresterion
   * .web.jpa.Session)
   */
  public String envkey(Session session) {
    return (String) lookup.inverse().get(session);
  }

  @Override
  public void register(Session session) {
    lookup.forcePut(keygen.generateKey(), session);

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.web.panel.services.DBSessionManager#resolve(java.lang .String)
   */
  public Session resolve(String envkey) {
    return ((envkey != null) && lookup.containsKey(envkey)) ? (Session) lookup.get(envkey) : null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.web.panel.services.DBSessionManager#cleanup(com.khresterion
   * .web.jpa.Session)
   */
  public Boolean cleanup(Session session) {
    if (session.getCalk() != null)
      session.getCalk().stop();

    String key = lookup.inverse().remove(session);

    if (key != null)
      removeAllInstances(session);

    return key != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.web.panel.services.DBSessionManager#cleanupByCode(java .lang.String)
   */
  public Boolean cleanupByCode(String key) {

    boolean result = false;

    if (key != null) {
      Session session = lookup.get(key);
      if (session != null) {

        if (session.getCalk() != null)
          session.getCalk().stop();
        removeAllInstances(session);
        session = null;
        result = true;
      } else {
        result = false;
      }
      lookup.remove(key);
    } else {
      result = false;
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.web.panel.services.DBSessionManager#cleanAll()
   */
  @PreDestroy
  public void cleanAll() {
    for (Session session : lookup.values()) {
      cleanup(session);
    }
    lookup.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.khresterion.web.panel.services.DBSessionManager#listAll()
   */
  public Set<String> listAll() {
    return lookup.keySet();
  }

  private boolean removeAllInstances(Session session) {

    Instance[] runningInstances = session.getInstanceManager().getInstances();
    for (Instance inst : runningInstances) {
      session.getInstanceManager().removeInstance(inst);
    }
    for (Instance insta : runningInstances) {
      if (insta != null)
        insta = null;
    }
    session.getInstanceManager().getEnvironment().getMemory().clear();
    session.getInstanceManager().getEnvironment().clear();
    session.getInstanceManager().getEnvironment().free();
    // kbp.getEntityManager().clear();
    return session.getInstanceManager().size() == 0;
  }
}
