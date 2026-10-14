package pianokeys;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MidiParserTestRun {

    @Test
    public void testOdeToJoy() throws Exception {

        MidiParser parser = new MidiParser();

        ArrayList<Note> notes = parser.parse(
                new File("midi/beethoven-symphony9-4-ode-to-joy-piano-solo.mid")
        );

        // Check that all 264 notes were parsed
        assertEquals(264, notes.size());

        // Check the first note
        Note firstNote = notes.get(0);

        assertEquals(62, firstNote.key());
        assertEquals(0.0, firstNote.startTime(), 0.001);
        assertEquals(0.483869,
                firstNote.endTime() - firstNote.startTime(), 0.001);
        assertEquals(0, firstNote.instrument());
    }
}