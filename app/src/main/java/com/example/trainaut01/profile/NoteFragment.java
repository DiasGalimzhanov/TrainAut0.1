package com.example.trainaut01.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.example.trainaut01.utils.ToastUtils;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Фрагмент для отображения и управления заметками о ребенке.
 * Позволяет просматривать список заметок, добавлять новые и удалять существующие.
 */
public class NoteFragment extends Fragment {

    @Inject
    ChildRepository childRepository;

    private RecyclerView recyclerViewNote;
    private Button notesAddButton;
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
     * Создает и "надувает" макет фрагмента.
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
        return inflater.inflate(R.layout.fragment_note, container, false);
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
        initViews(view);

        String userId = SharedPreferencesUtils.getString(requireContext(), USER_DATA_PREF, KEY_USER_ID, "");
        String childId = SharedPreferencesUtils.getString(requireContext(), CHILD_DATA_PREF, KEY_CHILD_ID, "");

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(childId)) {
            ToastUtils.showErrorMessage(requireContext(), getString(R.string.error_user_or_child_not_found));
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
                e -> ToastUtils.showErrorMessage(requireContext(), getString(R.string.error_loading_notes, e.getMessage()))
        );

        notesAddButton.setOnClickListener(v -> showAddNoteDialog(userId, childId));
    }

    /**
     * Освобождает ресурсы при уничтожении View.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (noteListener != null) {
            noteListener.remove();
        }
    }

    /**
     * Инициализирует RecyclerView и адаптер заметок.
     */
    private void initViews(View view) {
        recyclerViewNote = view.findViewById(R.id.recycler_view_note);
        notesAddButton = view.findViewById(R.id.notes_add_button);

        recyclerViewNote.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new NoteAdapter(notes, this::showNoteDetailsDialog);
        recyclerViewNote.setAdapter(noteAdapter);
    }

    /**
     * Показывает диалоговое окно для добавления новой заметки.
     *
     * @param userId идентификатор пользователя.
     * @param childId идентификатор ребенка.
     */
    private void showAddNoteDialog(String userId, String childId) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_notes, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText etTitleNote = dialogView.findViewById(R.id.et_title_note);
        EditText etLongText = dialogView.findViewById(R.id.et_long_text);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnSubmit.setOnClickListener(v -> {
            String title = etTitleNote.getText().toString().trim();
            String description = etLongText.getText().toString().trim();

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
                ToastUtils.showErrorMessage(requireContext(), getString(R.string.fill_all_fields));
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
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
                aVoid -> ToastUtils.showShortMessage(requireContext(), getString(R.string.note_saved_successfully)),
                e -> ToastUtils.showErrorMessage(requireContext(), getString(R.string.error_saving_note, e.getMessage()))
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
                e -> ToastUtils.showErrorMessage(requireContext(), getString(R.string.error_loading_notes, e.getMessage()))
        );
    }

    /**
     * Показывает диалог с подробностями о заметке.
     *
     * @param note выбранная заметка.
     */
    private void showNoteDetailsDialog(ChildNote note) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_details, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView tvNoteTitle = dialogView.findViewById(R.id.tvNoteTitle);
        TextView tvNoteContent = dialogView.findViewById(R.id.tvNoteContent);
        TextView tvNoteDate = dialogView.findViewById(R.id.tvNoteDate);
        Button btnClose = dialogView.findViewById(R.id.btnClose);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String date = formatter.format(new Date(note.getCreatedAt()));

        tvNoteTitle.setText(note.getTitle());
        tvNoteContent.setText(note.getContent());
        tvNoteDate.setText(date);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
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
        String userId = SharedPreferencesUtils.getString(requireContext(), USER_DATA_PREF, KEY_USER_ID, "");
        String childId = SharedPreferencesUtils.getString(requireContext(), CHILD_DATA_PREF, KEY_CHILD_ID, "");

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(childId)) {
            ToastUtils.showErrorMessage(requireContext(), getString(R.string.error_user_or_child_not_found));
            return;
        }

        childRepository.deleteNoteFromFirebase(userId, childId, note.getId(),
                aVoid -> {
                    ToastUtils.showShortMessage(requireContext(), getString(R.string.note_deleted));
                    notes.remove(note);
                    noteAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                },
                e -> ToastUtils.showErrorMessage(requireContext(), getString(R.string.error_deleting_note, e.getMessage()))
        );
    }
}

