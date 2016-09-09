/**
 * copyright Khresterion 2016
 */
package com.khresterion.due.model;

import java.util.List;

import com.google.common.collect.Lists;
import com.khresterion.due.Tree;
import com.khresterion.due.Section;

/**
 * @author khresterion
 *
 */
public class DefaultTree implements Tree {
    
    private List<Section> _internal;    
    
    /**
     * 
     */
    public DefaultTree() {
        super();
        this._internal = Lists.newArrayList();
    }

    /* (non-Javadoc)
     * @see com.khresterion.due.Plan#getList()
     */
    @Override
    public Section[] getData() {
        if(_internal.size()>0){
            Section[] arraySection = new Section[_internal.size()];
            return _internal.toArray(arraySection);
        } else {
            return new Section[0];
        }
    }

    /* (non-Javadoc)
     * @see com.khresterion.due.Plan#addSection(com.khresterion.due.Section)
     */
    @Override
    public void addSection(Section section) {
        _internal.add(section);        
    }

    /* (non-Javadoc)
     * @see com.khresterion.due.Plan#removeSection(int)
     */
    @Override
    public void removeSection(final int index) {
        if((index >= 0) && (index < _internal.size())){
            _internal.remove(index);
        }
        
    }

}
