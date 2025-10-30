package pianokeys;

/**
 * @param key       from PianoSound
 * @param startTime in seconds
 * @param endTime   in seconds
 */
public record Note(int key, double startTime, double endTime) {

    /**
     * All Notes should start and end in increments of 1/8 of a second.
     */
    public static final double TIME_STEP = 1 / 8.0;

    /**
     * @param time in seconds
     * @return time rounded to the nearest 8th of a second.
     */
    public static double roundToNearestEight(double time) {
        return Math.round(time * 8.0) / 8.0;
    }

}
