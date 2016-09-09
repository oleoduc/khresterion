package com.khresterion.due.util;

import com.khresterion.web.jpa.model.CoherenceObjectEntity;
import com.khresterion.web.jpa.model.InstanceEntity;
import com.khresterion.web.jpa.model.PropertyEntity;

public class SectionUtility {

  public SectionUtility() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @param entity
   * @param path
   * @return
   */
  public static Object getPropertyValue(final InstanceEntity entity, final String path) {

    PropertyEntity suiv = entity.findProperty(path);
    CoherenceObjectEntity coherenceList = suiv.getAddedOrAdvisedValue();
    if (coherenceList == null) {
      return null;
    } else {      
      return coherenceList.getValue();
    }
  }
    
  /**
   * @param entity
   * @param path
   * @param oldValue
   */
  public static void addPropertyValue(InstanceEntity entity, final String path, final Object oldValue){
    if(oldValue != null) {
      PropertyEntity text = entity.findProperty(path);
      text.addValue(oldValue.toString(), null);      
      }
  }

}
