
import controller.DatabaseController;
import model.DatabaseModel;
import view.DatabaseFrame;


/**
 * Main program for the database software
 * @author Christian Dave Janiola
 */
public class main {
    public static void main(String args[]) {
        //frame object for student database
        DatabaseFrame studentDB =  new DatabaseFrame("Student Database", 0);
        //frame object for course database
        DatabaseFrame coursesDB =  new DatabaseFrame("Courses Database", 1);
        //initializing static model instannce using a constructor in DatabaseController
        new DatabaseController(new DatabaseModel());
        // instance of DatabaseController, arguements: studentDB, courseDB, type
        new DatabaseController(studentDB, coursesDB, 1); 
    }
}
