package ru.yandex.mobilization.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.models.HistoryItem;
import ru.yandex.mobilization.services.FavoritesService;

public class HistoryListAdapter extends ArrayAdapter {

    ArrayList<HistoryItem> items;
    Context context;
    LayoutInflater inflater;
    FavoritesService favoritesService;

    public HistoryListAdapter(Context context, int resId, ArrayList<HistoryItem> items) {
        super(context, resId, items);
        this.context = context;
        this.items = items;
        this.favoritesService = new FavoritesService(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.history_item, parent, false);
        }

        final HistoryItem item = (HistoryItem) getItem(position);
        ((TextView) view.findViewById(R.id.hi_direction)).setText(item.getFrom() + "-" + item.getTo());
        ((TextView) view.findViewById(R.id.hi_sourceText)).setText(item.getSourceText());
        ((TextView) view.findViewById(R.id.hi_text)).setText(item.getText());

        ((Button) view.findViewById(R.id.hi_addToFavorite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesService.addToFavorites(item);
            }
        });
        return view;
    }
}
