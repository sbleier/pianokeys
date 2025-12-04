package pianokeys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteTest
{

    @Test
    public void roundToNearestEighth()
    {
        // then
        assertEquals(0, Note.roundToNearestEighth(0));
        assertEquals(.125, Note.roundToNearestEighth(.12));
        assertEquals(.125, Note.roundToNearestEighth(.13));
        assertEquals(.125, Note.roundToNearestEighth(.18));
        assertEquals(.25, Note.roundToNearestEighth(.20));
        assertEquals(1.0, Note.roundToNearestEighth(.99));
    }

    @Test
    void getName()
    {
        Note noteC5 = new Note(Note.C5, 0.0, 0.125);
        assertEquals("C5", noteC5.getName());

        Note noteE5 = new Note(Note.E5, 0.0, 0.125);
        assertEquals("E5", noteE5.getName());

        Note noteB4 = new Note(Note.B4, 0.0, 0.125);
        assertEquals("B4", noteB4.getName());

        Note noteDsharp5 = new Note(Note.D_SHARP5, 0.0, 0.125);
        assertEquals("D#5", noteDsharp5.getName());
    }

}