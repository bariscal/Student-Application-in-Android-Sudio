package com.example.assignment03_03;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.assignment03_03.DbManager;

import java.io.File;
import java.util.ArrayList;

import static android.R.layout.simple_spinner_dropdown_item;

public class MainActivity extends AppCompatActivity {

    //defining objects
    public TabHost tabHost;
    public EditText studentID, studentName, studentSurname, studentDept, studentGrade;
    public Button insert, delete, display, update;
    public ListView listView, listViewSearch;

    public Spinner deptSpinner, searchSpinner;
    public Button search;

    SQLiteDatabase database;
    DbManager manager;
    String path;

    /*
    There is two adapters and ArrayLists.
    one thems is for the first tabs listview,
    and other is for second tabs listview.
    One of them keeps all the datas in the database,
    and one of them keeps filtered datas which are
    show in the second tab.
     */
    ArrayList<String> students = new ArrayList<>();
    ArrayList<String> searchStudents = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> searchAdapter;

    //I created departments by manually. Firstly, I used textView for department info
    //but I think spinner seems more propore for this kind of app. This departments
    //uploading to two spinners which are in both tabs.
    String[] departments = { "Civil Engineering", "Computer Engineering", "Electrical and Electronics Engineering",
            "Energy Systems Engineering", "Genetics and Bioengineering", "Industrial Engineering",
            "Mathematics", "Mechanical Engineering", "Mechatronics Engineering"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //I hided title bar.
        getSupportActionBar().hide();

        //tabhost initialization
        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;

        //2 tabspecs for each tab
        tabSpec = tabHost.newTabSpec("Screen-1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("ADD", null);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Screen-2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("SEARCH", null);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });


        //initializing objects. textView for department is commented.
        //because I am using spinner.
        studentID = findViewById(R.id.studentID);
        studentName = findViewById(R.id.studentName);
        studentSurname = findViewById(R.id.studentSurname);
        //studentDept = findViewById(R.id.studentDept);
        deptSpinner = findViewById(R.id.deptSpinner);
        studentGrade = findViewById(R.id.studentGrade);

        //buttons initializing.
        insert = findViewById(R.id.insert);
        delete = findViewById(R.id.delete);
        display = findViewById(R.id.display);
        update = findViewById(R.id.update);

        listView = findViewById(R.id.listView);

        //database initializing
        File file = getApplication().getFilesDir();
        path = file + "/CMPE408_20"; // path of the db

        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        manager = new DbManager(this);

        //This adapter is for all the student datas.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, students);

        //tab2's objects initializing.
        searchSpinner = findViewById(R.id.searchSpinner);
        search = findViewById(R.id.search);
        listViewSearch = findViewById(R.id.listViewSearch);

        //this part is for spinners.
        ArrayAdapter searchAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, departments);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        searchSpinner.setAdapter(searchAdapter);
        deptSpinner.setAdapter(searchAdapter);

        //insert button's Listener.
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(v);
                display(v);
            }
        });

        //delete button's Listener.
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(v);
            }
        });

        //display button's Listener.
        /*
        Actually this button is useless because I added display to every button
        for dynamic looking. When you insert or update or delete something you
        can see the changes on the list immediately.
         */
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display(v);
                //searchAdapter = new ArrayAdapter<String>(this, simple_spinner_dropdown_item, students);
                searchSpinner.setAdapter(searchAdapter);
            }
        });

        //update button's Listener.
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
                display(v);
            }
        });

        //search button' Listener.
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(v);
            }
        });

    }

    //HELPER METHODS

    //this methods is for second tab. with this method
    //you can select students according to their departments.
    //I wrote a SQLite query on the other class. With this method,
    //I am taking selected item in spinner and I am sending it to
    //Manager class for select operation.
    public void search(View v){
        String dept = searchSpinner.getSelectedItem().toString();

        searchStudents.clear();
        Cursor cursor = manager.search(dept);
        //ArrayList<String> students = new ArrayList<>();
        searchAdapter = new ArrayAdapter<String>(this, simple_spinner_dropdown_item, searchStudents);
        //iterate record
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String surname = cursor.getString(cursor.getColumnIndex("surname"));
            String dep = cursor.getString(cursor.getColumnIndex("department"));
            String grade = cursor.getString(cursor.getColumnIndex("grade"));
            String result = id + " " + name + " " + surname + " " + dep + " " + grade;
            searchStudents.add(result);
        }

        listViewSearch.setAdapter(searchAdapter);
        database.close();

    }


    private boolean databaseExist() {
        File dbFile = new File(path);
        return dbFile.exists(); // returns boolean value

    }

    //This part inserts new data to database.
    //I added length as 10 because you wanted like that.
    //Query is in the other class.
    //Also this method gives Toast message when the ID length
    //under 10 digits.
    public void insert(View v) {
        String id = studentID.getText().toString().trim();
        String name = studentName.getText().toString().trim();
        String surname = studentSurname.getText().toString().trim();
        String dept = deptSpinner.getSelectedItem().toString();
        String grade = studentGrade.getText().toString().trim();

        if (id.length() == 10) {
            manager.insert(Double.parseDouble(id), name, surname, dept, Integer.parseInt(grade));
        } else {
            Toast.makeText(MainActivity.this, "ID must be 10 digits", Toast.LENGTH_SHORT).show();
        }

        studentID.setText("");
        studentName.setText("");
        studentSurname.setText("");
        //studentDept.setText("");
        studentGrade.setText("");
    }

    /*
    This method deletes data from database. I controlled ID length again for not
    take any error. There is alert dialog when you click delete button.
    You can cancel or you can confirm operation.
     */
    public void delete(View v){
        String id = studentID.getText().toString().trim();
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Do you want to delete student?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (id.length() == 10) {
                            Toast.makeText(MainActivity.this, "Student Deleted", Toast.LENGTH_SHORT).show();
                            manager.delete(Double.parseDouble(studentID.getText().toString()), studentName.getText().toString());
                            display(v);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "ID must be 10 digit", Toast.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        display(v);
                    }
                }).show();

    }


    /*
    This display method. It displays datas in the listview.
    I added this method to every button. I explained this above.
     */
    public void display(View v) {
        Cursor cursor = manager.display();
        ArrayList<String> students = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, simple_spinner_dropdown_item, students);
        //iterate record
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));//acces id
            String name = cursor.getString(cursor.getColumnIndex("name"));//acces name
            String surname = cursor.getString(cursor.getColumnIndex("surname"));
            String dept = cursor.getString(cursor.getColumnIndex("department"));
            String grade = cursor.getString(cursor.getColumnIndex("grade"));
            String result = id + " " + name + " " + surname + " " + dept + " " + grade;
            students.add(result);
        }

        listView.setAdapter(adapter);
        database.close();

    }

    //Update method. It controls ID and name at least and updates data.
    public void update(View v){
        manager.update(Double.parseDouble(studentID.getText().toString()),
                studentName.getText().toString(),
                studentSurname.getText().toString(),
                deptSpinner.getSelectedItem().toString(),
                Integer.parseInt(studentGrade.getText().toString()));

        studentID.setText("");
        studentName.setText("");
        studentSurname.setText("");
        //studentDept.setText("");
        studentGrade.setText("");
    }


}