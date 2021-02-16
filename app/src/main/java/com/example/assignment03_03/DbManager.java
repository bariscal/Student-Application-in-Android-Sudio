package com.example.assignment03_03;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DbManager extends SQLiteOpenHelper{

    /*
    I defined tables columns in this part. I examined example codes and
    created my own table like this.
     */

    private static final String DB_NAME = "CMPE408_Student_2";
    private static final int DB_VERSION = 2;
    private static final String TABLE = "students";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String DEPT = "department";
    private static final String GRADE = "grade";

    private static final String COMMAND = "CREATE TABLE " + TABLE + " (\n" +
            "    " + ID + " INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
            "    " + NAME + " varchar(255) NOT NULL,\n" +
            "    " + SURNAME + " varchar(255) NOT NULL,\n" +
            "    " + DEPT + " varchar(255) NOT NULL,\n" +
            "    " + GRADE + " INTEGER NOT NULL\n" +
            ");";

    public DbManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating table.
        db.execSQL(COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String sql = "DROP TABLE IF EXISTS " + TABLE + ";";
            db.execSQL(COMMAND);
            onCreate(db);
        }
    }


    public DbManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    //HELPER METHODS

    /*
    I am inserting data with using this method.
    With contentValues, I added data to related column.
    I wrote this part in try-catch for avoiding errors.
     */
    boolean insert(double id, String name, String surname, String dept, int grade){

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(ID, id);
            contentValues.put(NAME, name);
            contentValues.put(SURNAME, surname);
            contentValues.put(DEPT, dept);
            contentValues.put(GRADE, grade);
        }
        catch(Exception e){
            e.getMessage();
        }

        return database.insert(TABLE, null, contentValues) != -1;
    }


    /*
    This method is for delete data. With the query I am checking the
    name and ID. if it is true data is removing from database.
     */
    boolean delete(double id, String name){

        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE, ID + " = ? AND " + NAME + " = ?", new String[]{String.valueOf(id), name}) == 1;

    }

    //this for display all datas.
    Cursor display(){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery("SELECT * FROM " + TABLE, null);
    }

    //this method is searching students according to their departments.
    //This part was optional. This part placed in second tab.
    Cursor search(String dept){
        SQLiteDatabase database = getReadableDatabase();
        //Cursor cursor = database.rawQuery("select * from " + TABLE +  " where DEPT=?", new String[]{DEPT});
        String query = "SELECT * FROM "+TABLE+" WHERE "+DEPT+" = ?";
        Cursor c = database.rawQuery(query, new String[]{ dept });
        return c;
    }

    /*
    this part updates the data with controlling ID and Name.
     */
    boolean update(double id, String name, String surname, String dept, int grade){

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, id);
        contentValues.put(NAME, name);
        contentValues.put(SURNAME, surname);
        contentValues.put(DEPT, dept);
        contentValues.put(GRADE, grade);

        return database.update(TABLE, contentValues, ID + "=?", new String[]{String.valueOf(id)}) == 1;

    }

}
