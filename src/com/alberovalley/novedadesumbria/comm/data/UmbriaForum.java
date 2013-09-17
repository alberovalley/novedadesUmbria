package com.alberovalley.novedadesumbria.comm.data;

import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaForum implements Parcelable {

    private String forumName = "";
    private String forumUrl = "";
    private List<UmbriaForumThread> threads;

    private boolean errorFlag = false;
    private String errorMessageTitle = "";
    private String errorMessageBody = "";

    // ////////////////////////////////////////////////////////////
    // Constructors
    // ////////////////////////////////////////////////////////////
    public UmbriaForum(Parcel in) {
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

        dest.writeString(forumName);
        dest.writeString(forumUrl);
        dest.writeTypedList(threads);
        int intErrorFlag = errorFlag ? 1 : 0;
        dest.writeInt(intErrorFlag);
        dest.writeString(errorMessageTitle);
    }

    private void readFromParcel(Parcel in) {
        this.forumName = in.readString();
        this.forumUrl = in.readString();
        in.readTypedList(threads, UmbriaForumThread.CREATOR);
        int intErrorFlag = in.readInt();
        errorFlag = intErrorFlag == 1;
        errorMessageTitle = in.readString();

    }

    public static final Parcelable.Creator<UmbriaForum> CREATOR = new Parcelable.Creator<UmbriaForum>() {
        public UmbriaForum createFromParcel(Parcel in) {
            return new UmbriaForum(in);
        }

        public UmbriaForum[] newArray(int size) {
            return new UmbriaForum[size];
        }
    };

    // ////////////////////////////////////////////////////////////
    // Getters & Setters
    // ////////////////////////////////////////////////////////////

    public String getName() {
        return forumName;
    }

    public void setName(String forumName) {
        this.forumName = forumName;
    }

    public String getUrl() {
        return forumUrl;
    }

    public void setUrl(String forumUrl) {
        this.forumUrl = forumUrl;
    }

    public List<UmbriaForumThread> getScenes() {
        return threads;
    }

    public void setScenes(List<UmbriaForumThread> scenes) {
        this.threads = scenes;
    }

    public boolean isErrorFlag() {
        return errorFlag;
    }

    public String getErrorMessageTitle() {
        return errorMessageTitle;
    }

    public String getErrorMessageBody() {
        return errorMessageBody;
    }

    // ////////////////////////////////////////////////////////////
    // Other Methods
    // ////////////////////////////////////////////////////////////

    public int getNumberOfMessages() {
        int n = 0;

        Iterator<UmbriaForumThread> it = threads.iterator();
        while (it.hasNext()) {
            n += (it.next()).getNumberOfMessages();
        }

        return n;
    }

    public void addForumThread(UmbriaForumThread ft) {
        this.threads.add(ft);
    }
}
