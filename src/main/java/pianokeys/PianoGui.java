package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Supplier;

import static java.awt.Color.*;

public class PianoGui extends JFrame
{

    // index of the keyboard-shortcut home octave, matching KeyboardInputHandler's un-shifted mapping
    private static final int HOME_OCTAVE_INDEX = 3;

    //dropdown to change instrument
    private final JComboBox<String> instrumentDropdown;

    // MIDI sound system
    private PianoSound sound;

    private PianoController controller;

    // window for searching BitMidi, created the first time it is opened
    private BitMidiSearchGui bitMidiSearchGui;

    public PianoGui()
    {
        initMidi();
        midiCleanup();
        setUpFrame();

        Composition composition = new Composition();
        CompositionView compositionView = new CompositionView(composition);
        compositionView.setPreferredSize(new Dimension(2000, 400));

        // Because PianoController needs to access PianoView and PianoView needs PianoController
        // this creates a circular dependency. One way to fix this is to create a Supplier that is passed
        // to PianoView which will return the PianoController after it's created after PianoView is instantiated.
        Supplier<PianoController> controllerSupplier = () -> controller;

        PianoView pianoView = new PianoView(controllerSupplier);
        JScrollPane pianoScrollPane = createScrollPane(pianoView);

        // Adding the controller object
        controller = new PianoController(compositionView, sound, composition, pianoView);

        new KeyboardInputHandler(controllerSupplier, pianoView,
                octaveIndex -> centerOnOctave(pianoScrollPane, pianoView, octaveIndex))
                .registerOn(getRootPane());

        add(pianoScrollPane, BorderLayout.SOUTH);
        // vertical and  horizontal scroll panes for CompositionView
        JScrollPane compositionScrollPane = new JScrollPane(
                compositionView,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );
        compositionScrollPane.setPreferredSize(new Dimension(800, 200));

        add(compositionScrollPane);

        // buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton erase = new JButton(new ImageIcon(getClass().getResource("/images/erase.png")));
        JButton restart = new JButton(new ImageIcon(getClass().getResource("/images/restart.png")));
        JButton record = new JButton(new ImageIcon(getClass().getResource("/images/record.png")));
        JButton play = new JButton(new ImageIcon(getClass().getResource("/images/play.png")));

        // erase button - calling controller
        erase.addActionListener(e ->
        {
            controller.eraseComposition();
        });

        // restart button
        restart.addActionListener(e ->
        {
            controller.restartComposition();
        });


        play.addActionListener(e ->
        {
            if (controller.playComposition())
            {
                play.setIcon(new ImageIcon(getClass().getResource("/images/pause.jpeg")));
            } else
            {
                controller.stopComposition();
                play.setIcon(new ImageIcon(getClass().getResource("/images/play.png")));
            }
        });

        buttonPanel.add(erase);
        buttonPanel.add(restart);
        buttonPanel.add(record);
        buttonPanel.add(play);

        // Making the instrument dropdown look like a button
        instrumentDropdown = new JComboBox<>(PianoSound.instruments);
        instrumentDropdown.setFont(new Font("Arial", Font.BOLD, 16));
        instrumentDropdown.setBackground(WHITE);
        instrumentDropdown.setForeground(BLACK);
        instrumentDropdown.setBorder(BorderFactory.createLineBorder(GRAY, 2));

        // Link the combo box to instrument change
        instrumentDropdown.addActionListener(e ->
        {
            String instrument = (String) instrumentDropdown.getSelectedItem();
            controller.changeInstrument(instrument);
        });

        // adding it to the button panel
        buttonPanel.add(instrumentDropdown);

        // opens a window to search BitMidi for songs
        JButton bitMidiSearch = new JButton("Search BitMidi");
        bitMidiSearch.addActionListener(e -> openBitMidiSearch());
        buttonPanel.add(bitMidiSearch);

        add(buttonPanel, BorderLayout.NORTH);

        centerOnOctave(pianoScrollPane, pianoView, HOME_OCTAVE_INDEX);

    }

    private void setUpFrame()
    {
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private void initMidi()
    {
        try
        {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel channel = synthesizer.getChannels()[0];
            sound = new PianoSound(synthesizer, channel);
        } catch (MidiUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    private void midiCleanup()
    {
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (sound != null)
                {
                    sound.cleanup();
                }
            }
        });
    }

    private JScrollPane createScrollPane(JLayeredPane layeredPane)
    {
        // uses a scroll method so that you can easily see all the keys
        return new JScrollPane(
                layeredPane,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
    }

    private void centerOnOctave(JScrollPane scrollPane, PianoView pianoView, int octaveIndex)
    {
        // Centers the scroll pane's viewport on the given octave's first white key.
        SwingUtilities.invokeLater(() ->
        {
            int octaveStartX = pianoView.getOctaveStartX(octaveIndex);

            int targetX = octaveStartX - (scrollPane.getViewport().getWidth() / 2)
                    + (pianoView.getWhiteKeyWidth() / 2);
            targetX = Math.max(0, Math.min(targetX, scrollPane.getHorizontalScrollBar().getMaximum()
                    - scrollPane.getViewport().getWidth()));

            scrollPane.getHorizontalScrollBar().setValue(targetX);
        });
    }

    private void openBitMidiSearch()
    {
        if (bitMidiSearchGui == null)
        {
            bitMidiSearchGui = new BitMidiSearchGui();
            bitMidiSearchGui.setLocationRelativeTo(this);
        }
        bitMidiSearchGui.setVisible(true);
        bitMidiSearchGui.toFront();
    }

}