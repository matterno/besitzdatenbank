package de.hsbremen.androidkurs.besitzdatenbank.sqlite;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Attribute;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.helper.BesitzSQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AttributeDataSource {
	// Database fields
	private SQLiteDatabase database;
	private String[] allColumns = {
			BesitzSQLiteOpenHelper.COLUMN_ID,
			BesitzSQLiteOpenHelper.COLUMN_TYPE,
			BesitzSQLiteOpenHelper.COLUMN_NAME,
			BesitzSQLiteOpenHelper.COLUMN_VALUE,
			BesitzSQLiteOpenHelper.COLUMN_ITEMID };

	public AttributeDataSource(SQLiteDatabase database) {
		this.database = database;
	}

	public long insertAttribute(Attribute attribute) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteOpenHelper.COLUMN_TYPE, attribute.getType());
		values.put(BesitzSQLiteOpenHelper.COLUMN_NAME, attribute.getName());
		values.put(BesitzSQLiteOpenHelper.COLUMN_VALUE, attribute.getValue());
		values.put(BesitzSQLiteOpenHelper.COLUMN_ITEMID, attribute.getItemId());
		return database.insert(BesitzSQLiteOpenHelper.TABLE_ATTRIBUTE, null, values);
	}

	public long updateAttribute(Attribute attribute) {
		ContentValues values = new ContentValues();
		values.put(BesitzSQLiteOpenHelper.COLUMN_TYPE, attribute.getName());
		values.put(BesitzSQLiteOpenHelper.COLUMN_NAME, attribute.getName());
		values.put(BesitzSQLiteOpenHelper.COLUMN_VALUE, attribute.getValue());
		values.put(BesitzSQLiteOpenHelper.COLUMN_ITEMID, attribute.getItemId());
		return database.update(BesitzSQLiteOpenHelper.TABLE_ATTRIBUTE, values,
				BesitzSQLiteOpenHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(attribute.getId()) });
	}

	public void deleteAttribute(long id) {
		database.delete(BesitzSQLiteOpenHelper.TABLE_ATTRIBUTE,
				BesitzSQLiteOpenHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public Attribute getAttribute(long id) {
		Cursor cursor;
		Attribute attribute = null;
		
		if ((cursor = database.query(BesitzSQLiteOpenHelper.TABLE_ATTRIBUTE, allColumns, "_id = ?", new String[] { String.valueOf(id) }, null, null, null)) != null) {
			if (cursor.moveToFirst()) {
				attribute = this.cursorToAttribute(cursor);
			}
			cursor.close();
		}

		return attribute;
	}

	public Cursor fetchAttributes() {
		return database.query(BesitzSQLiteOpenHelper.TABLE_ATTRIBUTE,
				allColumns, null, null, null, null, null);
	}
	
	public List<Attribute> getAllAttributes() {
		List<Attribute> attributes = new ArrayList<Attribute>();

		Cursor cursor = fetchAttributes();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Attribute attribute = cursorToAttribute(cursor);
			attributes.add(attribute);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return attributes;
	}

	public List<Attribute> findByItemId(long itemId) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		Cursor cursor = database.query(BesitzSQLiteOpenHelper.TABLE_ATTRIBUTE, allColumns, "itemId = ?", new String[] {itemId + ""}, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Attribute attribute = cursorToAttribute(cursor);
			attributes.add(attribute);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return attributes;
	}
	
	private Attribute cursorToAttribute(Cursor cursor) {
		Attribute attribute = new Attribute(
				cursor.getInt(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getLong(4));
		attribute.setId(cursor.getLong(0));
		return attribute;
	}
}
