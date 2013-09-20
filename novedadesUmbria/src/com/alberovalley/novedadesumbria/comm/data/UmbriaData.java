package com.alberovalley.novedadesumbria.comm.data;


import android.content.Context;
import android.os.Parcelable;


/**
 * Interface
 * 
 */
public interface UmbriaData extends Parcelable{

	
	///////////////////////////////////////////////
	//
	//  METHODS
	//
	/////////////////////////////////////////////
	/**
	 * 
	 * @return the number of messages to be read in the Storyteller section 
	 */
	int getStorytellerMessageCount();
	
	/**
	 * 
	 * @return the number of messages to be read in the Player section 
	 */
	int getPlayerMessageCount();
	
	/**
	 * 
	 * @return the number of messages to be read in the VIP section 
	 */
	int getVIPMessageCount();
	
	/**
	 * 
	 * @return the number of Private Messages to be read 
	 */
	int getPrivateMessageCount();
	
	/**
	 * 
	 * @return whether an Exception happened during the process of gathering the data 
	 */
	boolean errorFlagged();
	
	/**
	 * Establishes the short and long messages of error that 
	 * @param shortErrorText
	 * @param longErrorText
	 */
	void flagError(String shortErrorText, String longErrorText);
	
	/**
	 * 
	 * @return whether there are messages waiting to be read
	 */
	boolean isThereAnythingNew();
	
	/**
	 * Creates the title for the notifications
	 * @param config Object that contains the user's preferences 
	 * @return formatted short message to be used in notifications
	 */
	String getShortNoticeText(UmbriaConfig config, Context ctx);
	
	/**
	 * Creates the body message for the notifications. Also used in widget 2x4
	 * @param @param config Object that contains the user's preferences 
	 * @return formatted long message to be used in notifications
	 */
	String getLongNoticeText(UmbriaConfig config, Context ctx);
}
