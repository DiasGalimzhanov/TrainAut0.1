package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * AgreementActivity предназначена для отображения текстов соглашений,
 * таких как пользовательское соглашение или политика конфиденциальности.
 */
public class AgreementActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_CONTENT_FILE = "EXTRA_CONTENT_FILE";

    private TextView tvTitle;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        init();
        displayContent();
    }

    /**
     * Инициализирует элементы пользовательского интерфейса.
     */
    private void init() {
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
    }

    /**
     * Загружает и отображает содержимое соглашения.
     * Заголовок и текст передаются через Intent.
     */
    private void displayContent() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        int contentFileId = getIntent().getIntExtra(EXTRA_CONTENT_FILE, 0);

        if (title != null) {
            tvTitle.setText(title);
        }

        String content = readTextFromRaw(contentFileId);
        if (content != null) {
            tvContent.setText(createSpannableContent(content));
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * Читает текст из файла в папке raw.
     *
     * @param resourceId ID ресурса.
     * @return Содержимое файла в виде строки, или null в случае ошибки.
     */
    private String readTextFromRaw(int resourceId) {
        StringBuilder text = new StringBuilder();
        try (InputStream is = getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return text.toString();
    }

    /**
     * Создаёт SpannableString для текста, добавляя ссылки на политику конфиденциальности.
     *
     * @param content Текст контента.
     * @return SpannableString с кликабельными ссылками.
     */
    private SpannableString createSpannableContent(String content) {
        String clickableText = getString(R.string.privacy_policy_title);
        SpannableString spannableString = new SpannableString(content);

        int startIndex = content.indexOf(clickableText);
        while (startIndex != -1) {
            int endIndex = startIndex + clickableText.length();

            spannableString.setSpan(createClickableSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            startIndex = content.indexOf(clickableText, endIndex);
        }

        return spannableString;
    }

    /**
     * Создаёт ClickableSpan для перехода на политику конфиденциальности.
     *
     * @return ClickableSpan с настроенным переходом.
     */
    private ClickableSpan createClickableSpan() {
        return new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(AgreementActivity.this, AgreementActivity.class);
                intent.putExtra(EXTRA_TITLE, getString(R.string.privacy_policy_title));
                intent.putExtra(EXTRA_CONTENT_FILE, R.raw.privacy_policy_russian);
                startActivity(intent);
            }
        };
    }
}
