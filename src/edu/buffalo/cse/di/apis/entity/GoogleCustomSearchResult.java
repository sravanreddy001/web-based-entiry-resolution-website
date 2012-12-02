/**
 * 
 */
package edu.buffalo.cse.di.apis.entity;

/**
 * @author sravanku@buffalo.edu
 */
public class GoogleCustomSearchResult {
    private final String title;
    private final String link;
    private final String snippet;
    private final String heading;
    
    public GoogleCustomSearchResult(String title, String link, String snippet, String heading) {
        this.title = title;
        this.link = link;
        this.snippet = snippet;
        this.heading = heading;
    }

    public String getTitle() {
        return title;
    }
    
    public String getLink() {
        return link;
    }
    
    public String getSnippet() {
        return snippet;
    }

    public String getHeading() {
		return heading;
	}

	@Override
    public String toString() {
        return "GoogleCustomSearchResult [title=" + title + ", link=" + link
                + ", heading=" + heading + ", snippet=" + snippet + "]";
    }
    
}
