package pianokeys;

public class CompositionRunnable implements Runnable {

    public double STEP = 1 / 8.0;

    private final PianoSound sound;
    private Composition composition;

    public CompositionRunnable(PianoSound sound, Composition composition) {
        this.sound = sound;
        this.composition = composition;
    }

    public CompositionRunnable(PianoSound sound, Composition composition, double step) {
        this.sound = sound;
        this.composition = composition;
        STEP = step;
    }

    @Override
    public void run() {
        double time = 0;

        while (time <= composition.duration()) {

            //loop through noteList to play all notes
            for (Note note : composition.getNoteList()) {

                if (note.endTime() == time) {
                    sound.stopNote(note.key());
                } else if (note.startTime() == time) {
                    sound.playNote(note.key());
                }

            }

            try {
                Thread.sleep((long) (STEP * 1000));
            } catch (InterruptedException e) {
               throw new RuntimeException(e);

            }

            time += STEP;
        }

    }
}
