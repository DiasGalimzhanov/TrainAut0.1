package com.example.trainaut01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trainaut01.R;
import com.example.trainaut01.enums.Language;

public class LanguageSpinnerAdapter extends ArrayAdapter<Language> {

    private final LayoutInflater inflater;
    private final int resource;
    private final int dropdownResource;
    private final Language[] languages;

    public LanguageSpinnerAdapter(@NonNull Context context, @NonNull Language[] languages, int resource, int dropdownResource) {
        super(context, resource, languages);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.dropdownResource = dropdownResource;
        this.languages = languages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, resource);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, dropdownResource);
    }

    private View createItemView(int position, View convertView, ViewGroup parent, int layout) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(layout, parent, false);
        }

        Language language = languages[position];

        TextView textView = view.findViewById(R.id.text_view_spinner_item);
        if (textView != null) {
            textView.setText(language.getDisplayName());
        }

        ImageView imageView = view.findViewById(R.id.image_view_spinner_item);
        if (imageView != null) {
            imageView.setImageResource(language.getIconResId());
        }

        ImageView dropdownImageView = view.findViewById(R.id.image_view_dropdown_icon);
        TextView dropdownTextView = view.findViewById(R.id.text_view_spinner_dropdown_item);

        if (dropdownImageView != null && dropdownTextView != null) {
            dropdownImageView.setImageResource(language.getIconResId());
            dropdownTextView.setText(language.getDisplayName());
        }

        return view;
    }

    public int getPosition(Language language) {
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(language)) {
                return i;
            }
        }
        return 0;
    }
}
