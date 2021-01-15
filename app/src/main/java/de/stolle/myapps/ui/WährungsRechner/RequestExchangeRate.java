package de.stolle.myapps.ui.WährungsRechner;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestExchangeRate{

    String symbols = "USD,AUD,CAD,PLN,MXN";
        String param = "latest";

    public RequestExchangeRate(String symbols) {
        this.symbols = symbols;
    }

    protected String GetString() {
        String fixerurl = "http://data.fixer.io/api/";
        String access = "?access_key=";
        String apiKey ="755dccb9711de847643fb6fb4636202c";
        String symbolsPre ="&symbols=";

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(fixerurl + param + access + apiKey + symbolsPre + symbols);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
