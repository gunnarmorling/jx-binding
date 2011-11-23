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
package de.gmorling.jxbinding.example.model;

import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

/**
 * 
 * @author Gunnar Morling
 *
 */
public class Person {
    
    private final StringProperty name = new SimpleStringProperty(this, "name");

    private final IntegerProperty age = new SimpleIntegerProperty(this, "age");

    private final ObjectProperty<Date> birthday = new SimpleObjectProperty<Date>(this, "birthday");
    
    private final ObjectProperty<Gender> gender = new SimpleObjectProperty<Gender>(this, "gender");
    
    @Size.List ({
//    	@Size(min=5, max=10),
    	@Size(min=3, max=10)
    })
    @NotNull
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }
    
    @NotNull
    @Min(10)
    public int getAge() { return age.get(); }
    public void setAge(int age) { this.age.set(age); }
    public IntegerProperty ageProperty() { return age; }

    @NotNull
    @Past
    public Date getBirthday() { return birthday.get(); }
    public void setBirthday(Date birthday) { this.birthday.set(birthday); }
    public ObjectProperty<Date> birthdayProperty() { return birthday; }
    
    @NotNull
    public Gender getGender() { return gender.get(); }
    public void setGender(Gender gender) { this.gender.set(gender); }
    public ObjectProperty<Gender> genderProperty() { return gender; }
	
    public StringProperty stringProperty() { return new SimpleStringProperty(toString()); }
    @Override
	public String toString() {
		return "Person [name=" + name.get() + ", age=" + age.get() + ", birthday="
				+ birthday.get() + ", gender=" + gender.get() + "]";
	}
    
}
