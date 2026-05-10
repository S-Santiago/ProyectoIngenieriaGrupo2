package persistence;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class JsonRepository {
    private String filePath;

    public JsonRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveData(String jsonData) {
        try {
            FileUtils.writeStringToFile(new File(filePath), jsonData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadData() {
        try {
            return FileUtils.readFileToString(new File(filePath), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
