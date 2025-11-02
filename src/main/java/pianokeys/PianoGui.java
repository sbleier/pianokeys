package pianokeys;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import static java.awt.Color.*;

public class PianoGui extends JFrame {

    private final Composition composition = new Composition();
    private long recordStartTime = -1;

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

    private JButton[] whiteButtons = new JButton[WHITE_KEY_NAMES.length * OCTAVES];
    private JButton[] blackButtons = new JButton[5 * OCTAVES];

    private JComboBox<String> instrumentDropdown;
    private PianoSound sound;

    public PianoGui() {
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

    // SETUP AND MIDI

    private void setUpFrame() {
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
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
                if (sound != null) {
                    sound.cleanup();
                }
            }
        });
    }

    //  KEYS

    private JPanel createWhiteKeysPanel() {
        JPanel whiteKeysPanel = new JPanel(null);
        whiteKeysPanel.setOpaque(true);
        whiteKeysPanel.setBackground(LIGHT_GRAY);

        for (int octave = 0; octave < OCTAVES; octave++) {
            for (int i = 0; i < WHITE_KEY_NAMES.length; i++) {
                int note = getWhiteNoteForOctave(octave, i);
                JButton key = makeKey(WHITE_KEY_NAMES[i], WHITE, BLACK, BorderFactory.createLineBorder(BLACK,
                        2));
                keyMouseBehavior(key, note, WHITE, LIGHT_GRAY, DARK_GRAY);
                keyPressedLog(key, WHITE_KEY_NAMES[i]);

                int keyIndex = octave * WHITE_KEY_NAMES.length + i;
                whiteButtons[keyIndex] = key;

                key.setBounds(keyIndex * WHITE_KEY_WIDTH, 0, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
                whiteKeysPanel.add(key);
            }
        }
        return whiteKeysPanel;
    }

    private JPanel createBlackKeysPanel() {
        JPanel blackKeysPanel = new JPanel();
        blackKeysPanel.setLayout(null);
        blackKeysPanel.setOpaque(false);

        int blackKeyIndex = 0;
        for (int octave = 0; octave < OCTAVES; octave++) {
            for (int i = 0; i < BLACK_KEY_NAMES.length; i++) {
                if (!BLACK_KEY_NAMES[i].isEmpty()) {
                    int note = getBlackNoteForOctave(octave, i);
                    if (note == -1) { continue; }

                    JButton key = makeKey(BLACK_KEY_NAMES[i], BLACK, WHITE, BorderFactory.createLineBorder(Color.GRAY,
                            1));
                    keyMouseBehavior(key, note, BLACK, LIGHT_GRAY, DARK_GRAY);
                    keyPressedLog(key, BLACK_KEY_NAMES[i]);

                    blackButtons[blackKeyIndex] = key;

                    int whiteKeyPosition = octave * WHITE_KEY_NAMES.length + i;
                    int blackKeyX = (whiteKeyPosition * WHITE_KEY_WIDTH) + WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2);
                    key.setBounds(blackKeyX, 0, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
                    blackKeysPanel.add(key);

                    blackKeyIndex++;
                }
            }
        }
        return blackKeysPanel;
    }

    private void keyMouseBehavior(
            JButton key, int note, Color baseColor, Color hoverColor, Color pressedColor)
    {
        key.addMouseListener(new MouseAdapter() {
            private long pressTime;

            @Override
            public void mouseEntered(MouseEvent e) {
                key.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                key.setBackground(baseColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                key.setBackground(pressedColor);
                long now = System.currentTimeMillis();
                if (recordStartTime == -1) { recordStartTime = now; }
                pressTime = now - recordStartTime;
                if (sound != null) { sound.playNote(note); }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                long releaseTime = System.currentTimeMillis() - recordStartTime;
                double startSec = pressTime / 1000.0;
                double endSec = releaseTime / 1000.0;

                composition.addNote(new Note(note, startSec, endSec));
                System.out.println("Recorded note: " + note + " from " + startSec + "s to " + endSec + "s");
                System.out.println("Total notes recorded: " + composition.getNoteList().size());

                if (sound != null) { sound.stopNote(note); }

                if (key.contains(e.getPoint())) {
                    key.setBackground(hoverColor);
                } else {
                    key.setBackground(baseColor);
                }
            }
        });
    }

    private JButton makeKey(String label, Color bg, Color fg, Border border) {
        JButton key = new JButton(label);
        key.setBackground(bg);
        key.setForeground(fg);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);
        key.setOpaque(true);
        key.setContentAreaFilled(true);
        key.setBorder(border);
        return key;
    }

    private void keyPressedLog(JButton key, String name) {
        key.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Key pressed: " + name);
            }
        });
    }

    private int getWhiteNoteForOctave(int octave, int whiteIndex) {
        return PianoSound.whiteNotes[whiteIndex] + (octave - 3) * 12;
    }

    private int getBlackNoteForOctave(int octave, int blackIndex) {
        int base = PianoSound.blackNotes[blackIndex];
        if (base == -1)  { return -1; }
        return base + (octave - 3) * 12;
    }

    private JLayeredPane createLayeredPane(JPanel whiteKeysPanel, JPanel blackKeysPanel) {
        JLayeredPane layeredPane = new JLayeredPane();
        int totalWidth = WHITE_KEY_NAMES.length * OCTAVES * WHITE_KEY_WIDTH;
        layeredPane.setPreferredSize(new Dimension(totalWidth, WHITE_KEY_HEIGHT));

        whiteKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);
        blackKeysPanel.setBounds(0, 0, totalWidth, WHITE_KEY_HEIGHT);

        layeredPane.add(whiteKeysPanel, Integer.valueOf(0));
        layeredPane.add(blackKeysPanel, Integer.valueOf(1));

        add(layeredPane, BorderLayout.CENTER);

        instrumentDropdown = new JComboBox<>(PianoSound.instruments);
        instrumentDropdown.addActionListener(e -> {
            String instrument = (String) instrumentDropdown.getSelectedItem();
            sound.setInstrument(instrument);
        });
        add(instrumentDropdown, BorderLayout.SOUTH);
        return layeredPane;
    }

    private JScrollPane createScrollPane(JLayeredPane layeredPane) {
        return new JScrollPane(
                layeredPane,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
    }

    private void centerOnMiddleC(JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            int middleCoctave = 3;
            int middleCindex = middleCoctave * WHITE_KEY_NAMES.length;
            int middleCx = middleCindex * WHITE_KEY_WIDTH - (scrollPane.getViewport().getWidth() / 2)
                    + (WHITE_KEY_WIDTH / 2);
            middleCx = Math.max(0, Math.min(middleCx,
                    scrollPane.getHorizontalScrollBar().getMaximum() - scrollPane.getViewport().getWidth()));
            scrollPane.getHorizontalScrollBar().setValue(middleCx);
        });
    }
}


