/**
 * 
 */
package edu.buffalo.cse.di.apis.entity;

/**
 * @author sravanku@buffalo.edu
 */
/**
 * @author sravanku@buffalo.edu
 */
public class GoogleProductSearchResult {
    private final String title;

    public GoogleProductSearchResult(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "GoogleProductSearchResult [title=" + title + "]";
    }
    
}
