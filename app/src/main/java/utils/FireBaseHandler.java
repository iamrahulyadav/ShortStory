package utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SimpleTimeZone;

/**
 * Created by Aisha on 8/11/2017.
 */

public class FireBaseHandler {
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mFirebaseDatabase;


    public FireBaseHandler() {

        mFirebaseDatabase = FirebaseDatabase.getInstance();

    }


    public void uploadStory(final Story story, final OnStorylistener OnStorylistener) {

        mFirebaseDatabase.getReference().push().setValue(story).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                OnStorylistener.onStoryDownLoad(story, true);
                OnStorylistener.onStoryUpload(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failed to Upload Story", e.getMessage());

                OnStorylistener.onStoryUpload(false);
                OnStorylistener.onStoryDownLoad(null, false);
            }
        });


    }

    public void downloadStory(String storyUID, final OnStorylistener onStorylistener) {


        DatabaseReference myRef = mFirebaseDatabase.getReference().child(storyUID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Story story = dataSnapshot.getValue(Story.class);

                if (story != null) {
                    story.setStoryID(dataSnapshot.getKey());
                }
                onStorylistener.onStoryDownLoad(story, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onStorylistener.onStoryDownLoad(null, false);
            }
        });


    }


    public void downloadStoryList(int limit, final OnStorylistener onStorylistener) {

        Query myref2 = mFirebaseDatabase.getReference().limitToLast(limit);

        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Story> storyArrayList = new ArrayList<Story>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Story story = snapshot.getValue(Story.class);
                    if (story != null) {

                        story.setStoryID(snapshot.getKey());

                    }
                    storyArrayList.add(story);
                }

                Collections.reverse(storyArrayList);

                onStorylistener.onStoryListDownLoad(storyArrayList, true);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onStorylistener.onStoryListDownLoad(null, false);

            }
        });


    }


    //interface
    public interface OnStorylistener {


        public void onStoryDownLoad(Story story, boolean isSuccessful);

        public void onStoryListDownLoad(ArrayList<Story> storyList, boolean isSuccessful);


        public void onStoryUpload(boolean isSuccessful);
    }


}
