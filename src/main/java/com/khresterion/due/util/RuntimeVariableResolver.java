/**
 * 
 */
package com.khresterion.due.util;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.khresterion.kengine.bo.BoPredicates;
import com.khresterion.kengine.bo.CoherenceObject;
import com.khresterion.kengine.bo.Instance;
import com.khresterion.kengine.bo.InstanceManager;
import com.khresterion.kengine.bo.Property;
import com.khresterion.kengine.bo.Reifications;
import com.khresterion.kengine.core.KType;

/**
 * @author ndriama
 *
 */
public class RuntimeVariableResolver {

  private static final String EUROPE_PARIS = "Europe/Paris";

  /**
     * @param varMap
     * @param instanceManager
     * @return
     */
    public static Map<String, String> resolve(final Map<String, String> varMap, final InstanceManager instanceManager) {

        Map<String, String> result = Maps.newHashMap();
        Set<String> keyList = varMap.keySet();

        for (String key : keyList) {
            String value = varMap.get(key);
            if (value == null) {
                result.put(key, StringUtils.EMPTY);
            } else if (value.equals("DATE_SYSTEM")) {
                result.put(key, getSystemDate());
            } else {
                if (value.contains("_") && value.contains("/Ontologie")) {
                    String instanceName = value.substring(0, value.indexOf('/'));
                    if (instanceName == null) {
                        result.put(key, StringEscapeUtils.escapeXml10(value.trim()));
                    } else {
                        result.put(key, getKengineValue(instanceManager, instanceName, value.trim()));
                    }
                } else {
                    result.put(key, value.trim());
                }
            }
        }
        return result;
    }

    /**
     * @param instance
     * @param path
     * @return
     */
    public static String getInstanceValue(Instance instance, String path){
      String computedValue = null;
      Property property = instance.findProperty(path);
      if (property == null) {
          computedValue = null;
      } else {
          Object objval = property.getValue();
          List<CoherenceObject> coherenceList = Arrays.asList(property.getAdvisedCoherenceInformations());
          Reifications reif = Reifications.value();
          if ((objval == null) && !coherenceList.isEmpty()) {

              if (KType.isValueTypeDate(property.getType())) {
                  DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
                  dateFormat.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                  calendar.setTimeInMillis(Long.valueOf(reif.linearizeForSystem(coherenceList.get(0))));                  
                  computedValue = dateFormat.format(calendar.getTime());
              } else {
                  computedValue = StringEscapeUtils.escapeXml10(reif.linearizeForUser(coherenceList.get(0)));
              }
          } else {

              if (KType.isValueTypeDate(property.getType())) {
                  DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
                  dateFormat.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                  Calendar calendar = Calendar.getInstance();
                  if(objval != null) {
                    calendar.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                    calendar.setTimeInMillis(Long.valueOf(reif.linearizeForSystem(property)));                    
                  }
                  computedValue = (objval == null) ? StringUtils.EMPTY :dateFormat.format(calendar.getTime());
              } else if (KType.isValueTypeInteger(property.getType())) {
                
                computedValue = (objval == null) ? StringUtils.EMPTY : reif.linearizeForSystem(property);
              } else {
                  computedValue = (objval == null) ? StringUtils.EMPTY : StringEscapeUtils.escapeXml10(reif.linearizeForUser(property));
              }
          }
      }

      return computedValue;

    }
    
    /**
     * get actual value or advised value
     * 
     * @return
     */
    private static String getKengineValue(InstanceManager instanceManager, String instanceName, String value) {

        Instance[] instanceArray = instanceManager.findInstances(BoPredicates.isA(instanceName));
        if (instanceArray.length > 0) {
            String computedValue = null;
            Property property = instanceArray[0].findProperty(value);
            if (property == null) {
                computedValue = value;
            } else {
                Object objval = property.getValue();
                List<CoherenceObject> coherenceList = Arrays.asList(property.getAdvisedCoherenceInformations());
                Reifications reif = Reifications.value();
                if ((objval == null) && !coherenceList.isEmpty()) {

                    if (KType.isValueTypeDate(property.getType())) {
                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
                        dateFormat.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                        calendar.setTimeInMillis(Long.valueOf(reif.linearizeForSystem(coherenceList.get(0))));                      
                        computedValue = dateFormat.format(calendar.getTime());
                    } else {
                        computedValue = StringEscapeUtils.escapeXml10(reif.linearizeForUser(coherenceList.get(0)));
                    }
                } else {

                    if (KType.isValueTypeDate(property.getType())) {
                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
                        dateFormat.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                        Calendar calendar = Calendar.getInstance();
                        if(objval != null) {
                          calendar.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
                          calendar.setTimeInMillis(Long.valueOf(reif.linearizeForSystem(property)));                          
                        }
                        computedValue = (objval == null) ? StringUtils.EMPTY :dateFormat.format(calendar.getTime());
                    } else if (KType.isValueTypeInteger(property.getType())) {                    
                      computedValue = (objval == null) ? StringUtils.EMPTY : reif.linearizeForSystem(property);
                    } else {
                        computedValue = (objval == null) ? StringUtils.EMPTY : StringEscapeUtils.escapeXml10(reif.linearizeForUser(property));
                    }
                }
            }

            return computedValue;
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * pretty formatting for date
     * 
     * @return
     */
    private static String getSystemDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(EUROPE_PARIS));       
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
        dateFormat.setTimeZone(TimeZone.getTimeZone(EUROPE_PARIS));
        return dateFormat.format(calendar.getTime());
    }
}
