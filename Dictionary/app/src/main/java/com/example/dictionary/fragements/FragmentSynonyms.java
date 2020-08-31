package com.example.dictionary.fragements;


        import android.content.Context;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.fragment.app.Fragment;

        import com.example.dictionary.R;
        import com.example.dictionary.WordMeaningActivity;

public class FragmentSynonyms extends Fragment {
    public FragmentSynonyms()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_definition,container, false);
        Context context = getActivity();
        TextView text = (TextView) view.findViewById(R.id.textViewD);
        String synonyms = ((WordMeaningActivity)context).synonyms;
        text.setText(synonyms);
        if(synonyms == null)
        {
            text.setText("No synonyms found");
        }
        return view;

    }
}
