package ru.yandex.mobilization.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.services.YandexApiService;

public class TranslatorActivity extends AppCompatActivity {
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> translationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        translationItems = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.translationItems);

        ListView translationListView = (ListView) this.findViewById(R.id.resultListView);
        translationListView.setAdapter(this.listAdapter);
        uploadLanguages();
    }

    public void onUpdateButtonClick(View view) {
        Language from = (Language) ((Spinner)this.findViewById(R.id.langSourceBtn)).getSelectedItem();
        Language to = (Language) ((Spinner)this.findViewById(R.id.langTargetBtn)).getSelectedItem();

        String text = ((EditText)this.findViewById(R.id.inputField)).getText().toString();
        IRequestCallback callback = new IRequestCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<String> resultList = (ArrayList<String>) result;
                translationItems.addAll(resultList);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {

            }
        };

        new YandexApiService(this.getResources().getString(R.string.api_key), this).translate(from.getCode(), to.getCode(), text, callback);
    }

    private void uploadLanguages() {
        final Spinner srcLanguageSpinner = (Spinner) this.findViewById(R.id.langSourceBtn);
        final Spinner targetLanguageSpinner = (Spinner) this.findViewById(R.id.langTargetBtn);

        final Context currentContext = this;

        // Подгрузим языки
        IRequestCallback callback = new IRequestCallback() {
            @Override
            public void onSuccess(Object result) {
                ArrayList<Language> languages = (ArrayList<Language>) result;
                ArrayAdapter spinnerAdapter = new ArrayAdapter(currentContext, android.R.layout.simple_spinner_item, languages.toArray());

                srcLanguageSpinner.setAdapter(spinnerAdapter);
                targetLanguageSpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onError(Exception e) {

            }
        };

        new YandexApiService(this.getResources().getString(R.string.api_key), currentContext).getLanguages(callback);
    }
}
