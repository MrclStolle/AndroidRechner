package de.stolle.myapps.ui.WährungsRechner;

import android.content.res.Configuration;
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

import de.stolle.myapps.ui.WechselgeldRechner.WechselGFragment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import de.stolle.myapps.R;
import de.stolle.myapps.ui.WechselgeldRechner.WechselGFragment;

import static de.stolle.myapps.ui.WechselgeldRechner.WechselGFragment.TryParseDouble;

public class WaehrungsRFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    //TODO low prio: anordnung der Textfelder ändern, von nebeneinander, zu untereinander
    private WaehrungsRViewModel waehrungsRViewModel;
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

    Spinner spinnerL;
    Spinner spinnerR;
    EditText decimalL;
    EditText decimalR;
    Button swapSpinner;
    TextView testView;

    String[] werte;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        waehrungsRViewModel =
                new ViewModelProvider(this).get(WaehrungsRViewModel.class);
        View root = inflater.inflate(R.layout.fragment_waehrungsrechner, container, false);

        werte = getResources().getStringArray(R.array.werteBaseEuro);

        decimalL = root.findViewById(R.id.decimalL);
        decimalR = root.findViewById(R.id.decimalR);
        testView = root.findViewById(R.id.testView);

        //arbeitet mit dem richtigen seperator je sprache, aber unter DE wird die qwertz-tastatur angezeigt und kein nummernfeld
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
                if(s.toString().contains(defaultSeperator))
                    decimalL.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                else
                    decimalL.setKeyListener(DigitsKeyListener.getInstance("0123456789" + defaultSeperator));


                Calc();
            }
        });


        spinnerL = root.findViewById(R.id.spinnerL);
        spinnerR = root.findViewById(R.id.spinnerR);

        swapSpinner = root.findViewById(R.id.swapSpinnerBtn);
        swapSpinner.setOnClickListener(this::BtSwapSpinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),R.array.waehrungen, R.layout.spinner_style);

        spinnerL.setAdapter(spinnerAdapter);
        spinnerR.setAdapter(spinnerAdapter);

        spinnerL.setOnItemSelectedListener(this);
        spinnerR.setOnItemSelectedListener(this);
        spinnerR.setSelection(1);

        Calc();


        System.out.println(RequestExchangeRate.GetXMLString());

        return root;
    }

    private void BtSwapSpinner(View view) {
    int tempInt = spinnerL.getSelectedItemPosition();
    spinnerL.setSelection(spinnerR.getSelectedItemPosition());
    spinnerR.setSelection(tempInt);
    Calc();
    }

    private void Calc(){

        double input = TryParseDouble(decimalL.getText().toString().replace(",","."),0d);
        System.out.println("inputI " + input);

        long inputI = (long) (input*1000);

        double valueLD = TryParseDouble(werte[(int)spinnerL.getSelectedItemId()],0d);
        double valueRD = TryParseDouble(werte[(int)spinnerR.getSelectedItemId()],0d);

        long result = (long)((inputI * valueRD)/valueLD);
        System.out.println("result " + result);

        decimalR.setText(nf.format((double)result / 1000));
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Calc();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}

