/**
 *  Copyright 2011 Gunnar Morling (http://www.gunnarmorling.de/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.gmorling.jxbinding.converter;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.validation.ConstraintViolation;

/**
 * 
 * @author Gunnar Morling
 *
 */
public class StringToConstraintViolationSetConverter implements Converter<List<ConstraintViolation<?>>, String> {

    public StringToConstraintViolationSetConverter() {
    }
    
    @Override
	public List<ConstraintViolation<?>> toModel(String target) {
        throw new UnsupportedOperationException("Conversion not supported");
    }

    @Override
	public String toTarget(List<ConstraintViolation<?>> model) {
    	
    	StringBuilder sb = new StringBuilder();
    	
    	for (ConstraintViolation<?> oneViolation : model) {
			sb.append(oneViolation.getMessage());
			sb.append(", ");
		}
    	
    	if(sb.length() > 0) {
    		return sb.substring(0, sb.length() - 2);
    	}
    	else {
    		return "";
    	}
    	
    }
    
//    @Override
	public ObservableList<ConstraintViolation<?>> getDefaultValue() {
        return FXCollections.emptyObservableList();
    }
    
}
