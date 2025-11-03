package pianokeys;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pianokeys.Note.TIME_STEP;
import static pianokeys.PianoSound.C4;
import static pianokeys.PianoSound.D4;
import static pianokeys.PianoSound.E4;

class SimpleCompositionFactoryTest
{

    @Test
    public void toComposition()
    {
        // given
        var factory = new SimpleCompositionFactory();

        // when
        var composition = factory.toComposition(
                new int[]{
                        C4,
                        C4,
                        E4,
                        D4
                },
                TIME_STEP
        );

        // then
        List<Note> notes = composition.getNoteList();
        assertEquals(new Note(C4, 0, TIME_STEP), notes.get(0));
        assertEquals(new Note(C4, TIME_STEP, .25), notes.get(1));
        assertEquals(new Note(E4, .25, .375), notes.get(2));
        assertEquals(new Note(D4, .375, .50), notes.get(3));
    }

}