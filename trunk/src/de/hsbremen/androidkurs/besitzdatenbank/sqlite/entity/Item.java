package de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity;

public class Item {
	
	private long id;
	private String name;
	private String picture;
	private long categoryId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	@Override
	public String toString() {
		return name;
	}
}
