package pianokeys;

import javax.swing.JComponent;
import java.awt.Graphics;

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

    // Use the composition from the composition class
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // if the composition (array) is full / has notes in it,
        // for each note
        if (composition != null)
        {

        }

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

    /**
     * Sets the notes displayed in this CompositionView
     */
    public void setComposition(Composition composition)
    {
        this.composition = composition;
        repaint();
    }

}
