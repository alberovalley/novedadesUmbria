package com.alberovalley.novedadesumbria.data;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaScene implements Parcelable{

	private String name;
	private String url;
	private int messages;

	public UmbriaScene(Parcel in)
	{
        readFromParcel(in);
    }
    public UmbriaScene()
    {
    }
    
	public void setName(String name)
	{
		this.name = name;
	}	
	
	public String getName()
	{
		return this.name;
	}

	public void setURL(String url)
	{
		this.url = url;
	}	
	public String getURL()
	{
		return this.url;
	}
	
	public void setMessages(int messages)
	{
		this.messages = messages;
	}	
	
	public int getMessages()
	{
		return this.messages;
	}
	
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	dest.writeString(this.name);
    	dest.writeString(this.url);
    	dest.writeInt(this.messages);
    }

    private void readFromParcel(Parcel in)
    {
    	this.name = in.readString();
    	this.url = in.readString();
    	this.messages = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

	public static final Parcelable.Creator<UmbriaScene> CREATOR = new Parcelable.Creator<UmbriaScene>() {
        public UmbriaScene createFromParcel(Parcel in) {
            return new UmbriaScene(in);
        }

        public UmbriaScene[] newArray(int size) {
            return new UmbriaScene[size];
        }
    };

}
