package pianokeys;

import pianokeys.net.BitMidiServiceFactory;

import javax.swing.*;
import java.util.function.Supplier;

public class BitMidiSearchGui extends JFrame
{
    private static final String BIT_MIDI_URL = "https://bitmidi.com/";

    private BitMidiSearchController controller;

    public BitMidiSearchGui()
    {
        setUpFrame();

        // Same approach as PianoGui: the view gets a Supplier because the controller
        // can only be created after the view exists.
        Supplier<BitMidiSearchController> controllerSupplier = () -> controller;
        BitMidiSearchView view = new BitMidiSearchView(controllerSupplier);

        controller = new BitMidiSearchController(view,
                new BitMidiServiceFactory(BIT_MIDI_URL).create());

        add(view);
    }

    private void setUpFrame()
    {
        setTitle("BitMidi Search");
        // hide instead of exit so closing this window doesn't close the piano
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(600, 500);
    }
}