/**
 * 
 */
package edu.buffalo.cse.di.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class will not be used currently.
 * @author sravanku@buffalo.edu
 */
public class WebpageReader {
    
    public static String getPageSource(String url) {
        try {
            InputStream stream = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            
            String content = "";
            String line = null;
            while ((line = reader.readLine()) != null) {
                content += line;
            }
            //System.out.println(content);
            return content;
        } catch (MalformedURLException e) {
            // TODO Add LOG statement here.
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Add LOG statement here.
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Document getDocument(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        System.out.println(doc.body().text().length());
        return doc;
    }
    
    public static void main(String[] args) throws IOException {
        getDocument("http://en.wikipedia.org/wiki/IPhone_4S");
    }
    
}
