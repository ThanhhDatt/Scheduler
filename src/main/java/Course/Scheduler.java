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
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
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
        Stack<Course> course1 = new Stack<Course>();
        int index = 0;
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List row : values) {
                Course course = new Course();
                ArrayList<Scope> scopes = new ArrayList<>();
                Scope scope = new Scope();
                course.setId(course1.size());
                course.setName((String) row.get(1));
                course.setCourseCode((String) row.get(2));
                /**
                 * Ignored merged cells case that following cell return null
                 ********** HARD CODE *******
                 **/
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
                    course.setId(course1.size()-1);
                    course.setName((String) values.get(index-1).get(1));
                    course.setCourseCode((String) values.get(index-1).get(2));
                    Scope scope1 = new Scope();
                    scope1.setScope((String) row.get(3));
                    scope1.setTime((String) row.get(4));
                    scope1.setWeekday((String) row.get(5));
                    scope1.setLocation((String) row.get(6));
                    scopes.add(scope1);
                    course1.pop();
                }
                course.setScope(scopes);
                course1.add(course);
                index++;
            }
        }

//        TODO:
//          - Add course info into table
//          - Create schedule base on course info

        /**Show course info*/
        for(Course course : course1){
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
        courses = course1;

        /**
         Write data to file (text file and json file) for future using
         **/
        WriteToFile.writeToFile("src/main/resources/Course/CourseRegister.txt", courses);
        String JsonData = ParseToJson.parse(courses);
        WriteToFile.writeToAJsonFile("src/main/resources/Course/JsonCourseRegister", JsonData);
        WriteToSheets();
    }

    /**
     * Append values to defined range
     * @throws IOException if cannot execute getCredential()
     * @throws GeneralSecurityException if cannot initial GoogleNetHttpTransport.newTrustedTransport()
     **/
    public static void WriteToSheets() throws IOException {
        try{
            range = "!A31:Z500";
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            ClearValues(range);

            List<List<Object>> values = loadCourseInfo(courses);

            ValueRange appendBody = new ValueRange().setValues(values);
            AppendValuesResponse appendResult = service.spreadsheets().values()
                    .append(SPREADSHEET_ID, range, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("OVERWRITE")
                    .setIncludeValuesInResponse(true)
                    .execute();
        } catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Clear cell values by defined range
     * @throws IOException if cannot execute getCredential()
     * @throws GeneralSecurityException if cannot initial GoogleNetHttpTransport.newTrustedTransport()
     **/
    public static void ClearValues(String range){
        try{
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            /**Write values to defined ranged*/
            ClearValuesRequest clearValuesRequest = new ClearValuesRequest();
            service.spreadsheets().values().clear(SPREADSHEET_ID, range, clearValuesRequest).execute();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Load course info into dynamic 2 dimension array
     * @return Array with course info and place into right cell with time constraint
     **/
    public static List<List<Object>> loadCourseInfo(List<Course> courses) throws IOException {
        /**
         * Init schedule form with no course data
         **/
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        List<List<Object>> values = new ArrayList<>();
        List<Object> rows = new ArrayList<>();
        rows.add("Thời gian");
        for(int i=2; i<=7; i++){
            String weekday = "Thứ " + i;
            rows.add(weekday);
        }
        values.add(rows);
        for(int j=0; j<12; j++){
            List<Object> tmpRows = new ArrayList<>();
            String lessonTime = "Tiết " + (j+1) + " (" + (j+7) + "h - " + (j+8) + "h)";
            tmpRows.add(lessonTime);
            for(int k=0; k<6; k++){
                tmpRows.add("");
            }
            values.add(tmpRows);
        }

        /**
         * Load course data intodule form
         **/
        for(Course course : courses){
            for(Scope scope : course.getScope()){
                switch (scope.getWeekday()){
                    case "Thu 2":

                        break;
                    case "Thu 3":

                        break;
                    case "Thu 4":

                        break;
                    case "Thu 5":

                        break;
                    case "Thu 6":

                        break;
                    case "Thu 7":

                        break;
                }
            }
        }

        return values;
    }

}