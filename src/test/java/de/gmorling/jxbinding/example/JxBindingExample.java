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

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Gunnar Morling
 *
 */
public class JxBindingExample {
    
    public static void main(String[] args) {
        Application.launch(JxBindingExampleApplication.class, args);
    }

    public static class JxBindingExampleApplication extends Application {
    
    	public JxBindingExampleApplication() {}
    	
        @Override
        public void start(Stage stage) throws Exception {
            
        	Parent root = FXMLLoader.load(
                getClass().getResource("PersonForm.fxml"),
                ResourceBundle.getBundle(PersonFormController.class.getName()));
            
            stage.setScene(new Scene(root, 640, 480));
            stage.show();
        }
    }
}
