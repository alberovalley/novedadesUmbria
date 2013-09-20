package com.alberovalley.novedadesumbria.comm.data;

/**
 * 
 * @author frank
 *
 */
public class UmbriaConfig {

	private boolean notifyPrivateMessages = false;
    private boolean notifyStorytellerMessages = false;
    private boolean notifyPlayerMessages = false;
    private boolean notifyVipMessages = false;
    private boolean useNotifications = false;

	///////////////////////////////////////////////
	//
	//  CONSTRUCTORS
	//
	/////////////////////////////////////////////    
    public UmbriaConfig(){
    	this.notifyPrivateMessages = false;
    	this.notifyStorytellerMessages = false;
        this.notifyPlayerMessages = false;
        this.notifyVipMessages = false;
        this.useNotifications = false;
    }
    
    public UmbriaConfig(boolean notifyPrvM, boolean notifyStrM, boolean notifyPlyM, boolean notifyVIPM){
    	this.notifyPrivateMessages = notifyPrvM;
    	this.notifyStorytellerMessages = notifyStrM;
        this.notifyPlayerMessages = notifyPlyM;
        this.notifyVipMessages = notifyVIPM;
    }
    
    public UmbriaConfig(boolean notifyPrvM, boolean notifyStrM, boolean notifyPlyM, boolean notifyVIPM, boolean useNotif){
    	this.notifyPrivateMessages = notifyPrvM;
    	this.notifyStorytellerMessages = notifyStrM;
        this.notifyPlayerMessages = notifyPlyM;
        this.notifyVipMessages = notifyVIPM;
        this.useNotifications = useNotif;
    }
	
    
	///////////////////////////////////////////////
	//
	//  METHODS
	//
	/////////////////////////////////////////////
	public boolean isNotifyPrivateMessages() {
		return notifyPrivateMessages;
	}

	public boolean isNotifyStorytellerMessages() {
		return notifyStorytellerMessages;
	}

	public boolean isNotifyPlayerMessages() {
		return notifyPlayerMessages;
	}

	public boolean isNotifyVipMessages() {
		return notifyVipMessages;
	}

    public boolean isUseNotifications(){
    	return this.useNotifications;
    }
    
    
    
}
