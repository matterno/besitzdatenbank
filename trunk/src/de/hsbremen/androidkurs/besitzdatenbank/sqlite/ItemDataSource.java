package de.hsbremen.androidkurs.besitzdatenbank.sqlite;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper.BesitzSQLiteHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ItemDataSource {
	// Database fields
	private SQLiteDatabase database;
	private BesitzSQLiteHelper dbHelper;
	private String[] allColumns = {
			BesitzSQLiteHelper.COLUMN_ID,
			BesitzSQLiteHelper.COLUMN_NAME,
			BesitzSQLiteHelper.COLUMN_PICTURE,
			BesitzSQLiteHelper.COLUMN_CATEGORYID };

	public ItemDataSource(Context context) {
		dbHelper = new BesitzSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteHelper.COLUMN_NAME, item.getName());
		values.put(BesitzSQLiteHelper.COLUMN_PICTURE, item.getPicture());
		values.put(BesitzSQLiteHelper.COLUMN_CATEGORYID, item.getCategoryId());
		return database.insert(BesitzSQLiteHelper.TABLE_CATEGORY, null, values);
	}

	public long updateItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteHelper.COLUMN_NAME, item.getName());
		values.put(BesitzSQLiteHelper.COLUMN_PICTURE, item.getPicture());
		values.put(BesitzSQLiteHelper.COLUMN_CATEGORYID, item.getCategoryId());
		return database.update(BesitzSQLiteHelper.TABLE_CATEGORY, values,
				BesitzSQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(item.getId()) });
	}

	public void deleteItem(long id) {
		database.delete(BesitzSQLiteHelper.TABLE_ITEM,
				BesitzSQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public Item getItem(long id) {
		Cursor cursor;
		Item item = null;

		if ((cursor = database.rawQuery("select "
				+ BesitzSQLiteHelper.COLUMN_NAME + " from " + BesitzSQLiteHelper.TABLE_ITEM
				+ " where _id = ?", new String[] { String.valueOf(id) })) != null) {
			if (cursor.moveToFirst()) {
				item = this.cursorToItem(cursor);
			}
			cursor.close();
		}

		return item;
	}

	public List<Item> getAllItems() {
		List<Item> items = new ArrayList<Item>();

		Cursor cursor = database.query(BesitzSQLiteHelper.TABLE_ITEM,
				allColumns, null, null, null, null, null);

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
