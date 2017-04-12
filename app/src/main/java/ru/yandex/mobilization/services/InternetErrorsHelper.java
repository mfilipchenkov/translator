package ru.yandex.mobilization.services;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

public class InternetErrorsHelper {
    public static String getExceptionDescription(Exception e) {
        if(e instanceof NoConnectionError) {
            return "Не удалось установить соединение";
        }
        if(e instanceof VolleyError) {
            VolleyError vException = (VolleyError) e;
            if(vException.networkResponse.statusCode == 403) return "Ошибка авторизации";
            return "Ошибка сети";
        }
        return e.getMessage().isEmpty() ? "Ошибка сети" : e.getMessage();
    }
}
