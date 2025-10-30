package pianokeys;

import javax.swing.JComponent;
import java.awt.*;
import java.util.ArrayList;

import static java.awt.Color.*;
import static pianokeys.PianoSound.C4;

/**
 * This displays the notes visually as rectangles.
 * The position on the x axis is time.
 * The position on the y axis is where the note is on the scale.
 */
public class CompositionView extends JComponent
{

    /**
     * Width of one second when displaying notes in the View.
     * Increased so that you can see the 8th subdivisions clearly
     */
    private static final int SECOND_WIDTH = 80;

    /**
     * Height of each note. This might need to be a percentage of the height of the View.
     */
    private static final int NOTE_HEIGHT = 10;

    /**
     * Width of the current time vertical line
     */
    private static final int CURRENT_TIME_WIDTH = 2;

    /**
     * Number of subdivisions per second (8 creates 8th note grid lines).
     */
    private static final int SUBDIVISIONS = 8;

    /**
     * Number of notes to display vertically
     * 2 octaves = 24 notes
     */
    private static final int NOTE_RANGE = 24;

    // This will change over time.
    private double currentTime = 1.25;

    private Composition composition = new Composition();

    // Use the composition from the composition class
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        drawTimeGrid(g);

        /*
         * if the composition (array) is full / has notes in it,
         * for each note, it loops through every note in the composition
         * for each note, you call the existing display note method: which has startTime, endTime, key
         */
        ArrayList<Note> notes = composition.getNoteList();
        for (Note note : notes)
        {
            displayNote(g, note.startTime(), note.endTime(), note.key());
        }

        displayCurrentTimeLine(g);
    }

    // Making the grid so that you can see the time and the note at which point it is clicked
    private void drawTimeGrid(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        int maxTime = 20;   // shows 20 seconds

        int viewWidth = maxTime * SECOND_WIDTH;
        int viewHeight = getHeight();

        for (int second = 0; second <= maxTime; second++)
        {
            int x = second * SECOND_WIDTH;

            // Draw the main lines
            g2d.setColor(GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, 0, x, viewHeight);

            // Draw the second label
            g2d.setColor(BLACK);
            g2d.drawString(second + "s", x + 2, 12);

            // Draw the subdivisions lines (8ths)
            g2d.setStroke(new BasicStroke(1));

            // loops to draw 7 subdivision lines between each second
            for (int sub = 1; sub < SUBDIVISIONS; sub++)
            {
                int subX = x + (sub * SECOND_WIDTH / SUBDIVISIONS);
                g2d.setColor(LIGHT_GRAY);
                g2d.drawLine(subX, 0, subX, viewHeight);
            }

            // Notes will automatically position themselves in the order so don't need actual lines
        }

    }

    private void displayNote(Graphics g, double noteStartTimeSeconds, double noteEndTimeSeconds, int note)
    {
        int modifiedNote = note - C4;
        int x1 = (int) (noteStartTimeSeconds * SECOND_WIDTH);
        int x2 = (int) (noteEndTimeSeconds * SECOND_WIDTH);

        // flips the Y axis so that the higher notes at the top and lower notes on the bottom
        int y1 = getHeight() - (modifiedNote + 1) * NOTE_HEIGHT;

        g.setColor(CYAN);
        g.fillRect(x1, y1, x2 - x1, NOTE_HEIGHT);
        g.setColor(BLACK);
        g.drawRect(x1, y1, x2 - x1, NOTE_HEIGHT);

        // Add the note name to the block as it is played
        g.setColor(BLACK);
        String noteLabel = getNoteName(note);
        g.drawString(noteLabel, x1 + 5, y1 + NOTE_HEIGHT / 2 + 5);

    }

    private String getNoteName(int note)
    {
        String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        int octave = (note / 12) - 1;  // MIDI note number to octave
        int noteIndex = note % 12;
        return noteNames[noteIndex] + octave;
    }

    private void displayCurrentTimeLine(Graphics g)
    {
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
