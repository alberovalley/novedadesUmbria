package com.alberovalley.novedadesumbria.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaGame implements Parcelable{
	
	private String name;
	private String url;
	private List<UmbriaScene> scenes;

	public UmbriaGame(Parcel in)
	{
        readFromParcel(in);
    }
    public UmbriaGame()
    {
    	scenes = new ArrayList<UmbriaScene>();
    }
    
    public UmbriaScene getScene(String name)
    {
    	int i = 0;
    	while(!this.scenes.get(i).getName().equals(name)) i++;
    	
    	if(i < this.scenes.size()) return this.scenes.get(i);
    	return null;
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
	
	public void setScenes(List<UmbriaScene> scenes)
	{
		this.scenes = scenes;
	}	
	public List<UmbriaScene> getScenes()
	{
		return this.scenes;
	}
	
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	dest.writeString(name);
    	dest.writeString(url);
    	dest.writeTypedList(scenes);
    }

    private void readFromParcel(Parcel in)
    {
    	this.name = in.readString();
    	this.url = in.readString();
    	in.readTypedList(scenes, UmbriaScene.CREATOR);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

	public static final Parcelable.Creator<UmbriaGame> CREATOR = new Parcelable.Creator<UmbriaGame>() {
        public UmbriaGame createFromParcel(Parcel in) {
            return new UmbriaGame(in);
        }

        public UmbriaGame[] newArray(int size) {
            return new UmbriaGame[size];
        }
    };

}
