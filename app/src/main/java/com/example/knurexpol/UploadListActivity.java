package com.example.knurexpol;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class UploadListActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final int READ_REQUEST_CODE_UPLOAD_LIST = 42;
    private static final int READ_REQUEST_CODE_UPLOAD_DUTY = 43;

    private static final String TAG = "UploadListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list);

        // Buttons
        findViewById(R.id.button_upload_list).setOnClickListener(this);
        findViewById(R.id.button_upload_duty).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE_UPLOAD_LIST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    Log.i(TAG, "Uri: " + generateListFromTxt(uri).toString());
                    generateDocumentsFromTxtAndSend(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == READ_REQUEST_CODE_UPLOAD_DUTY && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {

//                    Log.i(TAG, "Uri: " + generateListFromTxt(uri).toString());
                    //sendDocumentToFirebase(generateDutyListMapFromTxt(uri),"duty_list","documents");
                    generateDutyListFromTxtAndSend(uri);

                    //generateDocumentsFromTxtAndSend(uri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void sendListToFirebase(Map<String,Object> map){
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
    private JSONObject generateDutyListFromTxt(Uri uri) throws IOException {
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
    private JSONObject generateListFromTxt(Uri uri) throws IOException {
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
    private Map<String,Object> generateDutyListMapFromTxt(Uri uri) throws IOException {

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
    private Map<String,Object> generateMapFromTxt(Uri uri) throws IOException {

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

    private void sendDocumentToFirebase(Map<String,Object> map, String documentName, String collection){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(collection).document(documentName)
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
    private void generateDocumentsFromTxtAndSend(Uri uri) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

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
                sendDocumentToFirebase(userDetails,name+"."+surname,"allowed_users");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generateDutyListFromTxtAndSend(Uri uri) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
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
                sendDocumentToFirebase(userDetails,splitLine[0],"duties");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String readTextFromUri(Uri uri) throws IOException {
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


    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch(int requestCode) {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/plain");

        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_upload_list) {
            performFileSearch(READ_REQUEST_CODE_UPLOAD_LIST);
        }else if (i == R.id.button_upload_duty) {
            performFileSearch(READ_REQUEST_CODE_UPLOAD_DUTY);
        }
    }
}
