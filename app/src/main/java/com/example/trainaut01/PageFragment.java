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

import com.airbnb.lottie.LottieAnimationView;

/**
 * PageFragment используется для отображения страницы инструкции.
 * Каждая страница включает иконку (изображение или Lottie-анимацию) и текст, передаваемые в аргументах.
 */
public class PageFragment extends Fragment {

    private static final String ARG_RESOURCE_PATH = "resourcePath";
    private static final String ARG_TEXT = "text";
    private static final String ARG_IS_LOTTIE = "isLottie";

    private LottieAnimationView _lottieView;
    private ImageView _iconView;
    private TextView _textView;

    /**
     * Создает новый экземпляр фрагмента с переданными данными.
     *
     * @param resourcePath Путь к ресурсу (изображение или Lottie-анимация).
     * @param text         Текст для отображения на странице.
     * @return Новый экземпляр PageFragment.
     */
    public static PageFragment newInstance(String resourcePath, String text) {
        boolean isLottie = resourcePath.endsWith(".json");
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RESOURCE_PATH, resourcePath);
        args.putString(ARG_TEXT, text);
        args.putBoolean(ARG_IS_LOTTIE, isLottie);
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
        _lottieView = view.findViewById(R.id.lottieAnimationView);
        _iconView = view.findViewById(R.id.iconView);
        _textView = view.findViewById(R.id.textView);
    }

    /**
     * Устанавливает содержимое для иконки (изображение или анимация) и текста, используя аргументы.
     */
    private void setupContent() {
        Bundle args = getArguments();
        if (args != null) {
            String resourcePath = args.getString(ARG_RESOURCE_PATH);
            String text = args.getString(ARG_TEXT);
            boolean isLottie = args.getBoolean(ARG_IS_LOTTIE);

            _textView.setText(text);

            if (isLottie) {
                _iconView.setVisibility(View.GONE);
                _lottieView.setVisibility(View.VISIBLE);
                _lottieView.setAnimation(resourcePath);
                _lottieView.playAnimation();
            } else {
                _lottieView.setVisibility(View.GONE);
                _iconView.setVisibility(View.VISIBLE);
                int resId = requireContext().getResources().getIdentifier(resourcePath, "drawable", requireContext().getPackageName());
                _iconView.setImageResource(resId);
            }
        }
    }
}
