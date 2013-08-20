package com.alberovalley.novedadesumbria.comm;

public class UmbriaData {

    protected int storytellerMessages = 0;
    protected int playerMessages = 0;
    protected int vipMessages = 0;
    protected boolean errorFlag = false;
    protected String errorMessage = "";

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
}
