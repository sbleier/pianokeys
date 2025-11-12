package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import static java.awt.Color.*;

public class PianoGui extends JFrame
{
    private CompositionView compositionView;
    private final Composition composition = new Composition();

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

    private CompositionRunnable runnable;

    public PianoGui()
    {
        initMidi();
        midiCleanup();
        setUpFrame();

        compositionView = new CompositionView();
        compositionView.setComposition(composition);

        JLayeredPane layeredPane = new PianoView(sound, composition, compositionView);
        JScrollPane pianoScrollPane = createScrollPane(layeredPane);
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

        // erase button
        erase.addActionListener(e ->
        {
            composition.getNoteList().clear();
            compositionView.repaint();
        });

        // restart button
        restart.addActionListener(e ->
        {
            compositionView.setCurrentTime(0.0);
        });


        play.addActionListener(e -> {
            if (runnable == null) {
                runnable = new CompositionRunnable(sound, Composition.ODE_TO_JOY, compositionView);
                play.setIcon(new ImageIcon(getClass().getResource("/images/pause.jpeg")));
                buttonPanel.repaint();
                new Thread(runnable).start();
            } else {
                play.setIcon(new ImageIcon(getClass().getResource("/images/play.png")));
                buttonPanel.repaint();
                runnable.stop();
                runnable = null;
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
            if (instrument != null && sound != null)
            {
                sound.setInstrument(instrument);
            }
        });

        // adding it to the button panel
        buttonPanel.add(instrumentDropdown);

        add(buttonPanel, BorderLayout.NORTH);

        play.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                compositionView.setComposition(composition);
                compositionView.refreshLayout();

                // 1) Expand to fit the full composition
                compositionView.fitToSeconds(composition.duration());
                compositionScrollPane.revalidate();

                // 2) Start playback
                double stepValue = CompositionRunnable.STEP;
                CompositionRunnable player = new CompositionRunnable(
                        sound,
                        composition,
                        compositionView,
                        stepValue
                );
                new Thread(player, "composition-playback").start();

                // 3) Bounce back after playback ends (duration + small buffer)
                int ms = (int) ((composition.duration() + Note.TIME_STEP) * 1000);
                javax.swing.Timer t = new javax.swing.Timer(ms, ev -> {
                    compositionView.resetToDefaultSize();
                    compositionScrollPane.revalidate();
                });
                t.setRepeats(false);
                t.start();
            }
        });

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