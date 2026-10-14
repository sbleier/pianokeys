
package pianokeys;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class MidiParser {

    public ArrayList<Note> parse(File midiFile)
            throws IOException, InvalidMidiDataException {

        Sequence sequence = MidiSystem.getSequence(midiFile);
        Track[] tracks = sequence.getTracks();

        ArrayList<Note> notes = new ArrayList<>();
        ArrayList<MidiEvent> tempoEvents = new ArrayList<>();

        // Find tempo changes
        for (Track track : tracks) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);

                if (event.getMessage() instanceof MetaMessage) {
                    MetaMessage meta = (MetaMessage) event.getMessage();

                    if (meta.getType() == 0x51) {
                        tempoEvents.add(event);
                    }
                }
            }
        }

        tempoEvents.sort(Comparator.comparingLong(MidiEvent::getTick));

        // Read notes from each track
        for (Track track : tracks) {

            HashMap<Integer, Long> startTimes = new HashMap<>();
            HashMap<Integer, Integer> noteInstruments = new HashMap<>();
            int[] instruments = new int[16];

            for (int i = 0; i < track.size(); i++) {

                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {

                    ShortMessage shortMessage = (ShortMessage) message;

                    int command = shortMessage.getCommand();
                    int pitch = shortMessage.getData1();
                    int channel = shortMessage.getChannel();
                    int noteId = channel * 128 + pitch;
                    long tick = event.getTick();

                    if (command == ShortMessage.PROGRAM_CHANGE) {

                        instruments[channel] = pitch;

                    } else if (command == ShortMessage.NOTE_ON
                            && shortMessage.getData2() > 0) {

                        startTimes.put(noteId, tick);
                        noteInstruments.put(noteId, instruments[channel]);

                    } else if (command == ShortMessage.NOTE_OFF
                            || (command == ShortMessage.NOTE_ON
                            && shortMessage.getData2() == 0)) {

                        if (startTimes.containsKey(noteId)) {

                            long startTick = startTimes.remove(noteId);
                            int instrument = noteInstruments.remove(noteId);

                            double startSeconds =
                                    tickToSeconds(startTick, sequence, tempoEvents);

                            double endSeconds =
                                    tickToSeconds(tick, sequence, tempoEvents);

                            // Store all four values in a Note object
                            Note newNote = new Note(
                                    pitch,
                                    startSeconds,
                                    endSeconds,
                                    instrument
                            );

                            notes.add(newNote);
                        }
                    }
                }
            }
        }

        // Return notes in order of start time
        notes.sort(Comparator.comparingDouble(Note::startTime));

        return notes;
    }

    private double tickToSeconds(long targetTick, Sequence sequence,
                                 ArrayList<MidiEvent> tempoEvents) {

        int tempo = 500000;
        long previousTick = 0;
        double seconds = 0;

        for (MidiEvent event : tempoEvents) {

            long tick = event.getTick();

            if (tick > targetTick) {
                break;
            }

            seconds += (tick - previousTick)
                    * (tempo / 1000000.0)
                    / sequence.getResolution();

            MetaMessage meta = (MetaMessage) event.getMessage();
            byte[] data = meta.getData();

            if (data.length == 3) {
                tempo = ((data[0] & 0xFF) << 16)
                        | ((data[1] & 0xFF) << 8)
                        | (data[2] & 0xFF);
            }

            previousTick = tick;
        }

        seconds += (targetTick - previousTick)
                * (tempo / 1000000.0)
                / sequence.getResolution();

        return seconds;
    }
}
