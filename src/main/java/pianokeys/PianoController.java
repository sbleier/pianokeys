package pianokeys;

import pianokeys.net.CreateRequest;
import pianokeys.net.DeleteRequest;
import pianokeys.net.PianoService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PianoController
{
    private final CompositionView compositionView;
    private final PianoSound sound;
    private final Composition composition;
    private final PianoView pianoView;
    private final Recorder recorder;
    private CompositionRunnable runnable;
    private PianoService pianoService;

    private boolean recording = true;

    public PianoController(CompositionView compositionView,
                           PianoSound sound,
                           Composition composition,
                           PianoView pianoView,
                           PianoService pianoService)
    {
        this.compositionView = compositionView;
        this.sound = sound;
        this.composition = composition;
        this.pianoView = pianoView;
        this.recorder = new Recorder(composition);
        this.pianoService = pianoService;
    }

    /**
     * Play the note in PianoSound
     *
     * @param note the MIDI note value to play
     */
    public void playNote(int note)
    {
        sound.playNote(note);
        pianoView.showKeyPlayed(note, true);
        if (recording)
        {
            recorder.startNote(note);
        }
    }

    /**
     * Stops the note from being played
     *
     * @param note the MIDI note value to stop
     */
    public void stopNote(int note)
    {
        sound.stopNote(note);
        pianoView.showKeyPlayed(note, false);
        if (recording)
        {
            recorder.stopNote();
            compositionView.setCurrentTime(recorder.getCompositionTimeSeconds());
            compositionView.refreshLayout();
        }
    }

    public boolean playComposition()
    {
        //going to be using compositionRunnable
        if (runnable == null)
        {
            runnable = new CompositionRunnable(this, composition);
            new Thread(runnable).start();
            return true;
        }
        return false;
    }

    public void stopComposition()
    {
        if (runnable != null)
        {
            runnable.stop();
            runnable = null;
        }
    }

    //clears all notes from the composition
    public void eraseComposition()
    {
        composition.getNoteList().clear();
        compositionView.setCurrentTime(0);
        compositionView.repaint();
        recorder.reset();
    }

    //resets the composition playback to the beginning
    public void restartComposition()
    {
        compositionView.setCurrentTime(0);
    }

    //changes the current instrument sound
    public void changeInstrument(String instrument)
    {
        if (instrument != null && sound != null)
        {
            sound.setInstrument(instrument);
        }
    }

    public void setRecording(boolean recording)
    {
        this.recording = recording;
    }

    public double getCurrentTime()
    {
        return compositionView.getCurrentTime();
    }

    public void setCurrentTime(double time)
    {
        compositionView.setCurrentTime(time);
    }

    public void openCompositionLibrary(JFrame parent)
    {
        CompositionLibrary library = new CompositionLibrary(parent, this, pianoService);
        refreshLibrary(library);
        library.setVisible(true);
    }

    public void refreshLibrary(CompositionLibrary library)
    {
        pianoService.getComposition()
                .subscribe(
                        response ->
                        {
                            Playlist playlist = response.playlist();
                            library.updateList(playlist);
                        },
                        error ->
                        {
                            JOptionPane.showMessageDialog(library,
                                    "Error loading compositions: " + error.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                );
    }

    public void uploadComposition(CompositionLibrary library)
    {
        String name = JOptionPane.showInputDialog(library, "Enter composition name:");
        if (name == null || name.trim().isEmpty())
        {
            return;
        }
        composition.setName(name);
        CreateRequest request = new CreateRequest(composition);
        pianoService.createComposition(request)
                .subscribe(
                        () -> {
                            JOptionPane.showMessageDialog(library,
                                    "Composition uploaded successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshLibrary(library);
                        },
                        error -> {
                            JOptionPane.showMessageDialog(library,
                                    "Error uploading composition: " + error.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                );
    }

    public void deleteComposition(CompositionLibrary library, Composition selectedComposition)
    {
        if (selectedComposition == null)
        {
            JOptionPane.showMessageDialog(library,
                    "Please select a composition to delete");
            return;
        }

        int id = selectedComposition.getId();
        DeleteRequest request = new DeleteRequest(id);

        pianoService.deleteComposition(request)
                .subscribe(
                        () -> {
                            JOptionPane.showMessageDialog(library, "Deleted successfully");
                            refreshLibrary(library);
                        },
                        error -> {
                            System.out.println("DEBUG: Delete error: " + error.getMessage());
                            error.printStackTrace();
                            JOptionPane.showMessageDialog(library,
                                    "Error deleting: " + error.getMessage());
                        }
                );
    }

    public void loadComposition(CompositionLibrary library, Composition selectedComposition)
    {
        if (selectedComposition == null)
        {
            JOptionPane.showMessageDialog(library,
                    "Please select a composition to load");
            return;
        }

        composition.getNoteList().clear();
        composition.getNoteList().addAll(selectedComposition.getNoteList());
        composition.setName(selectedComposition.getName());

        compositionView.repaint();

        JOptionPane.showMessageDialog(library, "Composition loaded");
        library.dispose();
    }
}
