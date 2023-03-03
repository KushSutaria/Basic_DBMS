package cs.dal.ca.authentication;

public class TwoFactorAuthentication {
    /**
     * authenticate compares the hashed value of the answer stored in the database
     * and the hashed value of the answer entered by the user.
     *
     * @param question - it fetches question from the database for the username entered by the user in the console
     * @param inputAnswer - it is the input entered by the user corresponding to the answer scanner.
     * @param dbHashedAnswer - it is the hashed value of the answer stored in the database
     * @return - it returns if the hashed value of the answer entered by the user
     * and the hashed value of the answer in the database are same or not
     */
    public boolean authenticate(String question, String inputAnswer, String dbHashedAnswer) {
        //hashing the hashed value of question and answer for added privacy
        String hashedInputAnswer = Md5Hashing.getMd5Hash(Md5Hashing.getMd5Hash(question) + Md5Hashing.getMd5Hash(inputAnswer));
        return dbHashedAnswer.equals(hashedInputAnswer);
    }
}
