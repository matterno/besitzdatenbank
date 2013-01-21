package de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity;

public class Attribute {
	
	private long id;
	private int type;
	private String name;
	private String value;
	private long itemId;
	
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_LOCATION = 1;
	public static final int TYPE_DATE = 2;
	public static final int TYPE_RATING = 3;
	
	public Attribute(int type, String name, String value, long itemId) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.itemId = itemId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {
		return name;
	}
}
