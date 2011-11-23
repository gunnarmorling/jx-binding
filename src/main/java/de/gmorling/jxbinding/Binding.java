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
package de.gmorling.jxbinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gmorling.jxbinding.converter.ConversionException;
import de.gmorling.jxbinding.converter.Converter;

/**
 * Represents a bidirectional binding between a model property and a target
 * property (e.g. the <code>text</code> property of a text field) .
 * 
 * @author Gunnar Morling
 * 
 * @param <M>
 * @param <T>
 */
public class Binding<M, T> {

    private static final Logger logger = LoggerFactory.getLogger( Binding.class );

    private final ReadOnlyProperty<M> modelProperty;
    private final ReadOnlyProperty<T> targetProperty;
    private final UpdatePolicy modelUpdatePolicy;
    private final UpdatePolicy targetUpdatePolicy;
    private final Converter<M, T> converter;
    private final Validator validator;
    private final String labelText;
    private final ObjectProperty<List<BindingViolation>> targetConstraintViolations;

    /* package private */Binding(ReadOnlyProperty<M> modelProperty, ReadOnlyProperty<T> targetProperty,
            Converter<M, T> converter, Validator validator) {

        this.modelProperty = modelProperty;
        this.targetProperty = targetProperty;
        this.modelUpdatePolicy = UpdatePolicy.NEVER;
        this.targetUpdatePolicy = UpdatePolicy.NEVER;
        this.converter = converter;
        this.validator = validator;
        this.targetConstraintViolations = new SimpleObjectProperty<List<BindingViolation>>(
                Collections.<BindingViolation> emptyList() );
        this.labelText = null;

        Listener listener = new Listener( this );
        modelProperty.addListener( listener );
        targetProperty.addListener( listener );
    }

    /* package private */Binding(ReadOnlyProperty<M> modelProperty, Property<T> targetProperty,
            UpdatePolicy targetUpdatePolicy, Converter<M, T> converter, Validator validator) {

        this( modelProperty, targetProperty, targetUpdatePolicy, converter, validator, null );
    }

    /* package private */Binding(ReadOnlyProperty<M> modelProperty, Property<T> targetProperty,
            UpdatePolicy targetUpdatePolicy, Converter<M, T> converter, Validator validator, String labelText) {

        this.modelProperty = modelProperty;
        this.targetProperty = targetProperty;
        this.modelUpdatePolicy = UpdatePolicy.NEVER;
        this.targetUpdatePolicy = targetUpdatePolicy;
        this.converter = converter;
        this.validator = validator;
        this.targetConstraintViolations = new SimpleObjectProperty<List<BindingViolation>>(
                Collections.<BindingViolation> emptyList() );
        this.labelText = labelText;

        Listener listener = new Listener( this );
        modelProperty.addListener( listener );
        targetProperty.addListener( listener );
    }

    /* package private */Binding(Property<M> modelProperty, ReadOnlyProperty<T> targetProperty,
            UpdatePolicy modelUpdatePolicy, Converter<M, T> converter, Validator validator) {

        this.modelProperty = modelProperty;
        this.targetProperty = targetProperty;
        this.modelUpdatePolicy = modelUpdatePolicy;
        this.targetUpdatePolicy = UpdatePolicy.NEVER;
        this.converter = converter;
        this.validator = validator;
        this.targetConstraintViolations = new SimpleObjectProperty<List<BindingViolation>>(
                Collections.<BindingViolation> emptyList() );
        this.labelText = null;

        Listener listener = new Listener( this );
        modelProperty.addListener( listener );
        targetProperty.addListener( listener );
    }

    /* package private */Binding(Property<M> modelProperty, Property<T> targetProperty, UpdatePolicy modelUpdatePolicy,
            UpdatePolicy targetUpdatePolicy, Converter<M, T> converter, Validator validator) {
        this( modelProperty, targetProperty, modelUpdatePolicy, targetUpdatePolicy, converter, validator, null );
    }

    /* package private */Binding(Property<M> modelProperty, Property<T> targetProperty, UpdatePolicy modelUpdatePolicy,
            UpdatePolicy targetUpdatePolicy, Converter<M, T> converter, Validator validator, String labelText) {

        this.modelProperty = modelProperty;
        this.targetProperty = targetProperty;
        this.modelUpdatePolicy = modelUpdatePolicy;
        this.targetUpdatePolicy = targetUpdatePolicy;
        this.converter = converter;
        this.validator = validator;
        this.targetConstraintViolations = new SimpleObjectProperty<List<BindingViolation>>(
                Collections.<BindingViolation> emptyList() );
        this.labelText = labelText;

        Listener listener = new Listener( this );
        modelProperty.addListener( listener );
        targetProperty.addListener( listener );
    }

    public void validateTargetProperty() {
        validateTargetProperty( false );
    }

    public void validateModelProperty() {

    }

    public void updateModelProperty() {

        if ( modelUpdatePolicy == UpdatePolicy.NEVER ) {
            return;
        }

        validateTargetProperty( true );
    }

    public ReadOnlyProperty<T> targetProperty() {
        return targetProperty;
    }

    private void validateTargetProperty(boolean setValueUponSuccessfulValidation) {

        // 1. validate null before conversion
        if ( targetProperty.getValue() == null || "".equals( targetProperty.getValue() ) ) {
            if ( !validateTargetValue( null ) ) {
                return;
            }
        }

        // 2. convert value
        M valueToSet = null;

        try {
            valueToSet = convertTargetToModel();
        }
        catch ( ConversionException ce ) {
            return;
        }

        // 3. validate converted value
        boolean isValid = validateTargetValue( valueToSet );

        if ( isValid && setValueUponSuccessfulValidation && modelProperty instanceof Property ) {
            ( (Property<M>) modelProperty ).setValue( valueToSet );
        }
    }

    private M convertTargetToModel() {

        try {
            return converter.toModel( targetProperty.getValue() );
        }
        catch ( ConversionException ce ) {

            String message;

            if ( ce.getConversionViolation() != null ) {
                message = ce.getConversionViolation().getMessage();
            }
            else {
                message = "Error occurred during conversion";
                logger.error( message, ce );
            }

            Set<BindingViolation> violations = new HashSet<BindingViolation>();
            violations.add( new BindingViolation( labelText, message ) );

            targetConstraintViolations.setValue( Collections.unmodifiableList( new ArrayList<BindingViolation>(
                    violations ) ) );

            throw ce;
        }
    }

    public void updateTargetProperty() {

        if ( targetUpdatePolicy != UpdatePolicy.NEVER && targetProperty instanceof Property ) {
            try {
                ( (Property<T>) targetProperty ).setValue( converter.toTarget( modelProperty.getValue() ) );
            }
            catch ( ConversionException ce ) {
                System.out.println( "Error occurred during conversion: " + ce.getMessage() );
            }
        }
    }

    public ReadOnlyObjectProperty<List<BindingViolation>> targetConstraintViolationsProperty() {
        return targetConstraintViolations;
    }

    private boolean validateTargetValue(M value) {

        Class<?> beanClass = modelProperty.getBean() != null ? modelProperty.getBean().getClass() : null;
        String propertyName = modelProperty.getName();

        if ( beanClass == null || propertyName == null ) {
            logger.warn( "Can't validate bean property " + modelProperty + " without bean type and property name." );
            return true;
        }

        Set<? extends ConstraintViolation<?>> violations = validator.validateValue( beanClass, propertyName, value );

        targetConstraintViolations.setValue( Collections.unmodifiableList( asBindingViolations( violations ) ) );

        return targetConstraintViolations.getValue().isEmpty();
    }

    private List<BindingViolation> asBindingViolations(Set<? extends ConstraintViolation<?>> violations) {

        List<BindingViolation> theValue = new ArrayList<BindingViolation>();

        for ( ConstraintViolation<?> oneConstraintViolation : violations ) {
            theValue.add( new BindingViolation( labelText, oneConstraintViolation.getMessage() ) );
        }

        return theValue;
    }

    private static class Listener implements ChangeListener<Object> {

        private final Binding<?, ?> binding;

        private ObservableValue<?> eventSource;

        private Listener(Binding<?, ?> binding) {
            this.binding = binding;
        }

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {

            if ( eventSource != null ) {
                return;
            }

            try {
                eventSource = observable;

                if ( observable == binding.targetProperty && binding.modelUpdatePolicy == UpdatePolicy.INSTANTLY ) {
                    binding.updateModelProperty();
                }
                else if ( observable == binding.modelProperty && binding.targetUpdatePolicy == UpdatePolicy.INSTANTLY ) {
                    binding.updateTargetProperty();
                }
            }
            finally {
                eventSource = null;
            }
        }
    }

    @Override
    public String toString() {
        return "Binding [modelProperty=" + modelProperty + ", targetProperty=" + targetProperty + "]";
    }

}
