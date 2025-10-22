package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class PianoGui extends JFrame
{
    // declared everything
    private static final String[] WHITE_KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B", "C"};
    private static final String[] BLACK_KEY_NAMES = {"C#", "D#", "", "F#", "G#", "A#", ""};
    private static final Color WHITE_KEY_COLOR = Color.WHITE;
    private static final Color BLACK_KEY_COLOR = Color.BLACK;

    JButton[] whiteButtons = new JButton[WHITE_KEY_NAMES.length];
    JButton[] blackButtons = new JButton[BLACK_KEY_NAMES.length];

    JPanel whitePanel = new JPanel(new GridLayout(WHITE_KEY_NAMES.length, 1));

    public PianoGui()
    {
        // constructor
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create the panels for white and black keys
        JPanel whiteKeysPanel = new JPanel();
        whiteKeysPanel.setLayout(null);
        whiteKeysPanel.setOpaque(true);
        whiteKeysPanel.setBackground(Color.LIGHT_GRAY);

        JPanel blackKeysPanel = new JPanel();
        blackKeysPanel.setLayout(null);
        blackKeysPanel.setOpaque(false);

        // White keys
        int whiteKeyWidth = 100;
        int whiteKeyHeight = 200;

        // loop runs 8 times to lay out all the white keys and label them properly
        for (int i = 0; i < WHITE_KEY_NAMES.length; i++)
        {
            JButton button = createWhitePianoKey(WHITE_KEY_NAMES[i]);
            whiteButtons[i] = button;
            button.setBounds(i * whiteKeyWidth, 0, whiteKeyWidth, whiteKeyHeight);
            whiteKeysPanel.add(button);
        }

        // Black keys
        int blackKeyWidth = 60;
        int blackKeyHeight = 120;

        // same thing to loop through black keys to make them
        for (int i = 0; i < BLACK_KEY_NAMES.length; i++)
        {
            if (!BLACK_KEY_NAMES[i].isEmpty())
            {
                JButton button = createBlackPianoKey(BLACK_KEY_NAMES[i]);
                blackButtons[i] = button;

                // make sure that the black keys are between the white keys
                int blackKeyX = (i * whiteKeyWidth) + whiteKeyWidth - (blackKeyWidth / 2);
                button.setBounds(blackKeyX, 0, blackKeyWidth, blackKeyHeight);
                blackKeysPanel.add(button);
            }
        }

        // Layered pane to make the black keys on white keys
        JLayeredPane layeredPane = new JLayeredPane();
        int totalWidth = WHITE_KEY_NAMES.length * whiteKeyWidth;
        layeredPane.setPreferredSize(new Dimension(totalWidth, whiteKeyHeight));

        whiteKeysPanel.setBounds(0, 0, totalWidth, whiteKeyHeight);
        blackKeysPanel.setBounds(0, 0, totalWidth, whiteKeyHeight);

        // using different layers to add the panels
        layeredPane.add(whiteKeysPanel, Integer.valueOf(0));
        layeredPane.add(blackKeysPanel, Integer.valueOf(1));

        add(layeredPane, BorderLayout.CENTER);
    }

    private JButton createWhitePianoKey(String whiteKeyName)
    {
        JButton key = new JButton(whiteKeyName);

        // make it look like a piano key
        key.setBackground(WHITE_KEY_COLOR);
        key.setForeground(Color.BLACK);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);
        key.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        key.setOpaque(true);
        key.setContentAreaFilled(true);

        // hover to show the key
        key.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                key.setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                key.setBackground(WHITE_KEY_COLOR);
            }
        });

        key.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Key pressed: " + whiteKeyName);
                key.setBackground(Color.GRAY);
            }
        });
        return key;
    }

    private JButton createBlackPianoKey(String blackKeyName)
    {
        JButton key = new JButton(blackKeyName);

        key.setBackground(BLACK_KEY_COLOR);
        key.setForeground(Color.WHITE);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);
        key.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        key.setOpaque(true);
        key.setContentAreaFilled(true);

        key.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                key.setBackground(Color.DARK_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                key.setBackground(BLACK_KEY_COLOR);
            }
        });

        key.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Key pressed: " + blackKeyName);
                key.setBackground(Color.GRAY);
            }
        });
        return key;
    }

}




