package ru.yandex.mobilization.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.services.YandexApiService;

public class TranslatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
    }

    public void applyLanguage(final View view) {
        IRequestCallback callback = new IRequestCallback() {
            @Override
            public void onSuccess(String result) {

            }
        };
        new YandexApiService().getLanguages(this, callback);
    }
}
