package com.khresterion.due.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.inject.Inject;
import com.khresterion.web.kbuilder.KbuilderWebProxy;
import com.khresterion.kengine.bo.BoPredicates;
import com.khresterion.kengine.bo.BoUtilities;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.bo.Property;
import com.khresterion.kengine.calk.runtime.Calk;
import com.khresterion.kengine.calk.runtime.event.InfiniteLoopListener;
import com.khresterion.kengine.core.KBManager;
import com.khresterion.kengine.core.Reifications;
import com.khresterion.web.jpa.Session;

/**
 * manage Business Object actions (runtime changes)
 *
 */
@Service
@Scope("prototype")
public class RuntimeInstanceService {

    @Autowired
    KbuilderWebProxy kbp;

    @Inject
    private InfiniteLoopListener infiniteLoopListener;

    /**
     * default
     */
    public RuntimeInstanceService() {

    }

    /**
     * get whatever instance
     * 
     * @param instanceId
     * @param session
     * @return
     */
    public Instance getInstance(String instanceId, Session session) {

        return session.getInstanceManager().findRegisteredInstance(instanceId);
    }

    /**
     * Create whatever instance. In this version we should not use this method.
     * 
     * @param typeId
     * @param session
     * @return
     */
    public Instance createInstance(String typeId, String instanceId,
            Session session) {

        Instance instance = null;
        //synchronized (session) {
            startCalk(session);
            instance = session.getInstanceManager().createInstance(typeId,
                    instanceId);
        //}
        return instance;
    }

    /**
     * return all instances of a given type. More an utility to see if we have
     * multiple objects of the same type
     * 
     * @param typeId
     * @param session
     * @return
     */
    public List<Instance> getInstancesByType(String typeId, Session session) {
        Instance[] instanceList = session.getInstanceManager().findInstances(
                BoPredicates.isA(typeId));

        return Arrays.asList(instanceList);
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param value
     * @param session
     * @return
     * @throws Exception
     */
    public void updateProperty(String instanceId, String propertypath,
            String value, Session session) throws Exception {

        // startCalk(locSession);
        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);

            if (prop != null) {
                Reifications reif = Reifications.value();
                Object reifiedValue = reif.reifyFromSystem(prop.getType(),
                        value);
                BoUtilities.addValue(prop, reifiedValue);
            }
        //}
        // locSession.save();
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param session
     * @throws Exception
     */
    public void excludeProperty(String instanceId, String propertypath,
            Session session) throws Exception {

        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);

            if (prop != null) {
                prop.setPropertyExcluded(true);
            } else {
                // TODO
            }
        //}
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param session
     * @throws Exception
     */
    public void notExcludeProperty(String instanceId, String propertypath,
            Session session) throws Exception {

        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);

            if (prop != null) {
                prop.setPropertyExcluded(false);
            } else {
                // TODO
            }
        //}
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param session
     * @throws Exception
     */
    public void includeProperty(String instanceId, String propertypath,
            Session session) throws Exception {

        // startCalk(locSession);
        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);

            if (prop != null) {
                prop.setPropertyIncluded(true);
            } else {
                // TODO
            }
        //}
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param session
     * @throws Exception
     */
    public void notIncludeProperty(String instanceId, String propertypath,
            Session session) throws Exception {

        // startCalk(locSession);
        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);

            if (prop != null) {
                prop.setPropertyIncluded(false);
            } else {
                // TODO
            }
        //}
    }

    /**
     * shortcut method for included/excluded
     * 
     * @param instanecId
     * @param propertypath
     * @param session
     * @throws Exception
     */
    public void toggleProperty(String instanceId, String propertypath,
            Session session) throws Exception {

        // startCalk(locSession);
        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);

            if (prop != null) {
                if (prop.isIncluded()) {
                    prop.setPropertyIncluded(false);
                } else {
                    prop.setPropertyIncluded(true);
                }
            } else {
                // TODO
            }
        //}
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param session
     * @throws Exception
     */
    public void removeProperty(String instanceId, String propertypath,
            Session session) throws Exception {

        Instance instance = null;
        
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);
            //synchronized (instance) {
            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property prop = instance.findProperty(propertypath);
            removeValue(prop);
        //}
    }
    

    /**
     * @param instanceId
     * @param propertypath
     * @param session
     * @return
     */
    public Property getProperty(String instanceId, String propertypath, Session session) {
        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);
            Property prop = instance.findProperty(propertypath);
            return prop;
        //}
    }

    /**
     * @param instanceId
     * @param session
     * @return
     */
    public boolean removeInstance(String instanceId, Session session) {

        Instance instance = session.getInstanceManager()
                .findRegisteredInstance(instanceId);
        if (instance != null) {
            session.getInstanceManager().removeInstance(instance);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param instanceId
     * @param propertypath
     * @param linkedinstanceId
     * @param session
     * @return
     * @throws Exception
     */
    public Instance linkExistingInstance(String instanceId,
            String propertypath, String linkedinstanceId, Session session)
            throws Exception {

        // startCalk(locSession);
        Instance instance = null;
        Instance linked = null;
        Property prop = null;

        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);
            linked = session.getInstanceManager().findRegisteredInstance(
                    linkedinstanceId);

            if ((instance == null) || (linked == null))
                throw new Exception(
                        "Business Object Instance not found");

            prop = instance.findProperty(propertypath);

            if (prop != null) {
                BoUtilities.addValue(prop, linked);
            } else {
                System.out.println("Instance " + linked.getName()
                        + " not linked"); // debug
            }
        //}

        return instance;
    }

    /**
     * @param typeId
     * @param propertypath
     * @param instanceId
     * @param session
     * @return
     * @throws Exception
     */
    public Instance linkNewInstance(String typeId, String propertypath,
            String instanceId, Session session) throws Exception {

        Instance newInstance = createInstance(typeId,
                Integer.toString(session.getInstanceManager().size() + 1),
                session);
        linkExistingInstance(instanceId, propertypath, newInstance.getId(),
                session);

        return newInstance;
    }

    /**
     * remove a property and all dependent properties
     * 
     * @param instanceId
     * @param propertypath
     * @param value
     * @param dependentpathList
     * @param session
     * @return
     * @throws Exception
     */
    public Instance removePropertyGroup(String instanceId, String propertypath,
            String value, List<String> dependentpathList, Session session)
            throws Exception {

        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property property = instance.findProperty(propertypath);
            if (value == null) {
                removeValue(property);
            } else if (value.isEmpty()) {
                removeValue(property);
            } else {
                Reifications reif = Reifications.value();
                BoUtilities.addValue(property,
                        reif.reifyFromSystem(property.getType(), value));
            }

            for (String dependentpath : dependentpathList) {
                removeValue(instance.findProperty(dependentpath));
            }
        //}
        return instance;
    }

    /**
     * same as remove but set to default value
     * 
     * @param instanceId
     * @param propertypath
     * @param dependentpathList
     * @param session
     * @return
     * @throws Exception
     */
    public Instance resetPropertyGroup(String instanceId, String propertypath,
            List<String> dependentpathList, Session session)
            throws Exception {
        Instance instance = null;
        //synchronized (this) {
            instance = session.getInstanceManager().findRegisteredInstance(
                    instanceId);

            if (instance == null)
                throw new Exception(
                        "Business Object Instance not found");

            Property property = instance.findProperty(propertypath);
            if (property.getType().getDefaultValue() != null) {
                Reifications reif = Reifications.value();
                BoUtilities.addValue(property, reif.linearizeForSystem(property
                        .getType(), property.getType().getDefaultValue()));
            }

            for (String dependentpath : dependentpathList) {
                Property subproperty = instance.findProperty(dependentpath);
                if (subproperty != null) {
                    if (subproperty.getType().getDefaultValue() != null) {
                        Reifications reif = Reifications.value();
                        BoUtilities.addValue(subproperty, subproperty.getType()
                                .getDefaultValue());
                    }
                }
            }
        //}
        return instance;
    }
    
    /**
     * dirty hack
     * @return
     */
    public KBManager getKBManager(){
      return kbp.getKDataConfiguration().getKbManager();
    }

    /**
     * @param prop
     * @return
     */
    private boolean removeValue(Property prop) {

        if (prop != null) {
            if (prop.isIndexed()) {
                Object[] values = prop.getValues();
                if (values.length > 0)
                    prop.removeValue(values[values.length - 1]);
            } else {
                prop.removeValue(prop.getValue());
            }
            return true;
        } else {
            return false;
        }
    }

    private void startCalk(Session session) {

        Calk calk = session.getCalk();
        if (!calk.isRunning())
            calk.start();
        /*if (calk.isRunning()) {
            calk.addInfiniteLoopListener(getInfiniteLoopListener());
        }*/
    }

    private InfiniteLoopListener getInfiniteLoopListener() {
        return infiniteLoopListener;
    }

}
