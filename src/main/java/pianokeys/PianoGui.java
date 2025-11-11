package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.awt.Color.*;

public class PianoGui extends JFrame
{
    private CompositionView compositionView;
    private final Composition composition = new Composition();
    private long recordStartTime = -1;
    private long currentPressTime = -1;

    // declared everything
    private static final String[] WHITE_KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B"};
    private static final String[] BLACK_KEY_NAMES = {"C#", "D#", "", "F#", "G#", "A#", ""};

    // White keys
    private static final int WHITE_KEY_WIDTH = 50;
    private static final int WHITE_KEY_HEIGHT = 250;
    // Black keys
    private static final int BLACK_KEY_WIDTH = 30;
    private static final int BLACK_KEY_HEIGHT = 170;

    private static final int OCTAVES = 7;

    private static final Color C_BASE_COLOR = new Color(255, 245, 200);
    private static final Color C_HOVER_COLOR = new Color(255, 235, 180);

    private final JButton[] whiteButtons = new JButton[WHITE_KEY_NAMES.length * OCTAVES];
    private final JButton[] blackButtons = new JButton[5 * OCTAVES];

    //dropdown to change instrument
    private JComboBox<String> instrumentDropdown;

    // MIDI sound system
    private PianoSound sound;

    public PianoGui()
    {
        initMidi();
        midiCleanup();
        setUpFrame();
        JPanel whiteKeysPanel = createWhiteKeysPanel();
        JPanel blackKeysPanel = createBlackKeysPanel();

        JLayeredPane layeredPane = createLayeredPane(whiteKeysPanel, blackKeysPanel);
        JScrollPane pianoScrollPane = createScrollPane(layeredPane);

        add(pianoScrollPane, BorderLayout.SOUTH);

        compositionView = new CompositionView();
        compositionView.setComposition(composition);

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
        instrumentDropdown.addActionListener(e -> {
            String instrument = (String) instrumentDropdown.getSelectedItem();
            if (instrument != null && sound != null) {
                sound.setInstrument(instrument);
            }
        });

        // adding it to the button panel
        buttonPanel.add(instrumentDropdown);

        add(buttonPanel, BorderLayout.NORTH);

        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compositionView.setComposition(composition);

                // expand ONLY for playback
                compositionView.fitToSeconds(composition.duration());
                compositionScrollPane.revalidate();

                //play
                final CompositionRunnable player = new CompositionRunnable(sound, composition, 0.125);
                final Thread playThread = new Thread(player, "composition-play");
                playThread.start();

                // snap back after playback ends
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            playThread.join();
                        } catch (InterruptedException ignored) {
                            System.err.println("Playback thread interrupted");
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                compositionView.resetToDefaultSize();
                                compositionScrollPane.revalidate();
                            }
                        });
                    }
                }, "composition-resize-reset").start();
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

    private int getWhiteNoteForOctave(int octave, int whiteIndex)
    {
        return PianoSound.whiteNotes[whiteIndex] + (octave - 3) * 12;
    }

    private int getBlackNoteForOctave(int octave, int blackIndex)
    {
        int base = PianoSound.blackNotes[blackIndex];
        if (base == -1)
        {
            return -1;
        }
        return base + (octave - 3) * 12;
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

    private JPanel createWhiteKeysPanel()
    {
        JPanel whiteKeysPanel = new JPanel(null);
        whiteKeysPanel.setOpaque(true);
        whiteKeysPanel.setBackground(LIGHT_GRAY);

        // nested for loop to run each octave to get 56 white keys
        // switched out i for keyIndex so that we can loop through without them all overlapping
        for (int octave = 0; octave < OCTAVES; octave++)
        {
            for (int i = 0; i < WHITE_KEY_NAMES.length; i++)
            {
                int note = getWhiteNoteForOctave(octave, i);
                JButton button = createWhitePianoKey(WHITE_KEY_NAMES[i], note);

                // calculating position across all the indexes not just the first loop
                int keyIndex = octave * WHITE_KEY_NAMES.length + i;
                whiteButtons[keyIndex] = button;

                button.setBounds(keyIndex * WHITE_KEY_WIDTH, 0, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
                whiteKeysPanel.add(button);
            }
        }
        return whiteKeysPanel;
    }

    private JPanel createBlackKeysPanel()
    {
        JPanel blackKeysPanel = new JPanel();
        blackKeysPanel.setLayout(null);
        blackKeysPanel.setOpaque(false);

        int blackKeyIndex = 0;
        for (int octave = 0; octave < OCTAVES; octave++)
        {
            for (int i = 0; i < BLACK_KEY_NAMES.length; i++)
            {
                if (!BLACK_KEY_NAMES[i].isEmpty())
                {
                    int note = getBlackNoteForOctave(octave, i);
                    if (note == -1)
                    {
                        continue;
                    } // skip keys that don't exist (like between E-F or B-C)
                    JButton button = createBlackPianoKey(BLACK_KEY_NAMES[i], note);

                    blackButtons[blackKeyIndex] = button;

                    int whiteKeyPosition = octave * WHITE_KEY_NAMES.length + i;

                    // make sure that the black keys are between the white keys
                    int blackKeyX = (whiteKeyPosition * WHITE_KEY_WIDTH) + WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2);
                    button.setBounds(blackKeyX, 0, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
                    blackKeysPanel.add(button);

                    blackKeyIndex++;
                }
            }
        }
        return blackKeysPanel;
    }

    private JLayeredPane createLayeredPane(JPanel whiteKeysPanel, JPanel blackKeysPanel)
    {
        // Layered pane to make the black keys on white keys
        JLayeredPane layeredPane = new JLayeredPane();
        int totalWidth = WHITE_KEY_NAMES.length * OCTAVES * WHITE_KEY_WIDTH;
        layeredPane.setPreferredSize(new Dimension(totalWidth, WHITE_KEY_HEIGHT));

        whiteKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);
        blackKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);

        // using different layers to add the panels
        layeredPane.add(whiteKeysPanel, Integer.valueOf(0));
        layeredPane.add(blackKeysPanel, Integer.valueOf(1));


        add(layeredPane, BorderLayout.CENTER);

        instrumentDropdown = new JComboBox<>(PianoSound.instruments);

        instrumentDropdown.addActionListener(e ->
        {
            String instrument = (String) instrumentDropdown.getSelectedItem();
            sound.setInstrument(instrument);
        });
        add(instrumentDropdown, BorderLayout.SOUTH);
        return layeredPane;
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

    private long startRecord(int note)
    {
        long currentTime = System.currentTimeMillis();

        if (recordStartTime == -1)
        {
            recordStartTime = currentTime; // when it started
        }
        long pressTime = currentTime - recordStartTime; // makes it 0 for the first key - how long since start

        if (sound != null)
        {
            sound.playNote(note);
        }
        return pressTime;
    }

    private void endRecord(int note, long pressTime)
    {
        long releaseTime = System.currentTimeMillis() - recordStartTime; // time since first key pressed
        double startSec = pressTime / 1000.0;
        double endSec = releaseTime / 1000.0;

        // create and store the note in the composition
        composition.addNote(new Note(note, startSec, endSec));
        System.out.println("Recorded note: " + note + " from " + startSec + "s to " + endSec + "s");
        System.out.println("Total notes recorded: " + composition.getNoteList().size());

        compositionView.repaint();

        if (sound != null)
        {
            sound.stopNote(note);
        }
    }

    private JButton createWhitePianoKey(String whiteKeyName, int note)
    {
        JButton key = new JButton(whiteKeyName);

        // make it look like a piano key
        key.setForeground(BLACK);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);
        key.setBorder(BorderFactory.createLineBorder(BLACK, 2));
        key.setOpaque(true);
        key.setContentAreaFilled(true);

        final Color baseColor;
        final Color hoverColor;

        if (note == PianoSound.C4)
        {
            baseColor = C_BASE_COLOR;
            hoverColor = C_HOVER_COLOR;
            key.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            key.setToolTipText("Middle C (C4)");
        } else
        {
            baseColor = WHITE;
            hoverColor = LIGHT_GRAY;
        }
        key.setBackground(baseColor);

        // hover to show the key
        key.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                int modifiers = evt.getModifiersEx();
                if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0)
                {
                    // left mouse button
                    key.setBackground(DARK_GRAY);
                    currentPressTime = startRecord(note);
                } else
                {
                    key.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                key.setBackground(baseColor);
                int modifiers = evt.getModifiersEx();
                if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0)
                {
                    // left mouse button
                    endRecord(note, currentPressTime);
                }
            }

            @Override
            public void mousePressed(MouseEvent evt)
            {
                key.setBackground(DARK_GRAY);
                currentPressTime = startRecord(note);
            }

            @Override
            public void mouseReleased(MouseEvent evt)
            {
                endRecord(note, currentPressTime);
                if (key.contains(evt.getPoint()))
                {
                    key.setBackground(hoverColor);
                } else
                {
                    key.setBackground(baseColor);
                }
            }

        });

        key.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Key pressed: " + whiteKeyName);
            }
        });
        return key;
    }

    private JButton createBlackPianoKey(String blackKeyName, int note)
    {
        JButton key = new JButton(blackKeyName);
        key.setBackground(BLACK);
        key.setForeground(WHITE);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);
        key.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        key.setOpaque(true);
        key.setContentAreaFilled(true);

        key.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                int modifiers = evt.getModifiersEx();
                if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0)
                {
                    key.setBackground(DARK_GRAY);
                    // left mouse button
                    currentPressTime = startRecord(note);
                } else
                {
                    key.setBackground(LIGHT_GRAY);
                }
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                key.setBackground(BLACK);
                int modifiers = evt.getModifiersEx();
                if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0)
                {
                    // left mouse button
                    endRecord(note, currentPressTime);
                }
            }

            @Override
            public void mousePressed(MouseEvent evt)
            {
                key.setBackground(DARK_GRAY);
                currentPressTime = startRecord(note);
            }

            @Override
            public void mouseReleased(MouseEvent evt)
            {
                endRecord(note, currentPressTime);
                // Check if mouse is still over the component
                if (key.contains(evt.getPoint()))
                {
                    key.setBackground(LIGHT_GRAY);
                } else
                {
                    key.setBackground(BLACK);
                }
            }
        });

        key.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Key pressed: " + blackKeyName);
            }
        });

        return key;
    }
}