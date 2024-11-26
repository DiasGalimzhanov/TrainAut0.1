package com.example.trainaut01.utils;

public class ValidationUtils {

    /**
     * Проверяет, что все переданные строки не пустые.
     *
     * @param fields строки для проверки
     * @return true, если все строки заполнены, иначе false
     */
    public static boolean areFieldsFilled(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет, что телефонный номер соответствует шаблону.
     *
     * @param phone телефонный номер
     * @return true, если номер валиден, иначе false
     */
    public static boolean isPhoneNumberValid(String phone) {
        return phone != null && phone.matches("^\\d{10,12}$");
    }

    /**
     * Проверяет совпадение пароля и подтверждения пароля.
     *
     * @param password1         пароль
     * @param password2  подтверждение пароля
     * @return true, если пароли совпадают, иначе false
     */
    public static boolean doPasswordsMatch(String password1, String password2) {
        return password1 != null && password1.equals(password2);
    }
}
