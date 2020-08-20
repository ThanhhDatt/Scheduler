package Book;

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

public class BookInfoCrawler {
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
        InputStream in = BookInfoCrawler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        String path="https://docs.google.com/spreadsheets/d/1ZftYM8iagV7zlMY2U-ERhEYmFyKER9Quheo4YQLikMI/edit#gid=350250994";
        SPREADSHEET_ID = getSheetsID(path);
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String range = "!A1:G2184";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        String[] attributes = new String[7];
        ArrayList<Book> books = new ArrayList<>();
        attributes[0] = (String) values.get(0).get(0);
        attributes[1] = (String) values.get(0).get(1);
        attributes[2] = (String) values.get(0).get(2);
        attributes[3] = (String) values.get(0).get(3);
        attributes[4] = (String) values.get(0).get(4);
        attributes[5] = (String) values.get(0).get(5);
        attributes[6] = (String) values.get(0).get(6);
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            int index = values.size();
            for (List row : values) {
                String[] tmp = new String[7];
                System.out.println("ID: " + (values.size()-index+1));
                for(int i=0; i<row.size(); i++){
                    if(row.get(i)==""){
                        System.out.println(attributes[i] + ": " + "No data");
                        tmp[i]="No data";
                    } else{
                        System.out.println(attributes[i] + ": " + row.get(i));
                        tmp[i]= (String) row.get(i);
                    }
                }
                for(int j=row.size(); j<7; j++){
                    System.out.println(attributes[j] + ": " + "No data");
                    tmp[j]="No data";
                }
                tmp[0]= String.valueOf((values.size()-index+1));
                for(int i=0; i<7; i++){
                    Book book = new Book();
                    book.setID(tmp[i]);
                    book.setName(tmp[i]);
                    book.setShelf(tmp[i]);
                    book.setInitAmount(tmp[i]);
                    book.setCurrentAmount(tmp[i]);
                    book.setAuthor(tmp[i]);
                    book.setCategory(tmp[i]);
                    books.add(book);
                }
                System.out.println("\n");
                index--;
            }
        }
        System.out.print(books);
//        writeToAJsonFile("")
    }
}