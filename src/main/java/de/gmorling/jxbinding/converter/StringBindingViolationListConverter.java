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

import de.gmorling.jxbinding.BindingViolation;

/**
 * 
 * @author Gunnar Morling
 * 
 */
public class StringBindingViolationListConverter implements Converter<List<BindingViolation>, String> {

    public final static String SHORT_FORMAT = "${message}";

    public final static String LONG_FORMAT = "${label} ${message}";

    private final String format;

    public StringBindingViolationListConverter() {
        this( LONG_FORMAT );
    }

    public StringBindingViolationListConverter(String format) {
        this.format = format;
    }

    @Override
    public List<BindingViolation> toModel(String target) {
        throw new UnsupportedOperationException( "Conversion not supported" );
    }

    @Override
    public String toTarget(List<BindingViolation> model) {

        StringBuilder sb = new StringBuilder();

        for ( BindingViolation oneViolation : model ) {

            String violationString = format;

            String label = oneViolation.getLabel() != null ? oneViolation.getLabel() : "";
            violationString = violationString.replace( "${label}", label );

            String message = oneViolation.getMessage() != null ? oneViolation.getMessage() : "";
            violationString = violationString.replace( "${message}", message );

            sb.append( violationString );
            sb.append( "\n" );
        }

        if ( sb.length() > 0 ) {
            return sb.substring( 0, sb.length() - 1 );
        }
        else {
            return "";
        }

    }
}
