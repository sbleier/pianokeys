package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;

import static java.awt.Color.*;

public class PianoGui extends JFrame
{
    // declared everything
    private static final String[] WHITE_KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B"};
    private static final String[] BLACK_KEY_NAMES = {"C#", "D#", "", "F#", "G#", "A#", ""};

    // White keys
    private static final int WHITE_KEY_WIDTH = 50;
    private static final int WHITE_KEY_HEIGHT = 250;
    // Black keys
    private static final int BLACK_KEY_WIDTH = 30;
    private static final int BLACK_KEY_HEIGHT = 170;

    private JButton[] whiteButtons = new JButton[WHITE_KEY_NAMES.length * 7];
    private JButton[] blackButtons = new JButton[35];

    // MIDI sound system
    private PianoSound sound;

    public PianoGui()
    {
        setUpFrame();
        initMidi();
        JPanel whiteKeysPanel = createWhiteKeysPanel();
        JPanel blackKeysPanel = createBlackKeysPanel();

        JLayeredPane layeredPane = createLayeredPane(whiteKeysPanel, blackKeysPanel);
        JScrollPane scrollPane = createScrollPane(layeredPane);
        add(scrollPane, BorderLayout.CENTER);

        centerOnMiddleC(scrollPane);
        midiCleanup();
    }

    private void setUpFrame()
    {
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private int getWhiteNoteForOctave(int octave, int whiteIndex) {
        return PianoSound.whiteNotes[whiteIndex] + (octave - 3) * 12;
    }

    private int getBlackNoteForOctave(int octave, int blackIndex) {
        int base = PianoSound.blackNotes[blackIndex];
        if (base == -1) return -1;
        return base + (octave - 3) * 12;
    }

    private void initMidi() {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel channel = synthesizer.getChannels()[0];
            sound = new PianoSound(synthesizer, channel);
        } catch (MidiUnavailableException e) {
            System.err.println("MIDI system unavailable: " + e.getMessage());
        }
    }

    private void midiCleanup() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (sound != null) sound.cleanup();
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
        for (int octave = 0; octave < 7; octave++)
        {
            for (int i = 0; i < WHITE_KEY_NAMES.length; i++)
            {
                //JButton button = createWhitePianoKey(WHITE_KEY_NAMES[i]);
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
        for (int octave = 0; octave < 7; octave++)
        {
            for (int i = 0; i < BLACK_KEY_NAMES.length; i++)
            {
                if (!BLACK_KEY_NAMES[i].isEmpty())
                {
                    int note = getBlackNoteForOctave(octave, i);
                    if (note == -1) continue; // skip keys that don't exist (like between E-F or B-C)
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
        int totalWidth = WHITE_KEY_NAMES.length * 7 * WHITE_KEY_WIDTH;
        layeredPane.setPreferredSize(new Dimension(totalWidth, WHITE_KEY_HEIGHT));

        whiteKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);
        blackKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);

        // using different layers to add the panels
        layeredPane.add(whiteKeysPanel, Integer.valueOf(0));
        layeredPane.add(blackKeysPanel, Integer.valueOf(1));

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
        SwingUtilities.invokeLater(() -> {
            int middleCoctave = 3;
            int middleCindex = middleCoctave * WHITE_KEY_NAMES.length;

            int middleCx = middleCindex * WHITE_KEY_WIDTH
                    - (scrollPane.getViewport().getWidth() / 2) + (WHITE_KEY_WIDTH / 2);
            middleCx = Math.max(0, Math.min(middleCx, scrollPane.getHorizontalScrollBar().getMaximum()
                    - scrollPane.getViewport().getWidth()));

            scrollPane.getHorizontalScrollBar().setValue(middleCx);
        });
    }

    private JButton createWhitePianoKey(String whiteKeyName, int note)
    {
        JButton key = new JButton(whiteKeyName);
        // make it look like a piano key
        key.setBackground(WHITE);
        key.setForeground(BLACK);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);
        key.setBorder(BorderFactory.createLineBorder(BLACK, 2));
        key.setOpaque(true);
        key.setContentAreaFilled(true);

        // hover to show the key
        key.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent evt)
            {
                key.setBackground(LIGHT_GRAY);
            }

            public void mouseExited(MouseEvent evt)
            {
                key.setBackground(WHITE);
            }

            public void mousePressed(MouseEvent evt)
            {
                key.setBackground(DARK_GRAY);
                if (sound != null) {
                    sound.playNote(note);
                }
            }

            public void mouseReleased(MouseEvent evt)
            {
                if (sound != null) {
                    sound.stopNote(note);
                }

                if (key.contains(evt.getPoint()))
                {
                    key.setBackground(LIGHT_GRAY);
                } else
                {
                    key.setBackground(WHITE);
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
            public void mouseEntered(MouseEvent evt)
            {
                key.setBackground(LIGHT_GRAY);
            }

            public void mouseExited(MouseEvent evt)
            {
                key.setBackground(BLACK);
            }

            public void mousePressed(MouseEvent evt)
            {
                key.setBackground(DARK_GRAY);
                if (sound != null) {
                    sound.playNote(note);
                }
            }

            public void mouseReleased(MouseEvent evt)
            {
                if (sound != null) {
                    sound.stopNote(note);
                }

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
