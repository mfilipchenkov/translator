package ru.yandex.mobilization.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.models.FavoritesItem;
import ru.yandex.mobilization.services.FavoritesService;

public class FavoritesListAdapter extends ArrayAdapter {
    ArrayList<FavoritesItem> items;
    Context context;
    LayoutInflater inflater;
    FavoritesListAdapter adapter;

    public FavoritesListAdapter(Context context, int resId, ArrayList<FavoritesItem> items) {
        super(context, resId, items);
        this.context = context;
        this.items = items;
        this.adapter = this;
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
            view = inflater.inflate(R.layout.favorites_item, parent, false);
        }

        final FavoritesItem item = (FavoritesItem) getItem(position);
        ((TextView) view.findViewById(R.id.tv_f_direction)).setText(item.getFrom() + "-" + item.getTo());
        ((TextView) view.findViewById(R.id.tv_f_sourceText)).setText(item.getSourceText());
        ((TextView) view.findViewById(R.id.tv_f_text)).setText(item.getText());

        ImageButton removeBtn = (ImageButton) view.findViewById(R.id.ib_f_remove);
        removeBtn.setImageResource(R.drawable.icon_trash);

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesService service = new FavoritesService(context);
                service.delete(item);
                adapter.remove(item);
            }
        });

        return view;
    }
}
