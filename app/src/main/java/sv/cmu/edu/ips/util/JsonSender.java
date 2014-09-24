package sv.cmu.edu.ips.util;

/**
 * Created by sumeet on 9/19/14.
 */

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;


public class JsonSender {

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
                    Log.w("IPS JsonSender","Success - "+response);
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("IPS JsonSender","Failure in sending - "+ "Status code -" +statusCode+ " Error response -"+  errorResponse);
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
}