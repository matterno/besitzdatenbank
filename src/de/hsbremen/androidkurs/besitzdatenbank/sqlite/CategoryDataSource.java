package de.hsbremen.androidkurs.besitzdatenbank.sqlite;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Category;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper.BesitzSQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoryDataSource {
	// Database fields
	private SQLiteDatabase database;
	private String[] allColumns = { BesitzSQLiteOpenHelper.COLUMN_ID,
			BesitzSQLiteOpenHelper.COLUMN_NAME };

	public CategoryDataSource(SQLiteDatabase database) {
		this.database = database;
	}

	public long insertCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteOpenHelper.COLUMN_NAME, category.getName());
		return database.insert(BesitzSQLiteOpenHelper.TABLE_CATEGORY, null, values);
	}

	public long updateCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteOpenHelper.COLUMN_NAME, category.getName());
		return database.update(BesitzSQLiteOpenHelper.TABLE_CATEGORY, values,
				BesitzSQLiteOpenHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(category.getId()) });
	}

	public void deleteCategory(long id) {
		database.delete(BesitzSQLiteOpenHelper.TABLE_CATEGORY,
				BesitzSQLiteOpenHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public Category getCategory(long id) {
		Cursor cursor;
		Category category = null;

		if ((cursor = database.rawQuery("select "
				+ BesitzSQLiteOpenHelper.COLUMN_NAME + " from " + BesitzSQLiteOpenHelper.TABLE_CATEGORY
				+ " where _id = ?", new String[] { String.valueOf(id) })) != null) {
			if (cursor.moveToFirst()) {
				category = this.cursorToCategory(cursor);
			}
			cursor.close();
		}
		
		return category;
	}

	public Cursor fetchCategories() {
		return database.query(BesitzSQLiteOpenHelper.TABLE_CATEGORY,
				allColumns, null, null, null, null, null);
	}

	public List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();

		Cursor cursor = fetchCategories();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Category category = cursorToCategory(cursor);
			categories.add(category);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return categories;
	}
	
	private Category cursorToCategory(Cursor cursor) {
		Category category = new Category();
		category.setId(cursor.getLong(0));
		category.setName(cursor.getString(1));
		return category;
	}

	public long findIdByName(String value) {
		Cursor c = database.query(BesitzSQLiteOpenHelper.TABLE_CATEGORY, allColumns, "name LIKE ?", new String[]{value}, null, null, null);
		c.moveToFirst();
		if(c.isAfterLast()) {
			c.close();
			return 0;
		} else {
			long id = c.getLong(0);
			c.close();
			return id;
		}
	}
}
