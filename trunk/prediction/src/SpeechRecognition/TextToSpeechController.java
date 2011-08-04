package SpeechRecognition;


import java.io.File;
import java.util.Locale;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;
import javax.speech.EngineList;
import javax.speech.EngineCreate;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import SpeechRecognition.TextToSpeech;;

public class TextToSpeechController {
	
	public TextToSpeechController(){
		
	}
	
	    public static TextToSpeech audioFeedback = new TextToSpeech();
	    
	    public void getaudioFeedback(String text){

		audioFeedback.ConvertTextToSpeech(text);
	    }
		
	}
           