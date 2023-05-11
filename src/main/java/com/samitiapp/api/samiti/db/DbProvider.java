package com.samitiapp.api.samiti.db;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


//@Component
public class DbProvider {

//    @Bean(name = "db-instance")
    public static Firestore instance() throws IOException {
        InputStream serviceAccount = new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId(System.getenv("GOOGLE_PROJECT_ID"))
                .setCredentials(credentials)
                .build();

        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }

}
