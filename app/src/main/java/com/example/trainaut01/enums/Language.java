package com.example.trainaut01.enums;

import com.example.trainaut01.R;

import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("en", "English", R.drawable.ic_en),
    RUSSIAN("ru", "Русский", R.drawable.ic_ru),
    KAZAKH("kk", "Қазақша", R.drawable.ic_kk),
    CHINESE("zh", "中文", R.drawable.ic_zh);

    private final String code;
    private final String displayName;
    private final int iconResId;

    Language(String code, String displayName, int iconResId) {
        this.code = code;
        this.displayName = displayName;
        this.iconResId = iconResId;
    }

    /**
     * Получает `Language` по коду.
     *
     * @param code Код языка.
     * @return Соответствующий `Language` или `ENGLISH` по умолчанию.
     */
    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return RUSSIAN;
    }

    /**
     * Получает `Language` по отображаемому названию.
     *
     * @param displayName Отображаемое название языка.
     * @return Соответствующий `Language` или `ENGLISH` по умолчанию.
     */
    public static Language fromDisplayName(String displayName) {
        for (Language lang : values()) {
            if (lang.displayName.equalsIgnoreCase(displayName)) {
                return lang;
            }
        }
        return RUSSIAN;
    }
}

