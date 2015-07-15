package info.ronit.db;

/**
 * Created by Ronit on 4/2/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.ronit.db.model.*;


public class DbManager {

    private DbHandler dbHelper;

    private SQLiteDatabase database;

    private static Context c;

    public DbManager(Context context) {

        c = context;
    }

    public DbManager open()throws SQLiteException {

        this.dbHelper = new DbHandler(c);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (this.database != null && this.database.isOpen()) {
            this.database.close();
            this.database = null;
        }
        if (this.dbHelper != null) {
            this.dbHelper.close();
            this.dbHelper = null;
        }
    }


    // Adding a new member
    public void addMember(MemberDetail member) {
        ContentValues values = new ContentValues();
        values.put(DbHandler.KEY_NAME, member.getName()); // MemberDetail Name
        values.put(DbHandler.KEY_GENDER, member.getGender());
        values.put(DbHandler.KEY_AGE, member.getAge()); // MemberDetail Age
        values.put(DbHandler.KEY_RELATION, member.getRelation());
        values.put(DbHandler.KEY_RNAME, member.getRname());
        values.put(DbHandler.KEY_FLINK, member.getFlink());
        values.put(DbHandler.KEY_INLAW, member.getInlaw());
        values.put(DbHandler.KEY_IMAGE, member.getImage());

        // Inserting Row
        database.insert(DbHandler.TABLE_NAME, null , values);
        //close();
    }


    // Fetching Cursor
    public Cursor fetch() {
        String[] columns = new String[] { DbHandler.KEY_ID, DbHandler.KEY_NAME, DbHandler.KEY_GENDER, DbHandler.KEY_AGE, DbHandler.KEY_RELATION, DbHandler.KEY_RNAME, DbHandler.KEY_FLINK, DbHandler.KEY_IMAGE };
        Cursor cursor = database.query(DbHandler.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
       //close();
        return cursor;
    }

    public Cursor fetch(String flink) {
        String f[] = {flink,flink};
        String[] columns = new String[] { DbHandler.KEY_ID, DbHandler.KEY_NAME, DbHandler.KEY_GENDER, DbHandler.KEY_AGE, DbHandler.KEY_RELATION, DbHandler.KEY_RNAME, DbHandler.KEY_FLINK, DbHandler.KEY_IMAGE };
        Cursor cursor = database.query(DbHandler.TABLE_NAME, columns, "flink = ? OR flink = ?", f, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        //close();
        return cursor;
    }

    public int update(MemberDetail member) {
        ContentValues values = new ContentValues();
        values.put(DbHandler.KEY_NAME, member.getName()); // MemberDetail Name
        values.put(DbHandler.KEY_GENDER, member.getGender());
        values.put(DbHandler.KEY_AGE, member.getAge()); // MemberDetail Age
        values.put(DbHandler.KEY_RELATION, member.getRelation());
        values.put(DbHandler.KEY_RNAME, member.getRname());
        values.put(DbHandler.KEY_FLINK, member.getFlink());
        values.put(DbHandler.KEY_INLAW, member.getInlaw());
        values.put(DbHandler.KEY_IMAGE, member.getImage());

        int a = database.update(DbHandler.TABLE_NAME, values, DbHandler.KEY_ID + " = " + member.getID(), null);
        //close();
        return a;
    }

    public void delete(MemberDetail member) {
        database.delete(DbHandler.TABLE_NAME, DbHandler.KEY_ID + "= " + member.getID(), null);
        //close();
    }


    // Read records related to the search term
    public List<String> read(String searchTerm) {

        List<String> recordsList = new ArrayList<String>();

        // select query
        String sql = "";
        sql += "SELECT * FROM " + DbHandler.TABLE_NAME;
        sql += " WHERE " + DbHandler.KEY_NAME + " LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY " + DbHandler.KEY_NAME + " DESC";
        sql += " LIMIT 0,5";
       database = dbHelper.getWritableDatabase();

        // execute the query
        Cursor cursor = database.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String objectName = cursor.getString(cursor.getColumnIndex(DbHandler.KEY_NAME));

                //If ME!
                String ifme = cursor.getString(cursor.getColumnIndex(DbHandler.KEY_FLINK));
                if(ifme.compareToIgnoreCase("Me!")==0)
                    objectName += " (Me)";

                // add to list
                recordsList.add(objectName);

            } while (cursor.moveToNext());
        }

        cursor.close();
        //close();
        // return the list of records
        return recordsList;
    }

    //Gets the FLink!
    public String getFlinkfromDb(String forRname)
    {

        String gotflink="";

        // select query
        String sql = "";
        sql += "SELECT * FROM " + DbHandler.TABLE_NAME;
        sql += " WHERE " + DbHandler.KEY_NAME + " = '" + forRname + "';";

        database = dbHelper.getWritableDatabase();

        // execute the query
        Cursor cursor = database.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                gotflink = cursor.getString(cursor.getColumnIndex(DbHandler.KEY_FLINK));
            } while (cursor.moveToNext());
        }

        cursor.close();

        //close();
        // return the list of records
        return gotflink;
    }


    //If a searchterm exists
    public boolean exists(String searchTerm) {

        String Query = "Select * from " + DbHandler.TABLE_NAME + " where " + DbHandler.KEY_NAME + " = '" +searchTerm+ "'; ";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() == 0) {
            return false;
        }
        //close();
        return true;
    }


    //Gets the required member
    public MemberDetail getMember(String wname, String wage) {

        MemberDetail gotmember = new MemberDetail();
        String Query;
        if(wage.compareToIgnoreCase("NULL")==0)
            Query = "Select * from " + DbHandler.TABLE_NAME
                + " where " + DbHandler.KEY_NAME + " = '" + wname+ "' ; ";
        else
            Query = "Select * from " + DbHandler.TABLE_NAME
                    + " where " + DbHandler.KEY_NAME + " = '" + wname+ "' "
                    + "and " + DbHandler.KEY_AGE + "= '" + wage + "'; ";

        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            gotmember.setID(cursor.getInt(cursor.getColumnIndex(DbHandler.KEY_ID)));
            gotmember.setName(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_NAME)));
            gotmember.setGender(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_GENDER)));
            gotmember.setAge(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_AGE)));
            gotmember.setRelation(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_RELATION)));
            gotmember.setRname(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_RNAME)));
            gotmember.setFlink(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_FLINK)));
            gotmember.setInlaw(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_INLAW)));
            gotmember.setImage(cursor.getBlob(cursor.getColumnIndex(DbHandler.KEY_IMAGE)));
        }
        else
            gotmember.setName("NOT FOUND");
        //close();
        return gotmember;

    }

    //If is empty
    public boolean isEmpty() {

        String Query = "Select * from " + DbHandler.TABLE_NAME + "; ";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() == 0) {
            return true;
        }
        //close();
        return false;
    }

    public String import_db(String dbPath)throws IOException {
        if(dbHelper.importDatabase(dbPath))
            return "Database Imported!";
        return "Failed to Import!";
    }

    public void export_db()throws IOException{
        dbHelper.backupDatabase();
    }
}
