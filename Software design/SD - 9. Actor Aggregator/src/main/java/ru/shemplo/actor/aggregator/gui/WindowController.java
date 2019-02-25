package ru.shemplo.actor.aggregator.gui;

import static ru.shemplo.snowball.utils.fp.FunctionalUtils.*;
import static ru.shemplo.snowball.utils.fp.FunctionalUtils.Case.*;

import java.net.URL;

import java.util.ResourceBundle;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import ru.shemplo.actor.aggregator.RunSearchAggregator;
import ru.shemplo.actor.aggregator.engine.JAggregateEngine;
import ru.shemplo.actor.aggregator.engine.units.JSRequest;
import ru.shemplo.actor.aggregator.engine.units.JSResponse;
import ru.shemplo.actor.aggregator.engine.units.JSResponse.JSResponseRow;
import scala.util.Try;

public class WindowController implements Initializable {

    private final Props ENGINE_PROPS = Props.create (JAggregateEngine.class);
    private final ActorSystem actors = RunSearchAggregator.getActors ();
    
    private final AtomicInteger requestsCounter = new AtomicInteger ();
    
    @FXML private ListView <JSResponseRow> resultsList;
    @FXML private ListView <JSRequest> historyList;
    
    @FXML private TextField searchQuery;
    @FXML private Button searchButton;
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        searchQuery.setOnKeyReleased (ke -> {
            KeyCode code = ke.getCode ();
            if (KeyCode.ENTER.equals (code)) {
                sendSearchRequest ();
            }
        });
        
        searchButton.setOnMouseClicked (me -> {
            MouseButton button = me.getButton ();
            if (MouseButton.PRIMARY.equals (button)) {
                sendSearchRequest ();
            }
        });
        
        historyList.setCellFactory (__ -> new HistoryListCell (this));
        historyList.editableProperty ().set (false);
        
        resultsList.setCellFactory (__ -> new ResponseListCell ());
        resultsList.editableProperty ().set (false);
    }
    
    private void sendSearchRequest () {
        final String query = searchQuery.getText ().trim ();
        if (query.length () == 0) {
            System.err.println ("Search query is empty");
            System.err.flush ();
            
            return;
        }
        
        ActorRef actor = actors.actorOf (ENGINE_PROPS, getNextActorName ());
        final JSRequest request = new JSRequest (query);
        historyList.getItems ().add (request);
        
        Patterns.ask (actor, request, 10000L).onComplete (
            response -> switch$ (response,
                caseOf (r -> r instanceof Try, Try::get, r -> {
                    if (r instanceof JSResponse) {
                        JSResponse resp = (JSResponse) r;
                        request.setResponse (resp);
                        historyList.refresh ();
                    }
                    
                    return r;
                })), 
            actors.dispatcher ());
    }
    
    private String getNextActorName () {
        return String.format ("search-actor-%d", 
            requestsCounter.getAndIncrement ());
    }
    
    public void showResponse (JSResponse response) {
        if (response == null) { return; }
        
        Platform.runLater (() -> {
            resultsList.getItems ().clear (); // Clear previous response
            resultsList.getItems ().addAll (response.getRows ());
        });
    }
    
}
