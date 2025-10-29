package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;

public class Main
{
    public static void main(String[] args) {
        PianoGui gui = new PianoGui();
        gui.setVisible(true);
    }
}

