package com.vgs.android.vgs_cardform_demo;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Mobile_BackEndClient {

    public URL baseURL;
    private MobileBE_UICallback responseHandler;

    public Mobile_BackEndClient(URL baseURL) {
        this.baseURL = baseURL;

    }

    public void persistSensitive(final String payload, MobileBE_UICallback responseHandler) {
        this.responseHandler = responseHandler;

        mbeRequestTask requestTask = new mbeRequestTask();
        requestTask.execute(payload);
    }


    private class mbeResponse {
        public String result;

        public MobileBE_Error error;

        public mbeResponse() {
            result = null;
        }

        public mbeResponse(String token) {
            this.result = token;
        }

        public mbeResponse(MobileBE_Error error) {
            this.error = error;
        }
    }


    class mbeRequestTask extends AsyncTask<String, Void, mbeResponse> {

        @Override
        protected mbeResponse doInBackground(String... params) {
            mbeResponse response = new mbeResponse();
            final String payload = params[0];
            JSONObject json = new JSONObject();

            try {
                json = new JSONObject(payload);
            } catch (JSONException e) {
                e.printStackTrace();

            }

            try {
                response = makeMBERequest("POST", "/post", json);
            } catch (IOException e) {
                e.printStackTrace();
                response.error = MobileBE_Error.ServerError;
            }
            return response;
        }

        @Override
        protected void onPostExecute(mbeResponse response) {

            if (response.result != null) {
                try {
                    JSONObject json = new JSONObject(response.result);

                    responseHandler.onSuccess(json.getString("data"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    responseHandler.onFailure(MobileBE_Error.InvalidData);
                }

            } else {
                responseHandler.onFailure(response.error);
            }
        }
    }


    private mbeResponse makeMBERequest(String method, String path, JSONObject json) throws IOException {
        URL requestURL = new URL(baseURL.toExternalForm() + path);
        HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        //TODO define whatever headers your application requires
        //conn.setRequestProperty("User-Agent", String.format("MBE-Android-SDK/%s (build %s)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_NAME));
        conn.setRequestMethod(method);
        conn.connect();

        OutputStream os = conn.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(json.toString());
        osw.flush();
        osw.close();


        int status = conn.getResponseCode();


        switch (status) {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_CREATED:
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } finally {
                    br.close();
                }
                return new mbeResponse(sb.toString());
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                return new mbeResponse(MobileBE_Error.Unauthorized);
            case HttpURLConnection.HTTP_BAD_REQUEST:
                return new mbeResponse(MobileBE_Error.BadRequest);
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                return new mbeResponse(MobileBE_Error.ServerError);
            default:
                return new mbeResponse(MobileBE_Error.BadRequest);
        }

    }

}

enum MobileBE_Error {
    NoData, InvalidData, BadRequest, ServerError, Unauthorized
}

interface MobileBE_UICallback {

    void onSuccess(String token);

    void onFailure(MobileBE_Error error);
}