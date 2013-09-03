package com.alberovalley.novedadesumbria.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaSection implements Parcelable{

	public static final String STORYTELLER_TAG = "director";
	public static final String PLAYER_TAG = "jugador";
	public static final String VIP_TAG = "VIP";
	public static final String MESSAGES_TAG = "mensajes";
	public static final String FORUM_TAG = "foro";
	
	private String name;
	private boolean notify;
	private List<UmbriaGame> games;

	public UmbriaSection(Parcel in)
	{
        readFromParcel(in);
    }
    public UmbriaSection()
    {
    	games = new ArrayList<UmbriaGame>();
    }
    
    public UmbriaGame getGame(String name)
    {
    	int i = 0;
    	while(!this.games.get(i).getName().equals(name)) i++;
    	
    	if(i < this.games.size()) return this.games.get(i);
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
	
	public void setNotify(boolean notify)
	{
		this.notify = notify;
	}	
	public boolean isNotify()
	{
		return this.notify;
	}

	public void setGames(List<UmbriaGame> games)
	{
		this.games = games;
	}	
	public List<UmbriaGame> getGames()
	{
		return this.games;
	}
	
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	dest.writeString(name);
    	dest.writeInt(notify?1:0);
    	dest.writeTypedList(games);
    }

    private void readFromParcel(Parcel in)
    {
    	name = in.readString();
    	notify = in.readInt() == 1;
    	in.readTypedList(games, UmbriaGame.CREATOR);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

	public static final Parcelable.Creator<UmbriaSection> CREATOR = new Parcelable.Creator<UmbriaSection>() {
        public UmbriaSection createFromParcel(Parcel in) {
            return new UmbriaSection(in);
        }

        public UmbriaSection[] newArray(int size) {
            return new UmbriaSection[size];
        }
    };
}
