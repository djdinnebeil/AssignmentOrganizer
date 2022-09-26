import java.io.Serializable;
import javafx.collections.ObservableList;

/**
 * A class that stores the names and due dates of assignments
 * @author DJ Dinnebeil
 * 10/16/2020
 */

public class Assignment implements Serializable
{

    private static final long serialVersionUID = 2272102311618184317L;
    private String assignmentName;
    private String course;
    //The due date is stored as a string in numerical format as year-month-day  
    private String dueDate;
    
    /**
     * This constructor creates an assignment with a name and due date
     * @param name the name of the assignment
     * @param dueDate the due date stored as a string in numerical format as year-month-day
     */
    public Assignment(String name, String course, String dueDate)
    {
        this.assignmentName = name;
        this.course = course;
        this.dueDate = dueDate;
    }
    
    /**
     * This method gets the due date for comparisons
     * @return the due date as a string in numerical format as year-month-day
     */
    public String getDate()
    {
        return dueDate;
    }
    
    /**
     * This method checks to see if two assignments have the same name
     * @return true if the assignments have the same name
     */
    @Override
    public boolean equals(Object anotherAssignment)
    {
        if (this == anotherAssignment)
        {
            return true;
        }
        
        if (anotherAssignment == null || getClass() != anotherAssignment.getClass())
        {
            return false;
        }
        Assignment assignment = (Assignment)anotherAssignment;
        return assignmentName.equalsIgnoreCase(assignment.assignmentName) && course.equals(assignment.course);
    }
    
    /**
     * This returns a string representation of an assignment
     * @return a string representation of an assignment
     */
    @Override
    public String toString()
    {
        //Extracts the year from dueDate 
        String year = dueDate.substring(0, 4);
        //Extracts the month from dueDate
        String month = dueDate.substring(5, 7);
        //Extras the day from dueDate
        String day = dueDate.substring(8, 10);
        return course + "\n\t" + assignmentName + "\n\tDue: " + month + "/" + day + "/" + year + "\n";
    }
    
    /** 
     * Gets the formatted date in MM/DD/YYYY format
     * @return the formatted date
     * 
     */
    public String getFormattedDate()
    {
        //Extracts the year from dueDate 
        String year = dueDate.substring(0, 4);
        //Extracts the month from dueDate
        String month = dueDate.substring(5, 7);
        //Extras the day from dueDate
        String day = dueDate.substring(8, 10);
        return month + "/" + day + "/" + year + "\n";
    }
    
    /**
     * Gets the assignment name
     * @return the assignment name
     */
    public String getAssignmentName()
    {
        return assignmentName;
    }
    
    /**
     * Gets the course
     * @return the course
     */
    public String getCourse()
    {
        return course;
    }
    
}