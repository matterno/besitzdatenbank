package de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BesitzSQLiteOpenHelper extends SQLiteOpenHelper {

	private SQLiteDatabase database;
	
	public static final String TABLE_CATEGORY = "category";
	public static final String TABLE_ITEM = "item";
	public static final String TABLE_ATTRIBUTE = "attribute";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PICTURE = "picture";
	public static final String COLUMN_CATEGORYID = "categoryId";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_ITEMID = "itemId";
	
	private static final String DATABASE_NAME = "besitzdatenbank.db";
	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_CREATE_CATEGORY = "create table if not exists "
			+ TABLE_CATEGORY + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text unique not null);";

	private static final String DATABASE_CREATE_ITEM = "create table if not exists "
			+ TABLE_ITEM + " (" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text not null, " + COLUMN_PICTURE + " text, "
			+ COLUMN_CATEGORYID + " integer not null references " + TABLE_CATEGORY + "("
			+ COLUMN_ID + ") ON DELETE CASCADE);";
	
	private static final String DATABASE_CREATE_ATTRIBUTE = "create table if not exists " +
			TABLE_ATTRIBUTE + " (" + 
			COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_TYPE + " integer not null, " +
			COLUMN_NAME + " text not null, " +
			COLUMN_VALUE + " text not null, " +
			COLUMN_ITEMID + " integer not null references " + TABLE_ITEM + "(" + COLUMN_ID + ") ON DELETE CASCADE);";
			
	public BesitzSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		this.database = database;
		
		database.execSQL(DATABASE_CREATE_CATEGORY);
		database.execSQL(DATABASE_CREATE_ITEM);
		database.execSQL(DATABASE_CREATE_ATTRIBUTE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.database = db;
		
		switch (newVersion) {
		case DATABASE_VERSION:
			switch (oldVersion) {
			case 1:
				database.execSQL(DATABASE_CREATE_ATTRIBUTE);
				break;
			}
			break;
		}
	}
	
	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		this.database = super.getWritableDatabase();
		return this.database;
	}
}
