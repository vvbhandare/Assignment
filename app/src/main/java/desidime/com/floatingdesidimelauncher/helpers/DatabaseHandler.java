package desidime.com.floatingdesidimelauncher.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import desidime.com.floatingdesidimelauncher.models.CopounInfo;

/**
 * Created by Vishal-TS on 27/01/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dealsManager";
    private static final String TABLE_DEALS = "deals";
    private static final String KEY_ID = "id";
    private static final String KEY_DEAL_NAME = "deal_name";
    private static final String KEY_DEAL_BELOG_TO_WHICH_APP = "deal_belong_to";
    private static final String KEY_DEAL_ICON = "deal_icon";
    private static final String KEY_DEAL_LINK = "deal_url";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DEALS_TABLE = "CREATE TABLE " + TABLE_DEALS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEAL_NAME + " TEXT,"
                + KEY_DEAL_ICON + " TEXT,"
                + KEY_DEAL_LINK + " TEXT,"
                + KEY_DEAL_BELOG_TO_WHICH_APP + " INTEGER" + ")";
        db.execSQL(CREATE_DEALS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEALS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new deal
    public void addDeal(CopounInfo copounInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEAL_NAME, copounInfo.getCopounName()); // deal Name
        values.put(KEY_DEAL_ICON, copounInfo.getCopounIcon()); // deal Icon
        values.put(KEY_DEAL_LINK, copounInfo.getCopounLink());
        values.put(KEY_DEAL_BELOG_TO_WHICH_APP, copounInfo.getOfferID()); //deal belong to

        // Inserting Row
        db.insert(TABLE_DEALS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all deals in a list view
    public List<CopounInfo> getAllDeals(int appID) {
        List<CopounInfo> dealList = new ArrayList<CopounInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEALS + " WHERE " + KEY_DEAL_BELOG_TO_WHICH_APP + "=" + appID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CopounInfo copounInfo = new CopounInfo();
                copounInfo.setCopounName(cursor.getString(1));
                copounInfo.setCopounIcon(cursor.getString(2));
                copounInfo.setCopounLink(cursor.getString(3));
                // Adding deal to list
                dealList.add(copounInfo);
            } while (cursor.moveToNext());
        }

        // return deal list
        return dealList;
    }

    //Deleting all the deals
    public void deleteAll(int appID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DEALS + " WHERE " + KEY_DEAL_BELOG_TO_WHICH_APP + "=" + appID);
        db.close();
    }

    // Getting deals Count
    public int getDealsCount(int appID) {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_DEALS + " WHERE " + KEY_DEAL_BELOG_TO_WHICH_APP + "=" + appID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor != null && !cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }

        // return count
        return count;
    }

}
