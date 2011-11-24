# What is it?

The jx-binding project aims at providing advanced facilities for data binding, conversion and validation for the [JavaFX 2.0](http://javafx.com/) framework. For that purpose the project provides a fluent API which allows to define bindings between arbitrary properties, optionally using special converters and update policies like this:

    <GridPane xmlns:fx="http://javafx.com/fxml" ... fx:controller="VideoRentalController">
      <children>
        <TextField fx:id="fldMovieName" promptText="Enter the movie name"/>
        <TextField fx:id="fldRuntime" promptText="Enter the movie runtime in minutes"/>
        <TextField fx:id="fldReleaseDate" promptText="Enter the release date (DD.MM.YYYY)"/>
      </children>
    </GridPane>
    
    ...
    
    public class VideoRentalController implements Initializable {
    
        //...

        @Override
        public void initialize(URL url, ResourceBundle rb) {
     
            BindingContext context = new BindingContext();
            Movie model = new Movie();  
    
            //updates will instantly be propagated
            context.bind(model.nameProperty()).to(fldUserName.textProperty());
    
            //the model will only be updated on request; A String/Integer converter will be used by default
            context.bind(model.runtimeProperty())
                .withModelUpdatePolicy(ON_REQUEST)
                .to(fldRuntime.textProperty());
        
            //additional converters can be specified, e.g. for converting between String and Date
            Binding<Date, String> birthdayBinding = context.bind(model.birthdayProperty())
                .withConverter(new StringToDateConverter("dd.MM.yyyy"))
                .to(fldBirthday.textProperty());
       }
   }

		