package pianokeys;

import pianokeys.net.BitMidiTrack;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

import static java.awt.Color.*;

/**
 * Displays the BitMidi search box, the list of results and the page buttons.
 * Button presses are passed to the BitMidiSearchController.
 */
public class BitMidiSearchView extends JPanel
{

    // declared everything
    private static final Font FONT = new Font("Arial", Font.BOLD, 16);

    private final Supplier<BitMidiSearchController> controller;

    private final JTextField queryField = new JTextField(30);
    private final JButton searchButton = new JButton("Search");
    private final JButton previousButton = new JButton("< Prev");
    private final JButton nextButton = new JButton("Next >");
    private final JLabel pageLabel = new JLabel("Page 1");
    private final JLabel statusLabel = new JLabel("Enter a song name to search BitMidi");

    // the list shows these tracks
    private final DefaultListModel<BitMidiTrack> results = new DefaultListModel<>();

    public BitMidiSearchView(Supplier<BitMidiSearchController> controller)
    {
        this.controller = controller;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createResultsScrollPane(), BorderLayout.CENTER);
        add(createPagePanel(), BorderLayout.SOUTH);

        // nothing to page through until the first search
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);
    }

    private JPanel createSearchPanel()
    {
        JLabel searchLabel = new JLabel("Search BitMidi:");
        searchLabel.setFont(FONT);
        queryField.setFont(FONT);
        searchButton.setFont(FONT);

        // search when the button is clicked or enter is pressed in the text field
        searchButton.addActionListener(e ->
        {
            controller.get().search(queryField.getText());
        });
        queryField.addActionListener(e ->
        {
            controller.get().search(queryField.getText());
        });

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(queryField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        return searchPanel;
    }

    private JScrollPane createResultsScrollPane()
    {
        JList<BitMidiTrack> resultsList = new JList<>(results);
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setFont(FONT);
        resultsList.setBackground(WHITE);

        // show the song title instead of the whole BitMidiTrack
        resultsList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                BitMidiTrack track = (BitMidiTrack) value;
                setText(track.title());
                setToolTipText(track.pageUrl());
                return this;
            }
        });

        return new JScrollPane(resultsList);
    }

    private JPanel createPagePanel()
    {
        previousButton.addActionListener(e ->
        {
            controller.get().previousPage();
        });
        nextButton.addActionListener(e ->
        {
            controller.get().nextPage();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(previousButton);
        buttonPanel.add(pageLabel);
        buttonPanel.add(nextButton);

        JPanel pagePanel = new JPanel(new BorderLayout());
        pagePanel.add(statusLabel, BorderLayout.CENTER);
        pagePanel.add(buttonPanel, BorderLayout.EAST);
        return pagePanel;
    }

    /**
     * Replaces the tracks in the results list
     *
     * @param tracks the tracks to show
     */
    public void showResults(List<BitMidiTrack> tracks)
    {
        results.clear();
        results.addAll(tracks);
    }

    /**
     * Shows a message under the results list
     *
     * @param status the message to show
     */
    public void showStatus(String status)
    {
        statusLabel.setText(status);
    }

    /**
     * Shows the current page and turns the page buttons on or off
     *
     * @param page        the page being shown
     * @param hasPrevious true if there is a page before this one
     * @param hasNext     true if there might be a page after this one
     */
    public void showPage(int page, boolean hasPrevious, boolean hasNext)
    {
        pageLabel.setText("Page " + page);
        previousButton.setEnabled(hasPrevious);
        nextButton.setEnabled(hasNext);
    }

    /**
     * Turns the inputs off while a search is running so another one can't be started
     *
     * @param searching true while waiting for BitMidi to answer
     */
    public void setSearching(boolean searching)
    {
        searchButton.setEnabled(!searching);
        queryField.setEnabled(!searching);
        if (searching)
        {
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }
}