package pianokeys;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pianokeys.net.BitMidiService;
import pianokeys.net.BitMidiTrack;

import javax.swing.*;
import java.util.List;

public class BitMidiSearchController
{
    private final BitMidiSearchView view;
    private final BitMidiService service;

    private String query = "";
    private int page = 1;

    // the search that is currently running, if there is one
    private Disposable search = Disposable.disposed();

    public BitMidiSearchController(BitMidiSearchView view, BitMidiService service)
    {
        this.view = view;
        this.service = service;
    }

    /**
     * Starts a new search on the first page
     *
     * @param text the text the user typed
     */
    public void search(String text)
    {
        String trimmed = text.trim();
        if (trimmed.isEmpty())
        {
            view.showStatus("Please enter a search term");
            return;
        }
        searchPage(trimmed, 1);
    }

    //searches the page after the current one
    public void nextPage()
    {
        searchPage(query, page + 1);
    }

    //searches the page before the current one
    public void previousPage()
    {
        if (page > 1)
        {
            searchPage(query, page - 1);
        }
    }

    private void searchPage(String query, int page)
    {
        // stop a search that is still running so its results don't replace the new ones
        search.dispose();
        this.query = query;
        this.page = page;
        view.setSearching(true);
        view.showStatus("Searching for \"" + query + "\"...");

        // search on a background thread and update the view on the Swing thread
        search = service.search(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(this::showResults, this::showError);
    }

    private void showResults(List<BitMidiTrack> tracks)
    {
        view.showResults(tracks);
        view.setSearching(false);
        view.showPage(page, page > 1, !tracks.isEmpty());
        if (tracks.isEmpty())
        {
            view.showStatus("No results for \"" + query + "\"");
        } else
        {
            view.showStatus(tracks.size() + " results for \"" + query + "\"");
        }
    }

    private void showError(Throwable error)
    {
        view.showResults(List.of());
        view.setSearching(false);
        view.showPage(page, page > 1, false);
        view.showStatus("Search failed: " + error.getMessage());
    }
}