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
        obj.setList(list1);
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "list", list2
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.getId());
        Assertions.assertEquals(list2, updatedObj.getList());
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
        obj.setNestedList(Map.of("test", list1));
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "nestedList", Map.of("test", list2)
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.getId());
        Assertions.assertEquals(list2, updatedObj.getNestedList().get("test"));
    }

    @Test
    public void testJacksonNestedListShouldMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configOverride(List.class).setMergeable(true);
        final WithNestedList obj = new WithNestedList();
        final List list1 = new ArrayList(List.of("A", "B", "C"));
        final List list2 = new ArrayList(List.of("C", "D", "E", "F", "G"));
        obj.id = "ID";
        obj.setNestedList(Map.of("test", list1));
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "nestedList", Map.of("test", list2)
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.getId());
        Assertions.assertEquals(
                Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList()),
                updatedObj.getNestedList().get("test"));
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
        obj.setNestedObjects(Map.of("test", list1));
        final Map<String, Object> mapForUpdate = Map.of(
                "someProperty", "updatedValue",
                "nestedObjects", Map.of("test", list2)
        );
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.getId());
        Assertions.assertEquals(list2, updatedObj.getNestedObjects().get("test"));
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
        obj.setSpecificImplNestedList(new HashMap(Map.of("test", list1)));
        final HashMap<String, Object> mapForUpdate = new HashMap(Map.of(
                "someProperty", "updatedValue",
                "specificImplNestedList", Map.of("test", list2)
        ));
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.getId());
        Assertions.assertEquals(list2, updatedObj.getSpecificImplNestedList().get("test"));
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
        obj.setCustomMapNestedList(new CustomHashMap(Map.of("test", list1)));
        final CustomHashMap<String, Object> mapForUpdate = new CustomHashMap(Map.of(
                "someProperty", "updatedValue",
                "customMapNestedList", Map.of("test", list2)
        ));
        final ObjectReader reader = mapper.readerForUpdating(obj);
        final JsonNode tree = mapper.valueToTree(mapForUpdate);
        final WithNestedList updatedObj = reader.readValue(tree);
        Assertions.assertEquals("ID", updatedObj.getId());
        Assertions.assertEquals(list2, updatedObj.getCustomMapNestedList().get("test"));
    }

    private static class WithNestedList {
        private String id;
        private String someProperty;
        private List<String> list;
        private Map<String, List<String>> nestedList;
        private HashMap<String, ArrayList<String>> specificImplNestedList;
        private CustomHashMap<String, List<String>> customMapNestedList;
        private Map<String, Object> nestedObjects;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSomeProperty() {
            return someProperty;
        }

        public void setSomeProperty(final String someProperty) {
            this.someProperty = someProperty;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(final List<String> list) {
            this.list = list;
        }

        public Map<String, List<String>> getNestedList() {
            return nestedList;
        }

        public void setNestedList(final Map<String, List<String>> nestedList) {
            this.nestedList = nestedList;
        }

        public HashMap<String, ArrayList<String>> getSpecificImplNestedList() {
            return specificImplNestedList;
        }

        public void setSpecificImplNestedList(final HashMap<String, ArrayList<String>> specificImplNestedList) {
            this.specificImplNestedList = specificImplNestedList;
        }

        public CustomHashMap<String, List<String>> getCustomMapNestedList() {
            return customMapNestedList;
        }

        public void setCustomMapNestedList(final CustomHashMap<String, List<String>> customMapNestedList) {
            this.customMapNestedList = customMapNestedList;
        }

        public Map<String, Object> getNestedObjects() {
            return nestedObjects;
        }

        public void setNestedObjects(final Map<String, Object> nestedObjects) {
            this.nestedObjects = nestedObjects;
        }
    }

    private static class CustomHashMap<K, V> extends HashMap<K, V> {
        CustomHashMap(final Map<? extends K, ? extends V> m) {
            super(m);
        }
    }
}
