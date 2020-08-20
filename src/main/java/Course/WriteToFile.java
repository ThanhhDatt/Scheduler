package Course;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WriteToFile {
    public static void writeToFile(String path, List<Course> courses) throws IOException {
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            FileWriter myWriter = new FileWriter(path);
            myWriter.write("All courses updated at time: " + dtf.format(now) + "\n \n");
            for(int i=0; i<courses.size(); i++){
                myWriter.write("ID: " + (i+1) + "\n");
                myWriter.write("Name: " + courses.get(i).getName() + "\n");
                myWriter.write("Code: " + courses.get(i).getCourseCode() + "\n");
                myWriter.write("Course Scope: " + "\n");
                for(int j=0; j<courses.get(i).getScope().size(); j++){
                    myWriter.write("\t Scope " + (j+1) +": \n");
                    myWriter.write("\t\t + Scope: " + courses.get(i).getScope().get(j).getScope() + "\n");
                    myWriter.write("\t\t + Time: " + courses.get(i).getScope().get(j).getTime() + "\n");
                    myWriter.write("\t\t + Weekday: " + courses.get(i).getScope().get(j).getWeekday() + "\n");
                    myWriter.write("\t\t + Location: " + courses.get(i).getScope().get(j).getLocation() + "\n");
                }
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