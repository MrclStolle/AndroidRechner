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
import java.util.Hashtable;

import static java.lang.Long.getLong;

public class CurrentRates {
    boolean success;
    String base;
    String date;
    Hashtable<String, Double> RatesTable;
    Error error;

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public CurrentRates(String jsonString, String[] waehrungenkurz) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.success =jsonObject.getBoolean("success");

        //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // .ofPattern verlangt API-Version 26
        //LocalDateTime myDateObj = LocalDateTime.parse(dateString,myFormatObj);
        //this.date = myDateObj;

        if (success){
            //datum formatieren
            String dateString = jsonObject.getString("date");
            String[] dateSplit = dateString.split("-");
            this.date= dateSplit[2] +"."+ dateSplit[1] +"."+ dateSplit[0];

            this.base= jsonObject.getString("base");
            JSONObject ratesObj = jsonObject.getJSONObject("rates");
            System.out.println("ratesObject== " + ratesObj);

            RatesTable = new Hashtable<String, Double>();
            for (String symbol : waehrungenkurz
                    ) {
                //System.out.println("symbol== "+symbol);
                //System.out.println("symbolsRate== "+ratesObj.getString(symbol));
                RatesTable.put(symbol, Double.parseDouble(ratesObj.getString(symbol)));
            }


            System.out.println(RatesTable.get("USD"));

        }else  {
            JSONObject errorobj = jsonObject.getJSONObject("error");
            this.error = new Error(errorobj.getInt("code"),
                    errorobj.getString("info"),
                    errorobj.getString("type")
            );

        }

    }

    class Error {
        int code;
        String info;
        String type;

        public Error(int code, String info, String type) {
            this.code = code;
            this.info = info;
            this.type=type;
        }
    }



}
