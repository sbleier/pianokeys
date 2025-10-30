package pianokeys;

import javax.swing.*;


public class CompositionViewTest
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            // Create the frame
            JFrame frame = new JFrame("CompositionView Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1600, 400); // Set size large enough for clear view

            CompositionView compView = new CompositionView();

            Composition composition = new Composition();

            // First few notes of Fur Elise
            composition.addNote(new Note(76, 0.0, 0.5));  // E5
            composition.addNote(new Note(75, 0.5, 0.75)); // D#5
            composition.addNote(new Note(76, 0.75, 1.0)); // E5
            composition.addNote(new Note(75, 1.0, 1.25)); // D#5
            composition.addNote(new Note(76, 1.25, 1.5)); // E5
            composition.addNote(new Note(71, 1.5, 2.0));  // B4
            composition.addNote(new Note(74, 2.0, 2.5));  // D5
            composition.addNote(new Note(73, 2.5, 3.0));  // C5
            composition.addNote(new Note(71, 3.0, 4.0));  // B4

            compView.setComposition(composition);

            // Adding the components to the frame
            frame.add(compView);
            frame.setVisible(true);
        });
    }
}
