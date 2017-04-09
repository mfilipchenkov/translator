package ru.yandex.mobilization.interfaces;

// Интерфейс, описывающий функции обратного вызова для обработки ответов от YandexApiService
public interface IRequestCallback {
    void onSuccess(Object result);
    void onError(Exception e);
}
