package ru.yandex.mobilization.providers;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpRequestProvider {
    private static HttpRequestProvider instance;
    private RequestQueue queue;
    private Context context;

    private HttpRequestProvider(Context context) {
        this.context = context;
        this.queue = getRequestQueue();
    }

    public static synchronized HttpRequestProvider getInstance(Context context) {
        if(instance == null) {
            instance = new HttpRequestProvider(context);
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(this.queue == null) {
            this.queue = Volley.newRequestQueue(this.context);
            this.queue.start();
        }
        return this.queue;
    }

    public <T> void addToQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}