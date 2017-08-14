package com.jibbow.fastis.rendering;

import com.jibbow.fastis.Appointment;
import com.jibbow.fastis.DayView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Jibbow on 8/13/17.
 */
public class DayViewRenderer extends DayPaneRenderer {

    public Node createAllDayPane(List<Appointment> appointments) {
        AnchorPane pane = new AnchorPane();
        Label day = new Label("all day");
        pane.getChildren().add(day);
        pane.getStyleClass().add("alldaypane");
        return pane;
    }

    public Node createHeaderPane(DayView calView) {
        HBox container = new HBox();
        container.setAlignment(Pos.BOTTOM_LEFT);
        container.getStyleClass().add("headerpane");

        Label lblWeekday = new Label(calView.getDate().get().format(DateTimeFormatter.ofPattern("EEE")));
        lblWeekday.getStyleClass().add("label-weekday");

        Label lblDate = new Label(calView.getDate().get().toString());
        lblDate.getStyleClass().add("label-date");

        container.getChildren().add(lblWeekday);
        container.getChildren().add(lblDate);
        return container;
    }
}
