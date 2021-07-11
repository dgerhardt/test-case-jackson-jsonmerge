import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonJsonMergeNested {
    @Test
    public void testJacksonListNoMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(Map.class).setMergeable(true);
        mapper.configOverride(List.class).setMergeable(false);
        final WithNestedList obj = new WithNestedList();
        final List list1 = new ArrayList(List.of("A", "B", "C"));
        final List list2 = new ArrayList(List.of("C", "D", "E", "F", "G"));
        obj.id = "ID";
        obj.list = list1;
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "list", list2
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.id);
        Assertions.assertEquals(list2, updatedObj.list);
    }

    @Test
    public void testJacksonNestedListNoMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(Map.class).setMergeable(true);
        mapper.configOverride(List.class).setMergeable(false);
        final WithNestedList obj = new WithNestedList();
        final List list1 = new ArrayList(List.of("A", "B", "C"));
        final List list2 = new ArrayList(List.of("C", "D", "E", "F", "G"));
        obj.id = "ID";
        obj.nestedList = Map.of("test", list1);
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "nestedList", Map.of("test", list2)
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.id);
        Assertions.assertEquals(list2, updatedObj.nestedList.get("test"));
    }

    @Test
    public void testJacksonNestedListShouldMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configOverride(List.class).setMergeable(true);
        final WithNestedList obj = new WithNestedList();
        final List list1 = List.of("A", "B", "C");
        final List list2 = List.of("C", "D", "E", "F", "G");
        obj.id = "ID";
        obj.nestedList = Map.of("test", new ArrayList(List.copyOf(list1)));
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "nestedList", Map.of("test", list2)
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.id);
        Assertions.assertEquals(
                Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList()),
                updatedObj.nestedList.get("test"));
    }

    @Test
    public void testJacksonNestedListObjectTypeMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(Map.class).setMergeable(true);
        mapper.configOverride(List.class).setMergeable(false);
        final WithNestedList obj = new WithNestedList();
        final List list1 = new ArrayList(List.of("A", "B", "C"));
        final List list2 = new ArrayList(List.of("C", "D", "E", "F", "G"));
        obj.id = "ID";
        obj.nestedList = Map.of("test", list1);
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "nestedObjects", Map.of("test", list2)
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.id);
        Assertions.assertEquals(list2, updatedObj.nestedObjects.get("test"));
    }

    @Test
    public void testJacksonNestedListNoMergeOverrideForImpl() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(HashMap.class).setMergeable(true);
        mapper.configOverride(ArrayList.class).setMergeable(false);
        final WithNestedList obj = new WithNestedList();
        final ArrayList list1 = new ArrayList(List.of("A", "B", "C"));
        final ArrayList list2 = new ArrayList(List.of("C", "D", "E", "F", "G"));
        obj.id = "ID";
        obj.specificImplNestedList = new HashMap(Map.of("test", list1));
        final HashMap<String, Object> mapForUpdate = new HashMap(Map.of(
                "someProperty", "updatedValue",
                "specificImplNestedList", Map.of("test", list2)
        ));
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.id);
        Assertions.assertEquals(list2, updatedObj.specificImplNestedList.get("test"));
    }

    @Test
    public void testJacksonCustomMapNestedListNoMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(Map.class).setMergeable(false);
        mapper.configOverride(List.class).setMergeable(false);
        mapper.configOverride(CustomHashMap.class).setMergeable(true);
        final WithNestedList obj = new WithNestedList();
        final List list1 = new ArrayList(List.of("A", "B", "C"));
        final List list2 = new ArrayList(List.of("C", "D", "E", "F", "G"));
        obj.id = "ID";
        obj.customMapNestedList = new CustomHashMap(Map.of("test", list1));
        final CustomHashMap<String, Object> mapForUpdate = new CustomHashMap(Map.of(
                "someProperty", "updatedValue",
                "customMapNestedList", Map.of("test", list2)
        ));
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.id);
        Assertions.assertEquals(list2, updatedObj.customMapNestedList.get("test"));
    }

    private static class WithNestedList {
        public String id;
        public String someProperty;
        public List<String> list;
        public Map<String, List<String>> nestedList;
        public HashMap<String, ArrayList<String>> specificImplNestedList;
        public CustomHashMap<String, List<String>> customMapNestedList;
        public Map<String, Object> nestedObjects;
    }

    private static class CustomHashMap<K, V> extends HashMap<K, V> {
        CustomHashMap(final Map<? extends K, ? extends V> m) {
            super(m);
        }
    }
}
