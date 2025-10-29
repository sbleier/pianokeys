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

    public void play(PianoSound sound) {

        //loop through noteList to play all notes
        for (Note note: noteList) {
            //duration of note in milliseconds
            double duration = (note.endTime() - note.startTime()) * 1000;

            //note starts to play
            sound.playNote(note.key());

            //note plays for duration
            try {
                Thread.sleep((long) duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            //stop note after sleep of duration
            sound.stopNote(note.key());
        }
    }
}


