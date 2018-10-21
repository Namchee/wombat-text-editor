/**
 * Wombat Text Editor - About Window Controller class
 * 
 * @author Namchee
 * @version 1.1
 */

package app.view;

import app.App;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class AboutController {
    @FXML
    private Hyperlink link;
    
    private App app;
    
    @FXML
    private void initialize() {
        
    }
    
    @FXML
    private void link() {
        this.app.getHostServices().showDocument("https://github.com/Namchee/wombat-text-editor");;
    }
    
    public void setReference(App app) {
        this.app = app;
    }
}
