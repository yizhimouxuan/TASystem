package data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
public class JsonDataStore<T> {
    private final String filePath;
    private final Class<T[]> arrayClass;
    private final List<T> items;
    private final Gson gson;
    public JsonDataStore(String filePath, Class<T[]> arrayClass) {
        this(filePath, arrayClass, new GsonBuilder().setPrettyPrinting().create());
    }
    public JsonDataStore(String filePath, Class<T[]> arrayClass, Gson gson) {
        this.filePath = filePath;
        this.arrayClass = arrayClass;
        this.gson = gson;
        this.items = new ArrayList<>();
        load();
    }
    private void load() {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                Files.write(f.toPath(), "[]".getBytes());
            }
            String content = new String(Files.readAllBytes(f.toPath()));
            T[] arr = gson.fromJson(content, arrayClass);
            if (arr != null) items.addAll(Arrays.asList(arr));
        } catch (IOException e) { throw new RuntimeException("Failed to load data from " + filePath, e); }
    }
    public void save() {
        try {
            String json = gson.toJson(items);
            Files.write(new File(filePath).toPath(), json.getBytes());
        } catch (IOException e) { throw new RuntimeException("Failed to save data to " + filePath, e); }
    }
    public List<T> findAll() { return new ArrayList<>(items); }
    public void add(T item) { items.add(item); save(); }
    public void clear() { items.clear(); save(); }
    public void update(Predicate<T> predicate, T newItem) {
        for (int i = 0; i < items.size(); i++) {
            if (predicate.test(items.get(i))) { items.set(i, newItem); save(); return; }
        }
    }
    public void removeIf(Predicate<T> predicate) { items.removeIf(predicate); save(); }
}
