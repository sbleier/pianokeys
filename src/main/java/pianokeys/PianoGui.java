package pianokeys;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Color.*;

public class PianoGui extends JFrame
{

    // declared everything
    private static final String[] WHITE_KEY_NAMES = {"C", "D", "E", "F", "G", "A", "B"};

    // White keys
    private static final int WHITE_KEY_WIDTH = 50;
    private static final int WHITE_KEY_HEIGHT = 250;
    private static final int OCTAVES = 7;

    //dropdown to change instrument
    private JComboBox<String> instrumentDropdown;
    //array of possible instruments

    // MIDI sound system
    private PianoSound sound;

    PianoView pianoView;

    public PianoGui()
    {
        setUpFrame();
        /*JPanel whiteKeysPanel = createWhiteKeysPanel();
        JPanel blackKeysPanel = createBlackKeysPanel();

        JLayeredPane layeredPane = createLayeredPane(whiteKeysPanel, blackKeysPanel);
        JScrollPane pianoScrollPane = createScrollPane(layeredPane); */

        pianoView = new PianoView();


        add(pianoView, BorderLayout.SOUTH);

        CompositionView compositionView = new CompositionView();
        compositionView.setPreferredSize(new Dimension(2000, 400));

        // vertical and  horizontal scroll panes for CompositionView
        JScrollPane compositionScrollPane = new JScrollPane(
                compositionView,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );
        compositionScrollPane.setPreferredSize(new Dimension(800, 200));

        add(compositionScrollPane);

        // buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton erase = new JButton(new ImageIcon(getClass().getResource("/images/erase.png")));
        JButton restart = new JButton(new ImageIcon(getClass().getResource("/images/restart.png")));
        JButton record = new JButton(new ImageIcon(getClass().getResource("/images/record.png")));
        JButton play = new JButton(new ImageIcon(getClass().getResource("/images/play.png")));
        JButton chooseInstrument = new JButton(new ImageIcon(getClass().getResource("/images/instrument.png")));

        buttonPanel.add(erase);
        buttonPanel.add(restart);
        buttonPanel.add(record);
        buttonPanel.add(play);
        buttonPanel.add(chooseInstrument);

        add(buttonPanel, BorderLayout.NORTH);

        centerOnMiddleC(pianoView.getScrollPane());
    }

    private void setUpFrame()
    {
        setTitle("Piano Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }


    private void centerOnMiddleC(JScrollPane scrollPane)
    {
        // Centering the scroll pane to open on middle C - in octave 4 (0-based, so 3 is the 4th octave)
        SwingUtilities.invokeLater(() ->
        {
            int middleCoctave = 3;
            int middleCindex = middleCoctave * WHITE_KEY_NAMES.length;

            int middleCx = middleCindex * WHITE_KEY_WIDTH - (scrollPane.getViewport().getWidth() / 2)
                    + (WHITE_KEY_WIDTH / 2);
            middleCx = Math.max(0, Math.min(middleCx, scrollPane.getHorizontalScrollBar().getMaximum()
                    - scrollPane.getViewport().getWidth()));

            scrollPane.getHorizontalScrollBar().setValue(middleCx);
        });
    }





}