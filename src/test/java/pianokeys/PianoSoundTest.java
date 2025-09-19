package pianokeys;

import javax.sound.midi.MidiUnavailableException;

import org.junit.jupiter.api.Test;

class PianoSoundTest
{

    @Test
    public void testPianoPlayback() throws MidiUnavailableException
    {
        PianoSound piano = new PianoSound();

        try
        {
            // Play each note for half a second
            for (int i = 0; i < 6; i += 2)
            {
                System.out.println("Playing note " + i);
                piano.playNote(i);
                Thread.sleep(1000); // Wait 1000ms
                piano.stopNote(i);
                Thread.sleep(500); // Small gap between notes
            }

            Thread.sleep(1000);

            piano.playNote(0);
            piano.playNote(2);
            piano.playNote(4);

            Thread.sleep(3000);

            piano.stopNote(0);
            piano.stopNote(2);
            piano.stopNote(4);

            for (int i = 1; i < 7; i += 2)
            {
                System.out.println("Playing note " + i);
                piano.playNote(i);
                Thread.sleep(1000); // Wait 1000ms

                piano.stopNote(i);
                Thread.sleep(500); // Small gap between notes
            }

            Thread.sleep(1000);

            piano.playNote(1);
            piano.playNote(3);
            piano.playNote(5);

            Thread.sleep(3000);

            piano.stopNote(1);
            piano.stopNote(3);
            piano.stopNote(5);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } finally
        {
            piano.cleanup();
        }
    }
}