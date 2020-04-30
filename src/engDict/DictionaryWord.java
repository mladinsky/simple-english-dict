package engDict;

import javafx.beans.property.SimpleStringProperty;

public class DictionaryWord {
    public SimpleStringProperty Word;
    public SimpleStringProperty PartOfSpeech;
    public SimpleStringProperty Transcript;
    public SimpleStringProperty Translate;
    public SimpleStringProperty Example;

    DictionaryWord() {
    }

    DictionaryWord(String word, String partOfSpeech, String transcript, String translate, String example) {
        this.Word = new SimpleStringProperty(word);
        this.PartOfSpeech = new SimpleStringProperty(partOfSpeech);
        this.Transcript = new SimpleStringProperty(transcript);
        this.Translate = new SimpleStringProperty(translate);
        this.Example = new SimpleStringProperty(example);
    }

    // Property

    public SimpleStringProperty TranslateProperty() {
        return Translate;
    }

    public SimpleStringProperty WordProperty() {
        return Word;
    }

    public SimpleStringProperty PartOfSpeechProperty() {
        return PartOfSpeech;
    }

    public SimpleStringProperty TranscriptProperty() {
        return Transcript;
    }

    public SimpleStringProperty ExampleProperty() {
        return Example;
    }

    // Getters

    public String getTranslate() {
        return Translate.get();
    }

    public String getWord() {
        return Word.get();
    }

    public String getPartOfSpeech() {
        return PartOfSpeech.get();
    }

    public String getTranscript() {
        return Transcript.get();
    }

    public String getExample() {
        return Example.get();
    }

    // Setters

    public void setTranslate(String value) {
        Translate.set(value);
    }

    public void setWord(String value) {
        Word.set(value);
    }

    public void setPartOfSpeech(String value) {
        PartOfSpeech.set(value);
    }

    public void setTranscript(String value) {
        Transcript.set(value);
    }

    public void setExample(String value) {
        Example.set(value);
    }
}
