package Main;

import javax.sound.sampled.*;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;

public class VoiceParser {
    public static void main(String[] args) throws EngineException {
        System.setProperty(
                "freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        // Register Engine
        Central.registerEngineCentral(
                "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

        VoiceParser parser = new VoiceParser();
        parser.saySomething("Test Hello this is a program that is going to say something");
    }

    AudioInputStream audioInputStream = null;
    SourceDataLine sourceDataline = null;


    void playAudio(File file){

        try{
            byte[] songByte = Files.readAllBytes(file.toPath());
            songByte = Arrays.copyOfRange(songByte, 44, songByte.length); // Cutting the header off the file to play the audio directly

            sourceDataline.open();  // OPEN THE LINE
            sourceDataline.start(); // START THE LINE
            sourceDataline.write(songByte, 0, songByte.length); // Write the data to the line
            sourceDataline.drain(); // Drain the line


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    void setupAudio() {
        AudioInputStream audioInputStream = null;
        try {
            Mixer.Info mixer = null;

            for (Mixer.Info m : AudioSystem.getMixerInfo()) {

                // Getting all t he mixers in the System

                System.out.println(m.getName() + " " + m.getDescription());

                if ((m.getName().contains("Headphones") && !m.getName().contains("Port"))) {
                    mixer = m;
                    System.out.println((mixer.getName()));
                    break;
                }
            }
            // Getting the Mixer Object using the information
            Mixer ActualMixer = AudioSystem.getMixer(mixer);

            // Debug print statement to see if we have the right one
            System.out.println(mixer.getName());

            // Getting info about the Source line to the mixer
            Line.Info[] LineInfo = ActualMixer.getSourceLineInfo();

            System.out.println(Arrays.toString(LineInfo)); // Debug statement
            Line.Info lineInfo = LineInfo[0]; // Getting the first element of the info (There's actually nothing more huh)


            try {
                sourceDataline = (SourceDataLine) ActualMixer.getLine(lineInfo); //Get the line
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            System.out.println("sourceDataline: " + sourceDataline); // more amazing debug statements
        } catch (Exception e) {
            System.out.println("Error");
        }
    }




    void saySomething(String speaktext) {
        try {
            String voiceName = "kevin16";
            SynthesizerModeDesc desc = new SynthesizerModeDesc(null, "general", Locale.US, null, null);
            Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.ENGLISH));
            synthesizer.allocate();
            synthesizer.resume();
            desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
            Voice[] voices = desc.getVoices();
            Voice voice = null;
            for (Voice value : voices) {
                if (value.getName().equals(voiceName)) {
                    voice = value;
                    break;
                }
            }
            synthesizer.getSynthesizerProperties().setVoice(voice);
            System.out.print("Speaking : " + speaktext);
            synthesizer.speakPlainText(speaktext, null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synthesizer.deallocate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}