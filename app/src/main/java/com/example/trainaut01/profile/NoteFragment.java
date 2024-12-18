/**
 * Фрагмент для отображения и управления заметками о ребенке.
 * Позволяет просматривать список заметок, добавлять новые и удалять существующие.
 */
package com.example.trainaut01.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.NoteAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentNoteBinding;
import com.example.trainaut01.databinding.DialogNoteDetailsBinding;
import com.example.trainaut01.databinding.DialogNotesBinding;
import com.example.trainaut01.models.ChildNote;
import com.example.trainaut01.repository.ChildRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

public class NoteFragment extends Fragment {

    @Inject
    ChildRepository childRepository;

    private FragmentNoteBinding binding;
    private NoteAdapter noteAdapter;
    private final List<ChildNote> notes = new ArrayList<>();
    private ListenerRegistration noteListener;

    private static final String USER_DATA_PREF = "user_data";
    private static final String CHILD_DATA_PREF = "child_data";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_CHILD_ID = "childId";

    /**
     * Инициализирует Dagger-зависимости.
     *
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть).
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Создает и "надувает" макет фрагмента с использованием ViewBinding.
     *
     * @param inflater объект для "надувания" макета.
     * @param container родительский контейнер.
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть).
     * @return корневой View фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Вызывается, когда View фрагмента полностью создана.
     * Здесь происходит инициализация UI и загрузка данных.
     *
     * @param view корневой вид фрагмента.
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть).
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        String userId = getUserIdFromPrefs();
        String childId = getChildIdFromPrefs();

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(childId)) {
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

        binding.notesAddButton.setOnClickListener(v -> showAddNoteDialog(userId, childId));
    }

    /**
     * Освобождает ресурсы ViewBinding при уничтожении View.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (noteListener != null) {
            noteListener.remove();
        }
        binding = null;
    }

    /**
     * Инициализирует RecyclerView и адаптер заметок.
     */
    private void initViews() {
        binding.recyclerViewNote.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new NoteAdapter(notes, this::showNoteDetailsDialog);
        binding.recyclerViewNote.setAdapter(noteAdapter);
    }

    /**
     * Возвращает идентификатор пользователя из SharedPreferences.
     *
     * @return идентификатор пользователя или пустую строку при отсутствии данных.
     */
    private String getUserIdFromPrefs() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    /**
     * Возвращает идентификатор ребенка из SharedPreferences.
     *
     * @return идентификатор ребенка или пустую строку при отсутствии данных.
     */
    private String getChildIdFromPrefs() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(CHILD_DATA_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CHILD_ID, "");
    }

    /**
     * Показывает диалоговое окно для добавления новой заметки.
     *
     * @param userId идентификатор пользователя.
     * @param childId идентификатор ребенка.
     */
    private void showAddNoteDialog(String userId, String childId) {
        DialogNotesBinding dialogBinding = DialogNotesBinding.inflate(LayoutInflater.from(requireContext()));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            String title = dialogBinding.etTitleNote.getText().toString().trim();
            String description = dialogBinding.etLongText.getText().toString().trim();

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
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

        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Сохраняет заметку в Firestore.
     *
     * @param userId идентификатор пользователя.
     * @param childId идентификатор ребенка.
     * @param note объект заметки.
     */
    private void saveNoteToFirebase(String userId, String childId, ChildNote note) {
        childRepository.saveNoteToFirebase(userId, childId, note,
                aVoid -> Toast.makeText(requireContext(), "Заметка успешно сохранена", Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(requireContext(), "Ошибка сохранения заметки: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Загружает заметки из Firestore и обновляет список.
     *
     * @param userId идентификатор пользователя.
     * @param childId идентификатор ребенка.
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
     * Показывает диалог с подробностями о заметке.
     *
     * @param note выбранная заметка.
     */
    private void showNoteDetailsDialog(ChildNote note) {
        DialogNoteDetailsBinding dialogBinding = DialogNoteDetailsBinding.inflate(LayoutInflater.from(requireContext()));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String date = formatter.format(new Date(note.getCreatedAt()));

        dialogBinding.tvNoteTitle.setText(note.getTitle());
        dialogBinding.tvNoteContent.setText(note.getContent());
        dialogBinding.tvNoteDate.setText(date);

        dialogBinding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnDelete.setOnClickListener(v -> {
            handleNoteDelete(note, dialog);
        });

        dialog.show();
    }

    /**
     * Обрабатывает удаление заметки из Firestore.
     *
     * @param note заметка для удаления.
     * @param dialog диалоговое окно, из которого удаляется заметка.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void handleNoteDelete(ChildNote note, AlertDialog dialog) {
        String userId = getUserIdFromPrefs();
        String childId = getChildIdFromPrefs();

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(childId)) {
            Toast.makeText(requireContext(), "Не удалось определить пользователя или ребенка", Toast.LENGTH_SHORT).show();
            return;
        }

        childRepository.deleteNoteFromFirebase(userId, childId, note.getId(),
                aVoid -> {
                    Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                    notes.remove(note);
                    noteAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                },
                e -> Toast.makeText(requireContext(), "Ошибка удаления заметки: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
