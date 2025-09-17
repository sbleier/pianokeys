package pianokeys;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                PianoGUI gui = new PianoGUI();
                gui.setVisible(true);
            }
        });
    }
}
