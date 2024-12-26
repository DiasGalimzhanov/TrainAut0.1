package com.example.trainaut01.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;

/**
 * Адаптер для отображения списка стран в виде элементов RecyclerView.
 * Используется для выбора страны с отображением её имени и кода.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private final String[] _countryNames;
    private final String[] _countryCodes;
    private final OnCountrySelectedListener _listener;
    private final AlertDialog _dialog;

    /**
     * Интерфейс для обработки события выбора страны.
     */
    public interface OnCountrySelectedListener {
        /**
         * Вызывается при выборе страны.
         *
         * @param position позиция выбранной страны в списке.
         */
        void onCountrySelected(int position);
    }

    /**
     * Конструктор адаптера.
     *
     * @param countryNames массив названий стран.
     * @param countryCodes массив кодов стран.
     * @param dialog       объект AlertDialog, в котором отображается список стран.
     * @param listener     обработчик события выбора страны.
     */
    public CountryAdapter(String[] countryNames, String[] countryCodes, AlertDialog dialog, OnCountrySelectedListener listener) {
        this._countryNames = countryNames;
        this._countryCodes = countryCodes;
        this._listener = listener;
        this._dialog = dialog;
    }

    /**
     * Создает ViewHolder для элемента списка.
     *
     * @param parent   родительский ViewGroup.
     * @param viewType тип представления (не используется в данном случае).
     * @return объект CountryViewHolder.
     */
    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(view);
    }

    /**
     * Привязывает данные к ViewHolder для указанной позиции.
     *
     * @param holder   объект CountryViewHolder.
     * @param position позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        holder.tvCountryName.setText(_countryNames[position]);
        holder.tvCountryCode.setText(_countryCodes[position]);
        holder.itemView.setOnClickListener(v -> {
            _listener.onCountrySelected(position);
            _dialog.dismiss();
        });
    }

    /**
     * Возвращает общее количество элементов в списке.
     *
     * @return размер списка стран.
     */
    @Override
    public int getItemCount() {
        return _countryNames.length;
    }


    /**
     * ViewHolder для элемента списка страны.
     * Содержит текстовые поля для отображения названия страны и её кода.
     */
    static class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCountryName, tvCountryCode;

        /**
         * Конструктор ViewHolder.
         *
         * @param itemView представление элемента списка.
         */
        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCountryName = itemView.findViewById(R.id.tvCountryName);
            tvCountryCode = itemView.findViewById(R.id.tvCountryCode);
        }
    }
}
