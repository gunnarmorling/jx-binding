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

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ReadOnlyProperty;

public class ConverterFactory {

	private final static Map<Key, Class<? extends Converter<?, ?>>> CONVERTER_TYPES = new HashMap<Key, Class<? extends Converter<?, ?>>>();
	
	static {
        CONVERTER_TYPES.put(new Key(Integer.class, String.class), StringToIntConverter.class);
        CONVERTER_TYPES.put(new Key(int.class, String.class), StringToIntConverter.class);
    }
	
	@SuppressWarnings("unchecked")
	public static <M, T> Converter<M, T> getConverter(ReadOnlyProperty<M> modelProperty, ReadOnlyProperty<T> targetProperty) {

//		Object bean = modelProperty.getBean();
//		String name = modelProperty.getName();
		
		
        Class<?> modelPropertyType = null;
        Class<?> targetPropertyType = null;
        
        try {
            modelPropertyType = modelProperty.getClass().getMethod("getValue").getReturnType();
//        	if(bean != null && name != null) {
//        		modelPropertyType = bean.getClass().getMethod("get" + capitalize(name)).getReturnType();
//        	}
//        	else {
//        		modelPropertyType = Object.class;
//        	}
        	
            targetPropertyType = targetProperty.getClass().getMethod("getValue").getReturnType();
            
            if(modelPropertyType.equals(targetPropertyType)) {
            	return (Converter<M, T>)new NoOpConverter<M>();
            }
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } 
        catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }
                
        Class<? extends Converter<?, ?>> converterClass = CONVERTER_TYPES.get(new Key(modelPropertyType, targetPropertyType));
        
        if(converterClass == null) {
            throw new IllegalArgumentException("Couldn't find a converter type for model property type " + modelPropertyType + " and target property type " + targetPropertyType);
        }
        try {
            return (Converter<M, T>) converterClass.newInstance();
        } 
        catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
	
	@SuppressWarnings("unused")
	private static String capitalize(String s) {
		
		if(s == null || "".equals(s)) {
			return s;
		}
		else if(s.length() == 1) {
			return s.toUpperCase();
		}
		else {
			return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
		}
	}
	
	private static class Key {
        
        private final Class<?> c1;
        private final Class<?> c2;
        
        public Key(Class<?> c1, Class<?> c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;
            if (this.c1 != other.c1 && (this.c1 == null || !this.c1.equals(other.c1))) {
                return false;
            }
            if (this.c2 != other.c2 && (this.c2 == null || !this.c2.equals(other.c2))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + (this.c1 != null ? this.c1.hashCode() : 0);
            hash = 17 * hash + (this.c2 != null ? this.c2.hashCode() : 0);
            return hash;
        }

    }
}
