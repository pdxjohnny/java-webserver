package net.carpoolme.storage;

import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by John Andersen on 6/1/16.
 */
public class Hash {
    public static String sha256(Serializer serializer, Object[][] object) throws NoSuchAlgorithmException {
        DigestInputStream in = new DigestInputStream(serializer.toInputStream(object), MessageDigest.getInstance("SHA-256"));
        try {
            while (in.read() != -1) {
                // Put it all though the hash function
                // This should never cause an IExceptions because we have just serialized the object
            }
        } catch (IOException ignored) {}

        // Create the output
        String result = "";
        for (final byte b : in.getMessageDigest().digest()) {
            result += String.format("%02x", b);
        }

        return result;
    }
}
