package sv.cmu.edu.ips.util;

/**
 * Created by sumeet on 9/19/14.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;


public class IPSHttpClient {

    // http://loopj.com/android-async-http/
    public static String sendToServer(String data, Context context ,String server_url) {
        String result = "success";
        try {
            StringEntity entity = new StringEntity(data);

            Log.w("IPS JsonSender ", data);
            AsyncHttpClient client = new AsyncHttpClient();

            client.post(context, server_url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.w("IPS JsonSender result","Success - "+response);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("IPS JsonSender result","Failure in sending - "+ "Status code -" +statusCode+ " Error response -"+  errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = "failed : + " + e.getMessage();
            Log.d("IPS JsonSender",e.getMessage());
        }

//        data.Clean();
        return result;
    }

    public static String getDataFromServer(final Context context ,String server_url) {
        String response = "";
        try {

            Log.w("IPS getDataFromServer ", server_url);
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(context, server_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.w("IPS JsonSender", "Success - " + response);
                    Intent intent = new Intent("new-location-event");
                    intent.putExtra("message", response);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("IPS JsonSender", "Failure in sending - " + "Status code -" + statusCode + " Error response -" + errorResponse);
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "failed : + " + e.getMessage();
            Log.d("IPS JsonSender",e.getMessage());
        }

        return response;
    }
}