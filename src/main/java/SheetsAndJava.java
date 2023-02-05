import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2. GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;


public class SheetsAndJava {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Google Sheets Example";
    private static String SPREADSHEET_ID = "1__p0m01RTG_NPszezXZNyfN920LIliUI2glKWuiE5pE";
    private static String SPREADSHEET_ID2 = "1DRKPCzd5O4YhMxfuBwORAtRHpZROaJBA_FnvF5g_bsg";
    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in=SheetsAndJava.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize( "user");
        return credential;
    }
    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        sheetsService = getSheetsService();
        String range = "A1:R4";
        DeleteDimensionRequest deleteRequest =new DeleteDimensionRequest()
                .setRange(
                        new DimensionRange()
                                .setSheetId(0)
                                .setDimension("ROWS")
                                .setStartIndex(1)
                );
        List<Request> requests=new ArrayList<>();
        requests.add(new Request().setDeleteDimension(deleteRequest));
        BatchUpdateSpreadsheetRequest body=new BatchUpdateSpreadsheetRequest().setRequests(requests);
        sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID2,body).execute();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();
        List<List<Object>> values = response.getValues();
        String a="";
        String b="";
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for(int i = 1; i < values.size(); i++) {
                System.out.println(values.get(i).get(1) + " " + values.get(i).get(14));
                a= (String) values.get(i).get(1);
                b= (String) values.get(i).get(14);

                ValueRange appendBody = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList(a,b)
                        ));
                AppendValuesResponse appendResult=sheetsService.spreadsheets().values()
                        .append(SPREADSHEET_ID2,"A1:B1",appendBody)
                        .setValueInputOption("USER_ENTERED")
                        .setInsertDataOption("INSERT_ROWS")
                        .setIncludeValuesInResponse(true)
                        .execute();
            }
        }



    }


}