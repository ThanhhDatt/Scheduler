package Book;

import Book.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ParseToJson {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String parse(ArrayList<Book> books){
        String content = gson.toJson(books);
        System.out.println();
        System.out.println("All course: ");
        System.out.println(books.toString());
        return content;
    }
}