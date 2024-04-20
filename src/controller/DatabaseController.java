
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import model.DatabaseModel;
import view.DatabaseFrame;
import view.InputFrame;

/**
 * Main controller for the database software.
 * @author Christian Dave Janiola
 */
public class DatabaseController {
    
    private DatabaseFrame studentDB; //student frame
    private DatabaseFrame courseDB; //course frame
    private static DatabaseModel modelDB; //general model object
    private InputFrame input; //general input frame
    
    //integer for GUI type, 0-students 1-courses
    private static int type;
    //list of courses by code
    public static String[] courseList; 
    
    /*
    Class constructor taking DatabaseModel parameter to init model object.
    Also initializes extracting of course and student data for the first time.
    Also matches course codes if student and course data aren't empty.
    */
    public DatabaseController(DatabaseModel modelDB){
        this.modelDB = modelDB;
        this.modelDB.extractData(1);
        this.modelDB.extractData(0);
        if(!(this.modelDB.studentObjects.isEmpty() || this.modelDB.courseObjects.isEmpty())){
            this.modelDB.matchCourseCode();
        }
        if(!this.modelDB.courseObjects.isEmpty()){
            //converts 'courseCodeList' arraylist, into a String array stored in 'courseList'
            courseList = modelDB.courseCodeList.toArray(new String[0]);
        }
    }
    
    /*
    Another class constructor for either student/course controller initialization.
    */
    public DatabaseController(DatabaseFrame frameDB,DatabaseFrame frame2DB, int type){
            this.type = type;
            this.courseDB = frame2DB;
            this.studentDB = frameDB;
            
            this.studentDB.setVisible(false);

            this.courseDB.addAddListener(new addListener());
            this.courseDB.addEditListener(new editListener());
            this.courseDB.addDeleteListener(new deleteListener());
            this.courseDB.addClearListener(new clearListener());
            this.courseDB.addSearchListener(new searchListener());
            
            this.courseDB.addStudentsListener(new studentsListener(this.studentDB, this.courseDB));
            
            //populate table data if course objects isn't empty, otherwise populate table with no rows
            if(modelDB.courseObjects.isEmpty() == false){
                this.modelDB.populateTable(1);
                this.courseDB.generateTable(modelDB.tableData, 1);
            }
            else{
                this.courseDB.generateTable(new String[0][0], 1);
            }
            
            this.studentDB.addAddListener(new addListener());
            this.studentDB.addEditListener(new editListener());
            this.studentDB.addDeleteListener(new deleteListener());
            this.studentDB.addClearListener(new clearListener());
            this.studentDB.addSearchListener(new searchListener());
            
            this.studentDB.addCoursesListener(new coursesListener(this.studentDB, this.courseDB));
            
            //populate table data if student objects isn't empty, otherwise populate table with no rows
            if(!(modelDB.studentObjects.isEmpty())){
                this.modelDB.populateTable(0);
                this.studentDB.generateTable(modelDB.tableData, 0);
            }
            else{
                this.studentDB.generateTable(new String[0][0], 0);
            }
            
            studentDB.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    courseDB.dispose();
                }
            });
            
            courseDB.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    studentDB.dispose();
                }
            });
            
            this.studentDB.setCount(modelDB.studentList.size());
            this.courseDB.setCount(modelDB.courseCodeList.size());
    }
    
    public static void changeType(int newType){
        type = newType;
    }

    private static class coursesListener implements ActionListener {
        DatabaseFrame studentDB;
        DatabaseFrame coursesDB;
        public coursesListener(DatabaseFrame studentDB, DatabaseFrame coursesDB) {
            this.studentDB = studentDB;
            this.coursesDB = coursesDB;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            coursesDB.setVisible(true);
            studentDB.setVisible(false);
            changeType(1);
        }
    }

    private static class studentsListener implements ActionListener {
        DatabaseFrame studentDB;
        DatabaseFrame coursesDB;
        
        public studentsListener(DatabaseFrame studentDB, DatabaseFrame coursesDB) {
            this.studentDB = studentDB;
            this.coursesDB = coursesDB;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            studentDB.setVisible(true);
            coursesDB.setVisible(false);
            changeType(0);
        }
    }
    
    /*InputFrame ActionListener*/
    class submitListener implements ActionListener{
        //either 'add' or 'edit'
        private String actionType;
        //int index used for retrieving student/course object
        private int selectedIndex; 
        
        //class constructor that init values for 'actionType' and 'selectedIndex'
        public submitListener(String actionType, int selectedIndex){
            this.actionType = actionType;
            this.selectedIndex = selectedIndex;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(type == 0){
                if(actionType.equals("Add" )){
                    //check if name, id, and course is filled
                    if(input.getNameText().replace(" ", "").isEmpty() || input.getIdText().replace(" ", "").isEmpty()
                             || input.getCourseCode(modelDB.courseCodeList.toArray(new String[0]))== "None" || input.getID().replace(" ", "").isEmpty()){
                        JOptionPane.showMessageDialog( null, "Missing required fields.");
                        return;
                    } 
                    //check for duplicates in name and id
                    else if(!modelDB.validateID(input.getID())){
                        JOptionPane.showMessageDialog( null, "Invalid ID.");
                        return;
                    }
                    else if(modelDB.checkDuplicate(selectedIndex, input.getNameText(),input.getIdText()+String.format("%04d", Integer.parseInt(input.getID())), 0, "add")){
                        JOptionPane.showMessageDialog(null, "Student Name or ID Number already taken.");
                        return;
                    }
                    else{
                        modelDB.createNewStudent(input.getNameText(), input.getGenderType(),input.getIdText()+String.format("%04d", Integer.parseInt(input.getID())),
                                                 input.getYearText(), input.getCourseCode(modelDB.courseCodeList.toArray(new String[0])));
                        //save new student data to database
                        modelDB.saveData(0);
                        //calls refresh function
                        refresh(); 
                        //stores new student data to string variable to add to table
                        String[] newStudentData = {input.getNameText(), input.getGenderType(),input.getIdText()+String.format("%04d", Integer.parseInt(input.getID())),
                                                   input.getYearText(), modelDB.getCourseName(modelDB.studentList.size()-1)};
                        //add new row for new student data
                        studentDB.tableModel.addRow(newStudentData); 
                        input.dispose();
                    }
                }
                if(actionType.equals("Edit")){
                    //check if name, id, and course is filled
                    if(input.getNameText().replace(" ", "").isEmpty() || input.getIdText().replace(" ", "").isEmpty()
                             || input.getCourseCode(modelDB.courseCodeList.toArray(new String[0])) == "None" || input.getID().replace(" ", "").isEmpty()){
                        JOptionPane.showMessageDialog( null, "Missing required fields.");
                        return;
                    } 
                    // check for duplicates for name and id
                    else if(!modelDB.validateID(input.getID())){
                        JOptionPane.showMessageDialog( null, "Invalid ID.");
                        return;
                    }
                    else if(modelDB.checkDuplicate(selectedIndex, input.getNameText(),input.getIdText()+String.format("%04d", Integer.parseInt(input.getID())), 0, "edit")){
                        JOptionPane.showMessageDialog(null, "Student Name or ID Number already taken.");
                        return;
                    }
                    else{
                        String[] studentData = {input.getNameText(), input.getGenderType(),input.getIdText()+String.format("%04d", Integer.parseInt(input.getID())),
                                                 input.getYearText(), input.getCourseCode(modelDB.courseCodeList.toArray(new String[0]))};
                        modelDB.setData(selectedIndex, studentData, 0);
                        modelDB.saveData(0);
                        refresh();
                        studentDB.tableModel.removeRow(selectedIndex);
                        //System.out.println(selectedIndex+" - "+studentDB.tableModel.getRowCount());
                        studentData[4] = modelDB.getCourseName(selectedIndex);//replaces course code with code name to insert into the table
                        studentDB.tableModel.insertRow(selectedIndex,studentData);
                        input.dispose();
                    }
                }
                studentDB.setCount(modelDB.studentList.size());
            }
            if(type == 1){
                if(actionType.equals("Add")){
                    //check if the course code and name fields are filled
                    if(input.getCourseNameField().replace(" ", "").isEmpty() || input.getCourseField().replace(" ", "").isEmpty()){
                        JOptionPane.showMessageDialog( null, "Missing required fields.");
                        return;
                    } //check for duplicates in code and name
                    else if(modelDB.checkDuplicate(selectedIndex, input.getCourseNameField(),input.getCourseField(), 1, "add")){
                        JOptionPane.showMessageDialog(null, "Course Name or Code already taken.");
                        return;
                    }
                    else{
                        modelDB.createNewCourse(input.getCourseField(), input.getCourseNameField());
                        //save new course data to database
                        modelDB.saveData(1);
                        //stores new student data to string variable to add to table
                        String[] newCourseData = {input.getCourseField(), input.getCourseNameField()};
                        //add new row for new course data
                        courseDB.tableModel.addRow(newCourseData);
                        courseDataChange();
                        input.dispose();
                    }
                }
                if(actionType.equals("Edit")){
                    //check if the course code and name fields are filled
                    if(input.getCourseNameField().replace(" ", "").isEmpty() || input.getCourseField().replace(" ", "").isEmpty()){
                        JOptionPane.showMessageDialog( null, "Missing required fields.");
                        return;
                    } 
                    //check for duplicates in code and name
                    else if(modelDB.checkDuplicate(selectedIndex, input.getCourseNameField(),input.getCourseField(), 1, "edit")){
                        JOptionPane.showMessageDialog(null, "Course Name or Code already taken.");
                        return;
                    }
                    else{
                        String nameField;
                        if(input.getCourseNameField().isEmpty()){ //solves weird bug when course name field is empty
                            nameField = " ";
                        }else {
                            nameField = input.getCourseNameField();
                        }
                        String[] previousData = modelDB.getData(selectedIndex, 1);
                        String[] courseData = {input.getCourseField(), nameField}; //new course data
                        if(previousData[0] != courseData[0] || previousData[1] != courseData[1]){ //check if anything is changed
                            if(previousData[0].equals(courseData[0])==false){ //checked if course code is changed
                                String choice = courseDB.codeChanged();
                                //calls the codeChanged method confirming if student data should be changed or not, or cancel operation
                                if(choice == "yes"){
                                    modelDB.courseUpdate(modelDB.courseObjects.get(selectedIndex), courseData);//updates course codes of affected students
                                    //save data to database
                                    modelDB.saveData(0);     
                                }
                                //check if not "yes" or "no"
                                else if(choice == "cancel"){ 
                                    return;
                                }

                            }
                            modelDB.setData(selectedIndex, courseData, 1); //updates course data
                            modelDB.saveData(1); //save to database
                            
                            /*updates table*/
                            courseDB.tableModel.removeRow(selectedIndex);
                            courseDB.tableModel.insertRow(selectedIndex, courseData);
                            courseDataChange();//updates students' data
                        }
                        input.dispose();
                    }
                }
                courseDB.setCount(modelDB.courseCodeList.size());
            }
        }
    }
    
    /*DatabaseFrame ActionListeners*/
    class addListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(type == 0){
                studentDB.clearSearch();
                input = new InputFrame("Add Student Data", 0);
                input.addSubmitListener(new submitListener("Add", 0));
                input.setCourseCodeList(courseList);
            }
            if(type == 1){
                courseDB.clearSearch();
                input = new InputFrame("Add Course", 1);
                input.addSubmitListener(new submitListener("Add", 0));
            }
        }
    }
    class editListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(type == 0){
                if(studentDB.selected() == -1){
                    JOptionPane.showMessageDialog( null, "No selected row.");
                }
                else{
                    int selectedIndex = studentDB.getSelected();
                    input = new InputFrame("Edit Student Data", 0); //init new edit inputFrame
                    input.addSubmitListener(new submitListener("Edit", selectedIndex));
                    input.setCourseCodeList(courseList);
                    String[] currentData = modelDB.getData(selectedIndex, 0); //get data of selected student
                    //set fields
                    input.setNameText(currentData[0]);
                    input.setIdText(currentData[1]);
                    input.setYearText(currentData[2]);
                    input.setGenderType(currentData[3]);
                    input.setCourseText(currentData[4]);
                }
                studentDB.setCount(modelDB.studentList.size());
            }
            if(type == 1){
                if(courseDB.selected() == -1){
                    JOptionPane.showMessageDialog( null, "No selected row.");
                }
                else{
                    int selectedIndex = courseDB.getSelected();
                    input = new InputFrame("Edit Course Data", 1); //init new edit inputFrame
                    input.addSubmitListener(new submitListener("Edit", selectedIndex));
                    String[] currentData = modelDB.getData(selectedIndex, 1); //get data of selected course
                    //set fields
                    input.setCourseField(currentData[0]);
                    input.setCourseNameField(currentData[1]);
                }
                courseDB.setCount(modelDB.courseCodeList.size());
            }
        }
    }
    class deleteListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(type == 0){
                if(studentDB.selected() == -1){
                    JOptionPane.showMessageDialog( null, "No selected row.");
                }
                else{
                    int selectedIndex = studentDB.getSelected();
                    if(JOptionPane.showConfirmDialog(null, //dialog to confirm student deletion (skips deletion if false)
                                    "Are you sure you want to delete this student from the database? This operation is permanent.",
                                    "Student Delete alert", 2 )== JOptionPane.YES_OPTION){
                        modelDB.delete(selectedIndex, 0); //delete student object from arraylist
                        modelDB.saveData(0); //save data to database
                        refresh(); //performs database refresh
                        studentDB.tableModel.removeRow(selectedIndex); //update table
                    }
                }
                studentDB.setCount(modelDB.studentList.size());
            }
            if(type == 1){
                if(courseDB.selected() == -1){
                    JOptionPane.showMessageDialog( null, "No selected row.");
                }
                else{
                    int selectedIndex = courseDB.getSelected();
                    if(JOptionPane.showConfirmDialog(null, //dialog to confirm course deletion (skips deletion if false)
                                "Deleting This Course will affect all Students enrolled in this course, proceed?",
                                "Course Delete alert", 2 )== JOptionPane.YES_OPTION){
                    modelDB.delete(selectedIndex, 1); //delete course object from arraylist
                    modelDB.saveData(1); //save data to database
                    courseDB.tableModel.removeRow(selectedIndex); //update table
                    courseDataChange(); //update student data
                }
                }
                courseDB.setCount(modelDB.courseCodeList.size());
            }
        }
    }
    class clearListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(type == 0 && JOptionPane.showConfirmDialog(null, //dialog to confirm student data deletion (skips deletion if false)
                                "Are you sure you want to clear the whole sutdent database? This operation cannot be undone.",
                                "Student DB Delete alert", 2 )== JOptionPane.YES_OPTION){
                studentDB.clearSearch();
                modelDB.studentObjects.clear();
                modelDB.saveData(0);
                refresh();
                for(int i=studentDB.tableModel.getRowCount()-1; i>=0; i--){
                    studentDB.tableModel.removeRow(i);
                }
                studentDB.setCount(modelDB.studentList.size());
            }
            if(type == 1 && JOptionPane.showConfirmDialog(null, //dialog to confirm course data deletion (skips deletion if false)
                                "Are you sure you want to clear the whole course database? This operation cannot be undone.",
                                "Course DB Delete alert", 2 )== JOptionPane.YES_OPTION){
                courseDB.clearSearch();
                modelDB.courseObjects.clear();
                modelDB.saveData(1);
                for(int i=courseDB.tableModel.getRowCount()-1; i>=0; i--){
                    courseDB.tableModel.removeRow(i);
                }
                courseDataChange();
                courseDB.setCount(modelDB.studentList.size());
            }
        }
    }
    class searchListener implements KeyListener{
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            //...
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if(type == 0){
                studentDB.addFilter();
            }else{
                courseDB.addFilter();
            }
        }
    }
    
    /*Updates student database when course data is changed*/
    void courseDataChange(){
        modelDB.courseCodeList.clear();
        refresh();
        courseList = modelDB.courseCodeList.toArray(new String[0]);
        modelDB.populateTable(0);

        courseDB.setChangeStatus(true);
        courseDB.setChangeData(modelDB.tableData);
    }
    
    //performs a database refresh, clears any static variables, and assign values to them
    static void refresh(){
        modelDB.courseObjects.clear();
        modelDB.studentList.clear();
        modelDB.studentObjects.clear();
        
        modelDB.extractData(0);
        modelDB.extractData(1);
        modelDB.matchCourseCode();
    }
}
