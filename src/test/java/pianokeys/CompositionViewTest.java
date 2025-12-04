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

            Composition composition = new Composition();
            final CompositionView compView = new CompositionView(composition);

            // First few notes of Fur Elise
            composition.addNote(new Note(Note.E5, 0.0, 0.5));
            composition.addNote(new Note(Note.D_SHARP5, 0.5, 0.75));
            composition.addNote(new Note(Note.E5, 0.75, 1.0));
            composition.addNote(new Note(Note.D_SHARP5, 1.0, 1.25));
            composition.addNote(new Note(Note.E5, 1.25, 1.5));
            composition.addNote(new Note(Note.B4, 1.5, 2.0));
            composition.addNote(new Note(Note.D5, 2.0, 2.5));
            composition.addNote(new Note(Note.C5, 2.5, 3.0));
            composition.addNote(new Note(Note.B4, 3.0, 4.0));

            // Adding the components to the frame
            frame.add(compView);
            frame.setVisible(true);
        });
    }
}
