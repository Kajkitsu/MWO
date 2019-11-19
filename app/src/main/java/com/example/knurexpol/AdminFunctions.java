package com.example.knurexpol;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminFunctions {

    public static final int READ_REQUEST_CODE_UPLOAD_LIST = 42;
    public static final int READ_REQUEST_CODE_UPLOAD_DUTY = 43;



    //TODO ProgresDialog i upload button i weryfikacja danych

    private static final String TAG = "AdminFunctions";

    public static void sendDocumentToFirebase(Map<String, Object> map, String documentName, String collection, final ProgressDialog progressDialog){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(collection).document(documentName)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        if(progressDialog!=null) incProgressDialog(progressDialog);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(progressDialog!=null) progressDialog.dismiss();
                        Toast.makeText(progressDialog.getContext(),"Error writing document",Toast.LENGTH_LONG).show();

                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void generateDocumentsFromTxtAndSend(Uri uri, ContentResolver contetnt , ProgressDialog progressDialog) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream =
                     contetnt.openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String surname = line.substring(line.indexOf(" ") + 1);
                String name = line.substring(0,line.indexOf(" "));

                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("name", name);
                userDetails.put("surname", surname);
                sendDocumentToFirebase(userDetails,name+"."+surname,"allowed_users",progressDialog);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void generateDutyListFromTxtAndSend(Uri uri, ContentResolver contetnt, ProgressDialog progressDialog) throws IOException {
        try (InputStream inputStream =
                     contetnt.openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, Object> userDetails = new HashMap<>();
                String[] splitLine = line.split(" ");
                Log.w(TAG, " String[] splitLine = "+ Arrays.toString(splitLine));
                userDetails.put("date", splitLine[0]);
                userDetails.put("firstName", splitLine[2]);
                userDetails.put("firstSurname", splitLine[3]);
                userDetails.put("secondName", splitLine[5]);
                userDetails.put("secondSurname", splitLine[6]);
                sendDocumentToFirebase(userDetails,splitLine[0],"duties", progressDialog);
            }
        }
    }

    public static void incProgressDialog(final ProgressDialog progressDialog){
        // Start Process Operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                progressDialog.setProgress(progressDialog.getProgress()+1);
            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getNumberOfLines(Uri uri, ContentResolver contetnt) throws IOException {
        int numberOfLines = 0;
        try (InputStream inputStream =
                     contetnt.openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                numberOfLines++;
            }
        }
        return numberOfLines;
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }


    public static void sendListToFirebase(Map<String,Object> map){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("documents").document("listOfUsers")
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JSONObject generateDutyListFromTxt(Uri uri) throws IOException {
        JSONArray userList = new JSONArray();

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String splitLine[] = line.split(" ");
                JSONObject userDetails = new JSONObject();
                userDetails.put("date", splitLine[0]);
                userDetails.put("firstName", splitLine[2]);
                userDetails.put("firstSurname", splitLine[3]);
                userDetails.put("secondName", splitLine[5]);
                userDetails.put("secondSurname", splitLine[6]);


//                String data = line.substring(0,line.indexOf(" "));
//                String rest = line.substring(line.indexOf(" ") + 1);
//                String firstName = rest.substring(0,rest.indexOf(" ")).substring(0,rest.indexOf(" "));
//                rest.
//
//                JSONObject userDetails = new JSONObject();
//                userDetails.put("name", name);
//                userDetails.put("surname", surname);

                userList.put(userDetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("users", userList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public JSONObject generateListFromTxt(Uri uri) throws IOException {
        JSONArray userList = new JSONArray();

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String surname = line.substring(line.indexOf(" ") + 1);
                String name = line.substring(0,line.indexOf(" "));

                JSONObject userDetails = new JSONObject();
                userDetails.put("name", name);
                userDetails.put("surname", surname);

                userList.put(userDetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("users", userList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Map<String,Object> generateDutyListMapFromTxt(Uri uri) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Object> arrayDutys = new ArrayList<>();

        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(" ");
                Log.w(TAG, " String[] splitLine = "+ Arrays.toString(splitLine));
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("date", splitLine[0]);
                userDetails.put("firstName", splitLine[2]);
                userDetails.put("firstSurname", splitLine[3]);
                userDetails.put("secondName", splitLine[5]);
                userDetails.put("secondSurname", splitLine[6]);
                arrayDutys.add(userDetails);
            }
        }
        Map<String, Object> userList = new HashMap<>();
        userList.put("duty", arrayDutys);
        return userList;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Map<String,Object> generateMapFromTxt(Uri uri) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Object> arrayUsers = new ArrayList<>();

        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String surname = line.substring(line.indexOf(" ") + 1);
                String name = line.substring(0,line.indexOf(" "));

                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("name", name);
                userDetails.put("surname", surname);
                arrayUsers.add(userDetails);
            }
        }
        Map<String, Object> userList = new HashMap<>();
        userList.put("users", arrayUsers);
        return userList;
    }
     */

}
