package de.stolle.myapps.ui.WechselgeldRechner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import de.stolle.myapps.R;

public class WechselGFragment extends Fragment {

    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    Random random = new Random();
    String[] Noten;
    int minNoten = 5;
    int maxNoten = 30;
    int[] mengeNoten;

    EditText PreisET;
    EditText GebeGeldET;
    TextView WechselgeldWertTV;
    TextView WechelgeldScheineTV;
    Switch AutomatOnOff;
    Button RandomizeBT;
    LinearLayout LayoutGeldnoten;
    LinearLayout LayoutVorhandeneGeldnoten;
    TextView vorhandeneNotenList;
    TextView InfoZeile;

    long rueckgeld = 0;

    private WechselGViewModel wechselGViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wechselGViewModel = new ViewModelProvider(this).get(WechselGViewModel.class);
        View view = inflater.inflate(R.layout.fragment_wechselg, container, false);


        Noten = getResources().getStringArray(R.array.Euro);
        PreisET = view.findViewById(R.id.ptPreis);
        GebeGeldET = view.findViewById(R.id.ptGebegeld);
        WechselgeldWertTV = view.findViewById(R.id.twWechselgeldWert);
        WechelgeldScheineTV = view.findViewById(R.id.twWechselgeldScheine);
        AutomatOnOff = view.findViewById(R.id.automatSwitch);
        RandomizeBT = view.findViewById(R.id.randomizeBT);
        mengeNoten = GenerateArrayRandomNumbers(Noten.length, minNoten, maxNoten); //TODO array wird gelesen, auch trotz ausgeschaltetem switch
        LayoutGeldnoten = view.findViewById(R.id.LayoutGeldnoten);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) LayoutGeldnoten.getLayoutParams();
        vorhandeneNotenList = view.findViewById(R.id.vorhandeneNotenListTV);
        InfoZeile = view.findViewById(R.id.infoTV);

        LayoutVorhandeneGeldnoten = view.findViewById(R.id.LayoutVorhandeneGeldnoten);

        //besorgt den richtigen decimal-seperator der aktuellen spracheinstellung
        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = decFormat.getDecimalFormatSymbols();
        final String defaultSeperator = Character.toString(symbols.getDecimalSeparator());

        TextWatcher OnChangeCalc = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {

                Double preisD = TryParseDouble(PreisET.getText().toString().replace(',', '.'), 0d);
                long preis = (long) (preisD * 100);

                Double gebeD = TryParseDouble(GebeGeldET.getText().toString().replace(',', '.'), 0d);
                long gebe = (long) (gebeD * 100);

                rueckgeld = WechselgeldCalc(gebe, preis);

                WechselgeldWertTV.setText(nf.format((double) rueckgeld / 100));
                CalcGeldnoten();
            }
        };

        PreisET.addTextChangedListener(OnChangeCalc);
        PreisET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(defaultSeperator))
                    PreisET.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    PreisET.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));
            }
        });
        GebeGeldET.addTextChangedListener(OnChangeCalc);
        GebeGeldET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(defaultSeperator))
                    GebeGeldET.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    GebeGeldET.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));
            }
        });

        AutomatOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    RandomizeBT.setEnabled(true);
                    //layoutParams.weight = 1;
                    LayoutVorhandeneGeldnoten.setVisibility(View.VISIBLE);
                    InfoZeile.setVisibility(View.VISIBLE);
                    vorhandeneNotenList.setText(GetVorhandeneNoten());

                } else {
                    RandomizeBT.setEnabled(false);
                    //layoutParams.weight = 0;
                    LayoutVorhandeneGeldnoten.setVisibility(View.GONE);
                    InfoZeile.setVisibility(View.GONE);
                    vorhandeneNotenList.setText("");
                }
                CalcGeldnoten();
            }
        });

        RandomizeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mengeNoten = GenerateArrayRandomNumbers(Noten.length, minNoten, maxNoten);
                vorhandeneNotenList.setText(GetVorhandeneNoten());
                CalcGeldnoten();
            }
        });


        return view;
    }

    private long WechselgeldCalc(long geben, long preis) {

        long wechselgeldwert = geben - preis;

        if (wechselgeldwert < 0) {
            wechselgeldwert = 0;
        }
        return wechselgeldwert;
    }

    static public double TryParseDouble(String value, Double defaultvalue) {

        if (TryParseDouble(value))
            return Double.parseDouble(value);
        else
            return defaultvalue;
    }

    static public boolean TryParseDouble(String value) {

        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void CalcGeldnoten() {
        String ResultStringNoten = "";
        long rueckgeldTemp = rueckgeld;

        /*
        for (String note : Noten
        ) {
            int noteI = Integer.parseInt(note);
            if (rueckgeldTemp >= noteI) {
                //System.out.println(note);

                long count = (int) (rueckgeldTemp / noteI);
                //System.out.println("count "+count);

                rueckgeldTemp = rueckgeldTemp - (noteI * count);
                //System.out.println("remaining wert " + wert);

                ResultStringNoten += nf.format(count) + "x " + nf.format((double)noteI/100) + "€\n";
            }

        }
        */

        for (int i = 0; i < Noten.length; i++) {
            int noteI = Integer.parseInt(Noten[i]);

            if (rueckgeldTemp >= noteI) {
                //System.out.println(note);

                long count = (int) (rueckgeldTemp / noteI);
                //System.out.println("count "+count);
                if (mengeNoten[i] < count && AutomatOnOff.isChecked()) {
                    count = mengeNoten[i];
                }

                rueckgeldTemp = rueckgeldTemp - (noteI * count);
                //System.out.println("remaining wert " + wert);

                ResultStringNoten += nf.format(count) + "x " + nf.format((double) noteI / 100) + "€\n";
            }
        }


        if (rueckgeldTemp != 0) {
            InfoZeile.setText("\nDie Menge an Scheinen/Münzen hat nicht genügt\nVerbliebenes Rückgeld: " + nf.format((double) rueckgeldTemp / 100) + "€");
        }else{
            InfoZeile.setText("");
        }

        WechelgeldScheineTV.setText(ResultStringNoten);
    }

    private int[] GenerateArrayRandomNumbers(int size, int min, int max) {
        int[] intarray = new int[size];

        for (int i = 0; i < size; i++) {
            intarray[i] = random.nextInt(max - min) + min;
        }
        return intarray;
    }

    private String GetVorhandeneNoten() {
        String vorhandeneNoten="";

        for (int i = 0; i < Noten.length; i++) {
            int noteI = Integer.parseInt(Noten[i]);
                vorhandeneNoten += nf.format(mengeNoten[i]) + "x " + nf.format((double) noteI / 100) + "€\n";
            }


        return vorhandeneNoten;
    }
}