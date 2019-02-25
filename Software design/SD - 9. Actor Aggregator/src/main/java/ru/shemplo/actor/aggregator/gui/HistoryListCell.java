package ru.shemplo.actor.aggregator.gui;

import java.util.Date;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import ru.shemplo.actor.aggregator.engine.units.JSRequest;
import ru.shemplo.actor.aggregator.engine.units.JSResponse;

public class HistoryListCell extends ListCell <JSRequest> {
    
    private final WindowController controller;
    
    public HistoryListCell (WindowController controller) {
        this.controller = controller;
        //setBackground (Background.EMPTY);
        
        setOnMouseClicked (me -> {
            MouseButton button = me.getButton ();
            if (MouseButton.PRIMARY.equals (button) && !isEmpty ()
                    && getItem () != null) {
                JSResponse response = getItem ().getResponse ();
                controller.showResponse (response);
            }
        });
    }
    
    @Override
    protected void updateItem (JSRequest item, boolean empty) {
        super.updateItem (item, empty);
        if (item == null || empty) {
            setGraphic (null);
            return;
        }
        
        final VBox root = new VBox ();
        
        final Label query = new Label (item.getQuery ());
        query.getStyleClass ().add ("query-title");
        root.getChildren ().add (query);
        
        if (item.getResponse () == null) {
            final Label status = new Label ("Pending request...");
            status.getStyleClass ().add ("query-status");
            root.getChildren ().add (status);
        } else {
            final JSResponse response = item.getResponse ();
            Date date = new Date (response.getFinishTime ());
            
            String value = String.format ("%s, %d results found", date.toString (), 
                                          response.getRows ().size ());
            final Label status = new Label (value);
            status.getStyleClass ().add ("query-status");
            root.getChildren ().add (status);
            
            if (response.getJustFinished ()) {
                controller.showResponse (response);
                response.setJustFinished (false);
            }
        }
        
        setWrapText (true);
        setGraphic (root);
    }
    
}
