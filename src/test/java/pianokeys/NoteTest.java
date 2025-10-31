package pianokeys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pianokeys.Note.roundToNearestEight;

class NoteTest {

    @Test
    public void roundToNearestEighth() {
        // then
        assertEquals(0, roundToNearestEight(0));
        assertEquals(.125, roundToNearestEight(.12));
        assertEquals(.125, roundToNearestEight(.13));
        assertEquals(.125, roundToNearestEight(.18));
        assertEquals(.25, roundToNearestEight(.20));
        assertEquals(1.0, roundToNearestEight(.99));
    }

}