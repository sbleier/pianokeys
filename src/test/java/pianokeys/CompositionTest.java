package pianokeys;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositionTest
{
    @Test
    void addNote()
    {
        // given
        Composition composition = new Composition();
        Note note = new Note(PianoSound.A4, 1, 1.25);

        // when
        composition.addNote(note);

        // then
        assertEquals(1, composition.getNoteList().size());
        assertTrue(composition.getNoteList().contains(note));
    }

    @Test
    void addNotes()
    {
        // given
        Composition composition = new Composition();
        Note note1 = new Note(PianoSound.B4, 1, 1.25);
        Note note2 = new Note(PianoSound.C4, 3.45, 4);
        ArrayList<Note> notes = new ArrayList<>(Arrays.asList(note1, note2));

        // when
        composition.addNotes(note1, note2);

        // then
        assertEquals(2, composition.getNoteList().size());
        assertEquals(composition.getNoteList(), notes);
    }
}