package ru.yandex.mobilization.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.adapters.HistoryListAdapter;
import ru.yandex.mobilization.models.HistoryItem;
import ru.yandex.mobilization.services.HistoryService;

public class HistoryFragment extends Fragment {

    private HistoryListAdapter historyListAdapter;
    private ArrayList<HistoryItem> historyItems;
    private HistoryService historyService;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.historyItems = new ArrayList<>();
        this.historyListAdapter = new HistoryListAdapter(this.getActivity(), R.id.lv_history_list_view, this.historyItems);
        this.historyService = new HistoryService(this.getActivity().getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        ListView historyListView = (ListView) this.getActivity().findViewById(R.id.lv_history_list_view);
        historyListView.setAdapter(historyListAdapter);

        reloadHistory();
    }

    public void reloadHistory() {
        ArrayList<HistoryItem> historyItems = historyService.getHistory();

        historyListAdapter.clear();
        historyListAdapter.addAll(historyItems);
    }
}
