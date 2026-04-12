package io.github.fantasticname.xianyutradingplatform.util;

import java.util.regex.Pattern;

/**
 * 校验工具类：提供统一的正则表达式校验逻辑。
 *
 * @author FantasticName
 */
public final class ValidationUtil {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@(qq\\.com|163\\.com|126\\.com)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,12}$");

    private ValidationUtil() {
        // 禁止实例化
    }

    /**
     * 校验是否为合法的手机号（1开头，11位数字）。
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 校验是否为合法的邮箱（仅支持 QQ、163、126 邮箱）。
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 校验是否为合法的账号（手机号或指定格式邮箱）。
     */
    public static boolean isValidAccount(String account) {
        if (account == null) return false;
        String trimmed = account.trim();
        return isValidPhone(trimmed) || isValidEmail(trimmed);
    }

    /**
     * 校验是否为合法的密码（6-12位数字或字母）。
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}
