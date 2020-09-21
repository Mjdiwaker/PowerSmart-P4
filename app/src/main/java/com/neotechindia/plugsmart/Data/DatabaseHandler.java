package com.neotechindia.plugsmart.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neotechindia.plugsmart.Utilility.Utill;
import com.neotechindia.plugsmart.model.Contact;
import com.neotechindia.plugsmart.model.Contact_1;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, Utill.DATABASE_NAME, null, Utill.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String Create_Contact_Table = "CREATE TABLE " + Utill.TABLE_USERS + " ( " + Utill.BUYER_ID
                + "INTEGER PRIMARY KEY," + Utill.BUYER_NAME
                + " TEXT," + Utill.BUYER_PHONE
                + " TEXT," + Utill.BUYER_EMAIL
                + " TEXT," + Utill.ADMIN_PASS
                + " TEXT," + Utill.BUYER_LONGITUDE
                + " TEXT," + Utill.BUYER_LATITUDE
                + " TEXT," + Utill.UUID
                + " TEXT," + Utill.TYPE
                + " TEXT" + ")";


//        StringBuilder builder=new StringBuilder();
//        builder.append("CREATE TABLE ");
//        builder.append(Utill.TABLE_NAME);
//        builder.append(" ("+Utill.BUYER_ID+" INTERGER PRIMARY KEY, ");
//        builder.append(Utill.BUYER_NAME+" TEXT, ");
//        builder.append(Utill.BUYER_PHONE+" TEXT, ");
//        builder.append(Utill.BUYER_EMAIL+" TEXT, ");
//        builder.append(Utill.ADMIN_PASS+" TEXT )");

        db.execSQL(Create_Contact_Table);

        String Create_device_table = "CREATE TABLE " + Utill.DEVICES
                + " ( " + Utill.USER_ID + "INTEGER PRIMARY KEY,"
                + Utill.USER_NAME + " TEXT,"
                + Utill.MAC_ID + " TEXT,"
                + Utill.SERIAL_NUMBER + " TEXT,"
                + Utill.LICENCE_NUMBER + " TEXT,"
                + Utill.STATUS_DEVICE + " TEXT" + ")";

        db.execSQL(Create_device_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Utill.TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + Utill.DEVICES);

        //CREATE TABLE AGAIN
        onCreate(db);
    }

    public void AddBuyerInfo(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues value = new ContentValues();
        value.put(Utill.BUYER_NAME, contact.getBuyername());
        value.put(Utill.BUYER_PHONE, contact.getBuyerphone());
        value.put(Utill.BUYER_EMAIL, contact.getBuyeremail());
        value.put(Utill.ADMIN_PASS, contact.getAdminpass());
        value.put(Utill.BUYER_LONGITUDE, contact.getBuyerlong());
        value.put(Utill.BUYER_LATITUDE, contact.getBuyerlat());
        value.put(Utill.UUID, contact.getUUID());
        value.put(Utill.TYPE, contact.getType());


        //INSERT ROW
        db.insert(Utill.TABLE_USERS, null, value);
        db.close();
    }

    ////////Client Infomation/////
//
//    public void AddClientInfo(Contact contact) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//
//        ContentValues value = new ContentValues();
//        value.put(Utill.BUYER_NAME, contact.getBuyername());
//        value.put(Utill.BUYER_PHONE, contact.getBuyerphone());
//        value.put(Utill.BUYER_EMAIL, contact.getBuyeremail());
//        value.put(Utill.ADMIN_PASS, contact.getAdminpass());
//        value.put(Utill.BUYER_LONGITUDE, contact.getBuyerlong());
//        value.put(Utill.BUYER_LATITUDE, contact.getBuyerlat());
//        value.put(Utill.UUID, contact.getUUID());
//        value.put(Utill.TYPE, contact.getType());
//
//
//        //INSERT ROW
//        db.insert(Utill.TABLE_USERS, null, value);
//        db.close();
//    }

////////////// User Details/////////////////////

    public void AddUserInfo(Contact_1 contact1) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(Utill.USER_NAME, contact1.getDevice_name());
        value.put(Utill.MAC_ID, contact1.getMac_id());
        value.put(Utill.SERIAL_NUMBER, contact1.getSerial_number());
        value.put(Utill.LICENCE_NUMBER, contact1.getLicence_number());
        value.put(Utill.STATUS_DEVICE, contact1.getStatus());

        //INSERT ROW
        db.insert(Utill.DEVICES, null, value);
        db.close();
    }


    // GET SINGLE CONTACT
//    public Contact getBuyerInfo(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(Utill.TABLE_NAME, new String[]{Utill.BUYER_ID,
//                        Utill.BUYER_NAME, Utill.BUYER_PHONE,
//                        Utill.BUYER_EMAIL, Utill.ADMIN_PASS}, Utill.BUYER_ID + " =? ", new String[]
//                        {String.valueOf(id)}, null,
//                null, null, null);
//
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2),
//                cursor.getString(3), cursor.getString(4));
//
//        return contact;
//    }

    //    GET ALL CONTACT
    public List<Contact> getBuyerInfomations() {

        SQLiteDatabase db = this.getReadableDatabase();

        List<Contact> contactList = new ArrayList<>();

        String selectall = "SELECT * FROM " + Utill.TABLE_USERS;
        Cursor cursor = db.rawQuery(selectall, null);

        if (cursor.moveToFirst()) {
            do {

                Contact contact = new Contact();
                //  contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setBuyername(cursor.getString(1));
                contact.setBuyerphone(cursor.getString(2));
                contact.setBuyeremail(cursor.getString(3));
                contact.setAdminpass(cursor.getString(4));
                contact.setBuyerlong(cursor.getString(5));
                contact.setBuyerlat(cursor.getString(6));
                contact.setUUID(cursor.getString(7));
                contact.setType(cursor.getString(8));

                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        return contactList;
    }


    //    GET ALL Devices
    public List<Contact_1> getAllDevices() {

        SQLiteDatabase db = this.getReadableDatabase();

        List<Contact_1> contactList = new ArrayList<>();

        String selectall = "SELECT * FROM " + Utill.DEVICES;
        Cursor cursor = db.rawQuery(selectall, null);

        if (cursor.moveToFirst()) {
            do {

                Contact_1 contact1 = new Contact_1();
                contact1.setId(Integer.parseInt(cursor.getString(0)));
                contact1.setDevice_name(cursor.getString(1));
                contact1.setMac_id(cursor.getString(2));
                contact1.setSerial_number(cursor.getString(3));
                contact1.setLicence_number(cursor.getString(4));
                contact1.setStatus(cursor.getString(5));

                contactList.add(contact1);

            } while (cursor.moveToNext());
        }
        return contactList;
    }


}
