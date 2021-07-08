import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JacksonJsonMergeNested {
    @Test
    public void testJacksonListNoMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(Collection.class).setMergeable(false);
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
        Assertions.assertEquals(list2, updatedObj.getList());
    }

    @Test
    public void testJacksonNestedListNoMerge() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultMergeable(false);
        mapper.configOverride(Collection.class).setMergeable(false);
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
        Assertions.assertEquals(list2, updatedObj.getNestedList().get("test"));
    }

    private static class WithNestedList {
        private String id;
        private String someProperty;
        private List<String> list;
        @JsonMerge private Map<String, List<String>> nestedList;

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
    }
}
