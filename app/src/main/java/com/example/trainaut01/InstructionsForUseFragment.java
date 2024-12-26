package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trainaut01.adapter.ViewPagerAdapter;
import com.example.trainaut01.databinding.FragmentInstructionsForUseBinding;
import com.example.trainaut01.models.InstructionPageData;
import com.example.trainaut01.utils.ButtonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * InstructionsForUseFragment отвечает за отображение инструкций пользователя.
 * Использует ViewPager2 для показа пошагового руководства по приложению.
 */
public class InstructionsForUseFragment extends Fragment {

    private FragmentInstructionsForUseBinding _binding;

    /**
     * Создает и возвращает представление фрагмента.
     *
     * @param inflater  объект LayoutInflater для создания представлений
     * @param container контейнер для представления (может быть null)
     * @param savedInstanceState сохраненное состояние (может быть null)
     * @return корневое представление фрагмента
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = FragmentInstructionsForUseBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Вызывается после создания представления фрагмента.
     *
     * @param view               корневое представление фрагмента
     * @param savedInstanceState сохраненное состояние (может быть null)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setupViewPager();
    }

    /**
     * Вызывается при уничтожении представления фрагмента.
     * Очищает объект binding для предотвращения утечек памяти.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }


    /**
     * Инициализирует элементы интерфейса и задает обработчики событий.
     */
    private void init() {
        _binding.btnGoToHomePage.setOnClickListener(view1 -> navigateToBaseActivity());
    }

    /**
     * Настраивает ViewPager2 для отображения страниц инструкции.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity(), getInstructionPages());
        _binding.viewPager.setAdapter(adapter);

        _binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == getInstructionPages().size() - 1) {
                    ButtonUtils.updateButtonState(
                            requireContext(),
                            _binding.btnGoToHomePage,
                            getString(R.string.btn_go_to_home),
                            R.color.white,
                            R.drawable.btn1_intro_back,
                            true
                    );
                }
            }
        });
    }

    /**
     * Генерирует список страниц инструкции.
     *
     * @return Список объектов InstructionPageData, представляющих страницы инструкции.
     */
    private List<InstructionPageData> getInstructionPages() {
        List<InstructionPageData> pages = new ArrayList<>();
        pages.add(new InstructionPageData(R.drawable.ic_home, getString(R.string.instruction_home_page)));
        pages.add(new InstructionPageData(R.drawable.ic_training, getString(R.string.instruction_training)));
        pages.add(new InstructionPageData(R.drawable.img_dum, getString(R.string.instruction_motor_training)));
        pages.add(new InstructionPageData(R.drawable.img_idea, getString(R.string.instruction_cognitive_training)));
        pages.add(new InstructionPageData(R.drawable.ic_profile, getString(R.string.instruction_profile_page)));
        return pages;
    }

    /**
     * Переход на главный экран приложения.
     */
    private void navigateToBaseActivity() {
        Intent intent = new Intent(InstructionsForUseFragment.this.getContext(), BaseActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
