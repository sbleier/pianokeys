package pianokeys;

import java.util.ArrayList;
import java.util.Arrays;

public class Composition
{
    private ArrayList<Note> noteList = new ArrayList<>();

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
