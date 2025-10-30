package pianokeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Composition
{
    private final ArrayList<Note> noteList = new ArrayList<>();

    public ArrayList<Note> getNoteList()
    {
        return noteList;
    }

    public static final List<Note> furElise = Arrays.asList(
            new Note(PianoSound.E5, 0.0, 0.25),
            new Note(PianoSound.D5_SHARP, 0.25, 0.5),
            new Note(PianoSound.E5, 0.5, 0.75),
            new Note(PianoSound.D5_SHARP, 0.75, 1.0),
            new Note(PianoSound.E5, 1.0, 1.25),
            new Note(PianoSound.B4, 1.25, 1.5),
            new Note(PianoSound.D5, 1.5, 1.75),
            new Note(PianoSound.C5, 1.75, 2.0),
            new Note(PianoSound.A4, 2.0, 2.25),
            new Note(PianoSound.C4, 2.25, 2.5),
            new Note(PianoSound.E4, 2.5, 2.75),
            new Note(PianoSound.A4, 2.75, 3.0),
            new Note(PianoSound.B4, 3.0, 3.25),
            new Note(PianoSound.E4, 3.25, 3.5),
            new Note(PianoSound.G4_SHARP, 3.5, 3.75),
            new Note(PianoSound.B4, 3.75, 4.0),
            new Note(PianoSound.C5, 4.0, 4.25),
            new Note(PianoSound.E5, 4.25, 4.5),
            new Note(PianoSound.D5_SHARP, 4.5, 4.75),
            new Note(PianoSound.E5, 4.75, 5.0),
            new Note(PianoSound.D5_SHARP, 5.0, 5.25),
            new Note(PianoSound.E5, 5.25, 5.5),
            new Note(PianoSound.B4, 5.5, 5.75),
            new Note(PianoSound.D5, 5.75, 6.0),
            new Note(PianoSound.C5, 6.0, 6.25),
            new Note(PianoSound.A4, 6.25, 6.5),
            new Note(PianoSound.C4, 6.5, 6.75),
            new Note(PianoSound.E4, 6.75, 7.0),
            new Note(PianoSound.A4, 7.0, 7.25),
            new Note(PianoSound.B4, 7.25, 7.5),
            new Note(PianoSound.E4, 7.5, 7.75),
            new Note(PianoSound.C5, 7.75, 8.0),
            new Note(PianoSound.B4, 8.0, 8.25),
            new Note(PianoSound.A4, 8.25, 8.5)
    );

    // single note
    public void addNote(Note note)
    {
        noteList.add(note);
    }

    // multiple notes
    public void addNotes(Note... notes)
    {
        noteList.addAll(Arrays.stream(notes).toList());
    }

    // returns duration
    public double duration(Note note) {
        return (note.endTime() - note.startTime());
    }


}


