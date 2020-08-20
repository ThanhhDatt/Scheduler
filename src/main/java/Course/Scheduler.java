package Course;

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
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

public class Scheduler {
    /**
    * Initialize global instances and status
    */
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static String SPREADSHEET_ID = "";
    private static final String ROW_STATUS = "Đang xin mở lớp";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Scheduler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
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
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1LCqjwy3pERyOgmTOImHesObNyPYSb_ZO6OknWTwVwIk/edit#gid=0
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        Scanner sc = new Scanner(System.in);
//        String path = sc.next();
        String path="https://docs.google.com/spreadsheets/d/1LCqjwy3pERyOgmTOImHesObNyPYSb_ZO6OknWTwVwIk/edit#gid=0";
        SPREADSHEET_ID = getSheetsID(path);
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = "!A2:Z100";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        Stack<Course> courses = new Stack<Course>();
        ListIterator it = values.listIterator();
        int index = 0;
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
                Course course = new Course();
                ArrayList<Scope> scopes = new ArrayList<>();
                Scope scope = new Scope();
                course.setId(index);
                course.setName((String) row.get(1));
                course.setCourseCode((String) row.get(2));
                if(((String) row.get(2)).equalsIgnoreCase(ROW_STATUS))
                {
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
                if(values.get(index).get(1)==""){
                    course.setId(index-1);
                    course.setName((String) values.get(index-1).get(1));
                    course.setCourseCode((String) values.get(index-1).get(2));
                    Scope scope1 = new Scope();
                    scope1.setScope((String) row.get(3));
                    scope1.setTime((String) row.get(4));
                    scope1.setWeekday((String) row.get(5));
                    scope1.setLocation((String) row.get(6));
                    scopes.add(scope1);
                    courses.pop();
                }
                course.setScope(scopes);
                courses.add(course);
                index++;
            }
        }

//        TODO:
//          - Merge no course scope into previous course
//          - Parse full course into Json file
//          - Add course info into table
//          - Create schedule base on course info

        for(Course course : courses){
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
        List<Course> courses1 = courses;
        //Write data to file (text file and json file) for future using
        WriteToFile.writeToFile("src/main/resources/Course/CourseRegister.txt", courses1);
        String JsonData = ParseToJson.parse(courses1);
        WriteToFile.writeToAJsonFile("src/main/resources/Course/JsonCourseRegister", JsonData);
    }
}