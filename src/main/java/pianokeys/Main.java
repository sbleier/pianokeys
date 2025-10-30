package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class Main
{
    public static void main(String[] args) throws MidiUnavailableException {

        PianoGui gui = new PianoGui();
        gui.setVisible(true);

    }
}

