
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
     
     String url = "jdbc:mysql://127.0.0.1:3306/school";
     String username = "root";
     String password = "!DFoYtT7FHFez@rM";
    
    /*Class Constructor, init file objects with student and course data files*/
    public DatabaseModel(){
        //...
    }
    
    /*Save Students/Courses Data into dedicated files*/
    public void saveData(int type, String[] data){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            
            if(type == 0){
                    statement.executeUpdate("insert into students (student_name, gender, student_id, year_level, course) values("
                            +"\""+data[0]+"\","
                            +"\""+data[1]+"\","
                            +"\""+data[2]+"\","
                            +"\""+data[3]+"\","
                            +"\""+data[4]+"\""
                            +");"
                    
                    );
            }
            if(type == 1){
                    statement.executeUpdate("insert into courses (course_id, course_name) values("
                            +"\""+data[0]+"\","
                            +"\""+data[1]+"\""
                            +");"
                    
                    );
                }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*Save Students/Courses Data into dedicated files*/
    public void updateData(int type, String[] data, String oldName){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            
            if(type == 0){
                    String message = """
                                     update students
                                     set student_name = "%s",
                                     	gender = "%s",
                                         student_id = "%s",
                                         year_level = %d,
                                         course = "%s"
                                     where student_name = "%s"
                                     ;
                                     """;
                    statement.executeUpdate(String.format(message, data[0],data[1],data[2],Integer.parseInt(data[3]),data[4], oldName));
                    System.out.println(String.format(message, data[0],data[1],data[2],Integer.parseInt(data[3]),data[4], oldName));
            }
            if(type == 1){
                    statement.executeUpdate("insert into courses (course_id, course_name) values("
                            +"\""+data[0]+"\","
                            +"\""+data[1]+"\""
                            +");"
                    
                    );
                }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
                ResultSet result = statement.executeQuery("select student_name, gender, student_id, year_level, course from students order by add_stamp, student_name");

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
                ResultSet result = statement.executeQuery("select course_id, course_name from courses order by add_stamp, course_id");

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
            e.printStackTrace();
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
    public void delete(int indexF, int type){
        int index = indexF-1;//lmao this solves the bug WTF!!!! I don't know why :))))
        try{
            if(type == 0){
                studentObjects.remove(index);
                studentList.remove(index); System.out.println(studentList.size()+" - index = "+index);
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                String message ="""
                                delete from students
                                where student_name = "%s";
                                """;
                statement.executeUpdate(
                String.format(message, studentList.get(index))
                );
                System.out.println("Hello");
                System.out.println(String.format(message, studentList.get(index)));
            }
            if(type == 1){
                courseObjects.remove(index);
                courseCodeList.remove(index);
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                String message ="""
                                delete from courses
                                where course_id = "%s";
                                """;
                statement.executeUpdate(
                String.format(message, courseCodeList.get(index))
                );
                System.out.println(String.format(message, courseCodeList.get(index)));
            }
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
    
    public boolean validateID(String id){
        int intID;
        try {
            intID=Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return false;
        }
        if(intID<1 || intID>9999){
            return false;
        }
        return true;
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
    public void updateStudentCourse(Courses oldCourse, String[] NEW, String OLD){
        try{
                for(int index: oldCourse.studentList){
                    studentObjects.get(index).setCourseCode(NEW[0]);
                }
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                String message ="""
                                update students
                                set course = "%s"
                                where course = "%s";
                                """;
                statement.executeUpdate(
                String.format(message, NEW[0], OLD)
                );
                System.out.println(String.format(message, NEW, OLD));
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void courseUpdate(String[] NEW, String OLD){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String message ="""
                            update courses  
                            set course_id = "%s",
                                course_name = "%s"
                            where course_id = "%s";
                            """;
            statement.executeUpdate(
            String.format(message, NEW[0], NEW[1], OLD)
            );
            System.out.println(String.format(message, NEW[0], NEW[1], OLD));
            System.out.println(OLD);
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*Clears the student/course database*/
    public void clearDatabase(int type){
        try{
            if(type == 0){
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                String message ="""
                                delete from students;
                                """;
                statement.executeUpdate(
                message
                );
                System.out.println(message);
            }
            if(type == 1){
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                String message ="""
                                delete from courses;
                                """;
                statement.executeUpdate(
                message
                );
                System.out.println(message);
            }
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
