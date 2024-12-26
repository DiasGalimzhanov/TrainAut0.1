package com.example.trainaut01.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.trainaut01.PageFragment;
import com.example.trainaut01.models.InstructionPageData;

import java.util.List;

/**
 * Адаптер для ViewPager2, отвечающий за отображение страниц с инструкциями.
 * Каждая страница представлена объектом {@link InstructionPageData}.
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<InstructionPageData> _pageDataList;

    /**
     * Конструктор адаптера ViewPager2.
     *
     * @param fragmentActivity Активность, где используется ViewPager2.
     * @param pageDataList     Список данных для отображения на страницах.
     */
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<InstructionPageData> pageDataList) {
        super(fragmentActivity);
        this._pageDataList = pageDataList;
    }

    /**
     * Создает фрагмент для указанной позиции в ViewPager2.
     *
     * @param position Позиция страницы в адаптере.
     * @return Фрагмент для отображения на указанной позиции.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        InstructionPageData data = _pageDataList.get(position);
        return PageFragment.newInstance(data.getResourcePath(), data.getText());
    }

    /**
     * Возвращает общее количество страниц в ViewPager2.
     *
     * @return Количество страниц.
     */
    @Override
    public int getItemCount() {
        return _pageDataList.size();
    }
}
