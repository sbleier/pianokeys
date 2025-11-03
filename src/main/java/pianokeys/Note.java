package pianokeys;

/**
 * @param key       from PianoSound
 * @param startTime in seconds
 * @param endTime   in seconds
 */

public record Note(int key, double startTime, double endTime)
{
    /**
     * All Notes should start and end in increments of 1/8 of a second.
     */
    public static final double TIME_STEP = 1 / 8.0;

    public static final String[] NOTE_NAMES = {
            "C", "C#", "D", "D#", "E", "F",
            "F#", "G", "G#", "A", "A#", "B"};

    // MIDI constants for notes used in Fur Elise
    public static final int B4 = 71;
    public static final int C5 = 72;
    public static final int D5 = 74;
    public static final int D_SHARP5 = 75;
    public static final int E5 = 76;

    /**
     * @param time in seconds
     * @return time rounded to the nearest 8th of a second.
     */
    public static double roundToNearestEight(double time)
    {
        return Math.round(time * 8.0) / 8.0;
    }

    public String getName()
    {
        int octave = (key / 12) - 1;  // MIDI note number to octave
        int noteIndex = key % 12;
        return NOTE_NAMES[noteIndex] + octave;
    }

}
