package ca.dal;

import java.io.*;
import java.util.Scanner;

import static ca.dal.StaticVariables.*;

public class DataProcessingEngine {
    static int counter = 0;
    static int file_number = 1;

    /**
     *
     * @param title - title fromm newsAPI data
     * @param content - content from the newsAPI data
     *
     * This method captures the data from Code A and saves them in files. Each file will have 5 articles
     * and the last files will have 5 or less articles depending on the data.
     *
     * @throws IOException
     */
    public static void DataProcessor(String title, String content) throws IOException {
        File track_number = new File(USERDATA_FILE_PATH + FILE_TRACK);
        if (!track_number.exists()) {
            track_number.createNewFile();
            try (PrintWriter trackpw = new PrintWriter(USERDATA_FILE_PATH + FILE_TRACK)) {
                trackpw.write(Integer.toString(file_number));
                trackpw.write("\n");
                trackpw.write(Integer.toString(counter));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(USERDATA_FILE_PATH + FILE_TRACK))) {
            file_number = Integer.parseInt(br.readLine());
            counter = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.out.println(e);
            throw new IOException();
        }


        try (PrintWriter trackpw = new PrintWriter(USERDATA_FILE_PATH + FILE_TRACK)) {
            trackpw.write(Integer.toString(file_number));
            trackpw.write("\n");
            trackpw.write(Integer.toString(counter));
        } catch (IOException e) {
            e.printStackTrace();
        }


        File file = new File(StaticVariables.USERDATA_FILE_PATH + FILE_NAME + file_number);
        if (file_number == 1) {
            if (!file.exists())
                file.createNewFile();

        }
        Scanner myReader;
        myReader = new Scanner(title);
        while (myReader.hasNextLine()) {
            counter++;
            WriteInTracker();
            FileWriter fileWriter = new FileWriter(file, true);

            fileWriter.write(DELIMITER + "Article:" + (counter));
            fileWriter.write("\n");
            fileWriter.write(title);
            fileWriter.write("\n");
            if (!(content == null)) {
                fileWriter.write(content);
            }
            fileWriter.write("\n");
            fileWriter.close();
            myReader.nextLine();

            if (counter == FILE_CAPACITY) {
                counter = 0;
                TransformationEngine.transform(file);
                file_number++;

                WriteInTracker();
                file = new File(StaticVariables.USERDATA_FILE_PATH + FILE_NAME + file_number); //add file name according to the article name

            }

        }
    }

    /**
     * This method is for keeping a track of file number and counter in the file_track.txt file
     * It is to make sure that each file has only 5 articles at max and if there is more data, new file will be created
     */
    private static void WriteInTracker() {
        try (PrintWriter trackpw = new PrintWriter(USERDATA_FILE_PATH + FILE_TRACK)) {
            trackpw.write(Integer.toString(file_number));
            trackpw.write("\n");
            trackpw.write(Integer.toString(counter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}