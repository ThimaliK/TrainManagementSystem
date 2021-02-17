package com.trainstation.simulation;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class PassengerQueue implements Serializable {

    private ArrayList<Passenger> queueArray;
    private int first;
    private int last;
    private int maxStayInQueue;
    private int minStayInQueue;
    private ArrayList<Passenger> onBoardPassengers;
    private String key;                                  //date and trip number to uniquely identify each train queue
    private double averageStayInQueue;
    private int totalSeconds;
    private int maxQueueLength;

    public PassengerQueue(ArrayList<Passenger> queueArray, int first, int last, int maxStayInQueue, int minStayInQueue,
                          ArrayList<Passenger> onBoardPassengers,String key,double averageStayInQueue,int totalSeconds, int maxQueueLength) {
        super();
        this.queueArray=queueArray;
        this.first=first;
        this.last=last;
        this.maxStayInQueue=maxStayInQueue;
        this.minStayInQueue=minStayInQueue;
        this.onBoardPassengers=onBoardPassengers;
        this.key=key;
        this.averageStayInQueue=averageStayInQueue;
        this.totalSeconds=totalSeconds;
        this.maxQueueLength=maxQueueLength;
    }

    public ArrayList<Passenger> getQueueArray() {
        return this.queueArray;
    }

    public void setQueueArray(ArrayList<Passenger> queue) {
        this.queueArray = queue;
    }

    public void setFirst(int firstIndex) {
        this.first = firstIndex;
    }

    public int getFirst() {
        return this.first;
    }

    public void setLast(int size) {
        this.last = size;
    }

    public int getLast() {
        return this.last;
    }

    public void setMaxStayInQueue(int maxStayInQueue) {
        this.maxStayInQueue=maxStayInQueue;
    }

    public int getMaxStayInQueue() {
        return this.maxStayInQueue;
    }

    public void setMinStayInQueue(int minStayInQueue) {
        this.minStayInQueue=minStayInQueue;
    }

    public int getMinStayInQueue() {
        return this.minStayInQueue;
    }

    public void setOnBoardPassengers(ArrayList<Passenger> onBoard) { this.onBoardPassengers = onBoard; }

    public ArrayList<Passenger> getOnBoardPassengers() {
        return onBoardPassengers;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key=key;
    }

    public void setAverageStayInQueue (double averageStayInQueue) {
        this.averageStayInQueue=averageStayInQueue;
    }

    public double getAverageStayInQueue() {
        return this.averageStayInQueue;
    }

    public int getTotalSeconds() {
        return this.totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds=totalSeconds;
    }

    public void setMaxQueueLength(int maxQueueLength) {
        this.maxQueueLength = maxQueueLength;
    }

    public int getMaxQueueLength() {
        return this.maxQueueLength;
    }

    public void add(TrainStation trainStation, Stage stage) {

        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 1500, 950);
        stage.setScene(scene);
        root.setPadding(new Insets(30));
        stage.setTitle("Train Station Simulation | Waiting Room and Train Queue Lists");
        scene.setRoot(root);
        stage.show();

        PassengerQueue passengerQueue = trainStation.getTrainQueue();
        ArrayList<Passenger> currentWaitingRoomList = trainStation.getWaitingRoom();
        ArrayList<Passenger> trainQueueArray = passengerQueue.getQueueArray();
        int first = 0;
        passengerQueue.setFirst(first);

        Random random = new Random();
        int diceNumber = 0;
        while (true) {
            diceNumber = random.nextInt(7);
            if (diceNumber != 0 && diceNumber <= trainStation.getWaitingRoom().size()) break;  //dice number must NOT exceed the size of the waiting room
        }
        passengerQueue.setLast(diceNumber);

        if (trainStation.getWaitingRoom().size() > 1) {
            for (int i = getFirst(); i < getLast(); i++) {
                trainQueueArray.add(trainStation.getWaitingRoom().get(i));
                Passenger passenger = trainStation.getWaitingRoom().get(i);
                currentWaitingRoomList.remove(passenger);
                setLast(getLast() - 1);     //prevents IndexOutOfBounds exception
            }

        } else if (trainStation.getWaitingRoom().size() == 1) {
            trainQueueArray.add(trainStation.getWaitingRoom().get(0));
            currentWaitingRoomList.remove(trainStation.getWaitingRoom().get(0));
            Alert fullQueue = new Alert(Alert.AlertType.INFORMATION);
            fullQueue.setContentText("ALL Passengers who were in the Waiting Room has been added to the Train Queue");
            fullQueue.setHeaderText("The Waiting room is Empty");
            fullQueue.showAndWait();
        }

        passengerQueue.setQueueArray(trainQueueArray);
        trainStation.setWaitingRoom(currentWaitingRoomList);
        trainStation.setTrainQueue(passengerQueue);

        Label waitingRoomTitle = new Label("Current Waiting Room List");
        waitingRoomTitle.setLayoutX(50);
        waitingRoomTitle.setLayoutY(10);
        waitingRoomTitle.setStyle("-fx-font: 18px Tahoma;");
        Label trainQueueTitle = new Label("Current Train Queue");
        trainQueueTitle.setLayoutX(750);
        trainQueueTitle.setLayoutY(10);
        trainQueueTitle.setStyle("-fx-font: 18px Tahoma;");

        for (int i = 0; i < trainStation.getWaitingRoom().size(); i++) {
            Label waitingRoomItem = new Label();
            String name = trainStation.getWaitingRoom().get(i).getName();
            String seat = trainStation.getWaitingRoom().get(i).getSeat();
            waitingRoomItem.setText(i + 1 + ". " + name + seat);
            if (i <= 20) {
                waitingRoomItem.setLayoutY(i + ((i + 1) * 40));
                waitingRoomItem.setLayoutX(50);
            } else {
                waitingRoomItem.setLayoutX(400);
                waitingRoomItem.setLayoutY(i - 21 + ((i - 20) * 40));
            }
            waitingRoomItem.setStyle("-fx-font: 15px Tahoma;");
            root.getChildren().add(waitingRoomItem);
        }

        for (int i = 0; i < passengerQueue.getQueueArray().size(); i++) {
            Label queueArrayItem = new Label();
            String name = passengerQueue.getQueueArray().get(i).getName();
            String seat = passengerQueue.getQueueArray().get(i).getSeat();
            queueArrayItem.setText(i + 1 + ". " + name + seat);
            if (i <= 20) {
                queueArrayItem.setLayoutY(i + ((i + 1) * 40));
                queueArrayItem.setLayoutX(750);
            } else {
                queueArrayItem.setLayoutY(i - 21 + ((i - 20) * 40));
                queueArrayItem.setLayoutX(1100);
            }
            queueArrayItem.setStyle("-fx-font: 15px Tahoma;");
            root.getChildren().add(queueArrayItem);
        }

        Button back = new Button("Back To Menu");
        back.setLayoutY(900);
        back.setLayoutX(1350);
        root.getChildren().addAll(back, waitingRoomTitle, trainQueueTitle);

        back.setOnAction(e -> {
            stage.close();
            try {
                TrainStation.menu(stage, trainStation, passengerQueue, getKey());
            } catch (IOException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void remove(TrainStation trainStation, Stage stage) throws IOException, ClassNotFoundException, ParseException {

        PassengerQueue passengerQueue = trainStation.getTrainQueue();
        ArrayList<Passenger> waitingRoomArray = trainStation.getWaitingRoom();
        ArrayList<Passenger> trainQueueArray = passengerQueue.getQueueArray();

        System.out.println("------------Removing Passenger from the Train Queue--------------");
        System.out.println("Please enter first name of the Passenger:");
        Scanner sc = new Scanner(System.in);
        String deletingFirstName = sc.next().toUpperCase().trim();

        System.out.println("Please enter last name of the Passenger(If there is no last name, please press X): ");
        Scanner sc2 = new Scanner(System.in);
        String deletingLastName = sc2.next().toUpperCase().trim();

        String deletingFullName;
        if (deletingLastName.equals("X") || deletingLastName.equals("x")) {    //in the event that last name was not stored in booking details
            deletingFullName = deletingFirstName;
        } else {
            deletingFullName = deletingFirstName+" "+deletingLastName;
        }

        //ticket numbers were given only if multiple seats were booked under the same name
        System.out.println("Please enter the ticket number of the Passenger(If there is no ticket number mentioned in train queue list, please press X): ");
        Scanner ticket = new Scanner(System.in);
        String deletingTicket = ticket.next().trim();

        String deletingPassengerName;
        if (deletingTicket.equals("X") || deletingTicket.equals("x") ) {
            deletingPassengerName = deletingFullName + " - ";
        } else {
            deletingPassengerName = deletingFullName + " - Ticket - " + deletingTicket + " - ";
        }

        Passenger passenger;
        for (int i = 0; i < trainQueueArray.size(); i++) {
            passenger = trainQueueArray.get(i);
            String name = passenger.getName();
            if (deletingPassengerName.equals(name)) {
                trainQueueArray.remove(passenger);
                passengerQueue.setQueueArray(trainQueueArray);
                waitingRoomArray.remove(passenger);
                trainStation.setWaitingRoom(waitingRoomArray);
                trainStation.setTrainQueue(passengerQueue);
                System.out.println("\nPassenger " + deletingPassengerName + " was deleted from the train queue\n");
                break;
            }
            else if (i==(trainQueueArray.size()-1) && !deletingPassengerName.equals(name)) {  //if the passenger does not exist in the train queue
                System.out.println("This Passenger does not exist in the Train Queue");
            }
        }
        TrainStation.menu(stage,trainStation,passengerQueue, getKey());
    }

    public void display(TrainStation trainStation, Stage stage) {

            PassengerQueue passengerQueue = trainStation.getTrainQueue();
            ArrayList<Passenger> waitingRoomArray = trainStation.getWaitingRoom();
            ArrayList<Passenger> trainQueueArray = passengerQueue.getQueueArray();

            AnchorPane root = new AnchorPane();
            Scene scene = new Scene(root, 1800, 975);
            stage.setTitle("Train Station Simulation | Visualized Train Queue");
            stage.setScene(scene);
            scene.setRoot(root);
            stage.show();

            Label title = new Label("Visualized Train Queue");
            title.setLayoutX(800); title.setLayoutY(30); title.setStyle("-fx-font: 23px Tahoma;");
            Label trip = new Label(getKey());
            trip.setLayoutY(70); trip.setLayoutX(850); trip.setStyle("-fx-font: 20px Tahoma;");

            Label slot;
            Passenger passenger;
            int columns = 13, rows = 4, vertical = 72;
            for (int i = 2; i < columns; ++i) {
                String seatColumn;
                int seatRow = i - 1;
                for (int j = 1; j <= rows; ++j) {
                    if (j == 1) {
                        slot = new Label();
                        slot.setLayoutX(50);
                        slot.setLayoutY(vertical * i);
                        seatColumn = "A";
                    } else if (j == 2) {
                        slot = new Label();
                        slot.setLayoutX(465);
                        slot.setLayoutY(vertical * i);
                        seatColumn = "B";
                    } else if (j == 3) {
                        slot = new Label();
                        slot.setLayoutX(935);
                        slot.setLayoutY(vertical * i);
                        seatColumn = "C";
                    } else {
                        slot = new Label();
                        slot.setLayoutX(1350);
                        slot.setLayoutY(vertical * i);
                        seatColumn = "D";
                    }

                    String seatNumber = seatColumn + seatRow;
                    slot.setText("  "+seatNumber+"\n"+"  NotBooked");
                    slot.setStyle("-fx-background-color:#9db3b2;");

                    for (int t= 0; t<trainQueueArray.size();t++) {
                        passenger = trainQueueArray.get(t);
                        String seatInTrainQueue = passenger.getSeat();
                        if (seatNumber.equals(seatInTrainQueue)) {
                            String nameInTrainQueue = passenger.getName() ;
                            slot.setText("  "+seatNumber+"\n"+"  "+nameInTrainQueue+"in Train Queue");
                            slot.setStyle("-fx-background-color:#56d6b8;");
                        }
                    }

                    for (int w = 0; w<waitingRoomArray.size(); w++) {
                        passenger = waitingRoomArray.get(w);
                        String seatInWaitingList = passenger.getSeat();
                        if (seatNumber.equals(seatInWaitingList)) {
                            slot.setText("  "+seatNumber+"\n"+"  Empty Seat");
                            slot.setStyle("-fx-background-color:#4ebec2;");
                        }
                    }

                    for (int b= 0; b<passengerQueue.getOnBoardPassengers().size();b++) {
                        passenger = getOnBoardPassengers().get(b);
                        String seatInOnBoardPassengers = passenger.getSeat();
                        if (seatNumber.equals(seatInOnBoardPassengers)) {
                            String nameInTrainQueue = passenger.getName() ;
                            slot.setText("  "+seatNumber+"\n"+"  "+nameInTrainQueue+"On-Board");
                            slot.setStyle("-fx-background-color:#67e081;");
                        }
                    }

                    slot.setMinWidth(400);
                    slot.setMinHeight(60);
                    root.getChildren().add(slot);

                    if ((seatColumn.equals("C") && seatRow == 11) || (seatColumn.equals("D") && seatRow == 11)) {
                        root.getChildren().remove(slot);
                    }
                }
            }

        Button back = new Button("Back To Menu");
        back.setLayoutY(900); back.setLayoutX(1632);
        root.getChildren().addAll(back,title,trip);

        back.setOnAction(e -> {
            stage.close();
            try {
                TrainStation.menu(stage,trainStation,passengerQueue, getKey());
            } catch (IOException | ClassNotFoundException | ParseException ex) {
                ex.printStackTrace();
            }
        });
    }
}
