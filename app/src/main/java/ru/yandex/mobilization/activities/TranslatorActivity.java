package ru.yandex.mobilization.activities;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.services.InternetErrorsHelper;
import ru.yandex.mobilization.services.YandexApiService;

public class TranslatorActivity extends AppCompatActivity {
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> translationItems;

    private ArrayList<Language> languages;
    private ArrayAdapter spinnerAdapter;

    private Handler timeoutHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            translate();
        }
    };
    private Context activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        this.translationItems = new ArrayList<String>();
        this.languages = new ArrayList<Language>(Arrays.asList(new Language("", "Выберите язык")));

        this.listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.translationItems);
        this.spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.languages);

        Spinner srcLanguageSpinner = (Spinner) this.findViewById(R.id.source_language_spinner);
        Spinner targetLanguageSpinner = (Spinner) this.findViewById(R.id.target_language_spinner);

        srcLanguageSpinner.setAdapter(this.spinnerAdapter);
        targetLanguageSpinner.setAdapter(this.spinnerAdapter);

        ListView translationListView = (ListView) this.findViewById(R.id.translation_listview);
        translationListView.setAdapter(this.listAdapter);

        EditText sourceTextEditText = (EditText) this.findViewById(R.id.source_text_edittext);
        sourceTextEditText.addTextChangedListener(getTextWatcherWithTimer());

        uploadLanguages();
    }

    public void onSwapButtonClick(View view) {
        Spinner from = (Spinner)this.findViewById(R.id.source_language_spinner);
        Spinner to = (Spinner)this.findViewById(R.id.target_language_spinner);

        int tempValue = to.getSelectedItemPosition();

        to.setSelection(from.getSelectedItemPosition());
        from.setSelection(tempValue);
        translate();
    }

    private void uploadLanguages() {
        final Context currentContext = this;

        // Подгрузим языки
        IRequestCallback callback = new IRequestCallback() {
            @Override
            public void onSuccess(Object result) {
                spinnerAdapter.clear();
                spinnerAdapter.addAll((ArrayList<Language>) result);
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast message = Toast.makeText(currentContext, InternetErrorsHelper.getExceptionDescription(e), Toast.LENGTH_LONG);
                message.show();
            }
        };

        new YandexApiService(this.getResources().getString(R.string.api_key), currentContext).getLanguages(callback);
    }

    public TextWatcher getTextWatcherWithTimer() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeoutHandler.removeCallbacks(onTypingTimeout);
                timeoutHandler.postDelayed(onTypingTimeout, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public void translate() {
        try {
            final Context currentContext = this;
            Language from = (Language) ((Spinner)this.findViewById(R.id.source_language_spinner)).getSelectedItem();
            Language to = (Language) ((Spinner)this.findViewById(R.id.target_language_spinner)).getSelectedItem();

            if(from == null || from.getCode().isEmpty()) {
                throw new NullPointerException("Не выбран исходный язык");
            }

            if(to == null || to.getCode().isEmpty()) {
                throw new NullPointerException("Не выбран целевой язык");
            }

            String text = ((EditText)this.findViewById(R.id.source_text_edittext)).getText().toString();
            IRequestCallback callback = new IRequestCallback() {
                @Override
                public void onSuccess(Object result) {
                    listAdapter.clear();
                    listAdapter.addAll((ArrayList<String>)result);
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    Toast message = Toast.makeText(currentContext, InternetErrorsHelper.getExceptionDescription(e), Toast.LENGTH_LONG);
                    message.show();
                }
            };

            new YandexApiService(this.getResources().getString(R.string.api_key), this).translate(from.getCode(), to.getCode(), text, callback);
        }
        catch (UnsupportedEncodingException e) {
            Toast message = Toast.makeText(this, "Во время обработки текста возникла ошибка", Toast.LENGTH_LONG);
            message.show();
        }
        catch (NullPointerException e) {
            Toast message = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            message.show();
        }
    }
}
