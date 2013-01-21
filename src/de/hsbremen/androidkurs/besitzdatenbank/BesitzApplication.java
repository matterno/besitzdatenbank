package de.hsbremen.androidkurs.besitzdatenbank;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.AttributeDataSource;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.CategoryDataSource;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.ItemDataSource;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper.BesitzSQLiteOpenHelper;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BesitzApplication extends Application {
	private static SQLiteDatabase sqliteDatabase;
	private static BesitzSQLiteOpenHelper sqliteOpenHelper;
	private static CategoryDataSource categroyDataSource;
	private static ItemDataSource itemDataSource;
	private static AttributeDataSource attributeDataSource;

	public static CategoryDataSource getCategoryDataSource() {
		return categroyDataSource;
	}

	public static ItemDataSource getItemDataSource() {
		return itemDataSource;
	}
	
	public static AttributeDataSource getAttributeDataSource() {
		return attributeDataSource;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Context context = getApplicationContext();

		sqliteOpenHelper = new BesitzSQLiteOpenHelper(context);
		sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
		
		categroyDataSource = new CategoryDataSource(sqliteDatabase);
		itemDataSource = new ItemDataSource(sqliteDatabase);
		attributeDataSource = new AttributeDataSource(sqliteDatabase);
	}
	
	@Override
	public void onTerminate() {
		if(sqliteDatabase != null) {
			sqliteDatabase.close();
		}
		
		if(sqliteOpenHelper != null) {
			sqliteOpenHelper.close();
		}
		super.onTerminate();
	}
}
