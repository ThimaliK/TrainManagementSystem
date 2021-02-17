package com.trainstation.simulation;

import com.mongodb.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.mongodb.MongoClient;
import java.util.List;

public class TrainStation extends Application  {

    private ArrayList<Passenger> waitingRoom;
    public ArrayList<Passenger> getWaitingRoom() { return this.waitingRoom; }
    public void setWaitingRoom(ArrayList<Passenger> waitingRoom) { this.waitingRoom = waitingRoom; }

    private PassengerQueue trainQueue;
    public PassengerQueue getTrainQueue() {
        return this.trainQueue;
    }
    public void setTrainQueue(PassengerQueue passengers) {
        this.trainQueue = passengers;
    }

    Stage stage;
    private static HashMap<String,HashMap> allBookings = new HashMap<>();
    private static HashMap<String,ArrayList<Passenger>> waitingRooms = new HashMap<>();
    private static HashMap<String,PassengerQueue> trainQueues = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        settingUpWaitingRoom(stage);
    }

    public static void main(String[] args) {

        try {
            System.out.println("Loading Data from Database...");
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("train-bookings");
            DBCollection coll = db.getCollection("train");
            DBObject dbo = coll.findOne();
            Object data = dbo.get("allBookings");
            allBookings = (HashMap<String, HashMap>) data;     //loads all booking information into the data structure
            System.out.println("Data loading is complete");
        }catch (Exception e) {
            System.out.println("No data stored yet");
        }
        System.out.println("---------------------Train Station Simulation---------------------");
        Application.launch(args);
    }

    public static void settingUpWaitingRoom(Stage stage) {   //adds passengers to waiting room for a particular trip

        String key;
        try {

            HashMap bookings = new HashMap<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); //sets date pattern
            System.out.println("Please Enter Date : (example - 25/05/2020)");
            Scanner sc = new Scanner(System.in);
            String date = sc.nextLine();
            Date trueDate = formatter.parse(date);
            String actualDate = formatter.format(trueDate);

            try {

                System.out.println("Enter 1 if the trip is from Colombo to Badulla");
                System.out.println("Enter 2 if the trip is from Badulla to Colombo");
                String trip = sc.next();

                if (trip.equals("1") || trip.equals("2")) {      //checks if the entered trip number is correct
                    key = actualDate + " - " + trip;

                    if (allBookings.containsKey(key)) {         //checks if the entered trip exists
                        bookings = allBookings.get(key);
                    }

                    ArrayList<String> nameList = new ArrayList<>(bookings.keySet());
                    ArrayList<List> seatList = new ArrayList<>(bookings.values());
                    ArrayList<Passenger> allPassengers = new ArrayList<>();

                    if (bookings.size()>0) {                              //makes each passenger unique
                        for (int i = 0; i < bookings.size(); i++) {
                            ArrayList<String> seats = new ArrayList<>(seatList.get(i));
                            String name;
                            String seat;
                            String name2;
                            String seat2;

                            if (seats.size() > 1) {   //ticket number is given only if multiple seats were booked under the same name

                                for (int j = 0; j < seats.size(); j++) {
                                    Passenger passenger = new Passenger("", "", 0);
                                    name = nameList.get(i) + " - Ticket - " + (j + 1) + " - ";
                                    passenger.setName(name);
                                    seat = String.valueOf(seats.get(j));
                                    passenger.setSeat(seat);
                                    allPassengers.add(passenger);
                                }

                            } else {
                                Passenger passenger = new Passenger("", "", 0);
                                name2 = nameList.get(i) + " - ";
                                passenger.setName(name2);
                                seat2 = String.valueOf(seats.get(0));
                                passenger.setSeat(seat2);
                                allPassengers.add(passenger);
                            }
                        }

                        TrainStation trainStation = new TrainStation();
                        trainStation.setWaitingRoom(allPassengers);
                        ArrayList<Passenger> forQueueArray = new ArrayList<>();
                        ArrayList<Passenger> forOnBoardPassengers = new ArrayList<>();
                        PassengerQueue passengerQueue = new PassengerQueue(forQueueArray, 0, 0, 0,
                                0, forOnBoardPassengers, key, 0, 0,0);
                        trainStation.setTrainQueue(passengerQueue);
                        menu(stage, trainStation, passengerQueue, key);
                    }

                    else {
                        System.out.println("This Trip does not exist in the database, Please try a different trip");
                        settingUpWaitingRoom(stage);
                    }

                } else {
                    System.out.println("Invalid Trip Number");
                    settingUpWaitingRoom(stage);
                }

            } catch (Exception e) {
                System.out.println("First add passengers to train queue");
                settingUpWaitingRoom(stage);
            }

        } catch (Exception e) {
            System.out.println("Invalid Input. Try again.");
            settingUpWaitingRoom(stage);
        }
    }

    public static void menu(Stage stage, TrainStation trainStation, PassengerQueue passengerQueue, String key)
            throws IOException, ClassNotFoundException, ParseException {

        System.out.println("\nMENU-----------------------------------------------------------");
        System.out.println("A: Add a random number (1-6) of Passengers to the Train Queue");
        System.out.println("V: Visualize the Train Queue");
        System.out.println("D: Delete a Passenger from the Train Queue");
        System.out.println("S: Store Train Queue details into a plain text file");
        System.out.println("L: Load Train Queue data back from the file into trainQueue");
        System.out.println("R: Run the Simulation and produce Report");
        System.out.println("Q: Quit Application\n");
        System.out.print("Enter your option here: ");
        Scanner sc = new Scanner(System.in);
        String option = sc.next();

        switch (option) {
            case "A":
            case "a":
                addPassengerToQueue(stage,trainStation,passengerQueue);
                break;
            case "V":
            case "v":
                visualizeTrainQueue(trainStation,stage,passengerQueue);
                break;
            case "S":
            case "s":
                storeTrainQueue(trainStation,stage,passengerQueue);
                break;
            case "D":
            case "d":
                removePassengerFromQueue(trainStation,stage,passengerQueue);
                break;
            case "L":
            case "l":
                loadTrainQueue(trainStation,stage,key,passengerQueue);
                break;
            case "R":
            case "r":
                runSimulationAndDisplayReport(passengerQueue,trainStation,stage);
                break;
            case "Q":
            case "q":
                System.out.println("The Program is Exiting...");
                break;
            default:
                System.out.println("Invalid... Re-enter");
                menu(stage,trainStation,passengerQueue, key);
        }

    }

    private static void storeTrainQueue(TrainStation trainStation, Stage stage, PassengerQueue passengerQueue)
            throws IOException, ClassNotFoundException, ParseException {

        File file  = new File("trainQueue.txt");

        if (file.length()>0) {             //loads file contents if there are any, to avoid overriding the content, when storing
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (true) {
                try {
                    waitingRooms = (HashMap<String, ArrayList<Passenger>>) ois.readObject();
                    trainQueues = (HashMap<String, PassengerQueue>) ois.readObject();
                } catch (EOFException e) {
                    System.out.println("File Reading Complete");
                    break;
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        waitingRooms.put(passengerQueue.getKey(),trainStation.getWaitingRoom());
        trainQueues.put(passengerQueue.getKey(),trainStation.getTrainQueue());
        oos.writeObject(waitingRooms);
        oos.writeObject(trainQueues);
        System.out.println("Train Queue has been Stored to Text File");
        menu(stage,trainStation,passengerQueue, passengerQueue.getKey());

    }

    private static void loadTrainQueue(TrainStation trainStation, Stage stage, String key,PassengerQueue passengerQueue)
            throws IOException, ClassNotFoundException, ParseException {
        try {
            File file = new File("trainQueue.txt");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (true) {
                try {
                    waitingRooms = (HashMap<String, ArrayList<Passenger>>) ois.readObject(); //loads objects to data structure
                    trainQueues = (HashMap<String, PassengerQueue>) ois.readObject();
                } catch (EOFException e) {
                    System.out.println("File Reading Complete");
                    break;
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); //sets date pattern
            System.out.println("Please Enter Date : (example - 25/05/2020)");
            Scanner sc = new Scanner(System.in);
            String date = sc.nextLine();
            Date trueDate = formatter.parse(date);
            String actualDate = formatter.format(trueDate);

            try {

                System.out.println("Enter 1 if the trip is from Colombo to Badulla");
                System.out.println("Enter 2 if the trip is from Badulla to Colombo");
                String trip = sc.next();

                if (trip.equals("1") || trip.equals("2")) {      //checks if the entered trip number is correct
                    key = actualDate + " - " + trip;

                    if (waitingRooms.containsKey(key) && trainQueues.containsKey(key)) {
                        trainStation.setWaitingRoom(waitingRooms.get(key));
                        passengerQueue = trainQueues.get(key);
                        trainStation.setTrainQueue(passengerQueue);
                        passengerQueue.setKey(key);
                        System.out.println("Train Queue "+key+" is now loaded");

                    } else {
                        System.out.println("This Train Queue does not exist in file");
                    }

                } else {
                    System.out.println("This Train Queue does not exist in file");
                }

            } catch (Exception e) {
                System.out.println("Incorrect date entered...");
            }
        } catch (Exception e) {
            System.out.println("No data is stored in file at the moment");
        }

        menu(stage,trainStation,passengerQueue, key);
    }

    private static void runSimulationAndDisplayReport(PassengerQueue passengerQueue, TrainStation trainStation,
                                                      Stage stage) throws ParseException, IOException, ClassNotFoundException {
        try {

            int queueLength = passengerQueue.getQueueArray().size();
            if (queueLength>passengerQueue.getMaxQueueLength()) {
                passengerQueue.setMaxQueueLength(queueLength);
            }

            ArrayList<Passenger> onBoardList = passengerQueue.getOnBoardPassengers();
            int totalTime = 0;

            if (trainStation.getTrainQueue().getQueueArray().size() > 0 || passengerQueue.getOnBoardPassengers().size() == 0) {
                for (int i = 0; i < passengerQueue.getQueueArray().size(); i++) {

                    onBoardList.add(passengerQueue.getQueueArray().get(i));

                    //generates processing time of each passenger
                    Random random = new Random();
                    int diceNumber1 = 0;
                    int diceNumber2 = 0;
                    int diceNumber3 = 0;
                    while (true) {
                        diceNumber1 = random.nextInt(7);
                        if (diceNumber1 != 0) break;
                    }
                    while (true) {
                        diceNumber2 = random.nextInt(7);
                        if (diceNumber2 != 0) break;
                    }
                    while (true) {
                        diceNumber3 = random.nextInt(7);
                        if (diceNumber3 != 0) break;
                    }

                    int additionalWaitTime = diceNumber1 + diceNumber2 + diceNumber3;
                    passengerQueue.getQueueArray().get(i).setSecondsInQueue(passengerQueue.getTotalSeconds() + additionalWaitTime);
                    passengerQueue.setTotalSeconds(passengerQueue.getTotalSeconds() + additionalWaitTime);
                    totalTime = passengerQueue.getQueueArray().get(i).getSecondsInQueue() + totalTime;
                }

                passengerQueue.setOnBoardPassengers(onBoardList);
                passengerQueue.getQueueArray().clear();
                trainStation.setTrainQueue(passengerQueue);

                int maxTime = onBoardList.get(0).getSecondsInQueue();
                int minTime = onBoardList.get(0).getSecondsInQueue();

                for (int i = 0; i < onBoardList.size(); i++) {

                    if (onBoardList.get(i).getSecondsInQueue() < maxTime) {
                        minTime = onBoardList.get(i).getSecondsInQueue();
                    } else {
                        maxTime = onBoardList.get(i).getSecondsInQueue();
                    }
                }

                double averageStay = totalTime / passengerQueue.getOnBoardPassengers().size();
                double roundedAverageStay = Math.round(averageStay * 10.0) / 10.0;

                passengerQueue.setMaxStayInQueue(maxTime);
                passengerQueue.setMinStayInQueue(minTime);
                passengerQueue.setAverageStayInQueue(roundedAverageStay);

            } else {
                Alert emptyQueue = new Alert(Alert.AlertType.INFORMATION);
                emptyQueue.setContentText("The simulation of this trip - " + passengerQueue.getKey() + " has already been run and stored.");
                emptyQueue.setHeaderText("Simulation has already been run");
                emptyQueue.showAndWait();
                stage.show();
            }

            AnchorPane root = new AnchorPane();
            Scene scene = new Scene(root, 1750, 850);
            stage.setTitle("Train Station | Simulation Report");
            stage.setScene(scene);
            scene.setRoot(root);
            stage.show();

            Label title = new Label("Simulation Report - " + passengerQueue.getKey());
            title.setLayoutX(800);
            title.setLayoutY(5);
            title.setStyle("-fx-font: 20px Tahoma;");

            Label queueLengthLabel = new Label("Maximum Train Queue length is "+passengerQueue.getMaxQueueLength());
            queueLengthLabel.setLayoutX(50);
            queueLengthLabel.setLayoutY(570);
            queueLengthLabel.setStyle("-fx-font: 18px Tahoma;");

            Label maxStay = new Label("Maximum time spent in queue by a passenger is " + passengerQueue.getMaxStayInQueue() + "s");
            maxStay.setLayoutX(50);
            maxStay.setLayoutY(610);
            maxStay.setStyle("-fx-font: 18px Tahoma;");

            Label minStay = new Label("Minimum time spent in queue by a passenger is " + passengerQueue.getMinStayInQueue() + "s");
            minStay.setLayoutX(50);
            minStay.setLayoutY(650);
            minStay.setStyle("-fx-font: 18px Tahoma;");

            Label avgStay = new Label("Average time spent in queue by a passenger is " + passengerQueue.getAverageStayInQueue() + "s");
            avgStay.setLayoutY(690);
            avgStay.setLayoutX(50);
            avgStay.setStyle("-fx-font: 18px Tahoma;");

            Button back = new Button("Back To Menu");
            back.setLayoutY(760);
            back.setLayoutX(50);

            for (int i = 0; i < passengerQueue.getOnBoardPassengers().size(); i++) {
                Passenger passenger = passengerQueue.getOnBoardPassengers().get(i);
                passenger.display(passenger, root, i + 1);
            }

            back.setOnAction(e -> {
                stage.close();
                try {
                    saveReportToTextFile(passengerQueue.getOnBoardPassengers(), trainStation, passengerQueue, stage);
                } catch (IOException | ClassNotFoundException | ParseException ex) {
                    ex.printStackTrace();
                }
            });

            root.getChildren().addAll(back, queueLengthLabel, title, maxStay, minStay, avgStay);
        } catch (Exception e) {
            System.out.println("Add passengers to train queue first");
            menu(stage,trainStation,passengerQueue, passengerQueue.getKey());
        }
    }

    private static void saveReportToTextFile(ArrayList<Passenger> onBoardList, TrainStation trainStation,
                                             PassengerQueue passengerQueue, Stage stage)
            throws IOException, ClassNotFoundException, ParseException {

        Writer fw = new FileWriter("reports.txt",true);
        fw.write("Simulation Report - "+passengerQueue.getKey()+"\n\n");
        fw.flush();

        for (int i = 0; i<onBoardList.size(); i++) {
            String toFile = i+1+". "+onBoardList.get(i).getName()+onBoardList.get(i).getSeat()+" - "+onBoardList.get(i).getSecondsInQueue()+"s\n";
            fw.write(toFile);
            fw.flush();
        }

        fw.write("\nMaximum Train Queue length attained = "+passengerQueue.getMaxQueueLength()+"\n");
        fw.write("Maximum time spent in queue by a passenger = "+passengerQueue.getMaxStayInQueue()+"s\n");
        fw.flush();
        fw.write("Minimum time spent in queue by a passenger = "+passengerQueue.getMinStayInQueue()+"s\n");
        fw.flush();
        fw.write("Average time spent in queue by a passenger = "+ passengerQueue.getAverageStayInQueue()+"s\n\n\n");
        fw.flush();
        System.out.println("Writing the report into text file is completed");
        fw.close();
        menu(stage,trainStation,passengerQueue, passengerQueue.getKey());
    }

    private static void removePassengerFromQueue(TrainStation trainStation, Stage stage, PassengerQueue passengerQueue)
            throws IOException, ClassNotFoundException, ParseException {
        passengerQueue.remove(trainStation,stage);
    }

    private static void visualizeTrainQueue(TrainStation trainStation, Stage stage, PassengerQueue passengerQueue) {
        passengerQueue.display(trainStation,stage);
    }

    private static void addPassengerToQueue(Stage stage, TrainStation trainStation, PassengerQueue passengerQueue) {
        if (trainStation.getWaitingRoom().isEmpty()==false) {
            passengerQueue.add(trainStation, stage);
        } else {
            Alert emptyWaitingRoom = new Alert(Alert.AlertType.INFORMATION);
            emptyWaitingRoom.setContentText("ALL Passengers who were in the Waiting Room has been added to the Train Queue");
            emptyWaitingRoom.setHeaderText("The Waiting room is Empty");
            emptyWaitingRoom.showAndWait();
            stage.show();
        }
    }
}
