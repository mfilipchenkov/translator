package ru.yandex.mobilization.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.services.YandexApiService;

public class TranslatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
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

        new YandexApiService().getLanguages(this, callback);
    }
}
