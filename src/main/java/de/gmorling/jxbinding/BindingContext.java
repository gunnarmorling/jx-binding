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

import static de.gmorling.jxbinding.converter.StringBindingViolationListConverter.SHORT_FORMAT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import javax.validation.Validation;
import javax.validation.Validator;

import de.gmorling.jxbinding.converter.Converter;
import de.gmorling.jxbinding.converter.ConverterFactory;
import de.gmorling.jxbinding.converter.StringBindingViolationListConverter;

/**
 * 
 * @author Gunnar Morling
 * 
 */
public class BindingContext implements ChangeListener<List<BindingViolation>> {

    private final Set<Binding<?, ?>> bindings = new HashSet<Binding<?, ?>>();

    private final ObjectProperty<List<BindingViolation>> allConstraintViolations = new SimpleObjectProperty<List<BindingViolation>>(
            Collections.<BindingViolation> emptyList() );

    private final BooleanProperty isValidProperty = new SimpleBooleanProperty( false );

    private <M, T> void addBinding(Binding<M, T> binding) {
        bindings.add( binding );
        binding.targetConstraintViolationsProperty().addListener( this );
    }

    public void updateModels() {
        for ( Binding<?, ?> oneListener : bindings ) {
            oneListener.updateModelProperty();
        }
    }

    public void updateTargets() {
        for ( Binding<?, ?> oneListener : bindings ) {
            oneListener.updateTargetProperty();
        }
    }

    public <M> ReadOnlyBindingBuilderContext<M> bind(ReadOnlyProperty<M> modelProperty) {
        return new ReadOnlyBindingBuilderContext<M>( modelProperty, this );
    }

    public <M> BindingBuilderContext<M> bind(Property<M> modelProperty) {
        return new BindingBuilderContext<M>( modelProperty, this );
    }
    
    public ReadOnlyBindingBuilderConverterContext<List<BindingViolation>, String> bindBindingViolations(Binding<?, ?> binding) {
        
        return this.bind( binding.targetConstraintViolationsProperty() )
            .withConverter( new StringBindingViolationListConverter(SHORT_FORMAT) );
    }

    public ValidatorContextBuilder autoValidateTargetPropertyOf(Binding<?, ?> binding) {
        return new ValidatorContextBuilder( binding, binding.targetProperty() );
    }

    public ReadOnlyObjectProperty<List<BindingViolation>> constraintViolationsProperty() {
        return allConstraintViolations;
    }

    public ReadOnlyBooleanProperty isValidProperty() {
        return isValidProperty;
    }

    @Override
    public void changed(ObservableValue<? extends List<BindingViolation>> observable, List<BindingViolation> oldValue,
            List<BindingViolation> newValue) {

        List<BindingViolation> allViolations = new ArrayList<BindingViolation>();

        for ( Binding<?, ?> oneBinding : bindings ) {
            allViolations.addAll( oneBinding.targetConstraintViolationsProperty().getValue() );
        }

        allConstraintViolations.setValue( Collections.unmodifiableList( allViolations ) );
        isValidProperty.set( allViolations.isEmpty() );

        System.out.println( "BindingContext#isValid(): " + isValidProperty.getValue() );
    }

    public static class ReadOnlyBindingBuilderContext<M> {

        private final ReadOnlyProperty<M> modelProperty;
        private final BindingContext bindingContext;

        private UpdatePolicy targetUpdatePolicy;

        public ReadOnlyBindingBuilderContext(ReadOnlyProperty<M> modelProperty, BindingContext bindingContext) {
            this.modelProperty = modelProperty;
            this.bindingContext = bindingContext;
            this.targetUpdatePolicy = UpdatePolicy.INSTANTLY;
        }

        public <T> ReadOnlyBindingBuilderConverterContext<M, T> withConverter(Converter<M, T> converter) {
            return new ReadOnlyBindingBuilderConverterContext<M, T>( modelProperty, converter, bindingContext,
                    targetUpdatePolicy );
        }

        public ReadOnlyBindingBuilderContext<M> withTargetUpdatePolicy(UpdatePolicy targetUpdatePolicy) {
            this.targetUpdatePolicy = targetUpdatePolicy;
            return this;
        }

        public <T> Binding<M, T> to(Property<T> targetProperty) {

            Converter<M, T> converter = ConverterFactory.getConverter( modelProperty, targetProperty );
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Binding<M, T> binding = new Binding<M, T>( modelProperty, targetProperty, targetUpdatePolicy, converter,
                    validator );
            bindingContext.addBinding( binding );

            return binding;
        }

        public <T> Binding<M, T> to(ReadOnlyProperty<T> targetProperty) {

            Converter<M, T> converter = ConverterFactory.getConverter( modelProperty, targetProperty );
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Binding<M, T> binding = new Binding<M, T>( modelProperty, targetProperty, converter, validator );
            bindingContext.addBinding( binding );

            return binding;
        }
    }

    public static class BindingBuilderContext<M> {

        private final Property<M> modelProperty;
        private final BindingContext bindingContext;

        private UpdatePolicy modelUpdatePolicy;
        private UpdatePolicy targetUpdatePolicy;
        private String labelText;

        public BindingBuilderContext(Property<M> modelProperty, BindingContext bindingContext) {
            this.modelProperty = modelProperty;
            this.bindingContext = bindingContext;
            this.modelUpdatePolicy = UpdatePolicy.INSTANTLY;
            this.targetUpdatePolicy = UpdatePolicy.INSTANTLY;
        }

        public <T> BindingBuilderConverterContext<M, T> withConverter(Converter<M, T> converter) {
            return new BindingBuilderConverterContext<M, T>( modelProperty, converter, bindingContext,
                    modelUpdatePolicy, targetUpdatePolicy );
        }

        public BindingBuilderContext<M> withModelUpdatePolicy(UpdatePolicy modelUpdatePolicy) {
            this.modelUpdatePolicy = modelUpdatePolicy;
            return this;
        }

        public BindingBuilderContext<M> withTargetUpdatePolicy(UpdatePolicy targetUpdatePolicy) {
            this.targetUpdatePolicy = targetUpdatePolicy;
            return this;
        }

        public BindingBuilderContext<M> withLabel(Label labelText) {
            this.labelText = labelText.getText();
            return this;
        }

        public <T> Binding<M, T> to(Property<T> targetProperty) {

            Converter<M, T> converter = ConverterFactory.getConverter( modelProperty, targetProperty );
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Binding<M, T> binding = new Binding<M, T>( modelProperty, targetProperty, modelUpdatePolicy,
                    targetUpdatePolicy, converter, validator, labelText );
            bindingContext.addBinding( binding );

            return binding;
        }

        public <T> Binding<M, T> to(ReadOnlyProperty<T> targetProperty) {

            Converter<M, T> converter = ConverterFactory.getConverter( modelProperty, targetProperty );
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Binding<M, T> binding = new Binding<M, T>( modelProperty, targetProperty, modelUpdatePolicy, converter,
                    validator );
            bindingContext.addBinding( binding );

            return binding;
        }
    }

    public static class ReadOnlyBindingBuilderConverterContext<M, T> {

        private final ReadOnlyProperty<M> modelProperty;
        private final BindingContext bindingContext;
        private final Converter<M, T> converter;

        private String labelText;
        private UpdatePolicy targetUpdatePolicy;

        public ReadOnlyBindingBuilderConverterContext(ReadOnlyProperty<M> modelProperty, Converter<M, T> converter,
                BindingContext bindingContext, UpdatePolicy targetUpdatePolicy) {
            this.modelProperty = modelProperty;
            this.bindingContext = bindingContext;
            this.converter = converter;
            this.targetUpdatePolicy = targetUpdatePolicy;
        }

        public ReadOnlyBindingBuilderConverterContext<M, T> withTargetUpdatePolicy(UpdatePolicy targetUpdatePolicy) {
            this.targetUpdatePolicy = targetUpdatePolicy;
            return this;
        }

        public ReadOnlyBindingBuilderConverterContext<M, T> withLabel(Label label) {
            this.labelText = label.getText();
            return this;
        }

        public Binding<M, T> to(Property<T> targetProperty) {

            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Binding<M, T> binding = new Binding<M, T>( modelProperty, targetProperty, targetUpdatePolicy, converter,
                    validator, labelText );
            bindingContext.addBinding( binding );

            return binding;
        }
    }

    public static class BindingBuilderConverterContext<M, T> {

        private final Property<M> modelProperty;
        private final BindingContext bindingContext;
        private final Converter<M, T> converter;

        private String labelText;
        private UpdatePolicy modelUpdatePolicy;
        private UpdatePolicy targetUpdatePolicy;

        public BindingBuilderConverterContext(Property<M> modelProperty, Converter<M, T> converter,
                BindingContext bindingContext, UpdatePolicy modelUpdatePolicy, UpdatePolicy targetUpdatePolicy) {
            this.modelProperty = modelProperty;
            this.bindingContext = bindingContext;
            this.converter = converter;
            this.modelUpdatePolicy = modelUpdatePolicy;
            this.targetUpdatePolicy = targetUpdatePolicy;
        }

        public BindingBuilderConverterContext<M,T> withLabel(Label label) {
            this.labelText = label.getText();
            return this;
        }

        public BindingBuilderConverterContext<M, T> withModelUpdatePolicy(UpdatePolicy modelUpdatePolicy) {
            this.modelUpdatePolicy = modelUpdatePolicy;
            return this;
        }

        public BindingBuilderConverterContext<M, T> withTargetUpdatePolicy(UpdatePolicy targetUpdatePolicy) {
            this.targetUpdatePolicy = targetUpdatePolicy;
            return this;
        }

        public Binding<M, T> to(Property<T> targetProperty) {

            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Binding<M, T> binding = new Binding<M, T>( modelProperty, targetProperty, modelUpdatePolicy,
                    targetUpdatePolicy, converter, validator, labelText );
            bindingContext.addBinding( binding );

            return binding;
        }
    }

    public static class ValidatorContextBuilder {

        private final Binding<?, ?> binding;

        // private final ReadOnlyProperty<?> property;

        public ValidatorContextBuilder(Binding<?, ?> binding, ReadOnlyProperty<?> property) {
            this.binding = binding;
            // this.property = property;
        }

        public <T> ValidatorContextBuilderStep2<T> upon(ReadOnlyProperty<T> sourceProperty) {
            return new ValidatorContextBuilderStep2<T>( binding, sourceProperty );
        }

    }

    public static class ValidatorContextBuilderStep2<T> {

        private final ReadOnlyProperty<T> property;
        private final Binding<?, ?> binding;

        public ValidatorContextBuilderStep2(Binding<?, ?> binding, ReadOnlyProperty<T> property) {
            this.property = property;
            this.binding = binding;
        }

        public void becoming(final T value) {

            property.addListener( new ChangeListener<T>() {
                @Override
                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                    if ( ( newValue != null && newValue.equals( value ) ) || ( newValue == null && value == null ) ) {

                        binding.validateTargetProperty();
                    }
                }
            } );
        }

    }

}
