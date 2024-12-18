/**
 * Адаптер для отображения списка заметок.
 * При нажатии на заметку вызывается обработчик onNoteClick.
 */
package com.example.trainaut01.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trainaut01.R;
import com.example.trainaut01.databinding.ItemNoteBinding;
import com.example.trainaut01.models.ChildNote;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    /**
     * Интерфейс для обработки нажатия на заметку.
     */
    public interface OnNoteClickListener {
        /**
         * Вызывается при нажатии на заметку.
         * @param note нажатая заметка.
         */
        void onNoteClick(ChildNote note);
    }

    private final List<ChildNote> notes;
    private final OnNoteClickListener onNoteClickListener;

    /**
     * Создает адаптер для отображения списка заметок.
     * @param notes список заметок.
     * @param onNoteClickListener обработчик нажатия на заметку.
     */
    public NoteAdapter(List<ChildNote> notes, OnNoteClickListener onNoteClickListener) {
        this.notes = notes;
        this.onNoteClickListener = onNoteClickListener;
    }

    /**
     * Создает ViewHolder для элемента списка.
     * @param parent родительская ViewGroup.
     * @param viewType тип элемента (не используется).
     * @return новый NoteViewHolder.
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    /**
     * Привязывает данные заметки к ViewHolder.
     * @param holder NoteViewHolder для наполнения.
     * @param position позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        ChildNote note = notes.get(position);
        holder.bind(note);
        holder.itemView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(note);
            }
        });
    }

    /**
     * Возвращает количество заметок.
     * @return число заметок в списке.
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * ViewHolder для элемента заметки.
     */
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final ItemNoteBinding binding;

        /**
         * Создает NoteViewHolder с использованием ViewBinding.
         * @param binding биндинг для разметки элемента заметки.
         */
        public NoteViewHolder(@NonNull ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Привязывает данные заметки к элементам пользовательского интерфейса.
         * @param note заметка для отображения.
         */
        public void bind(ChildNote note) {
            binding.tvNoteName.setText(note.getTitle());
            binding.imgNoteIcon.setImageResource(R.drawable.icon_note);
        }
    }
}
