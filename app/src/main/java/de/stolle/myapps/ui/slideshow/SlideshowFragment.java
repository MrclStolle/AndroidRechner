package de.stolle.myapps.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import de.stolle.myapps.R;
import de.stolle.myapps.ui.OracleDBConnection;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    ArrayList<String[]>  result;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        TextView textView = root.findViewById(R.id.text_slideshow);

        result = (OracleDBConnection.OracleQuery("Select * From DEMO_CUSTOMERS"));
        textView.setText(result.get(0)[1]); //gibt den wert aus zeile 1, spalte 2 aus
        return root;
    }
}