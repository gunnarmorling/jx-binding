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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.gmorling.jxbinding.BindingViolation;

/**
 * 
 * @author Gunnar Morling
 *
 */
public class StringDateConverter implements Converter<Date, String> {

    private final DateFormat format;
    
    public StringDateConverter(String format) {
    	this.format = new SimpleDateFormat(format);
    }
    
    public StringDateConverter(DateFormat format) {
        this.format = format;
    }
    
    @Override
	public Date toModel(String target) {
    	
        try {
            return target == null ? null : format.parse(target);
        } 
        catch (ParseException pe) {
        	throw new ConversionException(new BindingViolation("must be a valid date"));
        }
    }

    @Override
	public String toTarget(Date model) {
        return model == null ? null : format.format(model);
    }
    
}
