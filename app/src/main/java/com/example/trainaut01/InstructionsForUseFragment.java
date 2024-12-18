package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trainaut01.adapter.ViewPagerAdapter;
import com.example.trainaut01.models.InstructionPageData;
import com.example.trainaut01.utils.ButtonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * InstructionsForUseFragment отвечает за отображение инструкций пользователя.
 * Использует ViewPager2 для показа пошагового руководства по приложению.
 */
public class InstructionsForUseFragment extends Fragment {

    private Button _btnGoToHomePage;
    private ViewPager2 _viewPager;


    /**
     * Создает и возвращает представление для фрагмента.
     *
     * @param inflater           Используется для "надувания" макета.
     * @param container          Родительский контейнер, в который будет добавлено представление.
     * @param savedInstanceState Сохраненное состояние фрагмента (если есть).
     * @return Представление фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructions_for_use, container, false);
        init(view);
        setupViewPager();
        return view;
    }

    private void init(View view) {
        _viewPager = view.findViewById(R.id.viewPager);
        _btnGoToHomePage = view.findViewById(R.id.btnGoToHomePage);

        _btnGoToHomePage.setOnClickListener(view1 -> navigateToBaseActivity());
    }

    /**
     * Настраивает ViewPager2 для отображения страниц инструкции.
     */
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity(), getInstructionPages());
        _viewPager.setAdapter(adapter);

        _viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == getInstructionPages().size() - 1) {
                    ButtonUtils.updateButtonState(requireContext(), _btnGoToHomePage, "На  главную", R.color.white, R.drawable.btn1_intro_back, true);
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
        pages.add(new InstructionPageData(R.drawable.ic_home,"Главная страница — отправная точка вашего взаимодействия с приложением."));
        pages.add(new InstructionPageData(R.drawable.ic_training,"Тренировки включают упражнения на моторику и когнитивные навыки.\n\n"));
        pages.add(new InstructionPageData(R.drawable.img_dum,"Моторные тренировки: упражнения для мелкой и крупной моторики.\nРазвивают координацию, ловкость и уверенность."));
        pages.add(new InstructionPageData(R.drawable.img_idea,"Когнитивные тренировки развивают память, внимание и навыки коммуникации (AAC).\nПомогают улучшить концентрацию и общение."));
        pages.add(new InstructionPageData(R.drawable.ic_profile,"На странице профиля вы можете управлять личными данными\n\n"));
        return pages;
    }

    /**
     * Переход на главный экран приложения.
     */
    private void navigateToBaseActivity() {
        Intent intent = new Intent(InstructionsForUseFragment.this.getContext(), BaseActivity.class);
        startActivity(intent);
    }
}
