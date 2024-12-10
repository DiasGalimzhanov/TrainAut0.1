package com.example.trainaut01.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.NoteAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.ChildNote;
import com.example.trainaut01.repository.ChildRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Фрагмент для отображения и работы со списком заметок.
 * Позволяет просматривать, добавлять и просматривать подробности заметок.
 */
public class NoteFragment extends Fragment {
    @Inject
    ChildRepository childRepository;

    private Button btnAddNotes;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<ChildNote> notes = new ArrayList<>();
    private ListenerRegistration noteListener;

    private static final String USER_DATA_PREF = "user_data";
    private static final String CHILD_DATA_PREF = "child_data";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_CHILD_ID = "childId";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initDependencies();
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        String userId = getUserIdFromPrefs();
        String childId = getChildIdFromPrefs();

        if (userId == null || userId.isEmpty() || childId == null || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Не удалось определить пользователя или ребенка", Toast.LENGTH_SHORT).show();
            return;
        }

        loadNotesFromFirebase(userId, childId);

        noteListener = childRepository.listenForNotesUpdates(
                userId,
                childId,
                updatedNotes -> {
                    notes.clear();
                    notes.addAll(updatedNotes);
                    noteAdapter.notifyDataSetChanged();
                },
                e -> Toast.makeText(requireContext(), "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );

        btnAddNotes.setOnClickListener(v -> showAddNoteDialog(userId, childId));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (noteListener != null) {
            noteListener.remove();
        }
    }

    /**
     * Инициализация зависимостей через Dagger.
     */
    private void initDependencies() {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Инициализация UI-элементов.
     */
    private void initViews(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recyclerViewNote);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnAddNotes = view.findViewById(R.id.notes_add_button);

        noteAdapter = new NoteAdapter(notes, this::showNoteDetailsDialog);
        recyclerView.setAdapter(noteAdapter);
    }

    private String getUserIdFromPrefs() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    private String getChildIdFromPrefs() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(CHILD_DATA_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CHILD_ID, "");
    }

    /**
     * Отображает диалог для добавления новой заметки.
     */
    private void showAddNoteDialog(String userId, String childId) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_notes, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText etTitle = dialogView.findViewById(R.id.et_title_note);
        EditText etDescription = dialogView.findViewById(R.id.etLongText);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (!title.isEmpty() && !description.isEmpty()) {
                long currentTime = System.currentTimeMillis();
                ChildNote newNote = new ChildNote(
                        childId,
                        UUID.randomUUID().toString(),
                        title,
                        description,
                        currentTime,
                        currentTime
                );

                saveNoteToFirebase(userId, childId, newNote);
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Сохранение заметки в Firestore через репозиторий.
     */
    private void saveNoteToFirebase(String userId, String childId, ChildNote note) {
        childRepository.saveNoteToFirebase(userId, childId, note, aVoid -> {
                    Toast.makeText(requireContext(), "Заметка успешно сохранена", Toast.LENGTH_SHORT).show();
                    },
                e -> Toast.makeText(requireContext(), "Ошибка сохранения заметки: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Загрузка заметок из Firestore.
     * После загрузки, список заметок обновляется.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadNotesFromFirebase(String userId, String childId) {
        childRepository.getChildNotes(
                userId,
                childId,
                loadedNotes -> {
                    notes.clear();
                    notes.addAll(loadedNotes);
                    noteAdapter.notifyDataSetChanged();
                },
                e -> Toast.makeText(requireContext(), "Ошибка загрузки заметок: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Отображение диалога с подробностями о выбранной заметке.
     */
    private void showNoteDetailsDialog(ChildNote note) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_details, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tvNoteTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvNoteContent);
        TextView tvDate = dialogView.findViewById(R.id.tvNoteDate);
        Button btnClose = dialogView.findViewById(R.id.btnClose);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String date = formatter.format(new Date(note.getCreatedAt()));

        tvTitle.setText(note.getTitle());
        tvContent.setText(note.getContent());
        tvDate.setText(date);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
            handleNoteDelete(note, dialog);
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleNoteDelete(ChildNote note, AlertDialog dialog) {
        String userId = getUserIdFromPrefs();
        String childId = getChildIdFromPrefs();

        if (userId.isEmpty() || childId.isEmpty()) {
            Toast.makeText(requireContext(), "Не удалось определить пользователя или ребенка", Toast.LENGTH_SHORT).show();
            return;
        }

        childRepository.deleteNoteFromFirebase(userId, childId, note.getId(), aVoid -> {
                    Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                    notes.remove(note);
                    noteAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                },
                e -> Toast.makeText(requireContext(), "Ошибка удаления заметки: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
