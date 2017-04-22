package ru.yandex.mobilization.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.adapters.FavoritesListAdapter;
import ru.yandex.mobilization.models.FavoritesItem;
import ru.yandex.mobilization.services.FavoritesService;

public class FavoritesFragment extends Fragment {

    private FavoritesListAdapter favoritesListAdapter;
    private ArrayList<FavoritesItem> favoritesItems;
    private FavoritesService favoritesService;

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.favoritesItems = new ArrayList<>();
        this.favoritesListAdapter = new FavoritesListAdapter(this.getActivity(), R.id.favorites_list_view, this.favoritesItems);

        this.favoritesService = new FavoritesService(this.getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            reloadFavorites();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ListView favoritesListView = (ListView) this.getActivity().findViewById(R.id.favorites_list_view);
        favoritesListView.setAdapter(favoritesListAdapter);

        reloadFavorites();
    }

    public void reloadFavorites() {
//        FavoritesService favoritesService = new FavoritesService(this.getActivity());
        ArrayList<FavoritesItem> historyItems = favoritesService.getFavorites();

        favoritesListAdapter.clear();
        favoritesListAdapter.addAll(historyItems);
    }
}
