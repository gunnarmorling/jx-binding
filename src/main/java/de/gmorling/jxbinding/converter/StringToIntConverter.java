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

import de.gmorling.jxbinding.BindingViolation;

/**
 * 
 * @author Gunnar Morling
 *
 */
public class StringToIntConverter implements Converter<Number, String> {

    @Override
	public Number toModel(String target) {
        
        try {
        	return target == null ? null : Integer.parseInt(target);
        }
        catch(NumberFormatException nfe) {
            throw new ConversionException(new BindingViolation("must be a valid number"));
        }
    }

    @Override
	public String toTarget(Number model) {
        return model == null ? null : model.toString();
    }
    
}
