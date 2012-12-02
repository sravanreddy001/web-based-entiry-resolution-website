/**
 * 
 */
package edu.buffalo.cse.di.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.buffalo.cse.di.util.algorithm.KNNAlgorithm;
import edu.buffalo.cse.di.util.entity.Node;

/**
 * Calculate the metrics related to Jaccard coefficient.
 * @author sravanku@buffalo.edu
 */
public class SimilarityScore {
    
    public enum SimilarityType {
        JACCARD,
        CUSTOM
    }
    
    /**
     * Given two string as input return the JaccardCoefficient of both.
     * Make use of Stemmer for better scores.
     * @param str1
     * @param str2
     * @return <b>double</b> jaccard coefficient of str1 and str2
     */
    public static double getJaccardSimilarty(String str1, String str2) {
        List<String> tokens1 = getTokens(str1);
        List<String> tokens2 = getTokens(str2);
        
        return getJaccardSimilartyForTokens(tokens1, tokens2);
    }

    public static double getJaccardSimilartyForTokens(List<String> tokens1, List<String> tokens2) {
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        Set<String> union = new HashSet<String>();
        
        set1.addAll(tokens1);
        set2.addAll(tokens2);
        union.addAll(set1);
        union.addAll(set2);
        
        return ((double)(set1.size() + set2.size() - union.size()))/union.size();
    }
    
    public static double getCustomSimilarity(String str1, String str2) {
        List<String> tokens1 = getTokens(str1);
        List<String> tokens2 = getTokens(str2);
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        Set<String> union = new HashSet<String>();

        set1.addAll(tokens1);
        set2.addAll(tokens2);
        union.addAll(set1);
        union.addAll(set2);
        
        int minSize = (set1.size() <= set2.size()) ? set1.size() : set2.size();
        
        return ((double)(set1.size() + set2.size() - union.size()))/minSize;
    }
    
    public static Node getBestNodeForCluster(List<Node> cluster) {
        double[] sumScores = new double[cluster.size()];
        for(int i=0;i < cluster.size(); i++) {
            for(int j=0; (j < cluster.size()) && (i != j); j++) {
                sumScores[i] += KNNAlgorithm.getDistanceBetweenNodes(cluster.get(i), cluster.get(j), null);
            }
        }
        double topScore = 0.0;
        int index = 0;
        for(int i=0;i<sumScores.length; i++) {
            if(topScore < sumScores[i]) {
                topScore = sumScores[i];
                index = i;
            }
        }
        return cluster.get(index);
        
    }
    
    /**
     * Given two list of strings as input return the the string with Highest SumSimilarityScore --- Used for determining the heading associated with the entity record ri and also for heading of authority file
     * @param headList --- Heading of Top K Documents for the entity record ri
     * @return
     */ 
    public static String getMaxSumSimilarity(List<String> headList) {
    	Iterator<String> list1iter = headList.iterator();
    	Iterator<String> list2iter = null;
    	double sumSim = 0;
    	String highSimScoreStr = "";
    	double maxSim = 0;
    	while(list1iter.hasNext()) {
    		String str1 = (String)list1iter.next();
    		sumSim = 0;
    		list2iter = headList.iterator();
    		while(list2iter.hasNext()) {
    			String str2 = (String)list2iter.next();
    			if (str1.equals(str2)) {
    				continue;
    			}
    			else {
    				double simH = getJaccardSimilarty(str1, str2);
        			sumSim = sumSim + simH;
    			}
    		}
    		if (sumSim > maxSim) {
    			maxSim = sumSim;
    			highSimScoreStr = str1;
    		} 
    		else {
    			continue;
    		}
    	}
    	return highSimScoreStr;
    }
    
    private static List<String> getTokens(String str) {
        String[] tokens = str.toLowerCase().split("[~`!@#$%^&()_-|{}][;:,' ,]");
        List<String> list = new ArrayList<String>();
        for(int i=0;i<tokens.length; i++) {
            if(tokens[i].length() > 1) {
                list.add(tokens[i]);
            }
        }
        return list;
    }
    
    public static double simScoreForTitles(List<String> titles1, List<String> titles2) {
        List<String> titlesTokens1 = new ArrayList<String>();
        List<String> titlesTokens2 = new ArrayList<String>();
        
        for(String title:titles1) {
            titlesTokens1.addAll(getTokens(title));
        }
        
        for(String title:titles2) {
            titlesTokens2.addAll(getTokens(title));
        }
        
        return getJaccardSimilartyForTokens(titlesTokens1, titlesTokens2);
    }

    public static double simScoreForURLS(List<String> urls1, List<String> urls2, int urlThreshold) {
        
        if(urlThreshold <= 0) {
            //throw new IllegalArgumentException("Invalid urlThreshold value ( =" +urlThreshold + ")");
            // TODO log error here
            return 0.0;
        }
        else if(urlThreshold > urls1.size() || urlThreshold > urls2.size()) {
            // TODO log error here.
            /*throw new IllegalArgumentException("Required number of common URL's cannot be lesser " +
            		"than size either url's list ( " + urls1.size() + ", " + urls2.size() + ", "
            		+ urlThreshold + " )");*/
            return 0.0;
        }
        
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        Set<String> union = new HashSet<String>();
        
        set1.addAll(urls1);
        set2.addAll(urls2);
        union.addAll(set1);
        union.addAll(set2);
        
        int commonUrls = set1.size() + set2.size() - union.size();
        
        if(commonUrls >= urlThreshold) {
            return 1.0;
        }
        
        return ((double)commonUrls)/urlThreshold;
        
    }
    
    public static void main(String[] args) {
        //System.out.println(getTokens("Helo man,world;how,are;you"));
        System.out.println(getJaccardSimilarty("Samsung Galaxy Note",
                "Samsung Galaxy Note - Carbon Blue Smartphone"));
        /*System.out.println(getCustomSimilarity("Apple iPhone 4S - 16 GB - Black",
                "Apple iPhone 4S 32 GB black GSM unlock"));*/
    }
}
