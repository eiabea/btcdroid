package com.eiabea.btcdroid.model;


public class CustomizeItem {
	private int id;
	private String name;
	private boolean isMain;
	
	public CustomizeItem(int id, String name, boolean isMain){
		this.id = id;
		this.name = name;
		this.isMain = isMain;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMain() {
		return isMain;
	}
	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}
	
	
}
