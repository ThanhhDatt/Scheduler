package Course;

import Course.Entity.Course;
import Course.Entity.Scope;
import Course.StoreData.ParseToJson;
import Course.StoreData.WriteToFile;
import Course.StoreData.WriteToJsonFile;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.collect.ImmutableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Tool for automatically create Schedule from registered courses.
 *
 * @Created by thanhhdattt on 19.8.2020.
 * @Updated by thanhhdattt on 21.8.2020.
 */

public class Scheduler {
    /**
    * Initialize global instances and status
    */
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static String SPREADSHEET_ID = "";
    private static String path, range;
    private static final String ROW_STATUS = "Đang xin mở lớp";
    private static List<Course> courses;
    private static NetHttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

        }
    }

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = ImmutableList.of(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public synchronized static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Scheduler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        /**
         * Build flow and trigger user authorization request.
         */
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static String getSheetsID(String path){
        String out = path.substring(39, 39+44);
        return out;
    }

    /**
     * Prints the names and courses info of courses in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1nI0Bw-gHn8ay5IN3FzQccF_G0jtDnOw5fkxYNAi0F7A/
     */
    public static void main(String... args) throws IOException {

        /**Load initial parameters*/
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("config.properties");
        Properties p = new Properties();
        p.load(is);
        path = p.getProperty("path");
        range = p.getProperty("range");
        SPREADSHEET_ID = getSheetsID(path);

        /**Build a new authorized API client service.*/
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        Stack<Course> coursesList = new Stack<Course>();
        int index = 0;
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
                Course course = new Course();
                ArrayList<Scope> scopes = new ArrayList<>();
                Scope scope = new Scope();
                course.setId(coursesList.size());
                course.setName((String) row.get(1));
                course.setCourseCode((String) row.get(2));
                /**
                 * Ignored merged cells case that following cell return null
                 ********** HARD CODE *******
                 **/
                if(((String) row.get(2)).equalsIgnoreCase(ROW_STATUS)) {
                    scope.setScope((String) row.get(2));
                    scope.setTime((String) row.get(2));
                    scope.setWeekday((String) row.get(2));
                    scope.setLocation((String) row.get(2));
                } else {
                    scope.setScope((String) row.get(3));
                    scope.setTime((String) row.get(4));
                    scope.setWeekday((String) row.get(5));
                    scope.setLocation((String) row.get(6));
                }
                scopes.add(scope);
                if(values.get(index).get(1)=="") {
                    course.setId(coursesList.size()-1);
                    course.setName((String) values.get(index-1).get(1));
                    course.setCourseCode((String) values.get(index-1).get(2));
                    Scope scope1 = new Scope();
                    scope1.setScope((String) values.get(index - 1).get(3));
                    scope1.setTime((String) values.get(index - 1).get(4));
                    scope1.setWeekday((String) values.get(index - 1).get(5));
                    scope1.setLocation((String) values.get(index - 1).get(6));
                    scopes.add(scope1);
                    coursesList.pop();
                }
                course.setScope(scopes);
                coursesList.add(course);
                index++;
            }
        }

//        TODO:
//          - Add course info into table
//          - Create schedule base on course info

        /**Show course info*/
        for(Course course : coursesList){
            System.out.println("ID: " + course.getId());
            System.out.println("Name: " + course.getName());
            System.out.println("Code: " + course.getCourseCode());
            System.out.print("Course Scope: " + "\n");
            for(int i=0; i<course.getScope().size(); i++){
                System.out.println("\t Scope " + (i+1) +": ");
                System.out.println("\t\t + Scope: " + course.getScope().get(i).getScope());
                System.out.println("\t\t + Time: " + course.getScope().get(i).getTime());
                System.out.println("\t\t + Weekday: " + course.getScope().get(i).getWeekday());
                System.out.println("\t\t + Location: " + course.getScope().get(i).getLocation());
            }
            System.out.println();
        }
        System.out.println("\n \n");
        courses = coursesList;

        /**
         * Write data to file (text file and json file) for future using
         * Divided into 2 thread for optimization
         * @thread1 for writing to text file
         * @thread2 for writing to json file
         * @thread3 for writing course info into sheets
         **/
        //thread 1
        Runnable r1 = new WriteToFile("src/main/resources/Course/CourseRegister.txt", courses);
        new Thread(r1).start();

        //thread 2
        String JsonData = ParseToJson.parse(courses);
        Runnable r2 = new WriteToJsonFile("src/main/resources/Course/JsonCourseRegister", JsonData);
        new Thread(r2).start();

        //thread 3
        Runnable r3 = new WriteToSheets(courses, SPREADSHEET_ID);
        new Thread(r3).start();
    }

}