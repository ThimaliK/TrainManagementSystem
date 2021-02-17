package com.trainstation.bookings;

import com.mongodb.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Train extends Application {

    private static HashMap<String,HashMap> allBookings = new HashMap<>();
    Stage stage;

    public static void main(String[] args) {
        System.out.println("---------------------Train Booking Program---------------------");
        launch();
    }

    public void menu() throws ParseException {
        System.out.println("\nMENU-----------------------------------------------");
        System.out.println("L: Load booking data first");
        System.out.println("A: Add a Customer");
        System.out.println("V: View all Seats");
        System.out.println("E: View Empty Seats");
        System.out.println("D: Delete a booked Seat");
        System.out.println("F: Find Seat(s) of a Customer");
        System.out.println("O: View all bookings alphabetically");
        System.out.println("S: Store new Bookings before exiting Program");
        System.out.println("Q: Quit Application\n");
        System.out.print("Enter your option here: ");
        Scanner sc = new Scanner(System.in);
        String option = sc.next();
        switch (option) {
            case "A":
            case "a":
            case "V":
            case "v":
            case "E":
            case "e":
            case "D":
            case "d":
            case "F":
            case "f":
            case "O":
            case "o":
                dateAndTrip(option);
                break;
            case "L":
            case "l":
                loadBookingsFromFile();
                break;
            case "S":
            case "s":
                storeBookingsIntoFile();
                break;
            case "Q":
            case "q":
                System.out.println("The program is exiting...");
                break;
            default:
                System.out.println("Invalid... Re-enter");    //handles wrong input
                menu();
        }
    }

    public void dateAndTrip(String option) throws ParseException {
        try {
            HashMap bookings = new HashMap<>();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); //sets date pattern
            String today = formatter.format(new Date());    //stores today's date

            Date trueToday = formatter.parse(today);    //turns entered string into an actual date

            System.out.println("Please Enter Date : (example - 12/05/2020)");
            Scanner sc = new Scanner(System.in);
            String date = sc.nextLine();

            Date trueDate = formatter.parse(date);
            String actualDate = formatter.format(trueDate);

            if (trueDate.after(trueToday) || trueDate.equals(trueToday)) {   //checks if the entered date is not a past date
                try {
                    System.out.println("Enter 1 if the trip is from Colombo to Badulla");
                    System.out.println("Enter 2 if the trip is from Badulla to Colombo");
                    String trip = sc.next();
                    if (trip.equals("1") || trip.equals("2")) {      //checks if the entered trip number is correct
                        String key = actualDate + " - " + trip;      //forms the key of the data structure
                        if (allBookings.containsKey(key)) {         //checks if the entered trip exists
                            bookings = allBookings.get(key);
                        }  //clears data of the previous trips

                        switch (option) {
                            case "A":
                            case "a":
                                addCustomer(option, key, bookings);
                                break;
                            case "V":
                            case "v":
                                viewAllSeats(option, key, bookings);
                                break;
                            case "E":
                            case "e":
                                viewEmptySeats(option, key, bookings);
                                break;
                            case "D":
                            case "d":
                                deleteBooking(key, bookings);
                                break;
                            case "F":
                            case "f":
                                findSeatBooking(key, bookings);
                                break;
                            case "O":
                            case "o":
                                viewSeatsAlphabetically(key, bookings);
                                break;
                            default:
                                System.out.println("Invalid... Re-enter");
                        }
                    } else {
                        System.out.println("Invalid Trip Number");
                        menu();
                    }

                } catch (Exception e) {
                    System.out.println("Invalid");
                }
            } else {
                System.out.println("Past Date... Please try again with today’s date or a future date");
                menu();
            }
        } catch (Exception e) {
            System.out.println("Invalid Input. Try again.");
            menu();
        }
    }

    public void loadBookingsFromFile() throws ParseException {
        try {
            System.out.println("Loading Data from Database...");
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("train-bookings");
            DBCollection coll = db.getCollection("train");
            DBObject dbo = coll.findOne();
            Object data = dbo.get("allBookings");
            allBookings = (HashMap<String, HashMap>) data;  //loads all booking information into the data structure
            System.out.println("\nBooking Information was successfully loaded");
        }catch (Exception e) {
            System.out.println("No data stored yet");
        }
        menu();
    }

    public void storeBookingsIntoFile() throws ParseException {
        MongoClient mongoClient = new MongoClient("localhost",27017);
        DB dbs = mongoClient.getDB("train-bookings");
        DBCollection coll = dbs.getCollection("train");
        BasicDBObject doc1 = new BasicDBObject();
        coll.remove(doc1);
        doc1.append("allBookings", allBookings);   //appends all data in data structure into the document
        coll.insert(doc1);                      //document is inserted to the collection and is stored in database
        System.out.println("\nAll updates done to Booking Information was stored successfully");
        menu();
    }

    private void addCustomer(String option, String key, HashMap bookings) {
        guiElements(option,key,bookings);         //directed to method which shows GUI
    }

    private void viewAllSeats(String option, String key, HashMap bookings) {
        guiElements(option,key,bookings);        //directed to method which shows GUI
    }

    private void viewEmptySeats(String option, String key,HashMap bookings) {
        guiElements(option,key,bookings);       //directed to method which shows GUI
    }

    public void deleteBooking(String key, HashMap bookings) throws ParseException {
        if(allBookings.containsKey(key)) {               //checks if the entered trip exists
            bookings = allBookings.get(key);
            List<String> nameList = new ArrayList<String>(bookings.keySet());
            List<List> seatList = new ArrayList<List>(bookings.values());
            System.out.print("Please enter your Name : ");
            Scanner input = new Scanner(System.in);
            String searchedName = input.nextLine();
            if (nameList.contains(searchedName.trim().toUpperCase())) {     //checks if entered name exists in bookings
                int i = nameList.indexOf(searchedName.trim().toUpperCase());
                List seatSet = seatList.get(i);                           //gets the respective seats
                System.out.println("These are the seats you booked : " + seatSet);
                System.out.println("Enter seat number of the seat that you want to delete from your booking :");
                Scanner input2 = new Scanner(System.in);
                String deletingSeat = input2.nextLine();
                if (seatSet.contains(deletingSeat.trim().toUpperCase())) {
                    seatSet.remove(deletingSeat.toUpperCase().trim());   //removes unwanted seat from seat list
                    System.out.println("Your seat " + deletingSeat.toUpperCase().trim() + " was deleted from Bookings");
                } else {
                    System.out.println("This seat does not exist in your bookings");
                }
            } else {
                System.out.println("Entered Name does not exist. Please Try Again");
            }
            allBookings.put(key,bookings);        //appends updated data to data structure
        } else {
            System.out.println("This trip does not exist...Please make sure that the date and trip number is correct");
        }
        menu();
    }

    public void findSeatBooking(String key, HashMap bookings) throws ParseException {
        if(allBookings.containsKey(key)) {           //checks if the entered trip exists
            bookings = allBookings.get(key);
            List<String> nameList = new ArrayList<String>(bookings.keySet());
            List<List> seatList = new ArrayList<List>(bookings.values());
            System.out.print("Please Enter your Name:");
            Scanner input = new Scanner(System.in);
            String bookedName = input.nextLine();
            if (nameList.contains(bookedName.trim().toUpperCase())) {       //checks if entered name exists in bookings
                int i = nameList.indexOf(bookedName.trim().toUpperCase());
                List bookedSeats = seatList.get(i);                         //gets the respective seats
                System.out.println("Date and Trip Number : "+key);          //displays date and trip number
                System.out.println("Your seats are : " + bookedSeats);      //displays the relevant seats
            } else {
                System.out.println("Incorrect Name entered. Please Try Again");
            }
        } else {
            System.out.println("This trip does not exist...Please make sure that the date and trip number is correct");
        }
        menu();
    }

    public void viewSeatsAlphabetically(String key, HashMap bookings) throws ParseException {
        if(allBookings.containsKey(key)) {                     //checks if the entered trip exists
            bookings = allBookings.get(key);                  //gets booking information of the relevant trip
            List<String> nameList = new ArrayList<String>(bookings.keySet());
            List<List> seatList = new ArrayList<List>(bookings.values());
            String temp;
            List tempList;
            int n = nameList.size();

            for (int j = 0; j < n - 1; j++) {
                for (int i = j + 1; i < n; i++) {
                    if (nameList.get(j).toUpperCase().compareTo(nameList.get(i).toUpperCase()) > 0) {  //compares two adjacent names at a time
                        temp = nameList.get(j);
                        tempList = seatList.get(j);
                        nameList.set(j, nameList.get(i));
                        seatList.set(j, seatList.get(i));
                        nameList.set(i, temp);
                        seatList.set(i, tempList);
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                System.out.println((i+1)+". "+nameList.get(i) + " - " + seatList.get(i)); //displays all names and respective seats alphabetically line by line
            }
        } else {
            System.out.println("This trip does not exist...Please make sure that the date and trip number is correct");
        }
        menu();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        menu();
    }

    public void guiElements(String option,String dateAndTripKey, HashMap bookings) {
        //elements necessary for GUI
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, 1100, 850);
        stage.setTitle("A/C Compartment | Add Customer");
        stage.setScene(scene);
        scene.setRoot(root);
        stage.show();

        ToggleButton seat;

        Label key = new Label("Selected\n\nAvailable\n\nBooked");
        key.setLayoutX(520); key.setLayoutY(450);
        key.setStyle("-fx-font-size:15px;");

        Rectangle selectedKey = new Rectangle(30,30);
        selectedKey.setLayoutX(480); selectedKey.setLayoutY(450);
        selectedKey.setStyle("-fx-fill:#43de52");

        Rectangle availableKey = new Rectangle(30,30);
        availableKey.setLayoutX(480); availableKey.setLayoutY(490);
        availableKey.setStyle("-fx-fill:#56d6b8");

        Rectangle bookedKey = new Rectangle(30,30);
        bookedKey.setLayoutX(480); bookedKey.setLayoutY(530);
        bookedKey.setStyle("-fx-fill:#ed362f");

        Button book = new Button("Confirm Booking");
        book.setLayoutX(480); book.setLayoutY(350);

        TextField name = new TextField();
        name.setLayoutX(555); name.setLayoutY(215);

        Label nameLabel = new Label("Name : ");
        nameLabel.setLayoutX(460); nameLabel.setLayoutY(200); nameLabel.setPadding(new Insets(20));

        Button back = new Button("Back to Menu");
        back.setLayoutX(630); back.setLayoutY(350);

        Label title = new Label("A/C Compartment - Denuwara Manike");
        title.setStyle("-fx-font: 30px Tahoma;");
        title.setPadding(new Insets(20));
        title.setLayoutX(275); title.setLayoutY(5);

        Label menuItem = new Label("Enter Passenger's Name, Select Seat(s) and Confirm Booking");
        menuItem.setStyle("-fx-font-size:20px;");
        menuItem.setLayoutX(480); menuItem.setLayoutY(150);

        Rectangle shape = new Rectangle(107,50);
        shape.setLayoutY(660); shape.setLayoutX(230);
        shape.setStyle("-fx-fill:#ebd79d;");

        Label label2 = new Label("Toilet");
        label2.setLayoutY(670); label2.setLayoutX(255);
        label2.setStyle("-fx-font-size:20px;");

        Label tripKey = new Label("TRIP NUMBERS\n\n" +
                "1 = Trip from Colombo to Badulla\n\n2 = Trip from Badulla to Colombo");
        tripKey.setLayoutX(700);tripKey.setLayoutY(450);

        Label tripAndDate = new Label("Date and Trip Number : "+dateAndTripKey);
        tripAndDate.setLayoutX(480);tripAndDate.setLayoutY(300);

        Label aboutDate = new Label("***If you wish to change date, trip number or continue with the program,\n" +
                "please click \"Back To Menu\" button to be re-directed to the menu");
        aboutDate.setLayoutX(480); aboutDate.setLayoutY(600);

        ToggleGroup bookedSeats = new ToggleGroup();

        root.getChildren().addAll(book, name, nameLabel,back,title,shape,label2,menuItem,key,selectedKey,availableKey,
                bookedKey,tripKey,tripAndDate,aboutDate);

        List<String> currentSelectedSeats = new ArrayList<>();

        int columns = 13, rows = 4, vertical = 50;
        for (int i = 2; i < columns; ++i) {           //toggle buttons representing seats are created through iteration
            String seatColumn;
            int seatRow = i - 1;
            for (int j = 1; j <= rows; ++j) {
                if (j == 1) {
                    seat = new ToggleButton();
                    seat.setLayoutX(70);
                    seat.setLayoutY((vertical + 5) * i);
                    seatColumn = "A";
                } else if (j == 2) {
                    seat = new ToggleButton();
                    seat.setLayoutX(125);
                    seat.setLayoutY((vertical + 5) * i);
                    seatColumn = "B";
                } else if (j == 3) {
                    seat = new ToggleButton();
                    seat.setLayoutX(230);
                    seat.setLayoutY((vertical + 5) * i);
                    seatColumn = "C";
                } else {
                    seat = new ToggleButton();
                    seat.setLayoutX(285);
                    seat.setLayoutY((vertical + 5) * i);
                    seatColumn = "D";
                }
                root.getChildren().add(seat);

                if ((seatColumn.equals("C") && seatRow == 11) || (seatColumn.equals("D") && seatRow == 11)) {
                    root.getChildren().remove(seat);
                }

                String seatNumber = seatColumn + seatRow;
                seat.setMinWidth(50); seat.setMinHeight(50);
                seat.setText(seatNumber);

                seat.setStyle("-fx-background-color:#56d6b8;");
                ToggleButton finalSeat = seat;

                List<List> seatList = new ArrayList<>(bookings.values());
                for (List list : seatList) {
                    List<String> oneBooking = new ArrayList<String>(list);
                    if (oneBooking.contains(seatNumber)) {
                        finalSeat.setDisable(true);
                        finalSeat.setStyle("-fx-background-color:#ed362f;-fx-opacity:1.0;");
                    }
                }
                switch (option) {
                    case "A":
                    case "a":
                        HashMap finalBookings = bookings;  //setting action to toggle button necessary for adding customer
                        seat.setOnAction(event -> modifyGuiForA(finalSeat, seatNumber, book, name, currentSelectedSeats,
                                bookedSeats, dateAndTripKey, finalBookings));
                        break;
                    case "V":
                    case "v":                               //modifying GUI for viewing seats
                        modifyGuiForV(root,book,name,nameLabel,menuItem,back,aboutDate);
                        break;
                    case "E":
                    case "e":                                                 //modifying GUI for viewing empty seats
                        modifyGuiForE(finalSeat,root,book, name, nameLabel,seatNumber,menuItem,bookings,back,aboutDate);
                        break;
                }
            }
            back.setOnAction(e -> {
                stage.hide();            //to exit GUI and return to the menu
                try {
                    menu();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    public void modifyGuiForA(ToggleButton seat, String seatNumber, Button book, TextField name,
                              List<String> currentSelectedSeats, ToggleGroup bookedSeats, String dateAndTripKey, HashMap finalBookings) {
        if (name.getText().trim().isEmpty()) {            //handling the instance where a name is not entered into text field
            seat.setSelected(false);
            book.setDisable(true);
            Alert noNameAlert = new Alert(Alert.AlertType.INFORMATION);
            noNameAlert.setContentText("Please enter passenger's name first");
            noNameAlert.setHeaderText("Name not entered");
            noNameAlert.showAndWait();
        }
        else if (finalBookings.containsKey(name.getText().toUpperCase().trim())) {   //handling the instance where a stored name is entered into text field
            seat.setSelected(false);
            book.setDisable(true);
            Alert sameNameAlert = new Alert(Alert.AlertType.INFORMATION);
            sameNameAlert.setContentText("This name is taken, please modify name");
            sameNameAlert.setHeaderText("Name taken");
            sameNameAlert.showAndWait();
        }
        else {
            if (seat.isSelected()) {                             //changing color of toggle button and updating relevant data structures when a seat is selected
                seat.setStyle("-fx-background-color:#43de52;");
                book.setDisable(false);
                currentSelectedSeats.add(seatNumber);
                if (!currentSelectedSeats.contains(seatNumber)) {
                    bookedSeats.getToggles().add(seat);
                }
            } else {                                            //going back to initial state when a toggle button is de-selected
                seat.setStyle("-fx-background-color:#56d6b8;");
                currentSelectedSeats.remove(seatNumber);
                if (currentSelectedSeats.contains(seatNumber)) {
                    bookedSeats.getToggles().remove(seat);
                }
            }
            book.setOnAction(e -> {                                         //confirms booking and update data structure
                String customerName = name.getText().trim().toUpperCase();
                List<String> temp = new ArrayList<>(currentSelectedSeats);
                currentSelectedSeats.clear();
                if (!finalBookings.containsValue(seatNumber)) {
                    finalBookings.put(customerName, temp);
                }
                name.clear();
                stage.hide();

                if (!allBookings.containsKey(dateAndTripKey)) {
                    allBookings.put(dateAndTripKey, finalBookings);
                }
                try {
                    menu();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void modifyGuiForV(AnchorPane root, Button book, TextField name, Label nameLabel, Label menuItem, Button back, Label aboutDate) {
        stage.setTitle("A/C Compartment | Seat Map");
        menuItem.setText("Seat Map"); menuItem.setLayoutY(70);
        back.setLayoutX(480);
        root.getChildren().removeAll(book,name,nameLabel,aboutDate);    //removes unnecessary GUI elements for viewing seats
    }

    private void modifyGuiForE(ToggleButton finalSeat, AnchorPane root, Button book, TextField name, Label nameLabel,
                               String seatNumber, Label menuItem, HashMap bookings, Button back, Label aboutDate) {
        stage.setTitle("A/C Compartment | Empty Seats");
        menuItem.setText("All Empty Seats"); menuItem.setLayoutY(70);
        back.setLayoutX(480);
        root.getChildren().removeAll(book,name,nameLabel,aboutDate);
        List<List> seatList = new ArrayList<>(bookings.values());
        for (List list : seatList) {                               //removes already booked seats(toggle buttons)
            List<String> oneBooking = new ArrayList<String>(list);
            if (oneBooking.contains(seatNumber)) {
                root.getChildren().remove(finalSeat);
            }
        }
    }
}



