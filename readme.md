# What is it?

The jx-binding project aims at providing advanced facilities for data binding, conversion and validation for the [JavaFX 2.0](http://javafx.com/) framework. 

For that purpose the project provides a fluent API which allows to define bindings between arbitrary properties (typically a model and a GUI property), optionally using special converters and update policies like this:

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
     
            //a binding context manages several bindings and offers a fluent API for their creation
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

# Update policies

There are three kind of update policies, which determine when updates from the model to the target property and vice versa shall be performed:

* INSTANTLY: Updates happen immediately as a value changes
* ON_REQUEST: Updates happen on demand by calling `Binding#updateModelProperty()` or `Binding#updateTargetProperty()`
* NEVER: Updates will never be propagated (useful for read-only properties)

# Triggering model updates

Model updates can be triggered on demand like this:

    BindingContext context;
    
    //...
    @FXML private void handleSubmitButtonAction(ActionEvent event) {
        context.updateModels();
    }
    
# Using validation

jx-binding integrates with the Bean Validation API (JSR 303). This means if you annotate model properties with constraint annotations these constraints will be automatically validated by the binding. Just annotate your model:

    public class Movie {
    
        private final StringProperty name = new SimpleStringProperty(this, "name");

        @NotNull
    	@Size(min=3, max=50)
        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }
        public StringProperty nameProperty() { return name; }
    }
    
To display any occured constraint violations just bind them to some label:

    context.bindBindingViolations(movieNameBinding).to(lblMovieNameViolations.textProperty());

Validation will happen automatically upon model updates. Additionally it can be triggered manually by invoking `Binding#validateTargetProperty()` or `Binding#validateOrProperty()` or depending on some property change:

    //validate the movie name if its text field loses the focus    
    context.autoValidateTargetPropertyOf(movieNameBinding).upon(fldMovieName.focusedProperty()).becoming(false);
    
# What's next?

To get a better understanding of what jx-binding can do for you, you might be interested in running the contained [example application](https://github.com/gunnarmorling/jx-binding/blob/master/src/test/java/de/gmorling/jxbinding/example/JxBindingExample.java).

In order to run the example it's currently required to manually install the JavaFX 2.0 library into your local Maven repository. To do so just follow [these instructions](http://java.dzone.com/articles/install-javafx-runtime-local).