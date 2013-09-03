package com.alberovalley.novedadesumbria.comm;

import com.alberovalley.novedadesumbria.data.UmbriaData;
import com.alberovalley.novedadesumbria.data.UmbriaGame;
import com.alberovalley.novedadesumbria.data.UmbriaScene;
import com.alberovalley.novedadesumbria.data.UmbriaSection;

import android.util.Log;

public class NovedadesParser {

    public static final String STORYTELLER_TAG = "<h3>Nuevos mensajes en partidas como director</h3>";
    public static final String PLAYER_TAG = "<h3>Nuevos mensajes en las partidas como jugador</h3>";
    public static final String FORUM_TAG = "<h3>Nuevos mensajes en los foros</h3>";
    public static final String VIP_TAG = "<h3>Nuevos mensajes en partidas VIP</h3>";

    public static final String NO_MSG_TAG = "<p>No hay novedades</p>";

    public static int findStorytellerMessages(String html) {
        Log.d("novUmbria", "NovedadesParser.findStorytellerMessages ");
        return findMessageByTag(html, STORYTELLER_TAG);

    }

    public static int findPlayerMessages(String html) {
        Log.d("novUmbria", "NovedadesParser.findPlayerMessages");
        return findMessageByTag(html, PLAYER_TAG);

    }

    public static int findVIPMessages(String html) {
        Log.d("novUmbria", "NovedadesParser.findVIPMessages");
        return findMessageByTag(html, VIP_TAG);

    }

    public static String findNumericalId(String html, String username) {
        String numericalId = "";
        String substring = html.substring(html.lastIndexOf(username));
        return numericalId;
    }

    public static int findMessageByTag(String html, String tag) {
        Log.d("novUmbria", "findMessageByTag tag: " + tag);
        int msg = 0;
        int indexOfTag = html.lastIndexOf(tag);
        String substring = html.substring(indexOfTag + tag.length(), indexOfTag + (3 * tag.length()));
        Log.d("novUmbria", "findMessageByTag substring: " + substring);
        indexOfTag = substring.lastIndexOf(NO_MSG_TAG);
        Log.d("novUmbria", "findMessageByTag indexOfTag: " + indexOfTag);
        switch (indexOfTag) {
        case -1:
            // sí hay mensaje
            Log.d("novUmbria", "findMessageByTag hay mensaje: ");
            msg = 1;
            break;
        default:
            // no hay mensaje
            msg = 0;
            Log.d("novUmbria", "findMessageByTag no hay mensaje: ");

        }

        return msg;
    }

    public static int findPrivateMessages(String html) {
        Log.d("novUmbria", "NovedadesParser.findPrivateMessages ");
        int msg = 0;
        Log.w("novUmbria", "findPrivateMessages html" + html);
        final String tag = "title=\"Mensajes privados\"><span>";
        int indexOfTag = html.lastIndexOf(tag) + tag.length();
        if (indexOfTag > 0) {
            String substring = html.substring(
                    indexOfTag, indexOfTag + 1
                    );
            // String substring = html.substring(indexOfTag, indexOfTag + 1);
            Log.w("novUmbria", "findPrivateMessages substring  " + substring);
            msg = Integer.parseInt(substring);
        } else {
            Log.w("novUmbria", "findPrivateMessages indexOfTag <=0 ");
        }
        return msg;
    }
    
    public static UmbriaData ParseData(String html)
    {
    	UmbriaData uD = new UmbriaData();
    	
    	String startModule = "<div id=\"m_";
    	String finalModule = "</div>";
    	
    	/* Creating Private Messages Section */
    	UmbriaSection uSMSG = new UmbriaSection();
    	UmbriaGame uGMSG = new UmbriaGame();
    	UmbriaScene uScMSG = new UmbriaScene();
    	uScMSG.setName("Recividos");
    	uScMSG.setMessages(findPrivateMessages(html));
    	uGMSG.getScenes().add(uScMSG);
    	uGMSG.setName("Mensajes Privados");
    	uSMSG.getGames().add(uGMSG);
    	uSMSG.setName("Mensajes Privados");
    	uD.getSections().add(uSMSG);
    	
    	/* Adding the remaining Sections */
    	
    	int i = 0;
    	int j = 0;
    	
    	while((i = html.substring(j).indexOf(startModule))!=-1)
    	{
    		i = j + i + html.substring(j).indexOf(">");
    		j = i + html.substring(i).indexOf(finalModule);
    		String Section = html.substring(i, j);
        	Log.w("novUmbria", "Section substring:\n " + Section);
    		
    		UmbriaSection uS = ParseSections(Section);
    		if(uS != null)
    			uD.getSections().add(uS);
    			
    		i = j + finalModule.length();
    	}    	
    	
    	return uD;
    }

	private static UmbriaSection ParseSections(String htmlSection)
	{
		UmbriaSection uS = new UmbriaSection();
		
		if(htmlSection.indexOf(STORYTELLER_TAG) != -1)
		{
			uS.setName(UmbriaSection.STORYTELLER_TAG);
		}
		else if(htmlSection.indexOf(PLAYER_TAG) != -1)
		{
			uS.setName(UmbriaSection.PLAYER_TAG);			
		}
		else if(htmlSection.indexOf(VIP_TAG) != -1)
		{
			uS.setName(UmbriaSection.VIP_TAG);
		}
		else if(htmlSection.indexOf(FORUM_TAG) != -1)
		{
			uS.setName(UmbriaSection.FORUM_TAG);
		}
		else
			return null;
		
		String htmlGames = htmlSection.substring(htmlSection.indexOf("<li>"), htmlSection.lastIndexOf("</li>")+5);
		
		int i = 0;
		int j = 0;
		int k = 0;
		int nodeCount = 0;
		while((i = htmlGames.substring(j).indexOf("<"))!=-1)
    	{
			i = j + i + 1;
			if(htmlGames.charAt(i)!= '/')
				nodeCount ++;
			else
				nodeCount --;
    		
    		if(nodeCount == 0)
    		{
    			i = i + 5;
    			if(i > htmlGames.length())
    				i = htmlGames.length();

    			UmbriaGame uG = ParseGames(htmlGames.substring(k, i));
        		if(uS != null)
        			uS.getGames().add(uG);
        		k = i;
    		}
    		j = i;
    	}
		
		
		return uS;
	}

	private static UmbriaGame ParseGames(String htmlGame)
	{
		UmbriaGame uG = new UmbriaGame();
		uG.setURL(htmlGame.substring(htmlGame.indexOf("href=\"") + 6, htmlGame.indexOf("\">")));
		uG.setName(htmlGame.substring(htmlGame.indexOf("\">") + 2, htmlGame.indexOf("</a>")).trim());
		
		String htmlScenes = htmlGame.substring(htmlGame.indexOf("<ul>")+4, htmlGame.lastIndexOf("</ul>"));
		
		int i = 0;
		int j = 0;
		int k = 0;
		int nodeCount = 0;
		
		while((i = htmlScenes.substring(j).indexOf("<"))!=-1)
    	{
			i = j + i + 1;
			if(htmlScenes.charAt(i)!= '/')
				nodeCount ++;
			else
				nodeCount --;
    		
    		if(nodeCount == 0)
    		{
    			i += 5;
    			if(i > htmlScenes.length())
    				i = htmlScenes.length();
    			
    			UmbriaScene uS = ParseScenes(htmlScenes.substring(k, i));
        		if(uG != null)
        			uG.getScenes().add(uS);
        		k = i;
    		}
    		j = i;
    	}
		return uG;
	}

	private static UmbriaScene ParseScenes(String htmlScene)
	{
		UmbriaScene uS = new UmbriaScene();
		
		uS.setURL(htmlScene.substring(htmlScene.indexOf("href=\"") + 6, htmlScene.indexOf("\">")));
		uS.setName(htmlScene.substring(htmlScene.indexOf("\">") + 2, htmlScene.indexOf("</a>")).trim());
		
		String messages = htmlScene.substring(htmlScene.indexOf("<span>") + 6, htmlScene.indexOf("</span>"));
		uS.setMessages(Integer.parseInt(messages));
		
		return uS;
	}

}
