package ru.yandex.mobilization.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.models.HistoryItem;
import ru.yandex.mobilization.services.HistoryService;

public class HistoryFragment extends Fragment {

    private ArrayAdapter<HistoryItem> historyListAdapter;
    private ArrayList<HistoryItem> historyItems;

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadHistory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.historyItems = new ArrayList<>();
        this.historyListAdapter = new ArrayAdapter<HistoryItem>(this.getActivity(), android.R.layout.simple_list_item_1, this.historyItems);
    }

    @Override
    public void onStart() {
        super.onStart();ListView historyListView = (ListView) this.getActivity().findViewById(R.id.history_list_view);
        historyListView.setAdapter(historyListAdapter);

        reloadHistory();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    public void reloadHistory() {
        ListView historyListView = (ListView) this.getActivity().findViewById(R.id.history_list_view);
        HistoryService historyService = new HistoryService(this.getActivity());
        ArrayList<HistoryItem> historyItems = historyService.getHistory();

        historyListAdapter.clear();
        historyListAdapter.addAll(historyItems);
        historyListAdapter.notifyDataSetChanged();
    }

}
