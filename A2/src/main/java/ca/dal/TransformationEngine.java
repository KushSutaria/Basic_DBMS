package ca.dal;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.dal.StaticVariables.*;

public class TransformationEngine {
    static Document document = null;

    public static void transform(File file) throws IOException {
        MongoCollection<Document> mongoCollection = null;

        ConnectionString connectionString = new ConnectionString(MONGODB_CONNECTION);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        mongoClient.getDatabase(MONGODB_DATABASE_NAME);


        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGODB_DATABASE_NAME);
        mongoCollection = mongoDatabase.getCollection(MONGODB_COLLECTION_NAME);


        Scanner myReader;

        myReader = new Scanner(file);
        int line_count = 1;
        while (myReader.hasNext()) {
            String readData = myReader.nextLine();

            if (line_count % 3 == 1) {
                document = new Document("_id", new ObjectId());
                readData = readData.replace(readData, "\n");
            } else if (line_count % 3 == 2) {
                readData = readData.replace(readData, cleanData(readData));
                document.append(TITLE, cleanData(readData));
            } else {
                document.append(CONTENT, cleanData(readData));
                mongoCollection.insertOne(document);
            }

            readData = readData.replace(readData, cleanData(readData));

            line_count++;

        }
        myReader.close();

    }


    public static String cleanData(String s) {

        Pattern url_pattern = Pattern.compile(URL_REGEX);
        Matcher matcher_url = url_pattern.matcher(s);
        String result = matcher_url.replaceAll("");

        Pattern html_tag_pattern = Pattern.compile(HTML_TAG_REGEX);
        Matcher matcher_tag = html_tag_pattern.matcher(result);
        result = matcher_tag.replaceAll(" ");


        Pattern emoji_regex_pattern = Pattern.compile(EMOJI_REGEX);
        Matcher matcher_emoji = emoji_regex_pattern.matcher(result);
        result = matcher_emoji.replaceAll("");


        Pattern special_char_pattern = Pattern.compile(SPECIAL_CHAR_REGEX);
        Matcher matcher_special_char = special_char_pattern.matcher(result);
        result = matcher_special_char.replaceAll("");


        return result;
    }
}


