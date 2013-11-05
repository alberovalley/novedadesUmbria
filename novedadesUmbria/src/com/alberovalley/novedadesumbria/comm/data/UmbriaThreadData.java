package com.alberovalley.novedadesumbria.comm.data;

public class UmbriaThreadData {

	public static final String TITLE_TAG = "TITULO";
	public static final String TEXT_TAG = "TEXTO";
	public static final String CONFIRM_TAG = "CONFIRMAR";
	
	public static final int CONFIRM = 1;
	private String title;
	private String text;
	
	
	
	private UmbriaThreadData() {
		this.title = "";
		this.text = "";
	}
	private UmbriaThreadData(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
	
}
