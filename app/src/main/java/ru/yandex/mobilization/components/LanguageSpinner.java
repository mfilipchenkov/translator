package ru.yandex.mobilization.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

// Переопределение Spinner, вызывает onItemSelected, даже если выбор не изменился (для подгрузки языков в случае подключения сети)
public class LanguageSpinner extends Spinner {
    OnItemSelectedListener onItemSelectedListener;

    public LanguageSpinner(Context context) {
        super(context);
    }

    public LanguageSpinner(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        boolean sameSelected = position == getSelectedItemPosition();
        if (sameSelected) {
            onItemSelectedListener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        super.setOnItemSelectedListener(listener);
        this.onItemSelectedListener = listener;
    }
}