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
        double startTime = compView.getCurrentTime();
        long startMillis = System.currentTimeMillis();

        while (playing && startTime <= composition.duration())
        {
            // calculate the time based only on the actual elapsed milliseconds from the beginning
            long elapsedMillis = System.currentTimeMillis() - startMillis;
            double time = startTime + (elapsedMillis / 1000.0);

            // Round to the nearest step for note matching
            double roundedTime = Note.roundToNearestEight(time);

            //loop through noteList to play all notes
            for (Note note : composition.getNoteList())
            {
                if (note.endTime() == roundedTime)
                {
                    sound.stopNote(note.key());
                    pianoView.showKeyPlayed(note.key(), false);

                } else if (note.startTime() == roundedTime)
                {
                    sound.playNote(note.key());
                    pianoView.showKeyPlayed(note.key(), true);
                }
            }

            // Update the visual timeline with the actual time
            compView.setCurrentTime(time);

            try
            {
                Thread.sleep((long) (sleepMs * 1000));
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        for (Note note : composition.getNoteList())
        {
            sound.stopNote(note.key());
        }
        
    }
}
