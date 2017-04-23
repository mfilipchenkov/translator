package ru.yandex.mobilization.services;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.yandex.mobilization.interfaces.IRequestCallback;
import ru.yandex.mobilization.models.Language;
import ru.yandex.mobilization.providers.HttpRequestProvider;

// Сервис для работы с API Яндекса
public class YandexApiService {
    // Ключ для работы с API Яндекса
    private String apiKey;
    private Context context;

    public YandexApiService(String apiKey, Context context) {
        this.apiKey = apiKey;
        this.context = context;
    }

    // Метод для получения списков языков.
    // Принимает контекст приложения и метод обратного вызова для обработки результата
    public void getLanguages(final IRequestCallback callback){
        HttpRequestProvider provider = HttpRequestProvider.getInstance(this.context.getApplicationContext());

        String url = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=" + this.apiKey + "&ui=ru";

        String cache = getCacheData(provider, url);

        if(cache == null || cache.isEmpty()) {
            StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processLanguage(response, callback);
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
        else {
            processLanguage(cache, callback);
        }
    }

    private void processLanguage(String response, IRequestCallback callback) {
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

    public void translate(String from, String to, final String text, final IRequestCallback callback) throws UnsupportedEncodingException {
        HttpRequestProvider provider = HttpRequestProvider.getInstance(this.context.getApplicationContext());
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate" +
                "?key=" + this.apiKey +
                "&text=" + URLEncoder.encode(text.isEmpty() ? " " : text, "UTF-8") +
                "&lang=" + (from.isEmpty() ? to : from + "-" + to) +
                "&format=plain";

        try {
            // Ограничение на длину GET-запроса
            if (url.length() >= 10000) {
                throw new Exception("Превышена максимально допустимая длина текста");
            }

            String cache = getCacheData(provider, url);

            if(cache == null || cache.isEmpty()) {
                StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            processTranslate(response, callback);
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
            else {
                processTranslate(cache, callback);
            }
        }
        catch(Exception e) {
            callback.onError(e);
        }
    }

    private void processTranslate(String response, IRequestCallback callback) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response).get("text");
            ArrayList<String> list = mapper.readValue(node.toString(), new TypeReference<ArrayList<String>>() {});
            callback.onSuccess(list);
        } catch(Exception e) {
            callback.onError(e);
        }
    }

    private String getCacheData(HttpRequestProvider provider, String url) {
        Cache.Entry entry = provider.getRequestQueue().getCache().get(url);
        if(entry != null) {
            return new String(entry.data);
        }
        return null;
    }
}