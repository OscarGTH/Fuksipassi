package com.example.osku.fuksipassi;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Osku on 11.4.2018.
 */

public class TempDataReader {
    final String COMPLETED_CHALLENGES = "compChallenges.txt";
    final String CHALLENGES_FILE = "challenges.txt";
    final int ZERO = 0;
    Context context;


    public TempDataReader(Context context) {
        this.context = context;
    }


    // Reads the text file.
    public List<List<String>> readFile(String read_type, boolean raw) {
        List<List<String>> super_list = new ArrayList<>();
        List<String> title_list = new ArrayList<>();
        List<String> desc_list = new ArrayList<>();
        List<String> date_list = new ArrayList<>();
        FileInputStream fileInputStream;
        BufferedReader reader = null;
        // If the reading is targeted for challenge list, initialize the reader for it.
        if(read_type == "challenges"){
            try {
                // Checking if there are already a text file for challenges in internal directory.
                File challFile = context.getFileStreamPath(CHALLENGES_FILE);
                if (challFile.exists() && !raw)  {
                    fileInputStream = context.openFileInput(CHALLENGES_FILE);
                    // Set the reader to read from internal storage instead of raw directory.
                    reader = new BufferedReader(
                            new InputStreamReader(fileInputStream));
                } else {
                    // If the user has not completed any challenges, reader will read from raw directory.
                    InputStream inputStream = context.getResources().openRawResource(R.raw.chall_file);
                    reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                }
            } catch (IOException e) {
                //log the exception
                e.printStackTrace();
                return null;
            }
            // Go here if reading is targeted for completed list of challenges.
        } else{
                // Checking if text file for completed challenges already exists.
                File compFile = new File(context.getFilesDir(),"compChallenges.txt");
                if(!compFile.exists()){
                    try {
                        // Creating the text file for completed challenges.
                        compFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Initializing reader for completed challenges file.
                try {
                    fileInputStream = context.openFileInput(COMPLETED_CHALLENGES);
                    InputStreamReader isr = new InputStreamReader(fileInputStream);
                    reader = new BufferedReader(isr);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
        // do reading
        try {
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.length() > ZERO) {
                    // Replacing BOM character with empty character.
                    mLine = mLine.replace("\uFEFF", "");
                    // If the line starts with T, add the line to title list.
                    if (mLine.charAt(ZERO) == 'T') {
                        // Taking the substring of the line, so the word "Title: " will be excluded.
                        title_list.add(mLine.substring(7));
                    } else if (mLine.charAt(ZERO) == 'D') {
                        desc_list.add(mLine.substring(13));
                    } else if (Character.isDigit(mLine.charAt(ZERO))) {
                        date_list.add(mLine);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Closing readers and throwing exceptions if needed.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();
                    return null;
                }
            }
        }

        // Adding all the sub lists to the main list.
        super_list.add(title_list);
        super_list.add(desc_list);
        // If program is reading completed challenges list, add the date list to the main list also.
        if(read_type.equals("completed")){
            super_list.add(date_list);
        } else {
            super_list.add(null);
        }
        // Returning the list containing all read data.
        return super_list;
    }
    // Method removes completed challenge from the list containing current challenges.
    public void removeChallenge(int index) {
        BufferedReader reader;
        FileOutputStream fos;
        FileInputStream fis;
        // Row index where the deletion will stop.
        int endDelete = 3*index;
        // Row index where the deletion will start.
        int startDelete = endDelete - 2;
        File challFile = context.getFileStreamPath(CHALLENGES_FILE);
        // Creating a new file to store the updated challenge list.
        File updatedChallFile = new File(context.getFilesDir(),"currentChallenges.txt");
        if(updatedChallFile.exists()) {
            updatedChallFile.delete();
        }
        try {
            // Check if there is a text file for challenges in internal memory.
            if(!challFile.exists()){
                // If there is not, creating a new file and storing the text from raw folder challenge file into internal memory.
                challFile.createNewFile();
                try {
                    InputStream inputStream = context.getResources().openRawResource(R.raw.chall_file);
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    reader = new BufferedReader(isr);
                    fos = context.openFileOutput(CHALLENGES_FILE, Context.MODE_APPEND);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
                    String line;
                    while ((line = reader.readLine()) != null){
                        outputStreamWriter.write(line+'\n');
                    }
                    outputStreamWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updatedChallFile.createNewFile();

            // Reading the challenge file and writing it to updated file but skipping the lines to be deleted.
            fis = context.openFileInput(CHALLENGES_FILE);
            reader = new BufferedReader(
                    new InputStreamReader(fis));
            fos = context.openFileOutput("currentChallenges.txt", Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            String line;
            int linecounter = 1;
            while((line = reader.readLine()) != null){
                if(!(linecounter >= startDelete && linecounter <= endDelete)){
                    outputStreamWriter.write(line+'\n');
                }
            linecounter++;
            }
            outputStreamWriter.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Renaming the new updated list for challenges.
      File newFile = context.getFileStreamPath("currentChallenges.txt");
        File oldFile = context.getFileStreamPath(CHALLENGES_FILE);
        newFile.renameTo(oldFile);

    }

    // Method writes list given in parameters into a text file consisting of completed challenges.
    public void saveFile(List<String> list) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(COMPLETED_CHALLENGES, Context.MODE_APPEND);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    fos.write("Title: ".getBytes());
                } else if (i == 1) {
                    fos.write("Description: ".getBytes());
                }
                fos.write(list.get(i).getBytes());
                fos.write("\r".getBytes());
            }
        } catch (IOException e) {
            System.out.println("Could not find comp_file or some other error!");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // Method authorizes the hash value by comparing the values.
    public boolean authorize(String hashValue){
        BufferedReader reader;
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.hash);
            reader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        try {
            String mLine;
            hashValue= hashValue.replace(" ", "");
            while ((mLine = reader.readLine()) != null) {
                if(mLine.equals(hashValue.substring(0,(hashValue.length()-1)))){
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
}


