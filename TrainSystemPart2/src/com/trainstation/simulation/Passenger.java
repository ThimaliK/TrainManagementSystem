package com.trainstation.simulation;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.Serializable;

public class Passenger implements Serializable {

    private String name;          //full name of a passenger
    private String seatNumber;
    private int secondsInQueue;

    public Passenger(String name, String seatNumber, int secondsInQueue) {
        super();
        this.name=name;
        this.seatNumber=seatNumber;
        this.secondsInQueue=secondsInQueue;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String passengerName) {
        this.name = passengerName;
    }

    public String getSeat() {
        return this.seatNumber;
    }

    public void setSeat(String passengerSeat) {
        this.seatNumber = passengerSeat;
    }

    public int getSecondsInQueue() {
        return this.secondsInQueue;
    }

    public void setSecondsInQueue(int timeInQueue) {
        this.secondsInQueue = timeInQueue;
    }

    public void display(Passenger passenger, AnchorPane root, int y) {

        Label passengerName = new Label(y+". "+passenger.getName()+passenger.getSeat()+" ---");
        Label timeInQueue = new Label(passenger.getSecondsInQueue()+"s");

        if (y<=11) {
            passengerName.setLayoutX(50);
            passengerName.setLayoutY(y * 45);
            timeInQueue.setLayoutX(375);
            timeInQueue.setLayoutY(y * 45);
        } else if (y<=22) {
            passengerName.setLayoutX(475);
            passengerName.setLayoutY((y-11)*45);
            timeInQueue.setLayoutX(800);
            timeInQueue.setLayoutY((y-11)*45);
        } else if (y<=33) {
            passengerName.setLayoutX(900);
            passengerName.setLayoutY((y-22)*45);
            timeInQueue.setLayoutX(1225);
            timeInQueue.setLayoutY((y-22)*45);
        } else {
            passengerName.setLayoutX(1325);
            passengerName.setLayoutY((y-33)*45);
            timeInQueue.setLayoutX(1675);
            timeInQueue.setLayoutY((y-33)*45);
        }
        root.getChildren().addAll(passengerName,timeInQueue);
    }
}
