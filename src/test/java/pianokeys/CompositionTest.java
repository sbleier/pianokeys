package pianokeys;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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
        assertTrue(composition.getNoteList().contains(note));
    }

    @Test
    void addNotes()
    {
        // given
        Composition composition = new Composition();
        Note note1 = new Note(PianoSound.B4, 1, 1.25);
        Note note2 = new Note(PianoSound.C4, 3.45, 4);
        List<Note> notes = Arrays.asList(note1, note2);

        // when
        composition.addNotes(note1, note2);

        // then
        assertEquals(composition.getNoteList(), notes);
    }

    @Test
    void removeNote()
    {
        // given
        Composition composition = new Composition();
        Note note1 = new Note(PianoSound.B4, 1, 1.25);
        Note note2 = new Note(PianoSound.C4, 3.45, 4);
        composition.addNotes(note1, note2);

        // when
        boolean removed = composition.removeNote(1.1, PianoSound.B4);

        // then
        assertTrue(removed);
        assertEquals(1, composition.getNoteList().size());
        assertTrue(composition.getNoteList().contains(note2));
    }


}