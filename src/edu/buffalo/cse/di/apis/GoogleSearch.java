/**
 * 
 */
package edu.buffalo.cse.di.apis;

/**
 * Methods common to both the custom search and product search
 * @author sravanku@buffalo.edu
 */
public abstract class GoogleSearch {
    
    /**
     * Very simple implementation of the query formatting.
     * Replaces spaces with +
     * @param query
     * @return
     */
    public static String formatQuery(String query) {
        // TODO modify this for a complex implementation.
        return query.replace(" ", "+");
    }
}
