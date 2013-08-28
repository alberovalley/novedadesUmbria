package com.alberovalley.novedadesumbria.comm;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaData implements Parcelable {

    protected String numericalId = "";
    protected int privateMessages = 0;
    protected int storytellerMessages = 0;
    protected int playerMessages = 0;
    protected int vipMessages = 0;
    protected boolean errorFlag = false;
    protected String errorMessage = "";

    public UmbriaData(Parcel in) {
        readFromParcel(in);
    }

    public UmbriaData() {
    }

    public void flagError(String errorMsg) {
        this.errorFlag = true;
        this.errorMessage = errorMsg;
    }

    public int getStorytellerMessages() {
        return storytellerMessages;
    }

    public void setStorytellerMessages(int storytellerMessages) {
        this.storytellerMessages = storytellerMessages;
    }

    public int getPlayerMessages() {
        return playerMessages;
    }

    public void setPlayerMessages(int playerMessages) {
        this.playerMessages = playerMessages;
    }

    public int getVipMessages() {
        return vipMessages;
    }

    public void setVipMessages(int vipMessages) {
        this.vipMessages = vipMessages;
    }

    public boolean isThereError() {
        return this.errorFlag;
    }

    public String getNumericalId() {
        return numericalId;
    }

    public void setNumericalId(String numericalId) {
        this.numericalId = numericalId;
    }

    public int getPrivateMessages() {
        return privateMessages;
    }

    public void setPrivateMessages(int privateMessages) {
        this.privateMessages = privateMessages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(numericalId);
        dest.writeInt(privateMessages);
        dest.writeInt(storytellerMessages);
        dest.writeInt(playerMessages);
        dest.writeInt(vipMessages);
        int intErrorFlag = errorFlag ? 1 : 0;
        dest.writeInt(intErrorFlag);
        dest.writeString(errorMessage);
    }

    private void readFromParcel(Parcel in) {
        numericalId = in.readString();
        privateMessages = in.readInt();
        storytellerMessages = in.readInt();
        playerMessages = in.readInt();
        vipMessages = in.readInt();
        int intErrorFlag = in.readInt();
        errorFlag = intErrorFlag == 1;
        errorMessage = in.readString();
    }

    public static final Parcelable.Creator<UmbriaData> CREATOR = new Parcelable.Creator<UmbriaData>() {
        public UmbriaData createFromParcel(Parcel in) {
            return new UmbriaData(in);
        }

        public UmbriaData[] newArray(int size) {
            return new UmbriaData[size];
        }
    };

}
