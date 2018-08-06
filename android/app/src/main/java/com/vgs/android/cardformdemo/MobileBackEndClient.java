package com.vgs.android.cardformdemo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MobileBackEndClient {

    public URL baseURL;
    public String mbe_endpoint;
    private MobileBeUICallback responseHandler;

    public MobileBackEndClient(URL baseURL, String mbe_endpoint) {
        this.baseURL = baseURL;
        this.mbe_endpoint = mbe_endpoint;

    }

    public void persistSensitive(final String payload, MobileBeUICallback responseHandler) {
        this.responseHandler = responseHandler;

        mbeRequestTask requestTask = new mbeRequestTask();
        requestTask.execute(payload);
    }


    private class mbeResponse {

        public String result;
        public MobileBeError error;

        public mbeResponse() {
            result = null;
        }

        public mbeResponse(String token) {
            this.result = token;
        }

        public mbeResponse(MobileBeError error) {
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
                Log.e(this.getClass().getName(),e.getLocalizedMessage());
            }

            try {
                response = makeMBERequest("POST", mbe_endpoint, json);
            } catch (IOException e) {
                Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                response.error = MobileBeError.ServerError;
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
                    Log.e(this.getClass().getName(),e.getLocalizedMessage());;
                    responseHandler.onFailure(MobileBeError.InvalidData);
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

        // Here: define whatever headers your application requires
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
                return new mbeResponse(MobileBeError.Unauthorized);
            case HttpURLConnection.HTTP_BAD_REQUEST:
                return new mbeResponse(MobileBeError.BadRequest);
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                return new mbeResponse(MobileBeError.ServerError);
            default:
                return new mbeResponse(MobileBeError.BadRequest);
        }

    }

}

//Some basic failures for VGS proxy:
enum MobileBeError {
    NoData, InvalidData, BadRequest, ServerError, Unauthorized
}

//Basic callback method for UI
interface MobileBeUICallback {
    void onSuccess(String token);

    void onFailure(MobileBeError error);
}