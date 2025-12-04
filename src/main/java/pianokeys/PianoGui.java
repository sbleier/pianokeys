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

    // declared everything
    private static final String[] WHITE_KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B"};

    // White keys
    private static final int WHITE_KEY_WIDTH = 50;
    private static final int WHITE_KEY_HEIGHT = 250;

    private static final int OCTAVES = 7;

    //dropdown to change instrument
    private final JComboBox<String> instrumentDropdown;

    // MIDI sound system
    private PianoSound sound;

    private PianoController controller;

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

        add(buttonPanel, BorderLayout.NORTH);

        centerOnMiddleC(pianoScrollPane);

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

    private void centerOnMiddleC(JScrollPane scrollPane)
    {
        // Centering the scroll pane to open on middle C - in octave 4 (0-based, so 3 is the 4th octave)
        SwingUtilities.invokeLater(() ->
        {
            int middleCoctave = 3;
            int middleCindex = middleCoctave * WHITE_KEY_NAMES.length;

            int middleCx = middleCindex * WHITE_KEY_WIDTH - (scrollPane.getViewport().getWidth() / 2)
                    + (WHITE_KEY_WIDTH / 2);
            middleCx = Math.max(0, Math.min(middleCx, scrollPane.getHorizontalScrollBar().getMaximum()
                    - scrollPane.getViewport().getWidth()));

            scrollPane.getHorizontalScrollBar().setValue(middleCx);
        });
    }

}