package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PianoGUI extends JFrame
{
    private final String[] KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B", "C"};
    private final Color KEY_COLOR = Color.WHITE;

    JButton[] buttons = new JButton[KEY_NAMES.length];

    public PianoGUI()
    {
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new GridLayout(1, 8, 2, 0));
        setLocationRelativeTo(null);

        for (String keyName : KEY_NAMES)
        {
            JButton button = createPianoKey(keyName);
            add(button);
        }
    }

    private JButton createPianoKey(String keyName)
    {
        JButton key = new JButton(keyName);

        // make it look like a piano key
        key.setBackground(KEY_COLOR);
        key.setForeground(Color.BLACK);
        key.setFont(new Font("Arial", Font.BOLD, 16));
        key.setFocusPainted(false);

        // hover to show the key
        key.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                key.setBackground(Color.LIGHT_GRAY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                key.setBackground(KEY_COLOR);
            }
        });

        key.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Key pressed: " + keyName);
                key.setBackground(Color.GRAY);
            }
        });
        return key;
    }
}




