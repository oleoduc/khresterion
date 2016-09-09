/**
 * 
 */
package com.khresterion.due.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.khresterion.kengine.bo.BoUtilities;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.bo.InstanceManager;
import com.khresterion.kengine.bo.Property;
import com.khresterion.kengine.core.KType;

/**
 * Copy instances from one instance manager to another. They keep the same Id to preserve link
 * between instances;
 * 
 * @author ndriama
 *
 */
public class PlanLoader {


  /**
   * @param origin
   * @param destination
   */
  public static void copyFrom(InstanceManager origin, InstanceManager destination) {

    Instance[] instanceList = origin.getInstances();
    for (Instance instance : instanceList) {
      copyInstance(instance, destination, instance.getId());
    }
  }

  /**
   * @param instance
   * @param destination
   * @param string
   */
  public static Instance copyInstance(Instance sourceInstance, InstanceManager destination,
      String instanceId) {
    Instance targetInstance =
        destination.createInstance(sourceInstance.getType().getId(), instanceId);

    List<String> propertyPaths = Lists.newArrayList();
    Property[] propertyList = sourceInstance.getProperties();
    for (Property property : propertyList) {
      propertyPaths.add(BoUtilities.createSerializedPath(property));
    }
    BoUtilities.copyOutPropertyValues(sourceInstance, targetInstance, propertyPaths);
    propertyPaths.clear();
    return targetInstance;
  }

  /**
   * @param sourceInstance
   * @param targetInstance
   * @return
   */
  public static void directCopyInstance(Instance sourceInstance, Instance targetInstance) {

    Property[] propertyList = sourceInstance.getProperties();
    for (Property property : propertyList) {
      
      if (property.isAggregate()) {
        /*System.out.println("aggregate level 1");*/
        copyProperty(property, sourceInstance, targetInstance);
      } else if (!property.isEntity()) {
        Property targetProperty =
            targetInstance.findProperty(BoUtilities.createSerializedPath(property));
        BoUtilities.addValue(targetProperty, property.getValue());
        /*System.out
        .println(property.getType().getId() + "\n -> " + targetProperty.getType().getId() + " value "+ property.getValue());*/
      } else {
        /*System.out.println("entity passed...");*/
      }
    }
  }

  /**
   * @param aggregate
   * @param sourceInstance
   * @param targetInstance
   */
  private static void copyProperty(Property aggregate, Instance sourceInstance,
      Instance targetInstance) {
    Property[] propertyList = aggregate.getProperties();
    for (Property property : propertyList) {
      
      if (property.isAggregate()) {
        
        copyProperty(property, sourceInstance, targetInstance);
      } else if (!property.isEntity()) {
        Property targetProperty =
            targetInstance.findProperty(BoUtilities.createSerializedPath(property));
        BoUtilities.addValue(targetProperty, property.getValue());
        /*System.out
            .println(property.getType().getId() + "\n -> " + targetProperty.getType().getId() + " value "+ property.getValue());*/
      } else {
        /*System.out.println("entity passed...");*/
      }
    }
  }

}
