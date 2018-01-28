package org.seemsGood.shara.util;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestHandler {

    private static RequestHandler instance;
    private RequestQueue requestQueue;
    private static Context context;

    private RequestHandler(Context ctx){
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestHandler getInstance(Context ctx){
        if(instance==null){
            instance = new RequestHandler(ctx);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public RequestHandler cancelAll(){
        getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
