package utilities;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TextToVoice {
	
	private static final String VOICENAME= "kevin16";
	static Voice voice;
	
	TextToVoice(){
		
		VoiceManager voiceManager = VoiceManager.getInstance();
	        
		voice = voiceManager.getVoice(VOICENAME);
		voice.allocate();
	}
	public static void speech(String text){
		
		voice.speak(text);
	}

	
}