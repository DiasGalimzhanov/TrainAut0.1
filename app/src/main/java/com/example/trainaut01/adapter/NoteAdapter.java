package com.example.trainaut01.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.models.ChildNote;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<ChildNote> notes;

    public interface OnNoteClickListener {
        void onNoteClick(ChildNote note);
    }

    private final OnNoteClickListener onNoteClickListener;

    public NoteAdapter(List<ChildNote> notes, OnNoteClickListener onNoteClickListener) {
        this.notes = notes;
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgIcon;
        private final TextView tvTitle;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgNoteIcon);
            tvTitle = itemView.findViewById(R.id.tvNoteName);
        }

        public void bind(ChildNote note) {
            tvTitle.setText(note.getTitle());
            imgIcon.setImageResource(R.drawable.icon_note);
        }
    }
}
