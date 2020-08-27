package Course;

import Course.Entity.Course;
import Course.Entity.Scope;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Course.Scheduler.*;

public class WriteToSheets implements Runnable{

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String SPREADSHEET_ID;
    private static final String ROW_STATUS = "Đang xin mở lớp";
    private static String range;
    private static NetHttpTransport HTTP_TRANSPORT;
    private List<Course> courses;

    public WriteToSheets(List<Course> courses, String SPREADSHEET_ID) {
        this.courses = courses;
        this.SPREADSHEET_ID = SPREADSHEET_ID;
    }

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
     * Append values to defined range
     * @throws IOException if cannot execute getCredential()
     * @throws GeneralSecurityException if cannot initial GoogleNetHttpTransport.newTrustedTransport()
     **/
    @Override
    public void run() {
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
         * Load course data into Schedule form
         **/
        for(Course course : courses){
            for(Scope scope : course.getScope()){
                switch (scope.getWeekday()) {
                    case "Thứ 2":
                        for (List row : values) {
                            List times = Arrays.asList(scope.getTime().split("-"));
                            times.set(1, "Tiết ".concat(times.get(1).toString()));
                            for(int it=0; it<times.size(); it++) if(times.get(it).toString().length()<7) times.set(it, times.get(it).toString().concat(" "));
                            for(Object time : times){
                                if(time.toString().equals(row.get(0).toString().substring(0, 7))){
                                    row.set(1, course.getName() + scope.toString());
                                    continue;
                                }
                            }
                        }
                        break;
                    case "Thứ 3":
                        for (List row : values) {
                            List times = Arrays.asList(scope.getTime().split("-"));
                            times.set(1, "Tiết ".concat(times.get(1).toString()));
                            for(int it=0; it<times.size(); it++) if(times.get(it).toString().length()<7) times.set(it, times.get(it).toString().concat(" "));
                            for(Object time : times){
                                if(time.toString().equals(row.get(0).toString().substring(0, 7))){
                                    row.set(2, course.getName() + scope.toString());
                                    continue;
                                }
                            }
                        }
                        break;
                    case "Thứ 4":
                        for (List row : values) {
                            List times = Arrays.asList(scope.getTime().split("-"));
                            times.set(1, "Tiết ".concat(times.get(1).toString()));
                            for(int it=0; it<times.size(); it++) if(times.get(it).toString().length()<7) times.set(it, times.get(it).toString().concat(" "));
                            for(Object time : times){
                                if(time.toString().equals(row.get(0).toString().substring(0, 7))){
                                    row.set(3, course.getName() + scope.toString());
                                    continue;
                                }
                            }
                        }
                        break;
                    case "Thứ 5":
                        for (List row : values) {
                            List times = Arrays.asList(scope.getTime().split("-"));
                            times.set(1, "Tiết ".concat(times.get(1).toString()));
                            for(int it=0; it<times.size(); it++) if(times.get(it).toString().length()<7) times.set(it, times.get(it).toString().concat(" "));
                            for(Object time : times){
                                if(time.toString().equals(row.get(0).toString().substring(0, 7))){
                                    row.set(4, course.getName() + scope.toString());
                                    continue;
                                }
                            }
                        }
                        break;
                    case "Thứ 6":
                        for (List row : values) {
                            List times = Arrays.asList(scope.getTime().split("-"));
                            times.set(1, "Tiết ".concat(times.get(1).toString()));
                            for(int it=0; it<times.size(); it++) if(times.get(it).toString().length()<7) times.set(it, times.get(it).toString().concat(" "));
                            for(Object time : times){
                                if(time.toString().equals(row.get(0).toString().substring(0, 7))){
                                    row.set(5, course.getName() + scope.toString());
                                    continue;
                                }
                            }
                        }
                        break;
                    case "Thứ 7":
                        for (List row : values) {
                            List times = Arrays.asList(scope.getTime().split("-"));
                            times.set(1, "Tiết ".concat(times.get(1).toString()));
                            for(int it=0; it<times.size(); it++) if(times.get(0).toString().length()<7) times.set(0, times.get(0).toString().concat(" "));
                            for(Object time : times){
                                if(time.toString().equals(row.get(0).toString().substring(0, 7))){
                                    row.set(6, course.getName() + scope.toString());
                                    continue;
                                }
                            }
                        }
                        break;
                    case ROW_STATUS:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + scope.getWeekday());
                }
            }
        }

        return values;
    }

}
