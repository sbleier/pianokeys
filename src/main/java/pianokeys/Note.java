package pianokeys;

public record Note(int key, double startTime, double endTime)
{
    public String getNoteName()
    {
        String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        int octave = (key / 12) - 1;  // MIDI note number to octave
        int noteIndex = key % 12;
        return noteNames[noteIndex] + octave;
    }
}
