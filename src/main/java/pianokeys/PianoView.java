package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.border.Border;

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
    private static final Font KEY_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Border WHITE_KEY_BORDER = BorderFactory.createLineBorder(BLACK, 2);
    private static final Border MIDDLE_C_BORDER = BorderFactory.createLineBorder(RED, 3);
    private static final Border BLACK_KEY_BORDER = BorderFactory.createLineBorder(GRAY, 1);

    private final Supplier<PianoController> controller;

    private final HashMap<Integer, JButton> buttonsByNote = new HashMap<>();

    // which notes currently show a keyboard-shortcut badge, so they can be cleared when the octave shifts
    private final Map<Integer, JLabel> keyboardLabelsByNote = new HashMap<>();

    private static final int KEYBOARD_LABEL_WIDTH = 16;
    private static final int KEYBOARD_LABEL_HEIGHT = 12;
    private static final int KEYBOARD_LABEL_BOTTOM_MARGIN = 2;
    // on white keys, sit just below where the black keys end, rather than at the very
    // bottom of the (much taller) white key, so the badge stays visible on shorter screens
    private static final int WHITE_KEY_LABEL_TOP_MARGIN = 4;

    public PianoView(Supplier<PianoController> controller)
    {
        this.controller = controller;
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
                buttonsByNote.put(note, button);

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

                    buttonsByNote.put(note, button);

                    blackKeyIndex++;
                }
            }
        }
        return blackKeysPanel;
    }

    /**
     * @return the pixel X position where the given octave's first (leftmost) white key starts
     */
    public int getOctaveStartX(int octaveIndex)
    {
        return octaveIndex * WHITE_KEY_NAMES.length * WHITE_KEY_WIDTH;
    }

    public int getWhiteKeyWidth()
    {
        return WHITE_KEY_WIDTH;
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
        key.setFont(KEY_FONT);
        key.setFocusPainted(false);
        key.setBorder(WHITE_KEY_BORDER);
        key.setOpaque(true);
        key.setContentAreaFilled(true);

        final Color baseColor;
        final Color hoverColor;

        if (note == C4)
        {
            baseColor = C_BASE_COLOR;
            hoverColor = C_HOVER_COLOR;
            key.setBorder(MIDDLE_C_BORDER);
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
                    controller.get().playNote(note);
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
                    controller.get().stopNote(note);
                }
            }

            @Override
            public void mousePressed(MouseEvent evt)
            {
                key.setBackground(DARK_GRAY);
                controller.get().playNote(note);
            }

            @Override
            public void mouseReleased(MouseEvent evt)
            {
                controller.get().stopNote(note);
                if (key.contains(evt.getPoint()))
                {
                    key.setBackground(hoverColor);
                } else
                {
                    key.setBackground(baseColor);
                }
            }

        });

        return key;
    }

    private JButton createBlackPianoKey(String blackKeyName, int note)
    {
        JButton key = new JButton(blackKeyName);
        key.setBackground(BLACK);
        key.setForeground(WHITE);
        key.setFont(KEY_FONT);
        key.setFocusPainted(false);
        key.setBorder(BLACK_KEY_BORDER);
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
                    controller.get().playNote(note);
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
                    controller.get().stopNote(note);
                }
            }

            @Override
            public void mousePressed(MouseEvent evt)
            {
                key.setBackground(DARK_GRAY);
                controller.get().playNote(note);
            }

            @Override
            public void mouseReleased(MouseEvent evt)
            {
                controller.get().stopNote(note);
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

        return key;
    }

    public void showKeyPlayed(int note, boolean pressed)
    {
        JButton button = buttonsByNote.get(note);
        Color releasedColor;
        if (isBlackNote(note))
        {
            releasedColor = BLACK;
        } else
        {
            releasedColor = (note == C4) ? C_BASE_COLOR : WHITE;
        }

        if (pressed)
        {
            button.setBackground(PINK);
        } else
        {
            button.setBackground(releasedColor);
        }
    }

    /**
     * MIDI notes repeat every 12 semitones. Remainder 1, 3, 6, 8, and 10 are the black keys
     * (C#, D#, F#, G#, A#); the rest are white keys.
     */
    private boolean isBlackNote(int note)
    {
        int pitchClass = Math.floorMod(note, 12);
        return pitchClass == 1 || pitchClass == 3 || pitchClass == 6 || pitchClass == 8 || pitchClass == 10;
    }


    /**
     * Shows a small keyboard-shortcut badge at the bottom of each note's key, replacing
     * whichever badges were shown before. Called once at startup and again every time the
     * keyboard octave shifts, so the badges always show what each key currently plays.
     *
     * Bottom placement (rather than a top corner) keeps the badge clear of the area where
     * black keys visually sit on top of white keys, since black keys don't extend all the
     * way down to the bottom of the white keys beneath them.
     */
    public void setKeyboardLabels(Map<Integer, String> noteToKeyLabel)
    {
        for (Map.Entry<Integer, JLabel> entry : keyboardLabelsByNote.entrySet())
        {
            JButton button = buttonsByNote.get(entry.getKey());
            if (button != null)
            {
                button.remove(entry.getValue());
                button.revalidate();
                button.repaint();
            }
        }
        keyboardLabelsByNote.clear();

        for (Map.Entry<Integer, String> entry : noteToKeyLabel.entrySet())
        {
            int note = entry.getKey();
            JButton button = buttonsByNote.get(note);
            if (button == null)
            {
                continue;
            }

            boolean isBlack = isBlackNote(note);
            int keyWidth = isBlack ? BLACK_KEY_WIDTH : WHITE_KEY_WIDTH;
            int labelY = isBlack
                    ? BLACK_KEY_HEIGHT - KEYBOARD_LABEL_HEIGHT - KEYBOARD_LABEL_BOTTOM_MARGIN
                    : BLACK_KEY_HEIGHT + WHITE_KEY_LABEL_TOP_MARGIN;

            JLabel label = createKeyboardLabel(entry.getValue());
            button.setLayout(null);
            button.add(label);
            label.setBounds(
                    (keyWidth - KEYBOARD_LABEL_WIDTH) / 2,
                    labelY,
                    KEYBOARD_LABEL_WIDTH,
                    KEYBOARD_LABEL_HEIGHT);

            keyboardLabelsByNote.put(note, label);
        }
    }

    private JLabel createKeyboardLabel(String text)
    {
        // overridden contains() lets clicks pass through to the button underneath,
        // so the badge is purely visual and doesn't interfere with playing the key
        JLabel label = new JLabel(text)
        {
            @Override
            public boolean contains(int x, int y)
            {
                return false;
            }
        };
        label.setFont(new Font("Arial", Font.BOLD, 9));
        label.setForeground(BLACK);
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 255, 220));
        label.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

}
