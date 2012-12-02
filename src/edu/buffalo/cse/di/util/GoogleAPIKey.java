/**
 * 
 */
package edu.buffalo.cse.di.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * @author sravanku@buffalo.edu
 */
public class GoogleAPIKey {

    private static Properties properties;
    private static Set<Object> keySet;
    private static Iterator<Object> iter;

    static {
        properties = new Properties();
        InputStream input = GoogleAPIKey.class.getResourceAsStream
                    ("./google-api-keys.properties");
        try {
            if( input != null ) {
                properties.load(input);
                keySet = properties.keySet();
                iter = keySet.iterator();
            }
            else {
                // TODO Add LOG statement here.
                System.out.println("Input Stream is NULL");
            }
        } catch (IOException e) {
            // TODO Add LOG statement here.
            e.printStackTrace();
        }
    }

    public static String getGoogleAPIKey(final String key){
        return properties.getProperty(key);
    }

    public static String getGoogleAPIKey(){
        if(iter.hasNext()) {
            return getGoogleAPIKey((String) iter.next());
        }
        else {
            iter = keySet.iterator();
            if(iter.hasNext()) {
                return getGoogleAPIKey((String) iter.next());
            }
        }
        return null;
    }

    public static int getKeyValuePairsCount() {
        return properties.size();
    }

}
