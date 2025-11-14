package pianokeys;

import static pianokeys.Note.TIME_STEP;

public class CompositionRunnable implements Runnable
{

    public static final double STEP = 1 / 8.0;

    private final double sleepMs;
    private final PianoSound sound;
    private final Composition composition;
    private final CompositionView compView;
    private final PianoView pianoView;
    private boolean playing = true;

    public CompositionRunnable(PianoSound sound, Composition composition, CompositionView compositionView,
                               PianoView pianoView) {
        this(sound, composition, TIME_STEP, compositionView, pianoView);
    }

    public CompositionRunnable(PianoSound sound, Composition composition, double sleepMs, CompositionView compView,
                               PianoView pianoView)
    {
        this.sound = sound;
        this.composition = composition;
        this.sleepMs = sleepMs;
        this.compView = compView;
        this.pianoView = pianoView;
    }

    public void stop() {
        playing = false;
    }

    @Override
    public void run()
    {
        double time = compView.getCurrentTime();

        while (playing && time <= composition.duration())
        {

            //loop through noteList to play all notes
            for (Note note : composition.getNoteList())
            {

                if (note.endTime() == time)
                {
                    sound.stopNote(note.key());
                    pianoView.showKeyPlayed(note.key(), false);

                } else if (note.startTime() == time)
                {
                    sound.playNote(note.key());
                    pianoView.showKeyPlayed(note.key(), true);
                }
            }

            try
            {
                Thread.sleep((long) (sleepMs * 1000));
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            time += STEP;

            compView.setCurrentTime(time);
        }

        for (Note note : composition.getNoteList()) {
            sound.stopNote(note.key());
        }



    }
}
