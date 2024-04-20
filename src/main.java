
import controller.DatabaseController;
import model.DatabaseModel;
import view.DatabaseFrame;


/**
 * Main program for the database software
 * @author Christian Dave Janiola
 */
public class main {
    
    
    public static void main(String args[]) {
        DatabaseFrame studentDB =  new DatabaseFrame("Student Database", 0);//frame object for student database
        DatabaseFrame coursesDB =  new DatabaseFrame("Courses Database", 1); //frame object for course database
        new DatabaseController(new DatabaseModel()); //initializing static model instannce using a constructor in DatabaseController
        new DatabaseController(studentDB, coursesDB, 1); // instance of DatabaseController, arguements: studentDB, courseDB, type
    }
}
