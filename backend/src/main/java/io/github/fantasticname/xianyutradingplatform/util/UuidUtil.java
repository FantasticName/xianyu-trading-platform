package io.github.fantasticname.xianyutradingplatform.util;

import java.util.UUID;

/**
 * @author FantasticName
 */
public final class UuidUtil {
    private UuidUtil() {
    }

    public static String newUuid() {
        return UUID.randomUUID().toString();
    }
}

