package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "story_db";

    public static final String TABLE_NAME = "story";

    public static final String COLUMN_ID = "uid";
    public static final String COLUMN_STORY_TITLE = "story_title";
    public static final String COLUMN_STORY_FULL = "story_full";
    public static final String COLUMN_STORY_AUTHORNAME = "story_author_name";
    public static final String COLUMN_STORY_DATE = "story_date";
    public static final String COLUMN_STORY_GENRE = "story_genre";
    public static final String COLUMN_STORY_LIKES = "story_likes";
    public static final String COLUMN_STORY_IMAGEADDRESS = "story_image_address";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_STORY_TITLE + " TEXT,"
                    + COLUMN_STORY_FULL + " TEXT,"
                    + COLUMN_STORY_AUTHORNAME + " TEXT,"
                    + COLUMN_STORY_DATE + " TEXT,"
                    + COLUMN_STORY_GENRE + " TEXT,"
                    + COLUMN_STORY_LIKES + " INTEGER,"
                    + COLUMN_STORY_IMAGEADDRESS + " TEXT"
                    + ")";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create Story table
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    //insert story
    public long insertStory(Story story) {

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, story.getStoryID());
        values.put(COLUMN_STORY_TITLE, story.getStoryTitle());
        values.put(COLUMN_STORY_FULL, story.getStoryFull());
        values.put(COLUMN_STORY_AUTHORNAME, story.getStoryAuthorNAme());
        values.put(COLUMN_STORY_DATE, story.getStoryDate());
        values.put(COLUMN_STORY_GENRE, story.getStoryGenre());
        values.put(COLUMN_STORY_LIKES, story.getStoryLikes());
        values.put(COLUMN_STORY_IMAGEADDRESS, story.getStoryImageAddress());

        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Story getStory(String uid) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Story story = new Story();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_STORY_TITLE, COLUMN_STORY_FULL, COLUMN_STORY_AUTHORNAME, COLUMN_STORY_DATE, COLUMN_STORY_GENRE, COLUMN_STORY_LIKES, COLUMN_STORY_IMAGEADDRESS},
                COLUMN_ID + "=?",
                new String[]{uid}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object

        story.setStoryID(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
        story.setStoryFull(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_FULL)));
        story.setStoryTitle(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_TITLE)));
        story.setStoryAuthorNAme(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_AUTHORNAME)));
        story.setStoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_DATE)));
        story.setStoryGenre(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_GENRE)));
        story.setStoryLikes(cursor.getInt(cursor.getColumnIndex(COLUMN_STORY_LIKES)));
        story.setStoryImageAddress(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_IMAGEADDRESS)));

        // close the db connection
        cursor.close();


        return story;
    }

    public ArrayList<Story> getAllStories() {
        ArrayList<Story> stories = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Story story = new Story();
                story.setStoryID(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                story.setStoryFull(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_FULL)));
                story.setStoryTitle(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_TITLE)));
                story.setStoryAuthorNAme(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_AUTHORNAME)));
                story.setStoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_DATE)));
                story.setStoryGenre(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_GENRE)));
                story.setStoryLikes(cursor.getInt(cursor.getColumnIndex(COLUMN_STORY_LIKES)));
                story.setStoryImageAddress(cursor.getString(cursor.getColumnIndex(COLUMN_STORY_IMAGEADDRESS)));

                stories.add(story);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return stories;
    }

}
