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

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.components.LanguageSpinner;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.services.YandexApiService;

public class TranslatorFragment extends Fragment {
    private View view;
    private ArrayAdapter<String> translateListAdapter;
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

    public TranslatorFragment() {

    }

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

    private void uploadLanguages() {
        final Context currentContext = this.getActivity();

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
                Toast message = Toast.makeText(currentContext, "Не удалось загрузить языки", Toast.LENGTH_LONG);
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

                if(s.length() == 0) {
                    translateListAdapter.clear();
                    translateListAdapter.notifyDataSetChanged();
                    return;
                }
                timeoutHandler.postDelayed(onTypingTimeout, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public void translate() {
        try {
            String text = ((EditText)this.view.findViewById(R.id.source_text_edittext)).getText().toString();

            if(text == null || text.isEmpty()) {
                return;
            }

            final Context currentContext = this.getActivity();
            Language from = (Language) ((LanguageSpinner)this.view.findViewById(R.id.source_language_spinner)).getSelectedItem();
            Language to = (Language) ((LanguageSpinner)this.view.findViewById(R.id.target_language_spinner)).getSelectedItem();

            if(from == null || from.getCode().isEmpty()) {
                throw new NullPointerException("Не выбран исходный язык");
            }

            if(to == null || to.getCode().isEmpty()) {
                throw new NullPointerException("Не выбран целевой язык");
            }

            IRequestCallback callback = new IRequestCallback() {
                @Override
                public void onSuccess(Object result) {
                    translateListAdapter.clear();
                    if(result instanceof ArrayList) {
                        ArrayList<String> resultArray = (ArrayList<String>) result;
                        resultArray.removeAll(Arrays.asList(null, "", " "));
                        translateListAdapter.addAll(resultArray);

                        translateListAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(currentContext, "Не удалось преобразовать ответ", Toast.LENGTH_LONG);
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

            new YandexApiService(this.view.getResources().getString(R.string.api_key), this.getActivity()).translate(from.getCode(), to.getCode(), text, callback);
        }
        catch (UnsupportedEncodingException e) {
            Toast.makeText(this.getActivity(), "Во время обработки текста возникла ошибка", Toast.LENGTH_LONG).show();
        }
        catch (NullPointerException e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_translator, container, false);

        this.translationItems = new ArrayList<String>();
        this.languages = new ArrayList<Language>(Arrays.asList(new Language("", "Выберите язык")));

        this.translateListAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, this.translationItems);
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

        ListView translationListView = (ListView) this.view.findViewById(R.id.translation_listview);
        translationListView.setAdapter(this.translateListAdapter);

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

}
