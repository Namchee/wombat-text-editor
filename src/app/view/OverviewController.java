/**
 * WombatTextEditor Controller File
 * 
 * @author Namchee
 * @version 1.1
 */

package app.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import app.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OverviewController {
    
    @FXML
    private TextArea textLog;
    
    @FXML
    private TextArea saveLog;
    
    @FXML
    private Label characterCount;
    
    @FXML
    private Label wordCount;
    
    @FXML
    private Label lineCount;
    
    @FXML
    private Label sentenceCount;
    
    @FXML
    private MenuItem wrapText;
    
    @FXML
    private MenuItem newButton;
    
    @FXML
    private MenuItem openButton;
    
    @FXML
    private MenuItem saveButton;
    
    @FXML
    private MenuItem exitButton;
    
    @FXML
    private MenuItem timerButton;
    
    @FXML
    private MenuItem lineButton;
    
    private File source = null;
    
    private boolean saved = true;
    private boolean wrap = false;
    private boolean control = false;
    
    private Timer timer;
    private App app;

    @FXML
    private void initialize() {
        this.count();      
        this.setTitle();
        this.setEvent();
        
        this.newButton.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        this.openButton.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        this.saveButton.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        this.timerButton.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        this.lineButton.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        
        this.wrapText.setText(this.wrapText.getText() + " (not wrapped)");
        
        this.timer = new Timer();
        TimerTask autoSave = new TimerTask() {
            @Override
            public void run() {
                if (source != null) {
                    save();
                }
            }
        };
        this.timer.schedule(autoSave, 0, 60000);
        
        this.saveLog.setText("--- PROGRAM INITIATED ---\n");
    }
    
    /**
     * setEvent()
     * Basically, it will handle key press event on textarea
     * It will call count() method to process the string
     * And set saved to false
     * Also, it avoids key combination problem by
     * setting a boolean named 'control'
     */
    private void setEvent() {
        this.textLog.setOnKeyPressed(a -> {
            if (a.getCode().equals(KeyCode.CONTROL)) {
                control = true;
            }
        });
        this.textLog.setOnKeyReleased(a -> {
            if (a.getCode().equals(KeyCode.CONTROL)) {
                control = false;
            }
        });
        this.textLog.setOnKeyTyped(a -> {
            if (!control) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        count();
                        saved = false;
                        setTitle();
                    }
                });
            }
        });
    }
    
    /**
     * setTitle()
     * Set the title of the window with current file status
     * It will omit empty file name if the source is null
     * It will omit asterisk if user hasn't saved changes yet
     */
    private void setTitle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               String title = "";
               if (source != null) {
                   title += source.getName();
               }
               else title += ".txt";
               if (!saved) {
                   title += "*";
               }
               title += " - Wombat Text Editor";
               ((Stage)textLog.getScene().getWindow()).setTitle(title);
            }   
        });
    }

    /**
     * newDoc()
     * It will create a new doc with empty source
     * Any saved changes will be purged
     * Any unsaved changes will be redirected to prompt
     */
    @FXML
    private void newDoc() {
        this.handleUnsaved();
        this.source = null;
        this.textLog.setText("");
        this.setTitle();
        this.count();
        saved = true;
    }
    
    /**
     * load()
     * Load a document, as simple as that
     * Of course, any unsaved changes will be redirected to prompt
     * 
     * @throws FileNotFoundException, IOException
     */
    @FXML
    private void load() {
        this.handleUnsaved();
        FileChooser src = new FileChooser();
        src.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Document (*.txt)", "*.txt"));
        src.setInitialFileName("tulisan_orang_ganteng.txt");
        File srcFile = src.showOpenDialog(new Stage());
        if (srcFile != null) {
            this.source = srcFile;
            try {
                saved = true;
                BufferedReader reader = new BufferedReader(new FileReader(this.source));
                String res = "";
                String read;
                try {
                    while ((read = reader.readLine()) != null) {
                        res += read;
                    }
                    this.textLog.setText(res);
                    this.setTitle();
                    this.writeOpenLog();
                    this.count();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * save()
     * Save a document
     * It will call saveAs() if the source is empty or null
     * 
     * @throws IOException
     */
    @FXML
    private void save() {
        if (this.source == null) {
            this.saveAs();
        } else {
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(this.source));
                writer.write(this.textLog.getText());
                writer.close();
                saved = true;
                this.writeSaveLog();
                this.setTitle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * saveAs()
     * Save a new document
     * It will override the current source if exists
     * 
     * @throws IOException
     */
    @FXML
    private void saveAs() {
        FileChooser dest = new FileChooser();
        dest.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Document (*.txt)", "*.txt"));
        dest.setInitialFileName("tulisan_orang_ganteng.txt");
        File destination = dest.showSaveDialog(new Stage());
        if (destination != null) {
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(destination));
                writer.write(this.textLog.getText());
                writer.close();
                saved = true;
                this.source = destination;
                this.setTitle();
                this.writeSaveLog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * close()
     * Close the application AND kill the timer
     */
    @FXML
    public void close() {
        this.handleUnsaved();
        
        this.timer.cancel();
        ((Stage)textLog.getScene().getWindow()).close();
    }
    
    /**
     * setFontSize()
     * Set the textarea font size
     * Must be higher than 10 and lower than 73
     */
    @FXML
    private void setFontSize() {
        TextInputDialog dialog = new TextInputDialog(this.textLog.getFont().getSize() + "");
        dialog.setTitle("Set Font Size");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the font size (10 - 72) : ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            double parsedSize = Double.parseDouble(result.get());
            if (parsedSize < 10 || parsedSize > 72) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Font size is too big, it must be higher than 10 and lower than 72");
                
                alert.showAndWait();
            } else {
                this.textLog.setFont(new Font(parsedSize));
            }
        }
    }
    
    /**
     * toggleWrap()
     * Toggle text wrapping on textarea
     */
    @FXML
    private void toggleWrap() {
        this.wrap = !this.wrap;
        this.textLog.setWrapText(this.wrap);
       
        String mod = this.wrapText.getText().substring(0, 20);
        if (this.wrap) mod += " (wrapped)";
        else mod += " (not wrapped)";
        this.wrapText.setText(mod);
    }
    
    /**
     * setTimer()
     * Reset the current autosave timer with a new, provided value
     * IN MINUTES
     */
    @FXML
    private void setTimer() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Set auto save timer");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter auto save timer (minutes, integer) : ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int milis = Integer.parseInt(result.get()) * 60000;
            if (milis <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Whoooooops");
                alert.setHeaderText(null);
                alert.setContentText("You cannot set the auto save interval to a value lower than zero darling! Try again");
                
                alert.showAndWait();
            } else {
                this.timer = new Timer();
                
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (source != null && saved) {
                            save();
                        }
                    }
                };
                timer.schedule(task, 0, milis);
                
                this.saveLog.setText(this.saveLog.getText() + "RESCHEDULED TIMER TO " + Integer.parseInt(result.get()) + " MINUTES.\n");
            }
        }
    }
    
    /**
     * showAbout()
     * Show about window
     */
    @FXML
    private void showAbout() {
        this.app.showAboutWindow();
    }
    
    /**
     * goToLine()
     * Positions the pointer on the selected line
     */
    @FXML
    private void goToLine() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Go To Line...");
        dialog.setHeaderText(null);
        dialog.setContentText("Destination : ");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int targetLine = Integer.parseInt(result.get());
            int currentLine = Integer.parseInt(this.lineCount.getText());
            if (currentLine < targetLine || targetLine < 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Cannot find line");
                
                alert.showAndWait();
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        int count = 1;
                        String[] arr = textLog.getText().split("");
                        for (int i = 0; i < arr.length; i++) {
                            if (count == targetLine) {
                                textLog.positionCaret(i);
                                break;
                            }
                            if (arr[i].equals("\n")) count++;
                        }
                    }
                });
            }
        }
    }
    
    /**
     * writeSaveLog()
     * Write save time and destination to the log area
     */
    private void writeSaveLog() {
        String res = "SAVE PROCEDURE INITIATED - ";
        res += java.util.Calendar.getInstance().getTime() + "\n";
        res += "Destination Path : " + this.source.getPath() + "\n";
        res += "Save successful\n";
        
        this.saveLog.setText(this.saveLog.getText() + res);
    }
    
    /**
     * writeOpenLog()
     * Write open document time and source to log area
     */
    private void writeOpenLog() {
        String res = "OPEN PROCEDURE INITIATED - ";
        res += java.util.Calendar.getInstance().getTime() + "\n";
        res += "Source Path : " + this.source.getPath() + "\n";
        res += "Load successful\n";
        
        this.saveLog.setText(this.saveLog.getText() + res);
    }
    
    /**
     * handleUnsaved()
     * Basically, it will ask the user if he / she want to save 
     * the currently unsaved work.
     * 
     * If 'Yes' is selected, it will call save() method
     */
    private void handleUnsaved() {
        if (!saved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText(null);
            
            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");
            alert.getButtonTypes().setAll(yes, no);
            alert.setContentText("It seems that you haven\'t saved your work yet, would you like to save before executing your next task?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == yes) {
                this.save();
            }
        }
    }
    
    /**
     * count()
     * Count the occurrences of characters, words, lines, and sentences
     * from the textarea
     */
    private void count() {
        String sub = this.textLog.getText().trim();
        
        int character = sub.length();
        int word = sub.isEmpty() ? 0 : sub.split("\\s+").length;
        int lines = sub.isEmpty() ? 0 : sub.split("\n").length;
        int sentence = sub.isEmpty() ? 0 : sub.replaceAll("\\.{2,}", "\\.").split("\\.").length;
        
        this.characterCount.setText(Integer.toString(character));
        this.wordCount.setText(Integer.toString(word));
        this.lineCount.setText(Integer.toString(lines));
        this.sentenceCount.setText(Integer.toString(sentence));
    }
    
    /**
     * setReference()
     * Used to set reference to main class
     * 
     * @param app
     */
    public void setReference(App app) {
        this.app = app;
    }
}
