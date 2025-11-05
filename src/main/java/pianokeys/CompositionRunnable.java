package pianokeys;

public class CompositionRunnable implements Runnable
{

    public static final double STEP = 1 / 8.0;

    private double sleepMs;
    private final PianoSound sound;
    private boolean paused = true;
    private boolean playing = false;
    private Composition composition;


    public CompositionRunnable(PianoSound sound, Composition composition, double sleepMs)
    {
        this.sound = sound;
        this.composition = composition;
        this.sleepMs = sleepMs;
    }

    public synchronized void pause() {
        paused = true;
        for (Note note : composition.getNoteList())
        {
            sound.stopNote(note.key());
        }
    }

    public synchronized void play() {
        paused = false;
        notifyAll();
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void run()
    {
        playing = true;
        double time = 0;

        while (time <= composition.duration())
        {

            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

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
                if (paused) {
                    break;
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

        playing = false;

    }
}
