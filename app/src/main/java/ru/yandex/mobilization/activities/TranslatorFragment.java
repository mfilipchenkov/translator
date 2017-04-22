package ru.yandex.mobilization.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.adapters.TranslationListAdapter;
import ru.yandex.mobilization.components.LanguageSpinner;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.models.TranslationItem;
import ru.yandex.mobilization.services.YandexApiService;

public class TranslatorFragment extends Fragment {
    private View view;
    private TranslationListAdapter translationListAdapter;
    private ArrayList<TranslationItem> translationItems;

    private ArrayList<Language> languages;
    private ArrayAdapter spinnerAdapter;

    private Handler timeoutHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            translate();
        }
    };

    private YandexApiService apiService;

    public static TranslatorFragment newInstance() {
        TranslatorFragment fragment = new TranslatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onSwapButtonClick(View view) {
        LanguageSpinner from = (LanguageSpinner)this.getView().findViewById(R.id.source_language_spinner);
        LanguageSpinner to = (LanguageSpinner)this.getView().findViewById(R.id.target_language_spinner);

        int tempValue = to.getSelectedItemPosition();

        to.setSelection(from.getSelectedItemPosition());
        from.setSelection(tempValue);
        translate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_translator, container, false);

        this.setApiService(new YandexApiService(getResources().getString(R.string.api_key), getContext()));

        this.translationItems = new ArrayList<TranslationItem>();
        this.languages = new ArrayList<Language>(Arrays.asList(new Language("", "Выберите язык")));

        this.translationListAdapter = new TranslationListAdapter(this.getActivity(), R.id.translation_list_view, this.translationItems);
        this.spinnerAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, this.languages);

        AdapterView.OnItemSelectedListener languageSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Language selectedItem = (Language) parent.getSelectedItem();
                if(selectedItem == null || selectedItem.getCode().isEmpty()) {
                    uploadLanguages();
                }
                else {
                    translate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        LanguageSpinner srcLanguageSpinner = (LanguageSpinner) this.view.findViewById(R.id.source_language_spinner);
        LanguageSpinner targetLanguageSpinner = (LanguageSpinner) this.view.findViewById(R.id.target_language_spinner);

        srcLanguageSpinner.setOnItemSelectedListener(languageSelectedListener);
        targetLanguageSpinner.setOnItemSelectedListener(languageSelectedListener);

        srcLanguageSpinner.setAdapter(this.spinnerAdapter);
        targetLanguageSpinner.setAdapter(this.spinnerAdapter);

        ListView translationListView = (ListView) this.view.findViewById(R.id.translation_list_view);
        translationListView.setAdapter(this.translationListAdapter);

        EditText sourceTextEditText = (EditText) this.view.findViewById(R.id.source_text_edittext);
        sourceTextEditText.addTextChangedListener(getTextWatcherWithTimer());

        Button swapLanguageButton = (Button) this.view.findViewById(R.id.swap_language_btn);
        swapLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwapButtonClick(v);
            }
        });

        return this.view;
    }

    private TextWatcher getTextWatcherWithTimer() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeoutHandler.removeCallbacks(onTypingTimeout);

                if(s.length() == 0) {
                    translationListAdapter.clear();
                    return;
                }
                timeoutHandler.postDelayed(onTypingTimeout, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void uploadLanguages() {
        final Context currentContext = this.getActivity();

        // Подгрузим языки
        IRequestCallback callback = new IRequestCallback() {
            @Override
            public void onSuccess(Object result) {
                spinnerAdapter.clear();
                spinnerAdapter.addAll((ArrayList<Language>) result);
            }

            @Override
            public void onError(Exception e) {
                Toast message = Toast.makeText(currentContext, "Не удалось загрузить языки", Toast.LENGTH_LONG);
                message.show();
            }
        };

        apiService.getLanguages(callback);
    }

    private void translate() {
        try {
            final String text = ((EditText)this.view.findViewById(R.id.source_text_edittext)).getText().toString();

            if(text == null || text.isEmpty()) {
                return;
            }

            final Context currentContext = this.getActivity();
            final Language from = (Language) ((LanguageSpinner)this.view.findViewById(R.id.source_language_spinner)).getSelectedItem();
            final Language to = (Language) ((LanguageSpinner)this.view.findViewById(R.id.target_language_spinner)).getSelectedItem();

            if(from == null || from.getCode().isEmpty()) {
                throw new NullPointerException("Не выбран исходный язык");
            }

            if(to == null || to.getCode().isEmpty()) {
                throw new NullPointerException("Не выбран целевой язык");
            }

            IRequestCallback callback = new IRequestCallback() {
                @Override
                public void onSuccess(Object result) {
                    translationListAdapter.clear();

                    if(result instanceof ArrayList) {
                        ArrayList<String> resultArray = (ArrayList<String>) result;

                        resultArray.removeAll(Arrays.asList(null, "", " "));

                        for (String s: resultArray) {
                            translationListAdapter.add(new TranslationItem(text, s, from, to, new Date()));
                        }
                    }
                    else {
                        Toast.makeText(currentContext, "Не удалось преобразовать ответ", Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onError(Exception e) {
                    if(e instanceof NoConnectionError) {
                        Toast.makeText(currentContext, "Нет подключения к сети", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(currentContext, "Во время перевода возникла ошибка", Toast.LENGTH_LONG).show();
                    }
                }
            };

            apiService.translate(from.getCode(), to.getCode(), text, callback);
        }
        catch (UnsupportedEncodingException e) {
            Toast.makeText(this.getActivity(), "Во время обработки текста возникла ошибка", Toast.LENGTH_LONG).show();
        }
        catch (NullPointerException e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setApiService(YandexApiService apiService) {
        this.apiService = apiService;
    }
}