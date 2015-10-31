package yourPackage Here; //EDIT HERE

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.roguelabs.clanmanager.app.config; //DEfines URL 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 
 */
public class NetConnect {
    static Boolean sync = false;
    static String calu = null;
    static Context context;
    static Map<String,Object> params;
    static ProgressDialog pDialog;
    public NetConnect(Context conx, Map<String,Object> _params){
        context = conx;
        params = new LinkedHashMap<>();
        params = _params;
        try {
            calu = new netco().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (calu != null)
           Log.w("THE RESPONSE VALUE", calu);
        else Log.e("NetConnect()", "Task Response is NULL");


    }



    public String getRes(){
        if (calu != null) {
            Log.w("THE RESPONSE VALUE", calu);
            //stopTask();
            return calu;
        }
        else Log.e("NetConnect()", "Task Response is NULL");
        return "Task Returns NULL. Read from cal";
    }


    public class netco extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            sync = false;
            Log.w("Sync", "FALSE");
            Handler handler =  new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    //Toast.makeText(context, "Connection Timeout. Please Try again!", Toast.LENGTH_LONG).show();
                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Loading...");
                    pDialog.setTitle("Please Wait");
                    pDialog.setCancelable(false);
                    //pDialog.setIndeterminate(true);
                    pDialog.show();
                }
            });
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            sync = true;
            Log.w("Sync", "TRUE");
            pDialog.dismiss();
            if (s != null)
                Log.w("PostExecute", "s: "+s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String output = "NOTHING READ";
            try {
                output = connec();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("doinBG", "MalformedURLEx: "+e.toString());
            }

            return output;
        }

        private String connec()  {
            String output = "connec INITIAL VALUE";
            //Map<String,Object> params = new LinkedHashMap<>();
            //params.put("tag", "sda");

            try {
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String postdata = postData.toString();
                Log.w("postData", postdata);
                byte[] postDataBytes = postData.toString().getBytes();
                Log.w("postDataBytes", String.valueOf(postDataBytes));

                URL url = new URL(config.URL_REGISTER);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(1000 * 10);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                /*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED){
                    Log.e("EROR", "HttpResp Msg: "+conn.getResponseMessage());
                }   */
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                //output = br.readLine();
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    return output;
                }

                conn.disconnect();


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e("connec()", "UnsupportedEncodingException: " + e.toString());
            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.e("connec()", "ProtocolException: " + e.toString());
            }  catch (IOException e) {
                e.printStackTrace();
                Log.e("connec()", "IOException: " + e.toString());
            }
            return output;
        }

    }

    //public class testcla extends

}
