package com.alberovalley.novedadesumbria.comm.data;

import com.alberovalley.novedadesumbria.R;
import com.alberovalley.utils.AlberoLog;
import com.alberovalley.utils.AlberoStrings;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO used to transfer the data received from the website
 * 
 * @author frank
 * 
 */
public class UmbriaSimpleData implements UmbriaData {
	// ////////////////////////////////////////////////////////////
	// Attributes
	// ////////////////////////////////////////////////////////////
	private String numericalId = "";
	private int privateMessages = 0;
	private int storytellerMessages = 0;
	private int playerMessages = 0;
	private int vipMessages = 0;
	private boolean errorFlag = false;
	private String errorMessageTitle = "";
	private String errorMessageBody = "";


	// ////////////////////////////////////////////////////////////
	// Constructors
	// ////////////////////////////////////////////////////////////
	public UmbriaSimpleData(Parcel in) {
		readFromParcel(in);
	}

	public UmbriaSimpleData() {
	}


	// ////////////////////////////////////////////////////////////
	// Parcelable implementation
	// ////////////////////////////////////////////////////////////
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(numericalId);
		dest.writeInt(privateMessages);
		dest.writeInt(storytellerMessages);
		dest.writeInt(playerMessages);
		dest.writeInt(vipMessages);
		int intErrorFlag = errorFlag ? 1 : 0;
		dest.writeInt(intErrorFlag);
		dest.writeString(errorMessageTitle);
		dest.writeString(errorMessageBody);

	}

	private void readFromParcel(Parcel in) {
		numericalId = in.readString();
		privateMessages = in.readInt();
		storytellerMessages = in.readInt();
		playerMessages = in.readInt();
		vipMessages = in.readInt();
		int intErrorFlag = in.readInt();
		errorFlag = intErrorFlag == 1;
		errorMessageTitle = in.readString();
		errorMessageBody= in.readString();

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
		return this.storytellerMessages;
	}

	@Override
	public int getPlayerMessageCount() {
		return this.playerMessages;
	}

	@Override
	public int getVIPMessageCount() {
		return this.vipMessages;
	}

	@Override
	public int getPrivateMessageCount() {
		return this.privateMessages;
	}

	@Override
	public boolean errorFlagged() {
		return this.errorFlag;
	}



	public void setStorytellerMessageCount(int sm) {
		this.storytellerMessages = sm;
	}

	public void setPlayerMessageCount(int pm) {
		this.playerMessages = pm;
	}

	public void setVIPMessageCount(int vipm) {
		this.vipMessages = vipm;
	}

	public void setPrivateMessageCount(int pm) {
		this.privateMessages = pm;
	}



	@Override
	public void flagError(String shortErrorText, String longErrorText) {
		this.errorFlag = true;
		this.errorMessageTitle = shortErrorText;
		this.errorMessageBody = longErrorText;
	}

	@Override
	public boolean isThereAnythingNew() {
		AlberoLog.d("UmbriaSimpleData.isThereSomethingNew: " +
				"playerMessages["+playerMessages+"]; " +
				"storytellerMessages["+storytellerMessages+"]; " +
				"vipMessages["+vipMessages+"]; " +
				"privateMessages["+privateMessages+"]" );
		return (
				this.playerMessages > 0 ||
				this.storytellerMessages > 0 ||
				this.vipMessages > 0 ||
				this.privateMessages > 0
				);
	}

	@Override
	public String getShortNoticeText(UmbriaConfig config, Context ctx) {
		String shortText = "";
		if(this.errorFlag){
			shortText = errorMessageTitle;
		}else{
			if (isThereAnythingNew()){
				shortText = ctx.getResources().getString(R.string.notification_news_title);
			}else{
				// if this is true, there was no new messages, so use the empty message
				// only used in widget 2x3
				shortText = ctx.getResources().getString(R.string.widget_text_empty);
			}

		}
		return shortText;
	}

	@Override
	public String getLongNoticeText(UmbriaConfig config, Context ctx) {
		String longText = "";
		if(this.errorFlag){
			longText = errorMessageTitle;
		}else{
			longText  = ctx.getResources().getString(R.string.new_messages);
			String temp = longText ;
			if (config.isNotifyPlayerMessages() && this.playerMessages > 0) {
				longText  += " " + ctx.getResources().getString(R.string.new_messages_player);
			}
			if (config.isNotifyStorytellerMessages() && this.storytellerMessages > 0) {
				longText = AlberoStrings
						.appendComma(
								temp,
								longText,
								ctx.getResources().getString(R.string.new_messages_storyteller));
			}
			if (config.isNotifyVipMessages() && this.vipMessages > 0) {
				longText = AlberoStrings
						.appendComma(
								temp,
								longText,
								ctx.getResources().getString(R.string.new_messages_vip));

			}
			if (config.isNotifyPrivateMessages() && this.privateMessages > 0) {
				longText = AlberoStrings
						.appendComma(
								temp,
								longText,
								ctx.getResources().getString(R.string.new_messages_private));
			}
			// if this is true, there was no new messages, so use the empty message
			// only used in widget 2x3
			if(longText.equalsIgnoreCase(ctx.getResources().getString(R.string.new_messages))){
				longText = ctx.getResources().getString(R.string.widget_text_empty);
			}

		}
		return longText;
	}


}
