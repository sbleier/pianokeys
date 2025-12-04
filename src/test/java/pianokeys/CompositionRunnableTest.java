package pianokeys;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CompositionRunnableTest
{

    @Test
    public void run()
    {
        //given
        PianoController controller = mock();
        Composition comp = new Composition();
        comp.addNote(new Note(PianoSound.C4, 0.0, 0.25));
        comp.addNote(new Note(PianoSound.D4, 0.25, 0.5));
        CompositionRunnable runnable = new CompositionRunnable(controller, comp, 0.0);

        // when
        runnable.run();

        //then
        verify(controller).playNote(PianoSound.C4);
        verify(controller, atLeastOnce()).stopNote(PianoSound.C4);
        verify(controller).playNote(PianoSound.D4);
        verify(controller, atLeastOnce()).stopNote(PianoSound.D4);
    }

}