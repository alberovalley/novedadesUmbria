package com.alberovalley.novedadesumbria.comm.data;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaForumThread implements Parcelable {
    // ////////////////////////////////////////////////////////////
    // Attributes
    // ////////////////////////////////////////////////////////////

    private String threadName = "";
    private String threadUrl = "";
    private int numberOfMessages = 0;

    // ////////////////////////////////////////////////////////////
    // Constructors
    // ////////////////////////////////////////////////////////////
    public UmbriaForumThread(Parcel in) {
        readFromParcel(in);
    }

    // ////////////////////////////////////////////////////////////
    // Parcelable Implementation
    // ////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(threadName);
        dest.writeString(threadUrl);
        dest.writeInt(numberOfMessages);
    }

    private void readFromParcel(Parcel in) {
        this.threadName = in.readString();
        this.threadUrl = in.readString();
        this.numberOfMessages = in.readInt();
    }

    public static final Parcelable.Creator<UmbriaForumThread> CREATOR = new Parcelable.Creator<UmbriaForumThread>() {
        public UmbriaForumThread createFromParcel(Parcel in) {
            return new UmbriaForumThread(in);
        }

        public UmbriaForumThread[] newArray(int size) {
            return new UmbriaForumThread[size];
        }
    };

    // ////////////////////////////////////////////////////////////
    // Getters & Setters
    // ////////////////////////////////////////////////////////////

    public String getName() {
        return threadName;
    }

    public void setName(String name) {
        this.threadName = name;
    }

    public String getUrl() {
        return threadUrl;
    }

    public void setUrl(String url) {
        this.threadUrl = url;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    // ////////////////////////////////////////////////////////////
    // Other Methods
    // ////////////////////////////////////////////////////////////

}
