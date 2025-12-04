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
        controller.setRecording(false);
        double time = controller.getCurrentTime();

        if (time >= composition.duration())
        {
            time = 0;
            controller.updateCurrentTime(0);
        }

        while (playing && time <= composition.duration())
        {

            //loop through noteList to play all notes
            for (Note note : composition.getNoteList())
            {
                // Updates the visual yellow line to current play position
                controller.updateCurrentTime(time);

                if (note.endTime() == time)
                {
                    controller.stopNote(note.key());

                } else if (note.startTime() == time)
                {
                    controller.playNote(note.key());
                }

            }

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
            controller.stopNote(note.key());
        }

        controller.setRecording(true);

    }
}
