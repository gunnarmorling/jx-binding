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

public class ConversionException extends RuntimeException {

	private static final long serialVersionUID = 373585914744023405L;

	private final BindingViolation bindingViolation;
	
    public ConversionException(String message) {
        super(message);
        this.bindingViolation = null;
    }

    public ConversionException(BindingViolation bindingViolation) {
        super();
        this.bindingViolation = bindingViolation;
    }
    
    public ConversionException(Throwable cause) {
        super(cause);
        this.bindingViolation = null;
    }
    
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
        this.bindingViolation = null;
    }
    
    public BindingViolation getBindingViolation() {
		return bindingViolation;
	}
    
}
