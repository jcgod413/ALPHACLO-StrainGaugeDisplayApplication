package alphaclo.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

public class HttpTask extends AsyncTask<String, Void, String> {

    IHttpRecvCallback m_cb;

    /**
     * HttpTask
     * @param cb
     */
    public HttpTask(IHttpRecvCallback cb)  {
        m_cb = cb;
    }

    /**
     * doInBackground
     * @param params
     * @return
     */
    protected String doInBackground(String... params)  {
        InputStream is = null;
        if( params[0].equals("GET") == true )  {
            is = getFromUrl(params[1]);
        }
        else if( params[0].equals("POST") )    {
            is = postFromUrl(params[1], params[2]);
        }
        String result = convertInputStreamToString(is);

        return result;
    }

    /**
     * onPostExecute
     * @param result
     */
    protected void onPostExecute(String result) {
        if( m_cb != null )  {
            m_cb.onRecv(result);
            Log.d("onPostExecute", result);
            return;
        }
    }

    /**
     * getFromUrl
     * @param url
     * @return
     */
    public InputStream getFromUrl(String url)   {
        InputStream content = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(new HttpGet(url));
            if( response.getStatusLine().getStatusCode() != 200 )   {
                // 네트워크 오류입니다
                Log.d("Hello", "Network Error");
            }
            content = response.getEntity().getContent();
        } catch (Exception e)   {
            Log.d("[GET REQUEST]", "Network exception", e);
        }
        return content;
    }

    /**
     * postFromUrl
     * @param url
     * @param message
     * @return
     */
    public InputStream postFromUrl(String url, String message)  {
        InputStream content = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            post.setEntity(new StringEntity(message, "UTF8"));
            post.setHeader("Content-Type", "application/json");
        } catch(Exception e)    {
            e.printStackTrace();
        }

        try {
            HttpResponse response = httpClient.execute(post);
            if( response == null )  {
                Log.d("HttpTask", "httpClient error. response if null");
            }
            content = response.getEntity().getContent();
        } catch (Exception e)   {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * convertInputStreamToString
     * @param is
     * @return
     */
    private static String convertInputStreamToString(InputStream is)    {
        if( is == null )
            return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch(IOException e)  {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
