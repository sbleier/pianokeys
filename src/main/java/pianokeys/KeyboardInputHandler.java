package pianokeys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import static pianokeys.PianoSound.C4;

/**
 * Lets the computer keyboard play notes, using the standard "musical typing"
 * layout (as used by GarageBand, Ableton, etc.):
 *
 * Black keys:  W E   T Y U   O P
 * White keys: A S D F G H J K L ;
 *
 * Z / X shift the whole mapping down/up an octave, so every note on the
 * piano is reachable from this one compact cluster of keys.
 *
 * Registers key bindings on the given root pane with WHEN_IN_FOCUSED_WINDOW,
 * so playing works no matter which component currently has focus, and tracks
 * which keys are currently held down so that OS key-repeat doesn't retrigger
 * notes, corrupt recorded durations, or re-shift the octave on every repeat.
 */
public class KeyboardInputHandler
{
    private static final Map<Integer, Integer> KEY_TO_NOTE = Map.ofEntries(
            // white keys
            Map.entry(KeyEvent.VK_A, C4),
            Map.entry(KeyEvent.VK_S, C4 + 2),
            Map.entry(KeyEvent.VK_D, C4 + 4),
            Map.entry(KeyEvent.VK_F, C4 + 5),
            Map.entry(KeyEvent.VK_G, C4 + 7),
            Map.entry(KeyEvent.VK_H, C4 + 9),
            Map.entry(KeyEvent.VK_J, C4 + 11),
            Map.entry(KeyEvent.VK_K, C4 + 12),
            Map.entry(KeyEvent.VK_L, C4 + 14),
            Map.entry(KeyEvent.VK_SEMICOLON, C4 + 16),

            // black keys
            Map.entry(KeyEvent.VK_W, C4 + 1),
            Map.entry(KeyEvent.VK_E, C4 + 3),
            Map.entry(KeyEvent.VK_T, C4 + 6),
            Map.entry(KeyEvent.VK_Y, C4 + 8),
            Map.entry(KeyEvent.VK_U, C4 + 10),
            Map.entry(KeyEvent.VK_O, C4 + 13),
            Map.entry(KeyEvent.VK_P, C4 + 15)
    );

    private static final Map<Integer, String> KEY_TO_LABEL = Map.ofEntries(
            Map.entry(KeyEvent.VK_A, "A"),
            Map.entry(KeyEvent.VK_S, "S"),
            Map.entry(KeyEvent.VK_D, "D"),
            Map.entry(KeyEvent.VK_F, "F"),
            Map.entry(KeyEvent.VK_G, "G"),
            Map.entry(KeyEvent.VK_H, "H"),
            Map.entry(KeyEvent.VK_J, "J"),
            Map.entry(KeyEvent.VK_K, "K"),
            Map.entry(KeyEvent.VK_L, "L"),
            Map.entry(KeyEvent.VK_SEMICOLON, ";"),
            Map.entry(KeyEvent.VK_W, "W"),
            Map.entry(KeyEvent.VK_E, "E"),
            Map.entry(KeyEvent.VK_T, "T"),
            Map.entry(KeyEvent.VK_Y, "Y"),
            Map.entry(KeyEvent.VK_U, "U"),
            Map.entry(KeyEvent.VK_O, "O"),
            Map.entry(KeyEvent.VK_P, "P")
    );

    // PianoView renders 7 octaves, with the home octave (this class's un-shifted
    // mapping) at index 3 - so the shift is only valid from -3 to +3.
    private static final int HOME_OCTAVE_INDEX = 3;
    private static final int MIN_OCTAVE_SHIFT = -3;
    private static final int MAX_OCTAVE_SHIFT = 3;

    private final Map<Integer, Integer> heldNotes = new HashMap<>();
    private final Set<Integer> heldControlKeys = new HashSet<>();
    private final Supplier<PianoController> controller;
    private final PianoView pianoView;
    private final IntConsumer onOctaveChanged;

    private int octaveShift = 0;

    public KeyboardInputHandler(Supplier<PianoController> controller, PianoView pianoView, IntConsumer onOctaveChanged)
    {
        this.controller = controller;
        this.pianoView = pianoView;
        this.onOctaveChanged = onOctaveChanged;
    }

    /**
     * Registers one press/release key binding per mapped note key, plus the
     * octave-shift keys, on the given root pane, scoped to WHEN_IN_FOCUSED_WINDOW.
     */
    public void registerOn(JRootPane rootPane)
    {
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        for (Map.Entry<Integer, Integer> entry : KEY_TO_NOTE.entrySet())
        {
            int keyCode = entry.getKey();
            int baseNote = entry.getValue();

            String pressKey = "pianokeys.press." + keyCode;
            String releaseKey = "pianokeys.release." + keyCode;

            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), pressKey);
            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), releaseKey);

            actionMap.put(pressKey, new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (!heldNotes.containsKey(keyCode))
                    {
                        int note = baseNote + octaveShift * 12;
                        heldNotes.put(keyCode, note);
                        controller.get().playNote(note);
                    }
                }
            });

            actionMap.put(releaseKey, new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Integer note = heldNotes.remove(keyCode);
                    if (note != null)
                    {
                        controller.get().stopNote(note);
                    }
                }
            });
        }

        registerOctaveShiftKey(inputMap, actionMap, KeyEvent.VK_Z, -1);
        registerOctaveShiftKey(inputMap, actionMap, KeyEvent.VK_X, 1);

        updateKeyboardLabels();
    }

    /**
     * Recomputes which note each mapped key currently plays (base note shifted by the
     * current octave) and pushes the corner badges to PianoView to match.
     */
    private void updateKeyboardLabels()
    {
        Map<Integer, String> noteToLabel = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : KEY_TO_NOTE.entrySet())
        {
            int keyCode = entry.getKey();
            int note = entry.getValue() + octaveShift * 12;
            noteToLabel.put(note, KEY_TO_LABEL.get(keyCode));
        }
        pianoView.setKeyboardLabels(noteToLabel);
    }

    private void registerOctaveShiftKey(InputMap inputMap, ActionMap actionMap, int keyCode, int direction)
    {
        String pressKey = "pianokeys.octave.press." + keyCode;
        String releaseKey = "pianokeys.octave.release." + keyCode;

        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), pressKey);
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), releaseKey);

        actionMap.put(pressKey, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (heldControlKeys.add(keyCode))
                {
                    int shifted = octaveShift + direction;
                    if (shifted >= MIN_OCTAVE_SHIFT && shifted <= MAX_OCTAVE_SHIFT)
                    {
                        octaveShift = shifted;
                        updateKeyboardLabels();
                        onOctaveChanged.accept(HOME_OCTAVE_INDEX + octaveShift);
                    }
                }
            }
        });

        actionMap.put(releaseKey, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                heldControlKeys.remove(keyCode);
            }
        });
    }
}
