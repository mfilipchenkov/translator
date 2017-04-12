package ru.yandex.mobilization.interfaces;

// Интерфейс, описывающий методы обратного вызова для обработки ответов от YandexApiService
public interface IRequestCallback {
    // Метод обратного вызоыва в случае успешного
    void onSuccess(Object result);
    void onError(Exception e);
}
