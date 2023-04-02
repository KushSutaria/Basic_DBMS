package ca.dal;

public class StaticVariables {
    //public static final String API_KEY = "38a70881c40c4eb1b75fcb5bf5af6ea1";
    //public static final String API_KEY="7a363d8a9a0449fcb7cefb63881f2c3c";
    public static final String API_KEY = "ac204200f13c407aa79405caffbaef3c";
    public static final String USERDATA_FILE_PATH = "src/main/resources/";
    public static final int FILE_CAPACITY = 5;
    public static final String DELIMITER = "---------------newdata----------------";
    public static final String TXT_EXTENSION = ".txt";
    public static final String FILE_NAME = "File_";
    public static final String FILE_TRACK = "file_track.txt";
    public static final String EMOJI_REGEX= "[\\u2600-\\u27FF\\uD83C\\uDF00-\\uD83D\\uDDFF\\uD83E\\uDD00-\\uD83E\\uDDFF\\uD83D\\uDE00-\\uD83D\\uDECF\\uDEFF\\uD83E\\uDD10-\\uD83E\\uDDFF]";

    public static final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    public static final String SPECIAL_CHAR_REGEX = "[^A-Za-z0-9\s+]";
    public static final String HTML_TAG_REGEX = "<[^>]*>";

    public static final String MONGODB_CONNECTION= "mongodb+srv://newUser:i0ul0O3OOfV5hRTA@atlascluster.9xolhec.mongodb.net/?retryWrites=true&w=majority";

    public static final String MONGODB_DATABASE_NAME="myMongoNews";
    public static final String MONGODB_COLLECTION_NAME="News";
    public static final String TITLE="title";
    public static final String CONTENT="content";

}
