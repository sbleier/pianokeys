package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import static java.awt.Color.*;
import static pianokeys.PianoSound.C4;

public class PianoView extends JLayeredPane
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

    private static final int OCTAVES = 7;

    private final JButton[] whiteButtons = new JButton[WHITE_KEY_NAMES.length * OCTAVES];
    private final JButton[] blackButtons = new JButton[5 * OCTAVES];

    private static final Color C_BASE_COLOR = new Color(255, 245, 200);
    private static final Color C_HOVER_COLOR = new Color(255, 235, 180);

    private final PianoSound sound;

    private final Composition composition = new Composition();
    private long recordStartTime = -1;
    private long currentPressTime = -1;

    private HashMap<Integer, JButton> blackHashMap = new HashMap<>();
    private HashMap<Integer, JButton> whiteHashMap = new HashMap<>();


    public PianoView(PianoSound sound)
    {
        this.sound = sound;
        JPanel whiteKeysPanel = createWhiteKeysPanel();
        JPanel blackKeysPanel = createBlackKeysPanel();

        int totalWidth = WHITE_KEY_NAMES.length * OCTAVES * WHITE_KEY_WIDTH;
        this.setPreferredSize(new Dimension(totalWidth, WHITE_KEY_HEIGHT));

        whiteKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);
        blackKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);

        // using different layers to add the panels
        this.add(whiteKeysPanel, Integer.valueOf(0));
        this.add(blackKeysPanel, Integer.valueOf(1));
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
                whiteHashMap.put(note, button);

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

                    blackHashMap.put(note, button);

                    blackKeyIndex++;
                }
            }
        }
        return blackKeysPanel;
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

        if (note == C4)
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


        if (sound != null)
        {
            sound.stopNote(note);
        }
    }

    public void showKeyPlayed(int note, boolean pressed)
    {
        JButton button;
        Color releasedColor;
        if (whiteHashMap.containsKey(note))
        {
            button = whiteHashMap.get(note);
            releasedColor = (note == C4) ? C_BASE_COLOR : WHITE;
        } else
        {
            button = blackHashMap.get(note);
            releasedColor = BLACK;
        }

        if (pressed)
        {
            button.setBackground(PINK);
        } else
        {
            button.setBackground(releasedColor);
        }
    }

}
