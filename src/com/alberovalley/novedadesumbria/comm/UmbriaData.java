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

    protected boolean notifyPrivateMessages = false;
    protected boolean notifyStorytellerMessages = false;
    protected boolean notifyPlayerMessages = false;
    protected boolean notifyVipMessages = false;

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

    public boolean isNotifyPrivateMessages() {
        return notifyPrivateMessages;
    }

    public void setNotifyPrivateMessages(boolean notifyPrivateMessages) {
        this.notifyPrivateMessages = notifyPrivateMessages;
    }

    public boolean isNotifyStorytellerMessages() {
        return notifyStorytellerMessages;
    }

    public void setNotifyStorytellerMessages(boolean notifyStorytellerMessages) {
        this.notifyStorytellerMessages = notifyStorytellerMessages;
    }

    public boolean isNotifyPlayerMessages() {
        return notifyPlayerMessages;
    }

    public void setNotifyPlayerMessages(boolean notifyPlayerMessages) {
        this.notifyPlayerMessages = notifyPlayerMessages;
    }

    public boolean isNotifyVipMessages() {
        return notifyVipMessages;
    }

    public void setNotifyVipMessages(boolean notifyVipMessages) {
        this.notifyVipMessages = notifyVipMessages;
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

        int iNotifyPrivateMessages = notifyPrivateMessages ? 1 : 0;
        dest.writeInt(iNotifyPrivateMessages);
        int iNotifyStorytellerMessages = notifyStorytellerMessages ? 1 : 0;
        dest.writeInt(iNotifyStorytellerMessages);
        int iNotifyPlayerMessages = notifyPlayerMessages ? 1 : 0;
        dest.writeInt(iNotifyPlayerMessages);
        int iNotifyVipMessages = notifyVipMessages ? 1 : 0;
        dest.writeInt(iNotifyVipMessages);

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

        int iNotifyPrivateMessages = in.readInt();
        notifyPrivateMessages = iNotifyPrivateMessages == 1;
        int iNotifyStorytellerMessages = in.readInt();
        notifyStorytellerMessages = iNotifyStorytellerMessages == 1;
        int iNotifyPlayerMessages = in.readInt();
        notifyPlayerMessages = iNotifyPlayerMessages == 1;
        int iNotifyVipMessages = in.readInt();
        notifyVipMessages = iNotifyVipMessages == 1;

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
