
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Main Model for the database software.
 * @author Christian Dave Janiola
 */
public class DatabaseModel {
     
     public ArrayList<String> studentList = new ArrayList<>(); //List of Students for Table
     public ArrayList<String> courseCodeList = new ArrayList<>();   //List of Course Code for matching
     
     public static ArrayList<Students> studentObjects = new ArrayList<>(); //List of Student Objects for OOP
     public static ArrayList<Courses> courseObjects = new ArrayList<>();  //List of Course Objects for OOP
     
     public String[][] tableData;   //2d string for Table Construction
    
    /*Class Constructor, init file objects with student and course data files*/
    public DatabaseModel(){
        //...
    }
    
    /*Save Students/Courses Data into dedicated files*/
    public void saveData(int type){
        String url = "jdbc:mysql://127.0.0.1:3306/school";
        String username = "root";
        String password = "!DFoYtT7FHFez@rM";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            
            if(type == 0){
                statement.executeUpdate("delete from students");
                for(Students student: studentObjects){
                    statement.executeUpdate("insert into students values("
                            +"\""+student.getName()+"\","
                            +"\""+student.getGender()+"\","
                            +"\""+student.getId()+"\","
                            +"\""+student.getYear()+"\","
                            +"\""+student.getCourseCode()+"\""
                            +");"
                    
                    );
                }
            }
            if(type == 1){
                statement.executeUpdate("delete from courses");
                for(Courses course: courseObjects){
                    statement.executeUpdate("insert into courses values("
                            +"\""+course.getCourseCode()+"\","
                            +"\""+course.getCourseName()+"\""
                            +");"
                    
                    );
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
        }
    }
    
    /*Method to extract Student/course data and input to designated Objects*/
    public void extractData(int type){
        String url = "jdbc:mysql://127.0.0.1:3306/school";
        String username = "root";
        String password = "!DFoYtT7FHFez@rM";
        try {
            if(type == 0){
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("select * from students");

                while (result.next()) {
                    String[] studentData = new String[5];
                    for (int i = 1; i < 6; i++) {
                        studentData[i-1]= result.getString(i);
                    }
                    studentObjects.add(new Students(studentData[0], studentData[1], studentData[2], studentData[3], studentData[4]));
                    studentList.add(studentData[0]);//stores student names into studentList
                }
            }
            if(type == 1){
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("select * from courses");

                while (result.next()) {
                    String[] courseData = new String[2];
                    for (int i = 1; i < 3; i++) {
                        courseData[i-1]= result.getString(i);
                    }
                    courseObjects.add(new Courses(courseData[0], courseData[1]));
                    courseCodeList.add(courseData[0]); //add course code to courseCodeList
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
        }
    }
    
    /*Create new Student Object*/
    public void createNewStudent(String name, String gender, String id, String yearLevel, String courseCode){
        studentObjects.add(new Students(name, gender, id, yearLevel, courseCode));
        studentList.add(name);
    }
    /*Create a new course object and set its code and name*/
    public void createNewCourse(String courseCode, String courseName){
        if(courseName.isEmpty()){ //solves bug happening when there is no course entered (this shouldn't happen anymore)
            courseObjects.add(new Courses(courseCode, " "));
        }
        else{
            courseObjects.add(new Courses(courseCode, courseName));
        }
        courseCodeList.add(courseCode);
    }
    
    /*Returns the student/course data of index specified student/course object*/
    public String[] getData(int index, int type){
        String[] data = null;
        if(type == 0){
            data = new String[]{studentObjects.get(index).getName(), studentObjects.get(index).getId(), studentObjects.get(index).getYear()
                                ,studentObjects.get(index).getGender(), studentObjects.get(index).getCourseCode()};
        }
        if(type == 1){
            data = new String[]{courseObjects.get(index).getCourseCode(), courseObjects.get(index).getCourseName()};
        }
        
        return data;
    }
    /*Set data of selected student or course*/
    public void setData(int index, String[] data, int type){
        if(type == 0){
            studentObjects.get(index).setName(data[0]);
            studentObjects.get(index).setGender(data[1]);
            studentObjects.get(index).setId(data[2]);
            studentObjects.get(index).setYear(data[3]);
            studentObjects.get(index).setCourseCode(data[4]);
            studentList.set(index, data[0]);
        }
        if(type == 1){
            courseObjects.get(index).setCourseCode(data[0]);
            courseObjects.get(index).setCourseName(data[1]);
            courseCodeList.set(index, data[0]);
        }
    }
    
    /*Returns the course name of the selected student index*/
    public String getCourseName (int index){
        return studentObjects.get(index).getCourseName();
    }
    
    /*Remove each instance of specified student/course object and data*/
    public void delete(int index, int type){
        if(type == 0){
            studentObjects.remove(index);
            studentList.remove(index);
        }
        if(type == 1){
            courseObjects.remove(index);
            courseCodeList.remove(index);
        }
    }
    
    //function to check if there is a duplicate in Student name, Student Id, course name, or course code when
    //adding/editing student/course data
    //RIP optimiizaiton
    public boolean checkDuplicate(int index, String name, String code, int type, String operation){
        String Name = name.replace(" ", ""); //remove whitespace
        String Code = code.replace(" ", ""); //remove whitespace
        int counter = 0;
            if(type == 0){
                for(Students student: studentObjects){
                    //check if any student Name/course Code match with new data
                    String studentName = student.getName().replace(" ", "");
                    String studentID = student.getId().replace(" ", "");
                    if(operation == "edit" && counter != index && (Name.toLowerCase().equals(studentName.toLowerCase()) 
                    || Code.toLowerCase().equals(studentID.toLowerCase()))){ //convert all to lowercase for more consitent matching
                        return true;
                    }
                    if(operation == "add" && (Name.toLowerCase().equals(studentName.toLowerCase()) 
                    || Code.toLowerCase().equals(studentID.toLowerCase()))){
                        return true;
                    }
                    counter++;
                }
            }
            if(type == 1){
                for(Courses course: courseObjects){
                    //check if any course Name/course Code match with new data
                    String courseName = course.getCourseName().replace(" ", "");
                    String courseCode = course.getCourseCode().replace(" ", "");
                    if(operation == "edit" && counter != index && (Name.toLowerCase().equals(courseName.toLowerCase()) 
                    || Code.toLowerCase().equals(courseCode.toLowerCase()))){
                        return true;
                    }
                    if(operation == "add" && (Name.toLowerCase().equals(courseName.toLowerCase()) 
                    || Code.toLowerCase().equals(courseCode.toLowerCase()))){
                        return true;
                    }
                    counter++;
                }
            }
        return false;
    }
    
    /*Match course code of student-course and assign values to each objects*/
    public void matchCourseCode(){
        int iterator = 0;
        for(Students student: studentObjects){
            for(Courses course: courseObjects){
                if(student.getCourseCode().equals(course.getCourseCode())){
                    student.setCourseName(course.getCourseName());
                    course.studentList.add(iterator);
                    break;
                }else{
                    student.setCourseName("N/A - Course Does not Exist (" + student.getCourseCode() +")");
                }
            }
            iterator++;
        }
    }
    
    /*Populating Table Data for Table Display*/
    public void populateTable(int type){
        if(type == 0){
            tableData = new String[studentObjects.size()][5];
            int iterator = 0;
            for(Students student: studentObjects){
                String[] individualData = {student.getName(), student.getGender(), student.getId(), student.getYear(), student.getCourseName()};
                tableData[iterator] = individualData;
                iterator++;
            }
        }
        if(type == 1){
            tableData = new String[courseObjects.size()][2];
            int iterator = 0;
            for(Courses course: courseObjects){
                String[] individualData = {course.getCourseCode(), course.getCourseName()};
                tableData[iterator] = individualData;
                iterator++;
            }
        }
    }
    
    /*Updates Students' course code based on course changes*/
    public void courseUpdate(Courses OLD, String[] NEW){
        for(int index: OLD.studentList){
            studentObjects.get(index).setCourseCode(NEW[0]);
        }
    }
}
