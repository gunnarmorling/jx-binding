# What is it?

The jx-binding project aims at providing advanced facilities for data binding, conversion and validation for the [JavaFX 2.0](http://javafx.com/) framework. For that purpose the project provides a fluent API which allows to define bindings between arbitrary properties, optionally using special converters and update policies like this:

    <GridPane xmlns:fx="http://javafx.com/fxml" ... fx:controller="SomeController">
      <children>
        <TextField fx:id="fldUserName" promptText="Enter your name"/>
   	    <TextField fx:id="fldAge" promptText="Enter your age"/>
      </children>
    </GridPane>
    
    ...
    
    BindingContext context = new BindingContext();
    Person model = new Person();  
    
    //updates will instantly be propagated
    context.bind(model.nameProperty()).to(fldUserName.textProperty());
    
    //the model will only be updated on request
    context.bind(model.ageProperty())
        .withModelUpdatePolicy(ON_REQUEST)
        .to(fldAge.textProperty());
        
    //a special converter is used for converting between String and Date
    Binding<Date, String> birthdayBinding = context.bind(model.birthdayProperty())
  	    .withConverter(new StringToDateConverter("dd.MM.yyyy"))
		    .to(fldBirthday.textProperty());        