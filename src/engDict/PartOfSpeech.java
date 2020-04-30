package engDict;

import javafx.beans.property.SimpleStringProperty;

public class PartOfSpeech {
    private SimpleStringProperty Shorthand;
    private SimpleStringProperty Name;

    PartOfSpeech() {

    }

    PartOfSpeech(String shorthand, String name) {
        this.Shorthand = new SimpleStringProperty(shorthand);
        this.Name = new SimpleStringProperty(name);
    }

    SimpleStringProperty ShorthandProperty() {
        return Shorthand;
    }

    SimpleStringProperty NameProperty() {
        return Name;
    }

    String getShorthand() {
        return Shorthand.get();
    }

    String getName() {
        return Name.get();
    }

    void setShorthand(String value) {
        Shorthand.set(value);
    }

    void setName(String value) {
        Name.set(value);
    }

    @Override
    public String toString() {
        return Name.get();
    }
}
