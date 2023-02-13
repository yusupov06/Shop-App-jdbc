package uz.md.shopappjdbc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uz.md.shopappjdbc.domain.Category;
import uz.md.shopappjdbc.domain.Product;
import uz.md.shopappjdbc.dtos.category.CategoryDto;
import uz.md.shopappjdbc.dtos.category.CategoryInfoDto;
import uz.md.shopappjdbc.dtos.product.ProductDto;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestUtil {

    private static final ObjectMapper mapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    public static byte[] createByteArray(int size, String data) {
        byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = Byte.parseByte(data, 2);
        }
        return byteArray;
    }


    private TestUtil() {
    }

    public static void checkProductsEquality(List<ProductDto> actual, List<Product> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(actual.get(i).getId(), expected.get(i).getId());
            assertEquals(actual.get(i).getName(), expected.get(i).getName());
            assertEquals(actual.get(i).getDescription(), expected.get(i).getDescription());
        }
    }

    public static void checkCategoriesEquality(List<CategoryDto> actual, List<Category> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(actual.get(i).getId(), expected.get(i).getId());
            assertEquals(actual.get(i).getName(), expected.get(i).getName());
            assertEquals(actual.get(i).getDescription(), expected.get(i).getDescription());
        }
    }

    public static void checkCategoriesInfoEquality(List<CategoryInfoDto> actual, List<Category> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(actual.get(i).getId(), expected.get(i).getId());
            assertEquals(actual.get(i).getName(), expected.get(i).getName());
            assertEquals(actual.get(i).getDescription(), expected.get(i).getDescription());
        }
    }

}
