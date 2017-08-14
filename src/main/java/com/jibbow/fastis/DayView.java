package com.jibbow.fastis;

import com.jibbow.fastis.rendering.DayViewRenderer;
import com.jibbow.fastis.components.DayPane;
import com.jibbow.fastis.components.TimeAxis;
import com.jibbow.fastis.components.TimeIndicator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jibbow on 8/12/17.
 */
public class DayView extends CalendarView {

    Node headerPane;
    Node allDayPane;
    DayPane dayPane;
    TimeAxis timeAxis;
    TimeIndicator timeIndicator;
    DayViewRenderer renderer;

    public DayView(LocalDate date, Calendar... calendar) {
        this(new SimpleObjectProperty<>(date), calendar);
    }

    public DayView(ObjectProperty<LocalDate> date, Calendar... calendar) {
        this(date, new DayViewRenderer(), calendar);
    }

    public DayView(ObjectProperty<LocalDate> date, DayViewRenderer renderer, Calendar... calendar) {
        this.getStylesheets().add(DayView.class.getClassLoader().getResource("css/DayView.css").toString());
        this.setPrefWidth(300);
        this.setPrefHeight(400);

        // set date and displayed calendars
        this.dateProperty = date;
        for (int i = 0; i < calendar.length; i++) {
            this.getCalendars().add(calendar[i]);
        }

        this.renderer = renderer;

        // set layout for this pane
        RowConstraints headerRow = new RowConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE);
        RowConstraints allDayRow = new RowConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE);
        RowConstraints dayPaneRow = new RowConstraints(150, 500, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.TOP, true);
        ColumnConstraints columnConstraints = new ColumnConstraints(100, 200, Double.POSITIVE_INFINITY, Priority.SOMETIMES, HPos.LEFT, true);
        this.getRowConstraints().addAll(headerRow, allDayRow, dayPaneRow);
        this.getColumnConstraints().add(columnConstraints);


        getDate().addListener(observable -> {
            setContent();
        });

        setContent();
    }


    private void setContent() {
        this.getChildren().clear();

        // get a list of appointments of all calendars for the current day
        List<Appointment> allAppointments = calendars.stream()
                .flatMap(cal -> cal.getAppointmentsFor(dateProperty.get()).stream())
                .collect(Collectors.toList());

        this.headerPane = renderer.createHeaderPane(this);
        this.dayPane = new DayPane(dateProperty.get());
        this.timeAxis = new TimeAxis(LocalTime.MIN, LocalTime.MAX, Duration.ofMinutes(60));
        this.timeIndicator = new TimeIndicator(dayPane);
        this.allDayPane = renderer.createAllDayPane(allAppointments.parallelStream()
                .filter(appointment -> appointment.isFullDayProperty().get()).collect(Collectors.toList()));
        // populate DayPane
        allAppointments.forEach(a -> dayPane.addAppointment(a));


        // ScrollPane that contains the DayPane and the TimeAxis
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color:transparent;"); // remove gray border

        // holds a column for the TimeAxis on the left side and the DayPane on the right side
        GridPane dayPaneHolder = new GridPane();
        ColumnConstraints timeColumn = new ColumnConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.ALWAYS, HPos.LEFT, false);
        ColumnConstraints appointmentsColumn = new ColumnConstraints(100, 100, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true);
        RowConstraints rowConstraint = new RowConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.TOP, true);
        dayPaneHolder.getColumnConstraints().addAll(timeColumn, appointmentsColumn);
        dayPaneHolder.getRowConstraints().add(rowConstraint);


        dayPaneHolder.add(timeAxis, 0, 0);
        dayPaneHolder.add(timeIndicator, 1, 0);
        scrollPane.setContent(dayPaneHolder);

        // ordering is important:
        this.add(scrollPane, 0, 2);
        this.add(allDayPane, 0, 1);
        this.add(headerPane, 0, 0);
    }
}
