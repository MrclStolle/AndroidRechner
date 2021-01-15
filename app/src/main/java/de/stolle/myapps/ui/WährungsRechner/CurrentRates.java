package de.stolle.myapps.ui.WährungsRechner;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.CertificateRevokedException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.lang.Long.getLong;

public class CurrentRates {
    boolean success;
    String base;
    LocalDateTime date;
    Rates rates;
    Error error;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CurrentRates(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.success =jsonObject.getBoolean("success");

        String dateString = jsonObject.getString("date");

        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime myDateObj = LocalDateTime.parse(dateString,myFormatObj);
        this.date = myDateObj;

        if (success){
            this.base= jsonObject.getString("base");
            JSONObject ratesObj = jsonObject.getJSONObject("rates");
            rates = new Rates(ratesObj.getLong("USD"),
                    ratesObj.getLong("GPD"),
                    ratesObj.getLong("TRY"),
                    ratesObj.getLong("JPY"),
                    ratesObj.getLong("BTC"),
                    ratesObj.getLong("CHF"),
                    ratesObj.getLong("UGX")
            );
        }else  {
            JSONObject errorobj = jsonObject.getJSONObject("error");
            this.error = new Error(errorobj.getInt("code"),
                    errorobj.getString("info")
            );
        }

    }

    class Error {
        int code;
        String info;

        public Error(int code, String info) {
            this.code = code;
            this.info = info;
        }
    }

    class Rates {
        long USD;
        long GPD;
        long TRY;
        long JPY;
        long BTC;
        long CHF;
        long UGX;

        public Rates(long USD, long GPD, long TRY, long JPY, long BTC, long CHF, long UGX) {
            this.USD = USD;
            this.GPD = GPD;
            this.TRY = TRY;
            this.JPY = JPY;
            this.BTC = BTC;
            this.CHF = CHF;
            this.UGX = UGX;
        }
    }
}
