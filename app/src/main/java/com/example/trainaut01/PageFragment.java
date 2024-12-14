package com.example.trainaut01;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * PageFragment используется для отображения страницы инструкции.
 * Каждая страница включает иконку и текст, передаваемые в аргументах.
 */
public class PageFragment extends Fragment {

    private static final String ARG_ICON = "icon";
    private static final String ARG_TEXT = "text";

    private ImageView _iconView;
    private TextView _textView;

    /**
     * Создает новый экземпляр PageFragment с заданной иконкой и текстом.
     *
     * @param iconResId ID ресурса иконки.
     * @param text      Текст для отображения на странице.
     * @return Экземпляр PageFragment с переданными аргументами.
     */
    public static PageFragment newInstance(int iconResId, String text) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ICON, iconResId);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_instruction_page, container, false);
        init(view);
        setupContent();
        return view;
    }

    /**
     * Инициализирует компоненты интерфейса.
     *
     * @param view Корневой вид фрагмента.
     */
    private void init(View view) {
        _iconView = view.findViewById(R.id.iconView);
        _textView = view.findViewById(R.id.textView);
    }

    /**
     * Устанавливает содержимое для иконки и текста, используя аргументы.
     */
    private void setupContent() {
        Bundle args = getArguments();
        if (args != null) {
            _iconView.setImageResource(args.getInt(ARG_ICON));
            _textView.setText(args.getString(ARG_TEXT));
        }
    }
}
