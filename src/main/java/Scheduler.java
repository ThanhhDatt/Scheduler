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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Scheduler {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static String SPREADSHEET_ID = "";

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
        final String range = "!A2:G12";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        ArrayList<Course> courses = new ArrayList<>();
        int index = values.size();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
                for(int i=0; i<row.size(); i++){
                    switch (i){
                        case 0:
                            System.out.print("ID: ");
                            break;
                        case 1:
                            System.out.print("Name: ");
                            break;
                        case 2:
                            System.out.print("Code: ");
                            break;
                        case 3:
                            System.out.print("Scope: ");
                            break;
                        case 4:
                            System.out.print("Time: ");
                            break;
                        case 5:
                            System.out.print("Weekday: ");
                            break;
                        case 6:
                            System.out.print("Location: ");
                            break;
                    }
                    System.out.println(row.get(i));
                }
                System.out.println("\n");
            }
        }
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
                Course course = new Course();
                Scope scope = new Scope();
//                scope.setScope(row.get());
            }
        }
        System.out.println("\n \n");
        for(Course course : courses) System.out.println(course.getName());
        WriteToFile.writeToFile("src/main/resources/CourseRegister.txt", courses);
        String JsonData = ParseToJson.parse(courses);
        WriteToFile.writeToAJsonFile("src/main/resources/JsonCourseRegister", JsonData);
    }
}