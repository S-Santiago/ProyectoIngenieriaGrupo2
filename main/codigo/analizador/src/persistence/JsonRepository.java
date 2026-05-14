package persistence;

import jackson.databind;
import jackson.datatype.jsr310;
import java.io.File;
import java.io.IOException;

public class JsonRepository {
    private ObjectMapper objectMapper;

    public JsonRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public <T> void saveToJson(String filePath, T data) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
    }

    public <T> T loadFromJson(String filePath, Class<T> valueType) throws IOException {
        return objectMapper.readValue(new File(filePath), valueType);
    }
}
