package de.hsbremen.androidkurs.besitzdatenbank.sqlite;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Category;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper.BesitzSQLiteHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CategoryDataSource {
	// Database fields
	private SQLiteDatabase database;
	private BesitzSQLiteHelper dbHelper;
	private String[] allColumns = { BesitzSQLiteHelper.COLUMN_ID,
			BesitzSQLiteHelper.COLUMN_NAME };

	public CategoryDataSource(Context context) {
		dbHelper = new BesitzSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteHelper.COLUMN_NAME, category.getName());
		return database.insert(BesitzSQLiteHelper.TABLE_CATEGORY, null, values);
	}

	public long updateCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteHelper.COLUMN_NAME, category.getName());
		return database.update(BesitzSQLiteHelper.TABLE_CATEGORY, values,
				BesitzSQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(category.getId()) });
	}

	public void deleteCategory(long id) {
		database.delete(BesitzSQLiteHelper.TABLE_CATEGORY,
				BesitzSQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public Category getCategory(long id) {
		Cursor cursor;
		Category category = null;

		if ((cursor = database.rawQuery("select "
				+ BesitzSQLiteHelper.COLUMN_NAME + " from " + BesitzSQLiteHelper.TABLE_CATEGORY
				+ " where _id = ?", new String[] { String.valueOf(id) })) != null) {
			if (cursor.moveToFirst()) {
				category = new Category();

				category.setId(id);
				category.setName(cursor.getString(1));
			}
			cursor.close();
		}
		
		return category;
	}

	public List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();

		Cursor cursor = database.query(BesitzSQLiteHelper.TABLE_CATEGORY,
				allColumns, null, null, null, null, null);

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
}
