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
package de.gmorling.jxbinding.example;

import static de.gmorling.jxbinding.UpdatePolicy.ON_REQUEST;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import de.gmorling.jxbinding.Binding;
import de.gmorling.jxbinding.BindingContext;
import de.gmorling.jxbinding.converter.StringToConstraintViolationSetConverter;
import de.gmorling.jxbinding.converter.StringToDateConverter;
import de.gmorling.jxbinding.example.model.Gender;
import de.gmorling.jxbinding.example.model.Person;

/**
 * 
 * @author Gunnar Morling
 *
 */
public class PersonFormController implements Initializable {

	private BindingContext context;
	private final Person model = new Person();
	
	@FXML private Label lblUserName;
	@FXML private TextField fldUserName;
	@FXML private Label lblUserNameViolations;
	
	@FXML private TextField fldAge;
	@FXML private Label lblAgeViolations;
	
	@FXML private TextField fldBirthday;
	@FXML private Label lblBirthdayViolations;
	
	@FXML private ChoiceBox<Gender> cbGender;
	@FXML private Label lblGenderViolations;
	
	
	@FXML private Button submitButton;
	@FXML private Label lblAllViolations;
	@FXML private Label lblPerson;
	
	@FXML
	private void handleSubmitButtonAction(ActionEvent event) {

		System.out.println("You clicked me!");

		context.updateModels();
		System.out.println(model);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		context = new BindingContext();

		ObservableList<Gender> genders = FXCollections.observableArrayList(Gender.values());
		ObjectProperty<ObservableList<Gender>> gendersProperty = new SimpleObjectProperty<ObservableList<Gender>>(genders);
		
		Binding<ObservableList<Gender>, ObservableList<Gender>> gendersBinding = context.bind(gendersProperty).to(cbGender.itemsProperty());
		gendersBinding.updateTargetProperty();
		cbGender.selectionModelProperty().getValue().selectFirst();
		
		context.bind(model.genderProperty()).to(cbGender.selectionModelProperty().getValue().selectedItemProperty());
		
		
		final Binding<String, String> nameBinding = context.bind(model.nameProperty())
			.withModelUpdatePolicy(ON_REQUEST)
			.withLabel(lblUserName)
			.to(fldUserName.textProperty());

		context.bind(nameBinding.targetConstraintViolationsProperty())
			.withConverter(new StringToConstraintViolationSetConverter())
			.to(lblUserNameViolations.textProperty());

		context.autoValidateTargetPropertyOf(nameBinding).upon(fldUserName.focusedProperty()).becoming(false);
		

		final Binding<Number, String> ageBinding = context.bind(model.ageProperty())
			.withModelUpdatePolicy(ON_REQUEST)
			.to(fldAge.textProperty());
		
		context.bind(ageBinding.targetConstraintViolationsProperty())
				.withConverter(new StringToConstraintViolationSetConverter())
				.to(lblAgeViolations.textProperty());
		
		context.autoValidateTargetPropertyOf(ageBinding).upon(fldAge.focusedProperty()).becoming(false);
		
//		context.bind(model.ageProperty()).to(lblAge.textProperty());
System.out.println(fldUserName.textProperty().getBean());
		final Binding<Date, String> birthdayBinding = context.bind(model.birthdayProperty())
				.withConverter(new StringToDateConverter("dd.MM.yyyy"))
				.withModelUpdatePolicy(ON_REQUEST)
				.withTargetUpdatePolicy(ON_REQUEST)
				.to(fldBirthday.textProperty());

		context.bind(birthdayBinding.targetConstraintViolationsProperty())
			.withConverter(new StringToConstraintViolationSetConverter())
			.to(lblBirthdayViolations.textProperty());
		
		context.bind(context.constraintViolationsProperty())
			.withConverter(new StringToConstraintViolationSetConverter())
			.to(lblAllViolations.textProperty());

		context.bind(model.stringProperty()).to(lblPerson.textProperty());
		
//		context.bind(model.birthdayProperty())
//				.withConverter(new StringToDateConverter("dd.MM.yyyy"))
//				.withModelUpdatePolicy(NEVER)
//				.withTargetUpdatePolicy(INSTANTLY)
//				.to(lblBirthday.textProperty());
		
		context.autoValidateTargetPropertyOf(birthdayBinding).upon(fldBirthday.focusedProperty()).becoming(false);
		
		
//		submitButton.disableProperty().bind(context.isValidProperty().not());
	}

}
