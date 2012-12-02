/**
 * 
 */
package edu.buffalo.cse.di.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Reference: http://stackoverflow.com/questions/6120657/how-to-generate-a-unique-hash-code-for-string-input-in-android
 * @author sravanku@buffalo.edu
 */
public class SecurityUtil {
    
    /**
     * Generate the SHA1 hashCode for the given String
     * returns hashCode of the input
     * @param input
     * @return String
     * @throws NoSuchAlgorithmException
     */
    public static String generateSHA1Hash(String input)
            throws NoSuchAlgorithmException
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes();
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return hexStr;
        }
}
