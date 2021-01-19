package com.example.buysell2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.UserDo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "buysell";
    String USER_TABLE = "CREATE TABLE IF NOT EXISTS tblUser ( _id INTEGER PRIMARY KEY AUTOINCREMENT, UP_ID TEXT, UP_Name TEXT, UP_User_Type TEXT, UP_Email TEXT, UP_Mobile_No TEXT, UP_UserID TEXT, UP_Password TEXT, UP_Status TEXT )";
    String SUPPLIER_TYPE_MASTER = "CREATE TABLE IF NOT EXISTS tblSupplierTypeMaster ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, Name TEXT )";
    String SUPPLIER_BUSINESS_TYPE_MASTER = "CREATE TABLE IF NOT EXISTS tblSupplierBusinessTypeMaster ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, Name TEXT )";
    String CATLOG_ITEMS = "CREATE TABLE IF NOT EXISTS tblCatlogItems ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, Name TEXT )";
    String SUB_CATLOG_ITEMS = "CREATE TABLE IF NOT EXISTS tblSubCatlogItems ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, Name TEXT )";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USER_TABLE);
        sqLiteDatabase.execSQL(SUPPLIER_TYPE_MASTER);
        sqLiteDatabase.execSQL(SUPPLIER_BUSINESS_TYPE_MASTER);
        sqLiteDatabase.execSQL(CATLOG_ITEMS);
        sqLiteDatabase.execSQL(SUB_CATLOG_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "tblUser");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "tblSupplierTypeMaster");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "tblSupplierBusinessTypeMaster");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "tblCatlogItems");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "tblSubCatlogItems");
        onCreate(sqLiteDatabase);
    }

    //====================================Login User================================================================///
    public boolean insertUser(UserDo user) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("UP_ID", user.UP_ID);
            values.put("UP_Name", user.UP_Name);
            values.put("UP_User_Type", user.UP_User_Type);
            values.put("UP_Email", user.UP_Email);
            values.put("UP_UserID", user.UP_UserID);
            values.put("UP_Password", user.UP_Password);
            values.put("UP_Status", user.UP_Status);
            values.put("UP_Mobile_No", user.UP_Mobile_No);
            db.insert("tblUser", null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null)
                db.close();
        }
        return true;
    }

    public UserDo getUser() {
        SQLiteDatabase db = null;
        UserDo user = new UserDo();
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblUser";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            user.UP_ID = c.getColumnIndex("UP_ID");
            user.UP_Name = c.getString(c.getColumnIndex("UP_Name"));
            user.UP_User_Type = c.getString(c.getColumnIndex("UP_User_Type"));
            user.UP_Email = c.getString(c.getColumnIndex("UP_Email"));
            user.UP_Mobile_No = c.getColumnIndex("UP_Mobile_No");
            user.UP_UserID = c.getColumnIndex("UP_UserID");
            user.UP_Password = c.getString(c.getColumnIndex("UP_Password"));
            user.UP_Status = c.getString(c.getColumnIndex("UP_Status"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return user;
    }

    public void deleteUser(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("tblUser", "UP_UserID=?", new String[]{"" + userId});
    }

    public boolean isUserExist(String UP_UserID) {
        SQLiteDatabase db = null;
        boolean dataCount = false;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblUsers WHERE UP_UserID ='" + UP_UserID + "'";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            if (c.getCount() > 0) {
                dataCount = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return dataCount;
    }

    public boolean isUserExist() {
        SQLiteDatabase db = null;
        boolean dataCount = false;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblUsers ";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            if (c.getCount() > 0) {
                dataCount = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return dataCount;
    }

    //===========================================Supplier Type, Supplier Bussiness Type ====================================//
    public boolean isSupplierTypeExit() {
        SQLiteDatabase db = null;
        boolean dataCount = false;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblSupplierTypeMaster ";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            if (c.getCount() > 0) {
                dataCount = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return dataCount;
    }

    public boolean isSupplierBusinessTypeExit() {
        SQLiteDatabase db = null;
        boolean dataCount = false;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblSupplierBusinessTypeMaster ";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            if (c.getCount() > 0) {
                dataCount = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return dataCount;
    }

    public boolean insertSupplierTypeMaster(List<SpinnerDo> SupplierTypeMasterList) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            for (int i = 0; i < SupplierTypeMasterList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("id", SupplierTypeMasterList.get(i).getId());
                values.put("Name", SupplierTypeMasterList.get(i).getName());
                db.insert("tblSupplierTypeMaster", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null)
                db.close();
        }
        return true;
    }

    public boolean insertSupplierBusinessTypeMaster(List<SpinnerDo> SupplierBusinessTypeMasterList) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            for (int i = 0; i < SupplierBusinessTypeMasterList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("id", SupplierBusinessTypeMasterList.get(i).getId());
                values.put("Name", SupplierBusinessTypeMasterList.get(i).getName());
                db.insert("tblSupplierBusinessTypeMaster", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null)
                db.close();
        }
        return true;
    }

    public List<SpinnerDo> getSupplierTypeMaster() {
        SQLiteDatabase db = null;
        List<SpinnerDo> SupplierTypeMasterList = new ArrayList();
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            String selectSupplierTypeMaster = "SELECT * FROM tblSupplierTypeMaster ";
            cursor = db.rawQuery(selectSupplierTypeMaster, null);

            if (cursor.moveToFirst()) {
                do {
                    SpinnerDo spin = new SpinnerDo(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("Name")));
                    SupplierTypeMasterList.add(spin);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null)
                db.close();
        }
        return SupplierTypeMasterList;
    }

    public List<SpinnerDo> getSupplierBusinessTypeMaster() {
        SQLiteDatabase db = null;
        List<SpinnerDo> SupplierBusinessTypeMasterList = new ArrayList();
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            String selectSupplierTypeMaster = "SELECT * FROM tblSupplierBusinessTypeMaster ";
            cursor = db.rawQuery(selectSupplierTypeMaster, null);

            if (cursor.moveToFirst()) {
                do {
                    SpinnerDo spin = new SpinnerDo(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("Name")));
                    SupplierBusinessTypeMasterList.add(spin);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null)
                db.close();
        }
        return SupplierBusinessTypeMasterList;
    }

    //==========================================Catlogitems, subCatlogitems ==============================================//
    public boolean isCatlogItemExit() {
        SQLiteDatabase db = null;
        boolean dataCount = false;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblCatlogItems ";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            if (c.getCount() > 0) {
                dataCount = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return dataCount;
    }

    public boolean isSubCatlogItemExit() {
        SQLiteDatabase db = null;
        boolean dataCount = false;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT  * FROM tblSubCatlogItems ";
            c = db.rawQuery(selectQuery, null);
            if (c != null)
                c.moveToFirst();
            if (c.getCount() > 0) {
                dataCount = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed())
                c.close();
            if (db != null)
                db.close();
        }
        return dataCount;
    }

    public boolean insertCatlogItem(List<SpinnerDo> catlogItemList) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            for (int i = 0; i < catlogItemList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("id", catlogItemList.get(i).getId());
                values.put("Name", catlogItemList.get(i).getName());
                db.insert("tblCatlogItems", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null)
                db.close();
        }
        return true;
    }

    public boolean insertSubCatlogItem(List<SpinnerDo> subCatlogItemList) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            for (int i = 0; i < subCatlogItemList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("id", subCatlogItemList.get(i).getId());
                values.put("Name", subCatlogItemList.get(i).getName());
                db.insert("tblSubCatlogItems", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null)
                db.close();
        }
        return true;
    }

    public List<SpinnerDo> getCatlogItemList() {
        SQLiteDatabase db = null;
        List<SpinnerDo> catlogItemList = new ArrayList();
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            String selectSupplierTypeMaster = "SELECT * FROM tblCatlogItems ";
            cursor = db.rawQuery(selectSupplierTypeMaster, null);

            if (cursor.moveToFirst()) {
                do {
                    SpinnerDo spin = new SpinnerDo(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("Name")));
                    catlogItemList.add(spin);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null)
                db.close();
        }
        return catlogItemList;
    }

    public List<SpinnerDo> getSubCatlogItemList() {
        SQLiteDatabase db = null;
        List<SpinnerDo> subCatlogItemList = new ArrayList();
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            String selectSupplierTypeMaster = "SELECT * FROM tblSubCatlogItems ";
            cursor = db.rawQuery(selectSupplierTypeMaster, null);

            if (cursor.moveToFirst()) {
                do {
                    SpinnerDo spin = new SpinnerDo(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("Name")));
                    subCatlogItemList.add(spin);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db != null)
                db.close();
        }
        return subCatlogItemList;
    }

    //==================================== delete all spinners data============================================================//
    public void deleteAllSpinners() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from tblSupplierTypeMaster");
        db.execSQL("delete from tblSupplierBusinessTypeMaster");
        db.execSQL("delete from tblCatlogItems");
        db.execSQL("delete from tblSubCatlogItems");
    }

}
