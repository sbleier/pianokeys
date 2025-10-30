package pianokeys;

import java.util.ArrayList;
import java.util.Arrays;

public class Composition
{
    private final ArrayList<Note> noteList = new ArrayList<>();

    public ArrayList<Note> getNoteList()
    {
        return noteList;
    }

    public static final ArrayList<Note> furElise = new ArrayList<>(Arrays.asList(
            new Note(76, 0.0, 0.25),
            new Note(75, 0.25, 0.5),
            new Note(76, 0.5, 0.75),
            new Note(75, 0.75, 1.0),
            new Note(76, 1.0, 1.25),
            new Note(71, 1.25, 1.5),
            new Note(74, 1.5, 1.75),
            new Note(72, 1.75, 2.0),
            new Note(69, 2.0, 2.25),
            new Note(60, 2.25, 2.5),
            new Note(64, 2.5, 2.75),
            new Note(69, 2.75, 3.0),
            new Note(71, 3.0, 3.25),
            new Note(64, 3.25, 3.5),
            new Note(68, 3.5, 3.75),
            new Note(71, 3.75, 4.0),
            new Note(72, 4.0, 4.25),
            new Note(76, 4.25, 4.5),
            new Note(75, 4.5, 4.75),
            new Note(76, 4.75, 5.0),
            new Note(75, 5.0, 5.25),
            new Note(76, 5.25, 5.5),
            new Note(71, 5.5, 5.75),
            new Note(74, 5.75, 6.0),
            new Note(72, 6.0, 6.25),
            new Note(69, 6.25, 6.5),
            new Note(60, 6.5, 6.75),
            new Note(64, 6.75, 7.0),
            new Note(69, 7.0, 7.25),
            new Note(71, 7.25, 7.5),
            new Note(64, 7.5, 7.75),
            new Note(72, 7.75, 8.0),
            new Note(71, 8.0, 8.25),
            new Note(69, 8.25, 8.5)
    ));

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


}


