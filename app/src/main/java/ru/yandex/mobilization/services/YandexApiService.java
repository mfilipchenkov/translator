package ru.yandex.mobilization.services;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.ArrayList;

import ru.yandex.mobilization.R;
import ru.yandex.mobilization.interfaces.IRequestCallback;
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
                    callback.onSuccess(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );

        provider.addToQueue(request);
    }
}