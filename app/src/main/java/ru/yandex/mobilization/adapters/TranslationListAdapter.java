package ru.yandex.mobilization.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.models.TranslationItem;
import ru.yandex.mobilization.services.HistoryService;

public class TranslationListAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    HistoryService historyService;
    ArrayList<TranslationItem> items;

    public TranslationListAdapter(Context context, int resource, ArrayList<TranslationItem> items) {
        super(context, resource);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.historyService = new HistoryService(context.getApplicationContext());
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.translator_item, parent, false);
        }

        TranslationItem item = (TranslationItem) getItem(position);
        ((TextView) view.findViewById(R.id.t_translation)).setText((position + 1) + ". " + item.getText());
        historyService.addToHistory(item);
        return view;
    }
}