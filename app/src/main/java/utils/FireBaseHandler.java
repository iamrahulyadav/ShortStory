package utils;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import app.story.craftystudio.shortstory.MainActivity;

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


        mDatabaseRef = mFirebaseDatabase.getReference().child("shortStory/");

        story.setStoryID(mDatabaseRef.push().getKey());

        DatabaseReference mDatabaseRef1 = mFirebaseDatabase.getReference().child("shortStory/" + story.getStoryID());


        mDatabaseRef1.setValue(story).addOnCompleteListener(new OnCompleteListener<Void>() {
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


        DatabaseReference myRef = mFirebaseDatabase.getReference().child("shortStory/" + storyUID);

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


        mDatabaseRef = mFirebaseDatabase.getReference().child("shortStory/");

        Query myref2 = mDatabaseRef.limitToLast(limit);

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

    public void downloadStoryList(int limit, String lastShortStoryID, final OnStorylistener onStorylistener) {


        mDatabaseRef = mFirebaseDatabase.getReference().child("shortStory/");

        Query myref2 = mDatabaseRef.orderByKey().limitToLast(limit).endAt(lastShortStoryID);

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

                storyArrayList.remove(storyArrayList.size() - 1);
                Collections.reverse(storyArrayList);
                onStorylistener.onStoryListDownLoad(storyArrayList, true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onStorylistener.onStoryListDownLoad(null, false);

            }
        });


    }


    public void uploadLike(Like like, final OnLikeListener onLikeListener) {
        mDatabaseRef = mFirebaseDatabase.getReference().child("likes/");
        mDatabaseRef.push().setValue(like).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onLikeListener.onLikeUpload(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                onLikeListener.onLikeUpload(true);
            }
        });


    }


    public void uploadStoryLikes(String storyUID, int likes, final OnLikeListener onLikeListener) {

        DatabaseReference myRef = mFirebaseDatabase.getReference().child("shortStory/" + storyUID + "/storyLikes");

        myRef.setValue(likes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onLikeListener.onLikeUpload(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onLikeListener.onLikeUpload(false);
            }
        });


    }

    //interface

    public interface OnLikeListener {
        void onLikeUpload(boolean isSuccessful);
    }

    public interface OnStorylistener {


        public void onStoryDownLoad(Story story, boolean isSuccessful);

        public void onStoryListDownLoad(ArrayList<Story> storyList, boolean isSuccessful);


        public void onStoryUpload(boolean isSuccessful);
    }


}
