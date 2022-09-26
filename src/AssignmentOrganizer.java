import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class AssignmentOrganizer extends Application
{
    private ArrayList<Assignment> fileList = new ArrayList<Assignment>();
    private ObservableList<Assignment> list = FXCollections.observableArrayList();
    private ArrayList<String> courseList = new ArrayList<String>();
    private ObservableList<String> courses = FXCollections.observableArrayList();
    private Pane mainDisplay = new Pane();
    private BorderPane borderPane = new BorderPane();
    private ToggleGroup viewGroup = new ToggleGroup();
    private VBox viewBox = new VBox();
    private HBox rightPane = new HBox();
    private HBox leftPane = new HBox();
    private HBox bottomMenu = new HBox();
    private MenuBar menuBar = new MenuBar();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        
        //Creates a scene
        Scene scene = new Scene(borderPane, 1000, 600);
        
        //Layout the border pane
        borderPane.setLeft(leftPane);
        borderPane.setRight(rightPane);
        borderPane.setCenter(mainDisplay);
        borderPane.setTop(menuBar);
        borderPane.setBottom(bottomMenu);
        borderPane.requestFocus();

        //Loads a file if there is one saved
        loadFileAtStart();
        
        //Creates the 
        createMenuBar();
        createBottomMenu(); 
        createRightPane();
        createLeftPane();

        //Set the size of the center pane
        mainDisplay.setPrefSize(600, 250);
        
        //Display the window
        primaryStage.setScene(scene);
        primaryStage.setTitle("Assignment Organizer");
        primaryStage.show();
      
    }
    
    public static void main(String[] args) 
    {
        Application.launch(args);
    }
    
    /**
     * Sets the left pane's preferred width
     */
    public void createLeftPane()
    {
        leftPane.setPrefWidth(171);

    }
    
    /**
     * Creates the right pane if "view" is selected
     */
    public void createRightPane()
    {
        RadioButton  viewCourse = new RadioButton("View by Course");
        RadioButton viewEarliest = new RadioButton("View Earliest Due");
        RadioButton viewAll = new RadioButton("View All");
        
        rightPane.setPrefWidth(171);

        //Setup the view components
        viewCourse.setToggleGroup(viewGroup);
        viewEarliest.setToggleGroup(viewGroup);
        viewAll.setToggleGroup(viewGroup);
        viewAll.setSelected(true);
        viewBox.getChildren().addAll(viewCourse, viewEarliest, viewAll);
        viewBox.setAlignment(Pos.CENTER_LEFT);
        viewBox.setSpacing(10);
        viewBox.setPadding(new Insets(15, 15, 15, 15));
        
        viewEarliest.setOnAction(event ->
        {
            findEarliest();
            borderPane.setCenter(mainDisplay);
        });
        
        viewAll.setOnAction(event ->
        {
            printAssignments();
            borderPane.setCenter(mainDisplay);
        });
        
        viewCourse.setOnAction(e ->
        {
            viewCourses();
            borderPane.setCenter(mainDisplay);
        });
    }
    
    /**
     * Creates the bottom menu with 
     * options for add, remove, and view
     */
    public void createBottomMenu()
    {
        //Create the buttons for the bottom pane
        Button addAssignment = new Button("Add");
        Button removeAssignment = new Button("Remove");
        Button view = new Button("View");
        Button[] buttons = {addAssignment, removeAssignment, view};
        
        //Set the size of the buttons
        for (Button button: buttons) {
            button.setPrefSize(100, 20);
        }
        
        bottomMenu.getChildren().addAll(addAssignment, removeAssignment, view);
        bottomMenu.setAlignment(Pos.CENTER);
        bottomMenu.setPadding(new Insets(10, 15, 10, 15));
        bottomMenu.setSpacing(10);
        bottomMenu.setStyle("-fx-background-color: #eaeaea");
        bottomMenu.setBorder(new Border(new BorderStroke(Color.BLACK, null, null, null, BorderStrokeStyle.SOLID, null, null, null, null, null, null)));

        //Displays the view menu
        view.setOnAction(e ->
        {
            borderPane.setRight(viewBox);
            RadioButton toggle =(RadioButton) viewGroup.getSelectedToggle();

            String text = toggle.getText();
       
            if (text.equals("View by Course"))
            {
                viewCourses();
            }
            else if (text.equals("View Earliest Due"))
            {
                findEarliest();
            }
            else if (text.equals("View All"))
            {
                printAssignments();
            }
            

            borderPane.setCenter(mainDisplay);

        });
        
        //Displays the add assignment main display
        addAssignment.setOnAction(e -> 
        {
            add();
            borderPane.setCenter(mainDisplay);
            borderPane.setRight(rightPane);

        });
        
        //Displays the remove assignment main display
        removeAssignment.setOnAction(e -> 
        {
            remove();
            borderPane.setCenter(mainDisplay);
            borderPane.setRight(rightPane);

        });
    }
    
    /**
     * Creates the menu bar with options
     * for load and save
     */
    private void createMenuBar() 
    {
        Menu fileMenu = new Menu("File");
        MenuItem save = new MenuItem("Save");
        MenuItem load = new MenuItem("Load");
        //Set up the menu bar
        fileMenu.getItems().addAll(save, load);
        menuBar.getMenus().add(fileMenu);
        

        load.setOnAction(event -> 
        {
            try
            {
                load();
                RadioButton toggle =(RadioButton) viewGroup.getSelectedToggle();

                String text = toggle.getText();
           
                if (text.equals("View by Course"))
                {
                    viewCourses();
                }
                else if (text.equals("View Earliest Due"))
                {
                    findEarliest();
                }
                else if (text.equals("View All"))
                {
                    printAssignments();
                }

                borderPane.setCenter(mainDisplay);
                borderPane.setRight(viewBox);
            }
            catch (IOException | ClassNotFoundException e)
            {
                Alert alert = new Alert(AlertType.ERROR, "File not loaded.");
                alert.show();
            }
           
        });
        
        save.setOnAction(event -> 
        {

            try
            {
                save();
                Alert alert = new Alert(AlertType.CONFIRMATION, "File saved.");
                alert.show();

            }
            catch (IOException e)
            {
                Alert alert = new Alert(AlertType.ERROR, "File did not save.");
                alert.show();
            }
     
        });
            }
    
    /**
    * Displays the add assignment window
    */
    private void add()
    {
        //The main container
        HBox container = new HBox();
        
        //The columns for the components
        VBox column1 = new VBox();
        VBox column2 = new VBox();
        
        //A container for adding or remove courses
        HBox editCourses = new HBox();
        
        //For selecting the due date
        DatePicker datePicker = new DatePicker();
        
        //The labels
        Label enterName = new Label("Please enter the name of the assignment: ");
        Label enterCourse = new Label("Please select the course: ");
        Label enterDueDate = new Label("Please select the due date: ");
        Label placeHolder = new Label("");
        Label placeHolder2 = new Label("");
        
        //The buttons
        Button addCourse = new Button("Add Course");
        Button removeCourse = new Button("Remove Course");
        Button submitButton = new Button("Submit");

        
        //A combobox for selecting courses
        ComboBox<String> courseSelection = new ComboBox<String>(courses);
        
        //A textfield for the assignment name
        TextField nameTextField = new TextField();
        
        //Padding
        Insets padding = new Insets(25, 25, 25, 25);
        
        //Setup the spacing and layout
        column1.setSpacing(10);
        column2.setSpacing(10);
        column1.setPadding(padding);
        column2.setPadding(padding);
        placeHolder.setPrefHeight(35);
        placeHolder2.setPrefHeight(35);
        enterName.setPrefHeight(35);
        enterCourse.setPrefHeight(35);
        enterDueDate.setPrefHeight(35);
        courseSelection.setMaxWidth(400);
        courseSelection.setPrefHeight(35);
        datePicker.setPrefHeight(35);
        datePicker.setPrefWidth(223);
        nameTextField.setPrefHeight(35);
        editCourses.setPrefHeight(35);
        editCourses.getChildren().addAll(addCourse, removeCourse);
        editCourses.setAlignment(Pos.CENTER_RIGHT);
        column1.setAlignment(Pos.CENTER_LEFT);
        column2.setAlignment(Pos.CENTER_RIGHT);
        container.setAlignment(Pos.CENTER);
        
        //Add the elements to the containers
        column1.getChildren().addAll(enterName, enterCourse, placeHolder2, enterDueDate, placeHolder);
        column2.getChildren().addAll(nameTextField, courseSelection, editCourses, datePicker, submitButton);
        container.getChildren().addAll(column1, column2);
        container.setAlignment(Pos.CENTER);
        
        mainDisplay = container;     
        //If the course is not in the menu, the user adds a new course
        //with the add button
        addCourse.setOnAction(e ->
        {
            //Set up the second stage for entering in new courses
            HBox addCourseContainer = new HBox();
            Label addCourseLabel = new Label("Please enter the course: ");
            TextField addCourseTextField = new TextField();
            Scene addCourseScene = new Scene(addCourseContainer);
            Stage addCourseStage = new Stage();
            
            //Sets up the layout
            addCourseContainer.setPadding(padding);
            addCourseContainer.getChildren().addAll(addCourseLabel, addCourseTextField);
            addCourseStage.setScene(addCourseScene);
            addCourseStage.show();
            
            //Sets up the add button for the second stage
            //for confirming a course
            addCourseTextField.setOnAction(event ->
            {
                String newCourse = addCourseTextField.getText().toUpperCase();
                
                //If nothing was entered, close the second window
                if (newCourse.equals(""))
                {
                    addCourseStage.close();
                }
                //If the course is already added, display an error
                else if (courses.contains(newCourse))
                {
                    Alert alert = new Alert(AlertType.ERROR, "Already added");
                    alert.show();
                }
                //Otherwise, add the course
                else 
                {
                    courses.add(newCourse);
                    courseSelection.getSelectionModel().select(courses.size() - 1);
                    addCourseStage.close();
                }
                
            });
            
        });
        
        
        //Removes a course from the course combobox
        removeCourse.setOnAction(e ->
        {
            String course = courseSelection.getValue();
            if (course != null)
            {
                int index = courses.indexOf(course);
                courses.remove(index);
                courseSelection.setValue(null);
            }
      
        });
        
    
        submitButton.setOnAction(e -> 
        {
            String assignmentName = nameTextField.getText();
            String courseName = courseSelection.getValue();
            String dueDate = (datePicker.getValue() == null) ? null : datePicker.getValue().toString();
            Assignment assignment = new Assignment(assignmentName,courseName, dueDate);
            if (assignmentName.equals("") || dueDate == null || courseName == null)
            {
                Alert alert = new Alert(AlertType.ERROR, "Please fill in missing information.");
                alert.showAndWait();
                        
            
            }
            else if (list.contains(assignment)) 
            {
                Alert alert = new Alert(AlertType.ERROR, "Assignment already added.");
                alert.showAndWait();

            }
            else 
            {
                list.add(assignment);
                Alert alert = new Alert(AlertType.CONFIRMATION, "Successfully added.");
                alert.showAndWait();
            }

        });
        
    }
        
    /**
     * Displays the remove window in the main display
     */
    private void remove()
    {
        HBox removeGridPane = new HBox();
        ComboBox<Assignment> assignments = new ComboBox<Assignment>(list);
        Button remove = new Button("Remove");
        Insets padding = new Insets(25, 25, 25, 25);
        
        removeGridPane.setAlignment(Pos.CENTER);

        //If list is empty, inform the user there are no assignments
        if (list.isEmpty())
        {
            Label noAssignments = new Label("No assignments have been added.");
            removeGridPane.getChildren().add(noAssignments);
        }
        //Otherwise add the remove button and the assignment combobox
        else
        {
            removeGridPane.setPadding(padding);
            removeGridPane.getChildren().addAll(assignments, remove);
        }
        
   
        mainDisplay =  removeGridPane;

        //Removes an assignment
        remove.setOnAction(e ->
        {
            Assignment removal = assignments.getValue();
            if (removal != null)
            {
                int index = list.indexOf(removal);
                list.remove(index);
                assignments.setValue(null);
            }
      
        });
        
        
            
    }
    
    
    /**
     * Displays the assignments with the earliest due dates
     */
    private void findEarliest()
    {
        //The container to view the assignments
        VBox assignmentGridPane = new VBox();
        //The text field to display the assignments
        TextArea assignmentDisplay = new TextArea();
        //An array list to store the assignments that are due the earliest
        ArrayList<Assignment> earliestDueDates = new ArrayList<Assignment>();
                
        for (Assignment assignment : list)
        {
            //If the list is empty, add the assignment
            if (earliestDueDates.isEmpty())
            {
                earliestDueDates.add(assignment);
            }
            //If the due date of the current element is sooner than the due date of what currently is stored,
            //create a new linked list object and add
            else if (assignment.getDate().compareTo(earliestDueDates.get(0).getDate()) < 0)
            {
                //Resets the linked list
                earliestDueDates = new ArrayList<Assignment>();
                earliestDueDates.add(assignment);
            }
            //If the due dates are the same, add the current element to the list
            else if (assignment.getDate().compareTo(earliestDueDates.get(0).getDate()) == 0)
            {
                earliestDueDates.add(assignment);
            }            
        }
        
        //Checks to see if the list is empty and displays that there are no assignments if it is
        if (earliestDueDates.isEmpty())
        {

            Label noAssignments = new Label("No assignments have been added.");
            assignmentGridPane.getChildren().add(noAssignments);
            assignmentGridPane.setAlignment(Pos.CENTER);
        }        
        //Otherwise display the assignments that are due the earliest
        else
        {
            String text = "Due: " + earliestDueDates.get(0).getFormattedDate() + "\n";
            
            for (Assignment assignment : earliestDueDates)
            {
                text += assignment.getCourse() + "\n\t" + assignment.getAssignmentName() + "\n\n";
            }
            
            setProperties(assignmentDisplay, assignmentGridPane, text);

        }

        mainDisplay = assignmentGridPane;

    }
        
    /**
     * Displays all the assignments in the order in which they were added
     */
    private void printAssignments()
    {
        //The container for the text field
        VBox assignmentGridPane = new VBox();
        //The textfield to display the assignments
        TextArea assignmentDisplay = new TextArea();
        
        //If there are no assignments, display this to the user
        if (list.isEmpty())
        {
            Label noAssignments = new Label("No assignments have been added.");
            assignmentGridPane.getChildren().add(noAssignments);
            assignmentGridPane.setAlignment(Pos.CENTER);
        }
        //Otherwise, display the assignments
        else
        {
            String text = "";
            
            for (Assignment assignment : list)
            {
                text += assignment + "\n";
            }
            
            setProperties(assignmentDisplay, assignmentGridPane, text);


        }
       
        mainDisplay = assignmentGridPane;
        
    }
    
    /**
     * Displays the assignments by course in the order in which they were added
     */
    private void viewCourses()
    {
        //The container for the text field
        HBox assignmentGridPane = new HBox();
        //The text field to display te assignments
        TextArea assignmentDisplay = new TextArea();
        //A hash map to store the assignments by course
        HashMap<String, ArrayList<Assignment>> hashMap = new HashMap<String, ArrayList<Assignment>>();
        //An array list to store the courses with pending assignments
        ArrayList<String> pendingCourses = new ArrayList<String>();

        //If there are no assignments, display a message to the user
        if (list.isEmpty())
        {
            Label noAssignments = new Label("No assignments have been added.");
            assignmentGridPane.getChildren().add(noAssignments);
            assignmentGridPane.setAlignment(Pos.CENTER);
        }
        else
        {
            //For each unique course for all the assignments,
            //add the course to pending courses
            for (Assignment assignment : list)
            {
                String course = assignment.getCourse();
                if (!pendingCourses.contains(course))
                {
                    pendingCourses.add(course);
                    ArrayList<Assignment> pendingAssignments = new ArrayList<>();
                    hashMap.put(course, pendingAssignments);
                }
                hashMap.get(course).add(assignment);
            }
            
            String text = "";
            //For each course that has assignments due, 
            //print the assignments
            for (String course : pendingCourses)
            {

                text += course + ":\n";
                for (Assignment assignment : hashMap.get(course))
                {
                    text += "\t" + assignment.getAssignmentName() + "\n\tDue: " + assignment.getFormattedDate() + "\n";
                }
                     
            }
            
            setProperties(assignmentDisplay, assignmentGridPane, text);

            
        }

        mainDisplay = assignmentGridPane;
        
    }
    
    private void setProperties(TextArea display, Pane pane, String text)
    {
        display.setText(text);
        display.setEditable(false);
        display.prefWidthProperty().bind(borderPane.prefWidthProperty());
        display.prefWidthProperty().bind(borderPane.widthProperty());
        display.prefHeightProperty().bind(borderPane.prefHeightProperty());
        display.prefHeightProperty().bind(borderPane.heightProperty());
        pane.getChildren().add(display);

    }
        
    /**
     * The assignment organizer only saves one file per individual copy of program
     * @throws IOException
     */
    private void save() throws IOException
    {
        FileOutputStream outFile = null;
        ObjectOutputStream objectOutput =null;

        try
        {
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            currentPath += "/assignmentOrganizer.dat";
            File file = new File(currentPath);
            outFile = new FileOutputStream(file);
            objectOutput = new ObjectOutputStream(outFile);
            fileList = new ArrayList<Assignment>(list);
            courseList = new ArrayList<String>(courses);
            Object[] object = new Object[2];
            object[0] = fileList;
            object[1] = courseList;
            objectOutput.writeObject(object);
            
        }
        finally
        {
            if (objectOutput != null) {
                objectOutput.close();
            }
            if (outFile != null) 
            {
                outFile.close();
            }

        }
        
        
    }
        
    /**
     * The assignment organizer only loads from one file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void load() throws IOException, ClassNotFoundException 
    {

        FileInputStream readFile = null;
        ObjectInputStream objectInput = null;
        
            try 
            {
                 String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
                 currentPath += "/assignmentOrganizer.dat";
                 File file = new File(currentPath);
                 readFile = new FileInputStream(file);
                 objectInput = new ObjectInputStream(readFile);
                 Object[] object = (Object[]) objectInput.readObject();
                 
                 fileList = (ArrayList<Assignment>) object[0];
                 list = FXCollections.observableArrayList(fileList);
                 courseList = (ArrayList<String>) object[1];
                 courses = FXCollections.observableArrayList(courseList);

            }
            finally
            {
                if (objectInput != null)
                {
                    objectInput.close();
                }
                if (readFile != null)
                {
                    readFile.close();
                }
            }
   
        
    }
    
    
    /**
     * If there is previous data, then this will be loaded at the start
     */
    private void loadFileAtStart()
    {
        
        try 
        {
            load();
            printAssignments();
            borderPane.setCenter(mainDisplay);
            borderPane.setRight(viewBox);

        }
        catch (IOException | ClassNotFoundException e)
        {
            //If file not found, do nothing
        }
    }

     
}
    


