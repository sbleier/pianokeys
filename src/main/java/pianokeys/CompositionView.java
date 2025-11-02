package pianokeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static java.awt.Color.BLACK;
import static java.awt.Color.CYAN;
import static java.awt.Color.ORANGE;
import static pianokeys.PianoSound.C4;

/**
 * This displays the notes visually as rectangles.
 * The position on the x axis is time.
 * The position on the y axis is where the note is on the scale.
 */
public class CompositionView extends JComponent
{
    private Composition composition;

    /**
     * Width of one second when displaying notes in the View.
     */
    private static final int SECOND_WIDTH = 20;

    /**
     * Height of each note. This might need to be a percentage of the height of the View.
     */
    private static final int NOTE_HEIGHT = 10;

    /**
     * Width of the current time vertical line
     */
    private static final int CURRENT_TIME_WIDTH = 2;

    // This will change over time.
    private double currentTime = 1.25;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // This is test data.
        displayNote(g, 5.25, 10.75, PianoSound.D4);
        displayNote(g, 0, 5, PianoSound.E4);
        displayNote(g, 1, 2, PianoSound.A4);
        displayNote(g, 0, 2, PianoSound.B4);
        displayNote(g, 4.5, 6.25, PianoSound.F4);

        displayCurrentTimeLine(g);
    }

    private void displayNote(Graphics g, double noteStartTimeSeconds, double noteEndTimeSeconds, int note) {
        int modifiedNote = note - C4;
        int x1 = (int) (noteStartTimeSeconds * SECOND_WIDTH);
        int x2 = (int) (noteEndTimeSeconds * SECOND_WIDTH);
        int y1 = modifiedNote * NOTE_HEIGHT;
        g.setColor(CYAN);
        g.fillRect(x1, y1, x2 - x1, NOTE_HEIGHT);
        g.setColor(BLACK);
        g.drawRect(x1, y1, x2 - x1, NOTE_HEIGHT);
    }

    private void displayCurrentTimeLine(Graphics g) {
        int currentTimeX = (int) (currentTime * SECOND_WIDTH);
        g.setColor(ORANGE);
        g.fillRect(currentTimeX, 0, CURRENT_TIME_WIDTH, getHeight());
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
        repaint();
    }

    public void addTime(double delta) {
        this.currentTime += delta;
        repaint();
    }

    public CompositionView()
    {
        // Use MouseListener for the clicking
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (SwingUtilities.isLeftMouseButton(e))
                {
                    if (e.getClickCount() == 1)
                    {
                        // Single click to set current time
                        setTimeAtPoint(e.getPoint());
                    } else if (e.getClickCount() == 2)
                    {
                        // Double click to delete note
                        deleteNoteAtPoint(e.getPoint());
                    }
                }
            }
        });
    }

    /**
     * Sets the notes displayed in this CompositionView
     */
    public void setComposition(Composition composition)
    {
        this.composition = composition;
        repaint();
    }

    private void deleteNoteAtPoint(Point p)
    {
        // Calculate the time and note from the mouse coordinates
        double clickedTime = (double) p.x / SECOND_WIDTH;
        int clickedNote = ((getHeight() - p.y) / NOTE_HEIGHT) + PianoSound.C4; // reverse calculation

        ArrayList<Note> notes = composition.getNoteList();

        // Find the note that was clicked
        for (Note note : new ArrayList<>(notes))
        {
            if (clickedNote == note.key()
                    && clickedTime >= note.startTime()
                    && clickedTime <= note.endTime())
            {
                notes.remove(note);
                repaint();
                break;
            }
        }
    }

    private void setTimeAtPoint(Point p)
    {
        double newTime = (double) p.x / SECOND_WIDTH;

        // Make sure that the time isn't negative
        if (newTime < 0)
        {
            newTime = 0;
        }

        setCurrentTime(newTime);
    }
}
