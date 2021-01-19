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
    String date;
    Rates rates;
    Error error;

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public CurrentRates(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.success =jsonObject.getBoolean("success");

        String dateString = jsonObject.getString("date");
        String[] dateSplit = dateString.split("-");
        this.date= dateSplit[2] +"."+ dateSplit[1] +"."+ dateSplit[0];
        //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // .ofPattern verlangt API-Version 26
        //LocalDateTime myDateObj = LocalDateTime.parse(dateString,myFormatObj);
        //this.date = myDateObj;

        if (success){
            this.base= jsonObject.getString("base");
            JSONObject ratesObj = jsonObject.getJSONObject("rates");
            System.out.println(ratesObj);

            rates = new Rates(ratesObj.getString("USD"),
                    ratesObj.getString("TRY"),
                    ratesObj.getString("JPY"),
                    ratesObj.getString("BTC"),
                    ratesObj.getString("CHF"),
                    ratesObj.getString("UGX")

            );
            System.out.println(rates.USD);
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
        String USD;
        String TRY;
        String JPY;
        String BTC;
        String CHF;
        String UGX;

        public Rates(String USD, String TRY, String JPY, String BTC, String CHF, String UGX) {
            this.USD = USD;

            this.TRY = TRY;
            this.JPY = JPY;
            this.BTC = BTC;
            this.CHF = CHF;
            this.UGX = UGX;
        }
    }


}
