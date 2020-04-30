package engDict;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class Controller {
    public TableView<DictionaryWord> mainTable;
    public TextField editWord;
    public TextField editExample;
    public ChoiceBox<PartOfSpeech> editPartOfSpeech;
    public TextField editTranscript;
    public TextField editTranslate;
    public ProgressIndicator parseProgress;
    public CheckBox boxOnlyEmptyPartOfSpeech;

    private void ResetInput() {
        editWord.clear();
        editExample.clear();
        editPartOfSpeech.getSelectionModel().select(-1);
        editTranscript.clear();
        editTranslate.clear();
    }

    private void LoadDictionary() {
        mainTable.getItems().clear();

        try {
            FileReader file = new FileReader("dict.tsv");
            BufferedReader reader = new BufferedReader(file);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] sep = line.split("\t", -1);
                mainTable.getItems().add(new DictionaryWord(sep[0], sep[1], sep[2], sep[3], sep[4]));
            }

            file.close();
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка загрузки словаря:\n" + e.getMessage(), ButtonType.OK).show();
        }
    }

    private void SaveDictionary() {
        try {
            FileWriter file = new FileWriter("dict.tsv");

            for (DictionaryWord i : mainTable.getItems()) {
                file.write(String.format("%s\t%s\t%s\t%s\t%s\n", i.getWord(), i.getPartOfSpeech(), i.getTranscript(), i.getTranslate(), i.getExample()));
            }

            file.close();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Ошибка сохранения словаря:\n" + ex.getMessage(), ButtonType.OK).show();
        }
    }

    private PartOfSpeech GetPartOfSpeechFromShorthand(String shorthand) {
        for (PartOfSpeech i : editPartOfSpeech.getItems()) {
            if (i.getShorthand().equals(shorthand)) {
                return i;
            }
        }
        return editPartOfSpeech.getItems().get(0);
    }

    private PartOfSpeech GetPartOfSpeechFromName(String name) {
        name = name.toLowerCase();

        for (PartOfSpeech i : editPartOfSpeech.getItems()) {
            if (i.getName().toLowerCase().startsWith(name)) {
                return i;
            }
        }
        return editPartOfSpeech.getItems().get(0);
    }

    @FXML
    private void initialize() {
        editPartOfSpeech.getItems().add(new PartOfSpeech("", "-"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("adj", "Adjective (Прил)"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("verb", "Verb (Глаг)"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("noun", "Noun (Сущ)"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("adv", "Adverb (Нар)"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("prep", "Preposition (Предл)"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("con", "Conjunction (Союз)"));
        editPartOfSpeech.getItems().add(new PartOfSpeech("int", "Interjection (Междомет)"));

        mainTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super DictionaryWord>) change -> {
            DictionaryWord word = mainTable.getSelectionModel().getSelectedItem();
            if (word == null) {
                ResetInput();
                return;
            }

            editWord.setText(word.getWord());
            editExample.setText(word.getExample());
            editPartOfSpeech.getSelectionModel().select(GetPartOfSpeechFromShorthand(word.getPartOfSpeech()));
            editTranscript.setText(word.getTranscript());
            editTranslate.setText(word.getTranslate());
        });
    }

    @FXML
    private void OnPressAdd(ActionEvent event) {
        String word = editWord.getText();
        if (word.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Необходимо ввести слово", ButtonType.OK).show();
            return;
        }

        mainTable.getItems().add(new DictionaryWord(editWord.getText(),
                editPartOfSpeech.getSelectionModel().getSelectedIndex() != -1 ? editPartOfSpeech.getSelectionModel().getSelectedItem().getShorthand() : "",
                editTranscript.getText(), editTranslate.getText(), editExample.getText()));

        mainTable.getSelectionModel().selectLast();
    }


    public void OnPressModify(ActionEvent actionEvent) {
        if (mainTable.getSelectionModel().getSelectedIndex() == -1) return;

        DictionaryWord word = mainTable.getSelectionModel().getSelectedItem();
        word.setWord(editWord.getText());
        word.setExample(editExample.getText());
        if (editPartOfSpeech.getSelectionModel().getSelectedIndex() != -1) {
            word.setPartOfSpeech(editPartOfSpeech.getSelectionModel().getSelectedItem().getShorthand());
        }
        word.setTranscript(editTranscript.getText());
        word.setTranslate(editTranslate.getText());
    }

    public void OnPressDelete(ActionEvent actionEvent) {
        int selectedIndex = mainTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) return;

        mainTable.getItems().remove(selectedIndex);
        // ResetInput();
    }

    public void OnPressReset(ActionEvent actionEvent) {
        ResetInput();
    }

    public void OnPressCopyToClipboard(ActionEvent actionEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        StringBuilder sb = new StringBuilder();
        sb.append("#\tСлово\tЧ/р\tТранскр.\tПеревод\tПример\n");

        int index = 0;
        for (DictionaryWord i : mainTable.getItems()) {
            index++;
            sb.append(String.format("%d\t%s\t%s\t%s\t%s\t%s\n", index, i.getWord(), i.getPartOfSpeech(), i.getTranscript(), i.getTranslate(), i.getExample()));
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    public void OnPressSaveTable(ActionEvent actionEvent) {
        SaveDictionary();
    }

    public void OnPressLoadTable(ActionEvent actionEvent) {
        LoadDictionary();
    }

    public void OnPressResetTable(ActionEvent actionEvent) {
        ResetInput();
        mainTable.getItems().clear();
    }

    public void OnKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                OnPressAdd(null);
                ResetInput();
                editWord.requestFocus();
                break;
            case DELETE:
                ResetInput();
                break;
        }
    }

    private void ParseData() {
        mainTable.setDisable(true);

        Task t = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<DictionaryWord> items = mainTable.getItems();
                int itemsCount = items.size();
                int index = 0;

                for (DictionaryWord i : mainTable.getItems()) {
                    if (i.getPartOfSpeech().isEmpty() || !boxOnlyEmptyPartOfSpeech.isSelected()) {
                        try {
                            Document doc = Jsoup.connect("https://dictionary.cambridge.org/ru/словарь/англо-русский/"
                                    + URLEncoder.encode(i.getWord(), Charset.defaultCharset())).get();

                            Element partOfSpeech = doc.getElementsByAttributeValueContaining("class", "pos dpos").first();
                            Element transcript = doc.getElementsByAttributeValueContaining("class", "ipa dipa").first();
                            Element translate = doc.getElementsByAttributeValueContaining("class", "trans dtrans dtrans-se").first();

                            i.setPartOfSpeech(partOfSpeech == null ? "" : GetPartOfSpeechFromName(partOfSpeech.text().strip()).getShorthand());
                            i.setTranscript(transcript == null ? "" : transcript.text().strip());
                            i.setTranslate(translate == null ? "" : translate.text().strip());

                            Thread.sleep(250);
                        } catch (Exception e) {
                            Thread.sleep(1000);
                        }
                    }

                    index++;
                    updateProgress(index, itemsCount);
                }

                updateProgress(1, 1);
                mainTable.setDisable(false);

                return null;
            }
        };

        parseProgress.progressProperty().bind(t.progressProperty());
        new Thread(t).start();
    }

    public void OnPressParse(ActionEvent actionEvent) {
        if(mainTable.isDisabled()) {
            new Alert(Alert.AlertType.ERROR, "Парсинг находится в процессе", ButtonType.OK).show();
            return;
        }

        mainTable.getSelectionModel().select(-1);
        ResetInput();

        ParseData();
    }
}
