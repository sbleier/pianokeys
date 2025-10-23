package pianokeys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositionTest
{
    Composition composition;

    @Test
    void addNote()
    {
        composition = new Composition();
        // given
        Note note = new Note(60, 1, 1.25);

        // when
        composition.addNote(note);

        // then
        assertEquals(1, composition.getNoteList().size());
        assertTrue(composition.getNoteList().contains(note));
    }

    @Test
    void addNotes()
    {
        composition = new Composition();
        // given
        Note note1 = new Note(60, 1, 1.25);
        Note note2 = new Note(37, 3.45, 4);

        // when
        composition.addNotes(note1, note2);

        // then
        assertEquals(2, composition.getNoteList().size());
        assertTrue(composition.getNoteList().contains(note1));
        assertTrue(composition.getNoteList().contains(note2));
    }
}