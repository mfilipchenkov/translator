package ru.yandex.mobilization.interfaces;

// Интерфейс, описывающий методы обратного вызова для обработки ответов от YandexApiService
public interface IRequestCallback {
    void onSuccess(Object result);
    void onError(Exception e);
}
