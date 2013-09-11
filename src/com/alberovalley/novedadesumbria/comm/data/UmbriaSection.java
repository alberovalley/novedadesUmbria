package com.alberovalley.novedadesumbria.comm.data;

import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UmbriaSection implements Parcelable {

    private String sectionName = "";
    private boolean notify = false;
    private List<UmbriaForum> forums;

    // ////////////////////////////////////////////////////////////
    // Constructors
    // ////////////////////////////////////////////////////////////
    public UmbriaSection(Parcel in) {
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

        dest.writeString(sectionName);
        int intNotify = notify ? 1 : 0;
        dest.writeInt(intNotify);
        dest.writeTypedList(forums);
    }

    private void readFromParcel(Parcel in) {
        this.sectionName = in.readString();
        int intNotify = in.readInt();
        notify = intNotify == 1;
        in.readTypedList(forums, UmbriaForum.CREATOR);

    }

    public static final Parcelable.Creator<UmbriaSection> CREATOR = new Parcelable.Creator<UmbriaSection>() {
        public UmbriaSection createFromParcel(Parcel in) {
            return new UmbriaSection(in);
        }

        public UmbriaSection[] newArray(int size) {
            return new UmbriaSection[size];
        }
    };

    // ////////////////////////////////////////////////////////////
    // Getters & Setters
    // ////////////////////////////////////////////////////////////

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public List<UmbriaForum> getForums() {
        return forums;
    }

    public void setForums(List<UmbriaForum> forums) {
        this.forums = forums;
    }

    // ////////////////////////////////////////////////////////////
    // Other Methods
    // ////////////////////////////////////////////////////////////

    public void addForum(UmbriaForum f) {
        this.forums.add(f);
    }

    public int getNumberOfMessages() {
        int n = 0;
        Iterator<UmbriaForum> it = forums.iterator();
        while (it.hasNext()) {
            n += (it.next()).getNumberOfMessages();
        }
        return n;
    }
}
