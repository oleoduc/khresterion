package com.khresterion.due.services;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.khresterion.web.jpa.Session;
import com.khresterion.web.jpa.model.EnvironmentEntity;

@Service
public interface DBSessionManager {

    /**
     * create a new Session from the referent use another method to create
     * another type of session
     * 
     * @return
     */

    public Session createReferentSession(String referentType);

    /**
     * create a session from the kbuilder use another method to create another
     * type of session
     * 
     * @return
     */

    public Session createKbuilderSession(String kbuilderType);

    /**
     * create a generic session for a single instance use another method to
     * create another type of session
     * 
     * @param typeId
     * @return
     */

    public Session createStandardSession(String typeId);
    
    /**
     * @param env
     * @return
     */
    public Session createStandardSession(EnvironmentEntity env);

    /**
     * return the envkey for a given session
     * 
     * @param session
     * @return
     */
    public String envkey(Session session);

    /**
     * return the session for a given envkey
     * 
     * @param envkey
     * @return
     */
    public Session resolve(String envkey);

    /**
     * @param session
     */
    public void register(Session session);

    /**
     * removing a session can be useful for memory
     * 
     * @param session
     */
    public Boolean cleanup(Session session);

    /**
     * removing a session by key
     * 
     * @param envkey
     * @return
     */
    public Boolean cleanupByCode(String key);

    /**
     * clean all sessions
     */
    public void cleanAll();

    /**
     * return all envkeys
     * 
     * @return
     */
    public Set<String> listAll();

}