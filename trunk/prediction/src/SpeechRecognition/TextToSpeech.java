package SpeechRecognition;

import java.io.File;
import java.util.Locale;

//import javax.speech.Central; 
import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;
import javax.speech.EngineList;
import javax.speech.EngineCreate;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class TextToSpeech {

	public TextToSpeech() {

	}

	public static void ConvertTextToSpeech(String text) {
		try {
			// Create a new SynthesizerModeDesc that will match the FreeTTS
			// Synthesizer.
			SynthesizerModeDesc desc = new SynthesizerModeDesc(null, null,
					Locale.ENGLISH, Boolean.FALSE, // running?
					null); // voice

			FreeTTSEngineCentral central = new FreeTTSEngineCentral();
			Synthesizer synthesizer = null;
			;

			EngineList list = central.createEngineList(desc);

			if (list.size() > 0) {
				EngineCreate creator = (EngineCreate) list.get(0);
				synthesizer = (Synthesizer) creator.createEngine();
			}

			if (synthesizer == null) {
				System.err.println("Can't find synthesizer");
				System.exit(1);
			}

			// get it ready to speak
			synthesizer.allocate();
			synthesizer.resume();

			// speak the "Hello world" string
			synthesizer.speakPlainText(text, null);

			// wait till speaking is done
			synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

			// clean up
			synthesizer.deallocate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
