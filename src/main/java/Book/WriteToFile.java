package Book;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WriteToFile {
    public static void writeToFile(String path, ArrayList<Book> books) throws IOException {
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            FileWriter myWriter = new FileWriter(path);
            myWriter.write("All books updated at time: " + dtf.format(now) + "\n \n");
            for(int i=0; i<books.size(); i++){
                myWriter.write("ID: " + books.get(i).getID() + "\n");
                myWriter.write("Name: " + books.get(i).getName() + "\n");
                myWriter.write("Shelf: " + books.get(i).getShelf() + "\n");
                myWriter.write("Init amount: " + books.get(i).getInitAmount() + "\n");
                myWriter.write("Current amount: " + books.get(i).getCurrentAmount() + "\n");
                myWriter.write("Author: " + books.get(i).getAuthor() + "\n");
                myWriter.write("Category: " + books.get(i).getCategory() + "\n");
                myWriter.write("\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the text file!");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeToAJsonFile(String path, String context){
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(context);
            myWriter.close();
            System.out.println("Successfully wrote to the Json file!");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}