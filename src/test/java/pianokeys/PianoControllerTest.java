package pianokeys;

import org.junit.jupiter.api.Test;
import pianokeys.net.PianoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pianokeys.PianoSound.C4;

class PianoControllerTest
{
    CompositionView compositionView = mock();
    PianoSound sound = mock();
    PianoView pianoView = mock();
    Composition composition = new Composition();
    PianoService pianoService = mock(PianoService.class);

    @Test
    void playNote()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        controller.playNote(C4);

        // then
        verify(sound).playNote(C4);
        verify(pianoView).showKeyPlayed(C4, true);
    }

    @Test
    void stopNote()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        controller.stopNote(C4);

        // then
        verify(sound).stopNote(C4);
        verify(pianoView).showKeyPlayed(C4, false);
        verify(compositionView).refreshLayout();
    }

    @Test
    void playComposition()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        boolean result = controller.playComposition();

        // then
        assertTrue(result);
    }

    @Test
    void playCompositionWhenAlreadyPlaying()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);
        controller.playComposition();

        // when
        boolean result = controller.playComposition();

        // then
        assertFalse(result);
    }

    @Test
    void stopComposition()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);
        controller.playComposition();

        // when
        controller.stopComposition();

        // then
        assertTrue(controller.playComposition());
    }

    @Test
    void eraseComposition()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);
        composition.addNote(new Note(C4, 0, 0.125));

        // when
        controller.eraseComposition();

        // then
        assertEquals(0, composition.getNoteList().size());
        verify(compositionView).repaint();
    }

    @Test
    void restartComposition()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        controller.restartComposition();

        // then
        verify(compositionView).setCurrentTime(0);
    }

    @Test
    void changeInstrument()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        controller.changeInstrument("Piano");

        // then
        verify(sound).setInstrument("Piano");
    }

    @Test
    void changeInstrumentWithNull()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        controller.changeInstrument(null);

        // then
        verify(sound, never()).setInstrument(any());
    }

    @Test
    void setRecording()
    {
        // given
        PianoController controller = new PianoController(compositionView, sound, composition, pianoView, pianoService);

        // when
        controller.setRecording(false);
        controller.playNote(C4);
        controller.stopNote(C4);

        // then
        assertEquals(0, composition.getNoteList().size());
    }
}