package de.stolle.myapps.ui.WährungsRechner;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.stolle.myapps.R;

import static de.stolle.myapps.ui.WechselgeldRechner.WechselGFragment.TryParseDouble;

public class WaehrungsRFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    //TODO low prio: anordnung der Textfelder ändern, von nebeneinander, zu untereinander
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    Spinner spinnerL;
    Spinner spinnerR;
    EditText decimalL;
    EditText decimalR;
    Button swapSpinner;
    TextView date;
    TextView testView;
    Double[] werte;
    CurrentRates currentRates;
    /*  //url-konstruktion
        String fixerurl = "http://data.fixer.io/api/";
        String param = "latest";
        String access = "?access_key=";
        String apiKey = "755dccb9711de847643fb6fb4636202c";
        String symbolsPre = "&symbols=";
        String symbols = "USD,TRY,JPY,BTC,CHF,UGX";
        String format = "&format=1";
        String urlString = fixerurl + param + access + apiKey + symbolsPre + symbols + format;
     */
    String url;
    String accesskey;
    String[] waehrungenkurz;
    String placeholderSymbols = "&symbols=";
    private WaehrungsRViewModel waehrungsRViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        waehrungsRViewModel =
                new ViewModelProvider(this).get(WaehrungsRViewModel.class);
        View root = inflater.inflate(R.layout.fragment_waehrungsrechner, container, false);

        //get static strings
        url = getResources().getString(R.string.url);
        accesskey = getResources().getString(R.string.accesskey);
        waehrungenkurz = getResources().getStringArray(R.array.waehrungenkurz);


        //url konstruktion
        for (int i = 0; i < waehrungenkurz.length; i++) {
            placeholderSymbols += waehrungenkurz[i];
            if (i != waehrungenkurz.length - 1)
                placeholderSymbols += ",";
        }
        url = url.replace("_PHAC_", accesskey);
        url = url.replace("_PHS_", placeholderSymbols);

        //send request and implement into app/place values, creates currentRates
        System.out.println(url);
        RequestAndSaveRates(url);

        decimalL = root.findViewById(R.id.decimalL);
        decimalR = root.findViewById(R.id.decimalR);
        date = root.findViewById(R.id.Date);
        date.setText(currentRates.date);
        testView = root.findViewById(R.id.testView);

        //v arbeitet mit dem richtigen seperator je sprache, aber unter DE wird die qwertz-tastatur angezeigt und kein nummernfeld
        //decimalL.setKeyListener(DigitsKeyListener.getInstance(Locale.getDefault(),false, true));

        //sucht den richtigen seperator der eingestellten sprache raus
        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = decFormat.getDecimalFormatSymbols();
        final String defaultSeperator = Character.toString(symbols.getDecimalSeparator());


        decimalL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //begrenzt die erlaubten zeichen je na vorhanden sein des seperators
                if (s.toString().contains(defaultSeperator))
                    decimalL.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    decimalL.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));


                Calc();
            }
        });

        swapSpinner = root.findViewById(R.id.swapSpinnerBtn);
        swapSpinner.setOnClickListener(this::BtSwapSpinner);

        //richtet die beiden spinner ein
        spinnerL = root.findViewById(R.id.spinnerL);
        spinnerR = root.findViewById(R.id.spinnerR);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.waehrungenkurz, R.layout.spinner_style);

        spinnerL.setAdapter(spinnerAdapter);
        spinnerR.setAdapter(spinnerAdapter);

        spinnerL.setOnItemSelectedListener(this);
        spinnerR.setOnItemSelectedListener(this);
        spinnerR.setSelection(1);


        Calc();

        return root;
    }

    private void RequestAndSaveRates(String url) {
        String result = null;

        //abfrage der Rates über url
        try {
            result = new JsonTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //verarbeiten des gesendeten strings(json)
        if (result != null) {
            try {
                currentRates = new CurrentRates(result, waehrungenkurz);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            werte = new Double[waehrungenkurz.length];
            int index = 0;
            for (String symbol : waehrungenkurz
            ) {
                werte[index] = currentRates.RatesTable.get(symbol);
                index++;
            }


        } else {
            //wenn result==NULL, dann sollen standardwerte geladen werden
            werte = new Double[getResources().getStringArray(R.array.ersatzwerteBaseEuro).length];
            int index = 0;
            for (String wert : getResources().getStringArray(R.array.ersatzwerteBaseEuro)
            ) {
                werte[index] = Double.parseDouble(wert);
                index++;
            }

        }
    }

    private void BtSwapSpinner(View view) {
        int tempInt = spinnerL.getSelectedItemPosition();
        spinnerL.setSelection(spinnerR.getSelectedItemPosition());
        spinnerR.setSelection(tempInt);
        Calc();
    }

    private void Calc() {

        double input = TryParseDouble(decimalL.getText().toString().replace(",", "."), 0d);
        //System.out.println("inputI " + input);

        long inputI = (long) (input * 1000);


        double valueLD = werte[(int) spinnerL.getSelectedItemId()];
        double valueRD = werte[(int) spinnerR.getSelectedItemId()];

        long result = (long) ((inputI * valueRD) / valueLD);
        //System.out.println("result " + result);

        decimalR.setText(nf.format((double) result / 1000));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Calc();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}

