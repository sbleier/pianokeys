package pianokeys;

import static pianokeys.Note.TIME_STEP;

public class CompositionRunnable implements Runnable
{
    private final double sleepMs;
    private final PianoController controller;
    private final Composition composition;
    private boolean playing = true;

    public CompositionRunnable(PianoController controller, Composition composition)
    {
        this(controller, composition, TIME_STEP);
    }

    public CompositionRunnable(PianoController controller, Composition composition, double sleepMs)
    {
        this.controller = controller;
        this.composition = composition;
        this.sleepMs = sleepMs;
    }

    public void stop()
    {
        playing = false;
    }

    @Override
    public void run()
    {
        double startTime = compView.getCurrentTime();
        long startMillis = System.currentTimeMillis();
        controller.setRecording(false);
        double time = 0;

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
                    controller.stopNote(note.key());

                } else if (note.startTime() == roundedTime)
                {
                    controller.playNote(note.key());
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

            time += TIME_STEP;
        }

        for (Note note : composition.getNoteList())
        {
            sound.stopNote(note.key());
        }
        
            controller.stopNote(note.key());
        }

        controller.setRecording(true);

    }
}
