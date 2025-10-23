package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;

import static java.awt.Color.*;

public class PianoGui extends JFrame
{
    // declared everything
    private static final String[] WHITE_KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B", "C"};
    private static final String[] BLACK_KEY_NAMES = {"C#", "D#", "", "F#", "G#", "A#", ""};

    // White keys
    private static final int WHITE_KEY_WIDTH = 100;
    private static final int WHITE_KEY_HEIGHT = 200;
    // Black keys
    private static final int BLACK_KEY_WIDTH = 60;
    private static final int BLACK_KEY_HEIGHT = 120;

    private JButton[] whiteButtons = new JButton[WHITE_KEY_NAMES.length];
    private JButton[] blackButtons = new JButton[BLACK_KEY_NAMES.length];

    // MIDI sound system
    private PianoSound sound;
    //private int[] blackKeyNotes = {61, 63, -1, 66, 68, 70, -1}; // C#, D#, skip, F#, G#, A#, skip

    public PianoGui()
    {
        // constructor
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Initialize MIDI
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel channel = synthesizer.getChannels()[0];
            sound = new PianoSound(synthesizer, channel);
        } catch (MidiUnavailableException e) {
            System.err.println("MIDI system unavailable: " + e.getMessage());
        }

        // Create the panels for white and black keys
        JPanel whiteKeysPanel = new JPanel();
        whiteKeysPanel.setLayout(null);
        whiteKeysPanel.setOpaque(true);
        whiteKeysPanel.setBackground(LIGHT_GRAY);

        JPanel blackKeysPanel = new JPanel();
        blackKeysPanel.setLayout(null);
        blackKeysPanel.setOpaque(false);

        // loop runs 8 times to lay out all the white keys and label them properly
        for (int i = 0; i < WHITE_KEY_NAMES.length; i++)
        {
            JButton button = createWhitePianoKey(WHITE_KEY_NAMES[i], PianoSound.notes[i]);
            whiteButtons[i] = button;
            button.setBounds(i * WHITE_KEY_WIDTH, 0, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
            whiteKeysPanel.add(button);
        }

        // same thing to loop through black keys to make them
        for (int i = 0; i < BLACK_KEY_NAMES.length; i++)
        {
            if (!BLACK_KEY_NAMES[i].isEmpty())
            {
                // each button directly corresponds to the correct note
                JButton button = createBlackPianoKey(BLACK_KEY_NAMES[i], PianoSound.blackNotes[i]);
                blackButtons[i] = button;

                // make sure that the black keys are between the white keys
                int blackKeyX = (i * WHITE_KEY_WIDTH) + WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2);
                button.setBounds(blackKeyX, 0, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
                blackKeysPanel.add(button);
            }
        }

        // Layered pane to make the black keys on white keys
        JLayeredPane layeredPane = new JLayeredPane();
        int totalWidth = WHITE_KEY_NAMES.length * WHITE_KEY_WIDTH;
        layeredPane.setPreferredSize(new Dimension(totalWidth, WHITE_KEY_HEIGHT));

        whiteKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);
        blackKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);

        // using different layers to add the panels
        layeredPane.add(whiteKeysPanel, Integer.valueOf(0));
        layeredPane.add(blackKeysPanel, Integer.valueOf(1));

        add(layeredPane, BorderLayout.CENTER);

        // Cleanup MIDI when window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (sound != null) {
                    sound.cleanup();
                }
            }
        });
    }

    private JButton createWhitePianoKey(String whiteKeyName, int note)
    {
        JButton key = new JButton(whiteKeyName);
        final int noteToPlay = note;

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
                    sound.playNote(noteToPlay);
                }
            }

            public void mouseReleased(MouseEvent evt)
            {
                if (sound != null) {
                    sound.stopNote(noteToPlay);
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
        final int noteToPlay = note;

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
                    sound.playNote(noteToPlay);
                }
            }

            public void mouseReleased(MouseEvent evt)
            {
                if (sound != null) {
                    sound.stopNote(noteToPlay);
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



