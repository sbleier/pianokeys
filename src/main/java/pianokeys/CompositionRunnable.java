package pianokeys;

public class CompositionRunnable implements Runnable {

    public static final double STEP = 1 / 8.0;

    private final PianoSound sound;
    private Composition composition;

    public CompositionRunnable(PianoSound sound, Composition composition) {
        this.sound = sound;
        this.composition = composition;
    }

    @Override
    public void run() {
        double time = 0;

        double max = 0;
        for (Note note : composition.getNoteList()) {
            if (note.endTime() > max) {
                max = note.endTime();
            }
        }

        while (time <= max) {

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
            } catch (InterruptedException ignored) {

            }

            time += STEP;
        }

    }
}
