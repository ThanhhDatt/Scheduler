package Course.StoreData;

import Course.Entity.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ParseToJson {
    /**
     * Parse an object array into Json String
     * @return Object JsonString
     **/
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String parse(List<Course> courses){
        String content = gson.toJson(courses);
        System.out.println();
        System.out.println("All course: ");
        System.out.println(courses.toString());
        return content;
    }
}