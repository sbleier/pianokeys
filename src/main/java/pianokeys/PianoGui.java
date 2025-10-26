package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public PianoGui()
    {
        setUpFrame();
        JPanel whiteKeysPanel = createWhiteKeysPanel();
        JPanel blackKeysPanel = createBlackKeysPanel();

        JLayeredPane layeredPane = createLayeredPane(whiteKeysPanel, blackKeysPanel);
        JScrollPane scrollPane = createScrollPane(layeredPane);
        add(scrollPane, BorderLayout.CENTER);

        centerOnMiddleC(scrollPane);
    }

    private void setUpFrame()
    {
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
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
                JButton button = createWhitePianoKey(WHITE_KEY_NAMES[i]);

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
                    JButton button = createBlackPianoKey(BLACK_KEY_NAMES[i]);
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

    private JButton createWhitePianoKey(String whiteKeyName)
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
            }

            public void mouseReleased(MouseEvent evt)
            {
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
                key.setBackground(Color.GRAY);
            }
        });
        return key;
    }

    private JButton createBlackPianoKey(String blackKeyName)
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
            }

            public void mouseReleased(MouseEvent evt)
            {
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
                key.setBackground(Color.GRAY);
            }
        });

        return key;
    }

}




