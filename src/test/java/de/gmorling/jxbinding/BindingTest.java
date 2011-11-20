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

import static de.gmorling.jxbinding.UpdatePolicy.ON_REQUEST;
import static org.fest.assertions.Assertions.assertThat;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.junit.Test;

import de.gmorling.jxbinding.Binding;
import de.gmorling.jxbinding.BindingContext;
import de.gmorling.jxbinding.example.model.Person;

public class BindingTest {

	@Test
	public void testSimpleBinding() {

		StringProperty model = new SimpleStringProperty();
		StringProperty target = new SimpleStringProperty();
		
		BindingContext context = new BindingContext();
		context.bind(model).to(target);
		
		model.set("foo");
		
		assertThat(target.getValue()).isEqualTo("foo");
	}

	@Test
	public void testTargetPropertyValidationUponUpdate() {
		
		Person person = new Person();
		StringProperty targetProperty = new SimpleStringProperty();
		
		BindingContext context = new BindingContext();
		Binding<String, String> nameBinding = context.bind(person.nameProperty()).to(targetProperty);
		
		targetProperty.setValue("Bob");
		assertThat(nameBinding.targetConstraintViolationsProperty().getValue()).hasSize(1);
	}

	@Test
	public void testExplicitTargetPropertyValidation() {
		
		//given
		Person person = new Person();
		StringProperty targetProperty = new SimpleStringProperty();
		
		BindingContext context = new BindingContext();
		Binding<String, String> nameBinding = context.bind(person.nameProperty())
			.withModelUpdatePolicy(ON_REQUEST)
			.to(targetProperty);
		
		//when
		targetProperty.setValue("Bob");
		
		//then
		assertThat(nameBinding.targetConstraintViolationsProperty().getValue()).isEmpty();
		
		//when
		nameBinding.validateTargetProperty();
		
		//then
		assertThat(nameBinding.targetConstraintViolationsProperty().getValue()).hasSize(1);
	}
}
