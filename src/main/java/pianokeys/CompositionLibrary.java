package pianokeys;

import pianokeys.net.PianoService;

import javax.swing.*;
import java.awt.*;

public class CompositionLibrary extends JDialog
{
    private final PianoController controller;
    private final PianoService pianoService;
    private JList<String> compositionList;
    private DefaultListModel<String> compositionListModel;
    private Playlist currentPlaylist;

    public CompositionLibrary(JFrame parent, PianoController controller, PianoService pianoService)
    {
        super(parent, "Composition Library", true);
        this.controller = controller;
        this.pianoService = pianoService;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        //List of compositions
        compositionListModel = new DefaultListModel<>();
        compositionList = new JList<>(compositionListModel);
        add(new JScrollPane(compositionList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton uploadButton = new JButton("Upload Current");
        JButton deleteButton = new JButton("Delete");
        JButton loadButton = new JButton("Load");

        refreshButton.addActionListener(e -> controller.refreshLibrary(this));
        uploadButton.addActionListener(e -> controller.uploadComposition(this));
        deleteButton.addActionListener(e -> controller.deleteComposition(this, getSelectedComposition()));
        loadButton.addActionListener(e -> controller.loadComposition(this, getSelectedComposition()));

        buttonPanel.add(refreshButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateList(Playlist playlist)
    {
        this.currentPlaylist = playlist;
        compositionListModel.clear();
        for (Composition composition : playlist)
        {
            String displayName = composition.getName() != null ? composition.getName() : "Untitled";
            compositionListModel.addElement(displayName);
        }
    }

    public Composition getSelectedComposition()
    {
        int selectedIndex = compositionList.getSelectedIndex();
        if (selectedIndex >= 0 && currentPlaylist != null)
        {
            return currentPlaylist.get(selectedIndex);
        }
        return null;
    }
}