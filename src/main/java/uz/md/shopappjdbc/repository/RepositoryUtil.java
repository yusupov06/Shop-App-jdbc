package uz.md.shopappjdbc.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RepositoryUtil {
    public static String getAsString(Iterable<?> idList) {
        StringBuilder ids = new StringBuilder();
        ids.append("( ");
        for (Object id : idList) {
            ids.append(id).append(", ");
        }
        ids.replace(ids.lastIndexOf(","), ids.lastIndexOf(",") + 1, "");
        ids.append(" )");
        System.out.println("ids = " + ids);
        return ids.toString();
    }

    public static LocalDateTime getLocalDateTimeFromString(String addedAt) {
//        2023-02-12 12:02:18.000000
        if (addedAt==null) return null;
        addedAt = addedAt.substring(0, addedAt.lastIndexOf("."));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(addedAt, formatter);
    }
}
