import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WriteToFile {
    public static void writeToFile(String path, ArrayList<Course> courses) throws IOException {
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            FileWriter myWriter = new FileWriter(path);
            myWriter.write("All news in time: " + dtf.format(now) + "\n \n");
            for(int i=0; i<courses.size(); i++){
                myWriter.write("ID: " + courses.get(i).getId() + "\n");
                myWriter.write("Course Name: " + courses.get(i).getName() + "\n");
                myWriter.write("Course Code: " + courses.get(i).getCourseCode() + "\n");
//                myWriter.write("Course Time: \n" + courses.get(i).getTime() + "\n");
//                myWriter.write("Weekday: " + courses.get(i).getWeekday() + "\n");
//                myWriter.write("Location: " + courses.get(i).getLocation() + "\n");
//                myWriter.write("Scope: \n" + courses.get(i).getScope() + "\n");
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