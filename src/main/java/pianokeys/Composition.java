package pianokeys;

import java.util.ArrayList;
import java.util.Arrays;

import static pianokeys.Note.TIME_STEP;
import static pianokeys.PianoSound.C4;
import static pianokeys.PianoSound.D4;
import static pianokeys.PianoSound.E4;
import static pianokeys.PianoSound.F4;
import static pianokeys.PianoSound.G4;

public class Composition {

    private static final Composition ODE_TO_JOY = new SimpleCompositionFactory()
            .toComposition(new int[]{E4, E4, F4, G4, G4, F4, E4, D4,
                            C4, C4, D4, E4, E4, D4, D4,
                            E4, E4, F4, G4, G4, F4, E4, D4,
                            C4, C4, D4, E4, D4, C4, C4,
                            D4, D4, E4, C4, D4, E4, F4, E4, C4,
                            D4, E4, F4, E4, D4, C4, D4, G4,
                            E4, E4, F4, G4, G4, F4, E4, D4,
                            C4, C4, D4, E4, D4, C4, C4},
                    TIME_STEP);

    private final ArrayList<Note> noteList = new ArrayList<>();

    public Composition() {

    }

    public ArrayList<Note> getNoteList() {
        return noteList;
    }

    // single note
    public void addNote(Note note) {
        noteList.add(note);
    }

    // multiple notes
    public void addNotes(Note... notes) {
        noteList.addAll(Arrays.stream(notes).toList());
    }

}


