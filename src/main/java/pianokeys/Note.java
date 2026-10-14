
package pianokeys;

/**
 * @param key       MIDI note pitch
 * @param startTime in seconds
 * @param endTime   in seconds
 * @param instrument MIDI instrument number
 */
public record Note(int key, double startTime, double endTime, int instrument)
{
    // Keeps older code using 3 values working
    public Note(int key, double startTime, double endTime) {
        this(key, startTime, endTime, 0);
    }

    public static final double TIME_STEP = 1 / 8.0;

    public static final String[] NOTE_NAMES = {
            "C", "C#", "D", "D#", "E", "F",
            "F#", "G", "G#", "A", "A#", "B"};

    public static final int B4 = 71;
    public static final int C5 = 72;
    public static final int D5 = 74;
    public static final int D_SHARP5 = 75;
    public static final int E5 = 76;

    public static double roundToNearestEighth(double time)
    {
        return Math.round(time * 8.0) / 8.0;
    }

    public String getName()
    {
        int octave = (key / 12) - 1;
        int noteIndex = key % 12;
        return NOTE_NAMES[noteIndex] + octave;
    }
}
