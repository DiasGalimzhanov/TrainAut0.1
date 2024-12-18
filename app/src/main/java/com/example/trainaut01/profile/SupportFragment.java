/**
 * Фрагмент для отправки сообщений в службу поддержки.
 * Пользователь может ввести тему и сообщение, после чего отправить их в Firestore.
 */
package com.example.trainaut01.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentSupportBinding;
import com.example.trainaut01.repository.UserRepository;

import javax.inject.Inject;

public class SupportFragment extends Fragment {

    @Inject
    UserRepository userRepository;

    private FragmentSupportBinding binding;

    /**
     * Инициализация Dagger-зависимостей.
     *
     * @param savedInstanceState состояние фрагмента, если было пересоздание.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Создает и инициализирует макет фрагмента с использованием ViewBinding.
     *
     * @param inflater объект для "надувания" макета фрагмента.
     * @param container родительский контейнер для макета.
     * @param savedInstanceState состояние фрагмента, если было пересоздание.
     * @return корневой View фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSupportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Вызывается, когда View фрагмента создана. Здесь настраиваются обработчики.
     *
     * @param view корневой вид фрагмента.
     * @param savedInstanceState состояние фрагмента, если было пересоздание.
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    /**
     * Освобождает ресурсы ViewBinding при уничтожении View.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Настраивает обработчик нажатия на кнопку отправки сообщения.
     */
    private void setupListeners() {
        binding.btnSupport.setOnClickListener(v -> {
            String theme = binding.etThema.getText().toString().trim();
            String message = binding.etMessege.getText().toString().trim();

            if (TextUtils.isEmpty(theme) || TextUtils.isEmpty(message)) {
                Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                userRepository.saveMessageToFirestore(theme, message, requireActivity());
            }
        });
    }
}
