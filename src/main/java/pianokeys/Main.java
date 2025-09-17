package pianokeys;

public class Main
{
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                PianoGui gui = new PianoGui();
                gui.setVisible(true);
            }
        });
    }
}
