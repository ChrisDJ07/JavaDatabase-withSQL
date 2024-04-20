
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Frame class for the student and course database interface.
 * @author Christian Dave Janiola
 */
public class DatabaseFrame extends JFrame{

    //int designation for frame dimensions
    int frameHeight = 600;
    int frameWidth = 1250;
    
    JPanel titlePanel;
    JLabel title;
    
    JPanel tablePanel;
    public JTable table;
    public DefaultTableModel tableModel;
    public TableRowSorter<DefaultTableModel> sorter;
    String[] columns;
    
    JPanel buttonPanel;
    
    JButton editKey;
    JButton addKey;
    JButton deleteKey;
    JButton clearKey;
    
    JButton students;
    JButton courses;
    
    JTextField searchField;
    JLabel searchDescription;
    
    JLabel count;
    JLabel countLabel;
    
    Color background = new Color(193, 237, 245);
    
    private static boolean changed; //toggles into true if there's a change in course data
    private static String[][] changedData; //stores the row information of the updated student data
    
    public DatabaseFrame(String name, int type){
        super(name);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(frameWidth+20,frameHeight-50);
        this.setLocationRelativeTo(this);
        
        this.getContentPane().setBackground(background);
        
    /*Adding main buttons*/
        students = createButton("STUDENTS");
        courses = createButton("COURSES");
        this.add(students);
        this.add(courses);
        students.setBounds(380, 20, 100,30);
        courses.setBounds(760, 20, 100,30);
        courses.setEnabled(false);
        students.setEnabled(false);
        
        
    /*Panel for the Title*/
        titlePanel = new JPanel();
        this.add(titlePanel);
        titlePanel.setBounds(4,2, frameWidth-4, frameHeight/10);
        titlePanel.setLayout(null);
        titlePanel.setBackground(background);
        
        //Title Labels
        if(type == 1){
            title = new JLabel("COURSE DATABASE");
            searchDescription = new JLabel("(Search Course ID or Name)");
            this.add(searchDescription);
            searchDescription.setBounds(560,(frameHeight/10)+(frameHeight/2)+107-67, 350, 10);
            students.setEnabled(true);
            
            countLabel = new JLabel("COURSE COUNT");
            this.add(countLabel);
        }
        if(type == 0){
            title = new JLabel("STUDENT DATABASE");
            searchDescription = new JLabel("(Search Student Name, Gender, ID, Year, Course Name)");
            this.add(searchDescription);
            searchDescription.setBounds(485,(frameHeight/10)+(frameHeight/2)+107-67, 350, 10);
            courses.setEnabled(true);
            
            countLabel = new JLabel("STUDENT COUNT");
            this.add(countLabel);
        }
        countLabel.setBounds(120,(frameHeight/10)+(frameHeight/2)+109-20, 120, 20);
        countLabel.setForeground(new Color(4, 108, 128));
        searchDescription.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        count = new JLabel("0");
        this.add(count);
        count.setFont(new Font("Unispace", 1, 48)); 
        count.setForeground(new Color(4, 108, 128));
        count.setBounds(120,(frameHeight/10)+(frameHeight/2)+109-70, 150, 45);
        
        titlePanel.add(title);
        title.setFont(new java.awt.Font("Unispace", 1, 24));
        title.setBounds(500,5, frameWidth/3, (frameHeight/10)-5);
        
    /*The students/course data table*/
        tablePanel = new JPanel();
        this.add(tablePanel);
        tablePanel.setBounds(4,(frameHeight/10)+4, frameWidth-4, (frameHeight/2)-2);
        tablePanel.setLayout(null);
        tablePanel.setBackground(background);
        tablePanel.setBorder(null);
        
    /*Button Panel*/
        buttonPanel = new JPanel();
        this.add(buttonPanel);
        buttonPanel.setLayout(null);
        buttonPanel.setBounds(380,(frameHeight/2)+110, frameWidth/2, frameHeight/10);
        buttonPanel.setBackground(background);
        
    /*Adding buttons to the interface*/
        editKey = createButton("EDIT");
        addKey = createButton("ADD");
        deleteKey = createButton("DELETE");
        clearKey = createButton("CLEAR");
        buttonPanel.add(editKey);
        buttonPanel.add(addKey);
        buttonPanel.add(deleteKey);
        buttonPanel.add(clearKey);
        addKey.setBounds(0, 10, 120,50);
        editKey.setBounds(125, 10, 120,50);
        deleteKey.setBounds(250, 10, 120,50);
        clearKey.setBounds(375, 10, 120,50);
        
        
    /*Searching*/
        searchField = new JTextField();
        JLabel searchLabel = new JLabel("Search:");
        this.add(searchField);
        this.add(searchLabel);
        searchField.setBounds(480,(frameHeight/10)+(frameHeight/2)+107-100, frameWidth/4-4, frameHeight/20);
        searchField.setBorder(null);
        searchLabel.setBounds(420,(frameHeight/10)+(frameHeight/2)+109-100, 70, 20);
        searchLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        
        /*
        Check every time the student (type = 0) frame is opened/(in focus) if course data is changed
        and prompts the user.
        */ 
        if(type == 0){
            this.addWindowFocusListener(new WindowFocusListener(){ //adding windowFocusListener
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    if(changed){ //check for "changed" boolean status
                        JOptionPane.showMessageDialog(null, "Change in Course Data detected"); //prompts user
                        tableModel.setRowCount(0); //deletes all active rows
                        for(String[] row: changedData){ //add rows to the table from the "changedData" 2d String
                            tableModel.addRow(row);
                        }
                    setChangeStatus(false); //toggles "changed" boolean status to false
                    }
                }
                @Override public void windowLostFocus(WindowEvent e) {}
            });
        }
        this.setVisible(true);
    }
    //adding main button listeners
    public void addStudentsListener(ActionListener studentsListener){
        students.addActionListener(studentsListener);
    }
    public void addCoursesListener(ActionListener coursesListener){
        courses.addActionListener(coursesListener);
    }
    
    // adding component listeners
    public void addEditListener(ActionListener editListener){
        editKey.addActionListener(editListener);
    }
    public void addAddListener(ActionListener addListener){
        addKey.addActionListener(addListener);
    }
    public void addDeleteListener(ActionListener deleteListener){
        deleteKey.addActionListener(deleteListener);
    }
    public void addClearListener(ActionListener clearListener){
        clearKey.addActionListener(clearListener);
    }
    public void addSearchListener(KeyListener searchListener){
        searchField.addKeyListener(searchListener);
    }
    
    // Method to generate the table area in the frame.
    public void generateTable(String[][] tableData, int type){
        if(type == 0){
           columns = new String[]{"Name", "Gender", "ID", "Year Level","Course"}; // assigns column headers for student data (type = 0)
        }
        if(type == 1){
           columns = new String[]{"Course Code", "Course Name"}; // assigns column headers for course data (type = 1)
        }
        
        /*Adding Table to the tablePanel*/
        tableModel = new DefaultTableModel(tableData, columns);
        table = new JTable(tableModel);
        //add sorter
        table.setAutoCreateRowSorter(true);
        
        //designing table
        table.setFocusable(false);
        table.setShowVerticalLines(false);
        table.setGridColor(table.getBackground());
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        centerAlign(table);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(15,132,158));
        table.getTableHeader().setForeground(new Color(255,255,255));
        table.getTableHeader().setBorder(null);
        
        if(type==0){
            table.getColumnModel().getColumn(4).setPreferredWidth(300);
        }
        
        sorter = new TableRowSorter<>(tableModel);
        sorter.toggleSortOrder(0);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        tablePanel.add(scrollPane);
        tablePanel.getComponent(0).setBounds(0,0, frameWidth-3, frameHeight/2); //default value for student data table
    }
    
    /*
    Method that is called when there is a changed in course code in the courses database.
    Prompts the user if they wanted to update student data or not.
    If they choose to update student data, all students enrolled in the course will also update their
    course code to the updated one.
    If they choose not to update student data, the student will retain the out of date course code (
    will display "N/A course does not exist"
    If they chose neither, the operation will be cancelled.
    ).
    */
    public String codeChanged(){
        int option = JOptionPane.showConfirmDialog(null, "Course Code change detected, update Student Data?"
                                      , "Course Code change alert", JOptionPane.YES_NO_CANCEL_OPTION);
        if(option == JOptionPane.YES_OPTION){
            return "yes";
        }
        else if(option == JOptionPane.NO_OPTION){
            return "no";
        }
        return "cancel";
    }
    public int selected(){
        return table.getSelectedRow();
    }
    public int getSelected(){
        return table.convertRowIndexToModel(table.getSelectedRow());
    }
    //toggles the "changed" boolean variable to true/false
    public void setChangeStatus(boolean status){
        DatabaseFrame.changed = status;
    }
    //set the value of "changedData" 2d String
    public void setChangeData(String[][] data){
        DatabaseFrame.changedData = data;
    }
    //add filter to row sorter
    public void addFilter(){
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(searchField.getText())));
    }
    //clear searchField
    public void clearSearch(){
        searchField.setText("");
        addFilter();
    }
    public void setCount(int num){
        count.setText(num+"");
    }
    
    static class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            g2.dispose();
        }
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createButton();
        }
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createButton();
        }
        private JButton createButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }
    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(5, 152, 179));
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setForeground(Color.white);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusable(false);
        return button;
    }
    private static void centerAlign(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
        }
    }
}
