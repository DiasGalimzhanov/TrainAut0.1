package com.example.trainaut01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.trainaut01.models.Child;
import com.example.trainaut01.models.ChildNote;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Репозиторий для работы с данными ребенка в Firestore.
 */
public class ChildRepository{

    private final FirebaseFirestore _db;

    /**
     * Конструктор, инициализирующий Firestore.
     */
    public ChildRepository() {
        this._db = FirebaseFirestore.getInstance();
    }

    /**
     * Добавляет ребенка в коллекцию `child` для указанного пользователя.
     *
     * @param userId    идентификатор пользователя
     * @param child     объект ребенка
     * @param context   контекст для доступа к SharedPreferences
     * @param onSuccess callback при успешном выполнении операции
     * @param onFailure callback при ошибке выполнения операции
     */
    public void addChild(String userId, Child child, Context context, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> childData = child.toMap();

        getChildCollection(userId)
                .document(child.getChildId())
                .set(childData)
                .addOnSuccessListener(aVoid -> {
                    saveChildToPreferences(child.toMap(), context);
                    onSuccess.onSuccess(aVoid);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Получает данные ребенка из коллекции `child` для указанного пользователя.
     *
     * @param userId    идентификатор пользователя
     * @param childId   идентификатор ребенка
     * @param onSuccess callback при успешном выполнении операции, возвращает данные ребенка
     * @param onFailure callback при ошибке выполнения операции
     */
    public void getChild(String userId, String childId, OnSuccessListener<Child> onSuccess, OnFailureListener onFailure) {
        getChildCollection(userId)
                .document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Child child = documentSnapshot.toObject(Child.class);
                        onSuccess.onSuccess(child);
                    } else {
                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailure);
    }


    /**
     * Обновляет данные ребенка в коллекции `child`.
     *
     * @param userId       идентификатор пользователя
     * @param updateChild  объект ребенка с обновленными данными
     * @param onSuccess    callback при успешном выполнении операции
     * @param onFailure    callback при ошибке выполнения операции
     */
    public void updateChild(String userId, Child updateChild, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> updatedChildData = updateChild.toMap();

        getChildCollection(userId)
                .document(updateChild.getChildId())
                .set(updatedChildData)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Возвращает ссылку на коллекцию `child` для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return ссылка на коллекцию `child`
     */
    private CollectionReference getChildCollection(String userId) {
        return _db.collection("users").document(userId).collection("child");
    }

    /**
     * Обновляет определенное поле ребенка в коллекции `child` и SharedPreferences.
     *
     * @param userId     идентификатор пользователя.
     * @param childId    идентификатор ребенка.
     * @param fieldName  имя поля для обновления.
     * @param fieldValue новое значение поля.
     * @param context    контекст для доступа к SharedPreferences и Toast.
     */
    public void updateChildItem(String userId, String childId, String fieldName, Object fieldValue, Context context) {
        _db.collection("users").document(userId).collection("child").document(childId)
                .update(fieldName, fieldValue)
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences sharedPref = context.getSharedPreferences("child_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    if (fieldValue instanceof String) {
                        editor.putString(fieldName, (String) fieldValue);
                    } else if (fieldValue instanceof Integer) {
                        editor.putInt(fieldName, (Integer) fieldValue);
                    } else if (fieldValue instanceof Boolean) {
                        editor.putBoolean(fieldName, (Boolean) fieldValue);
                    } else if (fieldValue instanceof Float) {
                        editor.putFloat(fieldName, (Float) fieldValue);
                    } else if (fieldValue instanceof Long) {
                        editor.putLong(fieldName, (Long) fieldValue);
                    } else {
                        Toast.makeText(context, "Неподдерживаемый тип данных", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    editor.apply();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Ошибка при обновлении поля: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Загружает данные первого ребенка и сохраняет их в SharedPreferences.
     *
     * @param userId  Идентификатор пользователя.
     * @param context Контекст для доступа к SharedPreferences.
     */
    public void saveChildData(String userId, Context context, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        getFirstChild(userId, childData -> {
            if (childData != null) {
                saveChildToPreferences(childData, context);
                onSuccess.onSuccess(null);
            } else {
                onFailure.onFailure(new Exception("No child document found for userId: " + userId));
            }
        }, error -> {
            Toast.makeText(context, "Ошибка загрузки данных ребенка: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            onFailure.onFailure(error);
        });
    }

    /**
     * Сохраняет данные ребенка в SharedPreferences.
     *
     * @param childData Данные ребенка.
     * @param context   Контекст для доступа к SharedPreferences.
     */

    private void saveChildToPreferences(Map<String, Object> childData, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("child_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("childId", (String) childData.get("childId"));
        editor.putString("fullName", (String) childData.get("fullName"));
        editor.putString("birthDate", (String) childData.get("birthDate"));
        editor.putString("gender", (String) childData.get("gender"));
        editor.putString("diagnosis", (String) childData.get("diagnosis"));
        editor.putFloat("height", ((Number) childData.get("height")).floatValue());
        editor.putFloat("weight", ((Number) childData.get("weight")).floatValue());
        editor.putInt("exp", ((Number) childData.get("exp")).intValue());
        editor.putInt("lvl", ((Number) childData.get("lvl")).intValue());
        editor.putInt("countDays", ((Number) childData.get("countDays")).intValue());

        editor.apply();
    }

    /**
     * Получить первого ребенка из коллекции `child` для конкретного пользователя.
     *
     * @param userId    Идентификатор пользователя.
     * @param onSuccess Callback для успешного получения данных ребенка.
     * @param onFailure Callback для обработки ошибок.
     */
    public void getFirstChild(String userId, OnSuccessListener<Map<String, Object>> onSuccess, OnFailureListener onFailure) {
        getChildCollection(userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot childSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        onSuccess.onSuccess(childSnapshot.getData());
                    } else {
                        onSuccess.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Загружает все заметки ребенка из Firestore.
     *
     * @param userId    идентификатор пользователя
     * @param childId   идентификатор ребенка
     * @param onSuccess callback при успешном выполнении операции
     * @param onFailure callback при ошибке выполнения операции
     */
    public void getChildNotes(String userId, String childId, OnSuccessListener<List<ChildNote>> onSuccess, OnFailureListener onFailure) {
        getChildCollection(userId)
                .document(childId)
                .collection("notes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ChildNote> notes = new ArrayList<>();
                    for (DocumentSnapshot snapshot : querySnapshot) {
                        notes.add(snapshot.toObject(ChildNote.class));
                    }
                    onSuccess.onSuccess(notes);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Сохраняет заметку ребенка в Firestore.
     *
     * @param userId    идентификатор пользователя
     * @param childId   идентификатор ребенка
     * @param note      объект заметки
     * @param onSuccess callback при успешном выполнении операции
     * @param onFailure callback при ошибке выполнения операции
     */
    public void saveNoteToFirebase(String userId, String childId, ChildNote note, OnSuccessListener<Void> onSuccess,
                                   OnFailureListener onFailure) {
        _db.collection("users")
                .document(userId)
                .collection("child")
                .document(childId)
                .collection("notes")
                .document(note.getId())
                .set(note.toMap())
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Слушатель для обновлений списка заметок ребенка в Firestore.
     *
     * @param userId      идентификатор пользователя
     * @param childId     идентификатор ребенка
     * @param onNoteUpdate callback при обновлении заметок
     * @param onFailure   callback при ошибке
     * @return ListenerRegistration объект для отмены слушателя
     */
    public ListenerRegistration listenForNotesUpdates(String userId, String childId, OnSuccessListener<List<ChildNote>> onNoteUpdate,
                                                      OnFailureListener onFailure) {
        return _db.collection("users")
                .document(userId)
                .collection("child")
                .document(childId)
                .collection("notes")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        onFailure.onFailure(e);
                        return;
                    }

                    if (snapshots != null) {
                        List<ChildNote> notes = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            notes.add(doc.toObject(ChildNote.class));
                        }
                        onNoteUpdate.onSuccess(notes);
                    }
                });
    }

    /**
     * Удаляет заметку ребенка из Firestore.
     *
     * @param userId    идентификатор пользователя
     * @param childId   идентификатор ребенка
     * @param noteId    идентификатор заметки
     * @param onSuccess callback при успешном выполнении операции
     * @param onFailure callback при ошибке выполнения операции
     */
    public void deleteNoteFromFirebase(String userId, String childId, String noteId, OnSuccessListener<Void> onSuccess,
                                       OnFailureListener onFailure) {
        _db.collection("users")
                .document(userId)
                .collection("child")
                .document(childId)
                .collection("notes")
                .document(noteId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}
