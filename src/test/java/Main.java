import com.jayway.jsonpath.JsonPath;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String jsonArray = "[{\"content\": \"我好极了，非常感谢你的关心！\", \"feeling\": 1}, " +
                "{\"content\": \"你好哇\", \"feeling\": -3}]";

        List<String> contents = JsonPath.read(jsonArray, "$.[*].content");

        for (String content : contents) {
            System.out.println("Content: " + content);
        }
    }
}