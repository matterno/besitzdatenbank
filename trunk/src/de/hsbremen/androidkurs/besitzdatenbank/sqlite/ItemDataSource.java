package de.hsbremen.androidkurs.besitzdatenbank.sqlite;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper.BesitzSQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemDataSource {
	// Database fields
	private SQLiteDatabase database;
	private String[] allColumns = {
			BesitzSQLiteOpenHelper.COLUMN_ID,
			BesitzSQLiteOpenHelper.COLUMN_NAME,
			BesitzSQLiteOpenHelper.COLUMN_PICTURE,
			BesitzSQLiteOpenHelper.COLUMN_CATEGORYID };

	public ItemDataSource(SQLiteDatabase database) {
		this.database = database;
	}

	public long insertItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteOpenHelper.COLUMN_NAME, item.getName());
		values.put(BesitzSQLiteOpenHelper.COLUMN_PICTURE, item.getPicture());
		values.put(BesitzSQLiteOpenHelper.COLUMN_CATEGORYID, item.getCategoryId());
		return database.insert(BesitzSQLiteOpenHelper.TABLE_ITEM, null, values);
	}

	public long updateItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteOpenHelper.COLUMN_NAME, item.getName());
		values.put(BesitzSQLiteOpenHelper.COLUMN_PICTURE, item.getPicture());
		values.put(BesitzSQLiteOpenHelper.COLUMN_CATEGORYID, item.getCategoryId());
		return database.update(BesitzSQLiteOpenHelper.TABLE_ITEM, values,
				BesitzSQLiteOpenHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(item.getId()) });
	}

	public void deleteItem(long id) {
		database.delete(BesitzSQLiteOpenHelper.TABLE_ITEM,
				BesitzSQLiteOpenHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public Item getItem(long id) {
		Cursor cursor;
		Item item = null;
		
		if ((cursor = database.query(BesitzSQLiteOpenHelper.TABLE_ITEM, allColumns, "_id = ?", new String[] { String.valueOf(id) }, null, null, null)) != null) {
			if (cursor.moveToFirst()) {
				item = this.cursorToItem(cursor);
			}
			cursor.close();
		}

		return item;
	}

	public Cursor fetchItems() {
		return database.query(BesitzSQLiteOpenHelper.TABLE_ITEM,
				allColumns, null, null, null, null, null);
	}
	
	public List<Item> getAllItems() {
		List<Item> items = new ArrayList<Item>();

		Cursor cursor = fetchItems();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	public List<Item> findByCategoryId(long categoryId) {
		List<Item> items = new ArrayList<Item>();
		
		Cursor cursor = database.query(BesitzSQLiteOpenHelper.TABLE_ITEM, allColumns, "categoryId = ?", new String[] {categoryId + ""}, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return items;
	}
	
	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getLong(0));
		item.setName(cursor.getString(1));
		item.setPicture(cursor.getString(2));
		item.setCategoryId(cursor.getLong(3));
		return item;
	}
}
