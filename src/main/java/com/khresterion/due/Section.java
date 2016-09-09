/**
 * copyright Khresterion 2016
 */
package com.khresterion.due;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author khresterion
 *
 */
@JsonSerialize
public interface Section extends Comparable<Section> {

    public String getId();
    
    public void setId(final String id);
    
    public String getName();
    
    public void setName(final String name);
        
    public String getLink();
    
    public void setLink(final String link);    
}
