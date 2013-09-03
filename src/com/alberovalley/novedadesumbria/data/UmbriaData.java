package com.alberovalley.novedadesumbria.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaData implements Parcelable {

    protected boolean errorFlag = false;
    protected String errorMessage = "";
	
	private List<UmbriaSection> sections;
	
	public UmbriaData(Parcel in)
	{
        readFromParcel(in);
    }
    public UmbriaData()
    {
    	this.sections =  new ArrayList<UmbriaSection>();
    }    

    /* ERRORS */
    public void flagError(String errorMsg) {
        this.errorFlag = true;
        this.errorMessage = errorMsg;
    }
    
    public boolean isThereError()
    {
        return this.errorFlag;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    /**/
    
    public UmbriaSection getSection(String name)
    {
    	int i = 0;
    	while((i < this.sections.size()) && (!this.sections.get(i).getName().equals(name))) i++;
    	
    	if(i < this.sections.size()) return this.sections.get(i);
    	return null;
    }

    public int getMessagesFrom(String name)
    {
    	if(this.getSection(name) != null && this.getSection(name).getGames().size()>0)
        	return 1;
        return 0;
    }    
    public int getStorytellerMessages()
    {
        return getMessagesFrom(UmbriaSection.STORYTELLER_TAG);
    }    
    public int getPlayerMessages()
    {
        return getMessagesFrom(UmbriaSection.PLAYER_TAG);
    }
    public int getVipMessages()
    {
        return getMessagesFrom(UmbriaSection.VIP_TAG);
    }
    public int getPrivateMessages()
    {
        return getMessagesFrom(UmbriaSection.MESSAGES_TAG);
    }
    
    public boolean isNotify(String name)
    {
    	if(this.getSection(name) != null)
        	return this.getSection(name).isNotify();
        return false;
    }
    public boolean isNotifyStorytellerMessages()
    {
        return isNotify(UmbriaSection.STORYTELLER_TAG);
    }
    public boolean isNotifyPlayerMessages()
    {
        return isNotify(UmbriaSection.PLAYER_TAG);
    }
    public boolean isNotifyVipMessages()
    {
        return isNotify(UmbriaSection.VIP_TAG);
    }
    public boolean isNotifyPrivateMessages()
    {
    	return isNotify(UmbriaSection.MESSAGES_TAG);
    }
    
    public void setNotify(String name, boolean notify)
    {
    	if(this.getSection(name) != null)
        	this.getSection(name).setNotify(notify);
    }
    public void setNotifyStorytellerMessages(boolean notifyStorytellerMessages)
    {
    	setNotify(UmbriaSection.STORYTELLER_TAG,notifyStorytellerMessages);
    }
    public void setNotifyPlayerMessages(boolean notifyPlayerMessages)
    {
    	setNotify(UmbriaSection.PLAYER_TAG,notifyPlayerMessages);
    }
    public void setNotifyVipMessages(boolean notifyVipMessages)
    {
    	setNotify(UmbriaSection.VIP_TAG,notifyVipMessages);
    }
    public void setNotifyPrivateMessages(boolean notifyPrivateMessages)
    {
    	setNotify(UmbriaSection.MESSAGES_TAG,notifyPrivateMessages);
    }
    
    
    public void setSections(List<UmbriaSection> sections)
	{
		this.sections = sections;
	}	
	public List<UmbriaSection> getSections()
	{
		return this.sections;
	}
    
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(errorFlag?1:0);
        dest.writeString(errorMessage);
    	dest.writeTypedList(sections);
    }
    
    private void readFromParcel(Parcel in)
    {
    	errorFlag = in.readInt() == 1;
    	errorMessage = in.readString();
    	in.readTypedList(sections, UmbriaSection.CREATOR);
    }
    
    public static final Parcelable.Creator<UmbriaData> CREATOR = new Parcelable.Creator<UmbriaData>() {
        public UmbriaData createFromParcel(Parcel in) {
            return new UmbriaData(in);
        }

        public UmbriaData[] newArray(int size) {
            return new UmbriaData[size];
        }
    };

	@Override
	public int describeContents()
	{
		return 0;
	}

}
