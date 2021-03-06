package com.dh.foundation.utils;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dh.foundation.exception.DataFormatError;
import com.dh.foundation.exception.NetRequestError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 网络请求调用类
 * Created By: Seal.Wu
 * Date: 2015/10/20
 * Time: 16:11
 */
class NetRequest<ReturnObj> extends StringRequest {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 网络请求参数
     */
    private final RequestParams requestParams;


    public NetRequest(int method, String url, RequestParams requestParams, Type returnType, HttpNetUtils.HttpJsonRequest<ReturnObj> requestListener) {

        super(method, url, new Listener<ReturnObj>(returnType, requestListener), new ErrorListener(requestListener));

        this.requestParams = requestParams;

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(30 * 1000, 0, 1);

        setRetryPolicy(retryPolicy);

        setShouldCache(false);

        Object tag = getTag(method, url, requestParams);

        setTag(tag);
    }

    private static String getTag(int method, String url, RequestParams requestParams) {

        return method == Method.GET ? url : url + requestParams;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        if (requestParams.getHeaders() != null) {

            return requestParams.getHeaders();
        }
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        return requestParams.getParams();
    }

    @Override
    protected String getParamsEncoding() {
        return requestParams.getParamsEncoding();
    }

    private static class Listener<ReturnObj> implements Response.Listener<String> {

        /**
         * 返回对象数据类型
         */
        private final Type returnType;

        /**
         * 返回数据监听接口
         */
        private final HttpNetUtils.HttpJsonRequest requestListener;

        public Listener(Type returnType, HttpNetUtils.HttpJsonRequest<ReturnObj> requestListener) {

            this.returnType = returnType;

            this.requestListener = requestListener;
        }

        @Override
        public void onResponse(final String response) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    Gson gson = new Gson();

                    ReturnObj o;

                    try {

                        o = gson.fromJson(response, returnType);

                    } catch (JsonSyntaxException e) {

                        DLoggerUtils.e(e);

                        requestListener.onFailed(new DataFormatError(e));

                        requestListener.onFinished();

                        return;
                    }

                    requestListener.onSuccess(o);

                    requestListener.onFinished();
                }
            });
        }
    }


    private static class ErrorListener implements Response.ErrorListener {

        /**
         * 返回数据监听接口
         */
        private final HttpNetUtils.HttpJsonRequest requestListener;

        public ErrorListener(HttpNetUtils.HttpJsonRequest requestListener) {

            this.requestListener = requestListener;
        }

        @Override
        public void onErrorResponse(final VolleyError error) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    DLoggerUtils.e(error);

                    requestListener.onFailed(new NetRequestError(error));

                    requestListener.onFinished();
                }
            });
        }
    }
}
