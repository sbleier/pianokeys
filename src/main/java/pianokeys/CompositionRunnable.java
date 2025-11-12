package pianokeys;

public class CompositionRunnable implements Runnable
{

    public static final double STEP = 1 / 8.0;

    private double sleepMs;
    private final PianoSound sound;
    private Composition composition;


    public CompositionRunnable(PianoSound sound, Composition composition, CompositionView compositionView,
                               double sleepMs)
    {
        this.sound = sound;
        this.composition = composition;
        this.sleepMs = sleepMs;
    }

    @Override
    public void run()
    {
        double time = 0;

        while (time <= composition.duration())
        {

            //loop through noteList to play all notes
            for (Note note : composition.getNoteList())
            {

                if (note.endTime() == time)
                {
                    sound.stopNote(note.key());
                } else if (note.startTime() == time)
                {
                    sound.playNote(note.key());
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
        }

    }
}
