package com.github.llchen.utils;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author llchen12
 * @date 2018/6/20
 */
public class HttpUtil {


    private static OkHttpClientPool pool = new OkHttpClientPool();

    static {
        pool.setMinIdle(1);
        pool.setMaxIdle(2);
        pool.setMaxTotal(50);
        pool.setMaxWaitMillis(2000);
    }


    public static String doGet(String url, Map<String, Object> params) {

        StringBuilder sb = new StringBuilder(url);
        if (params != null) {
            if (!url.contains("?")) {
                sb.append("?");
            } else {
                if (!url.endsWith("?")) {
                    sb.append("&");
                }
            }

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        Request request = new Request.Builder()
                .url(sb.toString())
                .get()
                .build();

        String res = null;
        OkHttpClient client = null;
        try {
            client = pool.getClient();
            Response response = client.newBuilder()
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .execute();
            res = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.returnClient(client);
        }
        return res;
    }

    /**
     * 上传文件和参数
     *
     * @param url
     * @param params
     * @param file
     * @param callback
     * @throws IOException
     */
    public static void doPost(String url, Map<String, Object> params, File file, Callback callback) throws IOException {

        OkHttpClient client = pool.getClient();
        // form 表单形式上传
        MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("application/zip"), file);
            String fileName = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            multipartBody.addFormDataPart("jsonFile", fileName, body);
        }

        if (params != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                multipartBody.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody.build())
                .build();

        client.newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
                .newCall(request)
                .enqueue(callback);
    }


    /**
     * 上传文件和参数
     *
     * @param url
     * @param params
     * @param file
     * @throws IOException
     */
    public static String doPost(String url, Map<String, Object> params, File file) {

        OkHttpClient client = pool.getClient();
        // form 表单形式上传
        MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("application/*"), file);
            String fileName = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            multipartBody.addFormDataPart("jsonFile", fileName, body);
        }

        if (params != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                multipartBody.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody.build())
                .build();

        String res = null;
        try {
            Response response = client.newBuilder()
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .execute();
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.returnClient(client);
        }

        return res;
    }


    public static void main(String[] args) {
        String s = doGet("https://blog.csdn.net/xx326664162/article/details/77714123", null);
        System.out.println(s);
    }
}
