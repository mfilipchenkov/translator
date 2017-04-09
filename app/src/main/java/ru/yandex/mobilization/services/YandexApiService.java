package ru.yandex.mobilization.services;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.providers.HttpRequestProvider;

// Сервис для работы с API Яндекса
public class YandexApiService {
    // Метод для получения списков языков.
    // Принимает контекст приложения и метод обратного вызова для обработки результата
    public void getLanguages(Context context, final IRequestCallback callback) {
        String apiKey = context.getResources().getString(R.string.api_key);
        final ArrayList<String> list = new ArrayList<>();

        HttpRequestProvider provider = HttpRequestProvider.getInstance(context.getApplicationContext());

        String url = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + apiKey + "&ui=ru";

        StringRequest request = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode root = mapper.readTree(response);
                        // В ответе ищем элемент langs, если его нет, кидаем исключение
                        if(!root.has("langs")) {
                            throw new Exception("Не найден элемент langs");
                        }

                        JsonNode langs = root.get("langs");

                        LinkedHashMap<String, String> map = mapper.convertValue(langs, new TypeReference<LinkedHashMap<String, String>>(){});
                        ArrayList<Language> languageList = new ArrayList<>();

                        for(Map.Entry<String, String> entry: map.entrySet()) {
                            languageList.add(new Language(entry.getKey(), entry.getValue()));
                        }

                        callback.onSuccess(languageList);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onError(error);
                }
            }
        );

        provider.addToQueue(request);
    }
}