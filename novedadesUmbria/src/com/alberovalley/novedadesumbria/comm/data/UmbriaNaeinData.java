package com.alberovalley.novedadesumbria.comm.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;


public class UmbriaNaeinData implements UmbriaData {

	// ////////////////////////////////////////////////////////////
	// Attributes
	// ////////////////////////////////////////////////////////////
	private String numericalId = "";
	private UmbriaSection StorytellerSection;
	private UmbriaSection PlayerSection;
	private UmbriaSection VIPSection;
	private int privateMessages = 0;
	private boolean errorFlag = false;
	private String errorMessageTitle = "";
	private String errorMessageBody = "";

	
	// ////////////////////////////////////////////////////////////
	// Constructors
	// ////////////////////////////////////////////////////////////
	
	// ////////////////////////////////////////////////////////////
	// Parcelable implementation
	// ////////////////////////////////////////////////////////////
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(numericalId);
		
		dest.writeParcelable(this.StorytellerSection, flags);
		dest.writeParcelable(this.PlayerSection, flags);
		dest.writeParcelable(this.VIPSection, flags);
		
		dest.writeInt(privateMessages);
		int intErrorFlag = errorFlag ? 1 : 0;
		dest.writeInt(intErrorFlag);
		dest.writeString(errorMessageTitle);

	}

	private void readFromParcel(Parcel in) {
		numericalId = in.readString();
		
		this.StorytellerSection = in.readParcelable(UmbriaSection.class.getClassLoader());
		this.PlayerSection = in.readParcelable(UmbriaSection.class.getClassLoader());
		this.VIPSection = in.readParcelable(UmbriaSection.class.getClassLoader());
		
		privateMessages = in.readInt();
		int intErrorFlag = in.readInt();
		errorFlag = intErrorFlag == 1;
		errorMessageTitle = in.readString();

	}

	public static final Parcelable.Creator<UmbriaSimpleData> CREATOR = new Parcelable.Creator<UmbriaSimpleData>() {
		public UmbriaSimpleData createFromParcel(Parcel in) {
			return new UmbriaSimpleData(in);
		}

		public UmbriaSimpleData[] newArray(int size) {
			return new UmbriaSimpleData[size];
		}
	};


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	// ////////////////////////////////////////////////////////////
	// UmbriaData Methods
	// ////////////////////////////////////////////////////////////		
	@Override
	public int getStorytellerMessageCount() {
		return this.StorytellerSection.getNumberOfMessages();
	}

	@Override
	public int getPlayerMessageCount() {
		return this.PlayerSection.getNumberOfMessages();
	}

	@Override
	public int getVIPMessageCount() {
		return this.VIPSection.getNumberOfMessages();
	}

	@Override
	public int getPrivateMessageCount() {
		return this.privateMessages;
	}

	@Override
	public boolean errorFlagged() {
		return this.errorFlag;
	}

	@Override
	public void flagError(String shortErrorText, String longErrorText) {
		this.errorFlag = true;
		this.errorMessageTitle = shortErrorText;
		this.errorMessageBody = longErrorText;
	}

	@Override
	public boolean isThereAnythingNew() {
		int playerMessages = getPlayerMessageCount();
		int storytellerMessages = getStorytellerMessageCount(); 
		int vipMessages = getVIPMessageCount();
		
		return (
				playerMessages != 0 ||
				storytellerMessages != 0 ||
				vipMessages != 0 ||
				this.privateMessages != 0
				);
	}

	@Override
	public String getShortNoticeText(UmbriaConfig config, Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLongNoticeText(UmbriaConfig config, Context ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	//TODO methods that return the iterators?
}
