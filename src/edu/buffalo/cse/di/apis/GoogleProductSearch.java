package edu.buffalo.cse.di.apis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;



import edu.buffalo.cse.di.apis.entity.GoogleCustomSearchResult;
import edu.buffalo.cse.di.apis.entity.GoogleProductSearchResult;
import edu.buffalo.cse.di.util.GoogleAPIKey;
import edu.buffalo.cse.di.util.SimilarityScore;
import edu.buffalo.cse.di.util.SimilarityScore.SimilarityType;
import edu.buffalo.cse.di.util.algorithm.KNNAlgorithm;
import edu.buffalo.cse.di.util.entity.Node;

/**
 * Class to search against the google products using google product SearchAPI
 * @author sravanku@buffalo.edu
 */
public class GoogleProductSearch extends GoogleSearch {

    private static final String BASE_URL 
    = "https://www.googleapis.com/shopping/search/v1/public/products?";

    private static final String optionalParms = "&country=US";
    private static final String outputFormat = "&alt=json";

    /**
     * Returns the JSONOutput
     * @param searchString
     * @return
     */
    public static String queryGoogleProductSearch(String searchString) {

        String completeURL = BASE_URL + "key="+GoogleAPIKey.getGoogleAPIKey() +
                optionalParms + "&q=" + formatQuery(searchString) + outputFormat;

        try {
            InputStream stream = new URL(completeURL).openStream();
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

    /**
     * Returns the list of GoogleProductSearchResults for a query
     * (to support API call for blank ignoreList)
     * @param query
     * @return List<GoogleProductSearchResult> - List of product search results
     */
    public static List<GoogleProductSearchResult> searchProducts(String query) {
        return searchProducts(query, new ArrayList<String>());
    }

    /**
     * Returns the list of GoogleProductSearchResults. <br/>
     * Ignores records if it matches with any word in ignore list.
     * @param query
     * @param ignoreList
     * @return List<GoogleProductSearchResult> - List of product search results
     */
    public static List<GoogleProductSearchResult> searchProducts(String query, List<String> ignoreList) {
        String content = queryGoogleProductSearch(query);
        List<GoogleProductSearchResult> itemNames = new ArrayList<GoogleProductSearchResult>();
        try {
            JSONObject obj = (JSONObject) JSONSerializer.toJSON(content);
            JSONArray items = obj.getJSONArray("items");
            
            for(int i=0; i<items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject product = item.getJSONObject("product");

                //Filtered the titles to ignore 
                String title = product.getString("title");
                boolean flag = false;
                if(ignoreList != null && ignoreList.size() != 0) {
                    for (String ignoreStr: ignoreList) {
                        if (title.toLowerCase().contains(ignoreStr.toLowerCase())) {
                            flag = true;
                        }	
                    }
                }

                if (!(flag)) {
                    GoogleProductSearchResult result = new GoogleProductSearchResult(title); 
                    itemNames.add(result);
                }
                //String title = 
                //System.out.println(item.get("product"));
                //System.out.println(result);
            }
        }
        catch(Exception e) {
            //TODO handle error.
        }
        return itemNames;
    }

    public static Node getNodesForProductSearch(GoogleProductSearchResult request ) {

        List<GoogleCustomSearchResult> list = GoogleCustomSearch.getItemNames(request.getTitle());
        //System.out.println("Hello");
        if(list != null) { // Happened for one String.
            List<String> titles = new ArrayList<String>(list.size());
            List<String> urls = new ArrayList<String>(list.size());
            List<String> headings = new ArrayList<String>(list.size());
            for(GoogleCustomSearchResult item:list) {
                titles.add(item.getTitle());
                urls.add(item.getLink());
                headings.add(item.getHeading());
            }
            return new Node(request.getTitle(), urls, titles, SimilarityScore.getMaxSumSimilarity(headings), null);
        }
        return null;
    }

    /**
     * This Method add's an additional functionality to automatically 
     * calculate metrics over the existing implmentation present in 
     * getNodesForProductSearch(GoogleProductSearchResult request)
     * @param request
     * @return Node
     */
    public static Node getNodesForProductSearchModified(GoogleProductSearchResult request ) {
        String[] params = request.getTitle().split(";;");
        List<GoogleCustomSearchResult> list = GoogleCustomSearch.getItemNames(params[1]);
        if(list != null) { // Happened for one String.
            List<String> titles = new ArrayList<String>(list.size());
            List<String> urls = new ArrayList<String>(list.size());
            List<String> headings = new ArrayList<String>(list.size());
            for(GoogleCustomSearchResult item:list) {
                titles.add(item.getTitle());
                urls.add(item.getLink());
                headings.add(item.getHeading());
            }
            return new Node(params[1], urls, titles, SimilarityScore.getMaxSumSimilarity(headings), null, params[0]);
        }
        return null;
    }

    /**
     * Wrapper for the Algorithm when the product names are directly available.
     * @param data
     * @return 
     */
    public static List<List<Node>> runAlgorithmforStrings(List<String> data, SimilarityType type) {
        List<GoogleProductSearchResult> results = new ArrayList<GoogleProductSearchResult>(data.size());
        for(String item: data) {
            results.add(new GoogleProductSearchResult(item));
        }
        return runAlgorithm(results, type);
    }

    /**
     * Runs the algorithm and returns the clusters.
     * @param data
     * @return 
     */
    public static List<List<Node>> runAlgorithm(List<GoogleProductSearchResult> data, SimilarityType type) {
    	System.out.println("runAlgorithm : " + data.size());
        List<Node> nodes = new ArrayList<Node>();
        for(GoogleProductSearchResult item: data) {
            // TODO use "Modified" if required for calculation of classification rate.
            Node node = getNodesForProductSearch(item);
            if(node != null) {
                nodes.add(node);
            }
        }
        //Stage 1:
        System.out.println(nodes.size());
        KNNAlgorithm algorithm = new KNNAlgorithm(nodes, 3, 0.15);
        List<List<Node>> clusters = algorithm.generateClusters(type);
        System.out.println("Step 1: UnMerged Clusters: " + clusters.size());

        List<List<Node>> mergedClusters = getMergedClusters(clusters,0.0);

        System.out.println("Step 2: Merged Clusters: " + mergedClusters.size());
        /*for(List<Node> cluster: mergedClusters) {
            System.out.println(cluster);
            //System.out.println(SimilarityScore.getBestNodeForCluster(cluster));
        }*/

        //Step 2:
        /*List<Node> headerNodes = new ArrayList<Node>();
        for(List<Node> cluster: clusters) {
            headerNodes.add(SimilarityScore.getBestNodeForCluster(cluster));
        }
        KNNAlgorithm algorithm1 = new KNNAlgorithm(nodes, 2, 0.3);
        List<List<Node>> clusters1 = algorithm1.generateClusters(SimilarityType.JACCARD);
        System.out.println("Step 2:" + clusters1.size());
        for(List<Node> cluster: clusters1) {
            System.out.println(cluster);
        }*/

        return clusters;
    }

    public static List<List<Node>> getMergedClusters(List<List<Node>> clusters, double threshold) {
        List<List<Node>> minClusters = new ArrayList<List<Node>>();
        List<List<Node>> maxClusters = new ArrayList<List<Node>>();
        List<Node> unClassifiedCluster = new ArrayList<Node>();

        for(List<Node> cluster: clusters) {
            if (cluster.size() < 3) 
                minClusters.add(cluster);
            else
                maxClusters.add(cluster);
        }
        //System.out.println("Get ClusterHeads : ");
        List<Node> headerNodes = new ArrayList<Node>();
        for(List<Node> cluster: maxClusters) {
            headerNodes.add(SimilarityScore.getBestNodeForCluster(cluster));
        }

        //SimilarityScore simScore = new SimilarityScore();
        for(List<Node> minCluster: minClusters) {
            for(Node minNode: minCluster) {
                Node minNodeNearestNode = null;
                double minDist = 0.0;
                int index = 0;
                int count = 0;
                for(Node headerNode: headerNodes) {
                    //double curSimScore = simScore.getJaccardSimilarty(minNodeName, headerNode.getString());
                    double curDist = KNNAlgorithm.getDistanceBetweenNodes(headerNode, minNode, SimilarityType.JACCARD);
                    if (minDist == 0.0) {
                        minDist = curDist;
                        minNodeNearestNode = headerNode;
                        index = count;
                    } else {
                        if ((curDist > minDist) && (curDist > threshold)) {
                            minDist = curDist;
                            minNodeNearestNode = headerNode;
                            index = count;
                        }
                    }
                    count++;
                }

                if (minNodeNearestNode != null) {
                    maxClusters.get(index).add(minNode);
                    //maxClusters.remove(index);
                    //cluster.add(minNode);
                    //System.out.println(minNodeNearestNode + "---> " + minNode);
                    //maxClusters.add(cluster);
                } else {
                    unClassifiedCluster.add(minNode);
                }
            }
        }

        maxClusters.add(unClassifiedCluster);
        return maxClusters;
    }


    public static void main(String[] args) throws IOException {
        //actualTest("MobilePhonesFiltered.txt");
        //actualTestModified("TelevisionDataSet.txt");
        //String[] ignoreListForMobile = new String[] {"case","Charger"};
        testData(null, "SampleLaptopsList.txt");
        //testData(Arrays.asList(ignoreList));
    }

    
    public static List<List<Node>> performEntityResolution(List<String> entities, SimilarityType type) throws IOException {
        List<String> productTitles = new ArrayList<String>();
        for(String entity : entities) {
            if(!entity.equals("")) {
                productTitles.add(entity);
            }
        }
        return runAlgorithmforStrings(productTitles, type);
    }

    public static void actualTest(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line = null;
        List<String> productTitles = new ArrayList<String>();
        while((line = reader.readLine()) != null) {
            if(!line.equals("")) {
                productTitles.add(line);
            }
        }
        reader.close();
        runAlgorithmforStrings(productTitles, SimilarityType.JACCARD);
    }

    /**
     * Automation of classification rate calculation
     * @param fileName
     * @throws IOException
     */

    public static void actualTestModified(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line = null;
        List<String> productTitles = new ArrayList<String>();
        while((line = reader.readLine()) != null) {
            if(!line.equals("")) {
                if(line.length() < 85)
                    productTitles.add(line);
            }
        }
        reader.close();
        List<List<Node>> clusters = runAlgorithmforStrings(productTitles, SimilarityType.JACCARD);
        //calculateClassificationRate(clusters);
    }

    public static void calculateClassificationRate(List<List<Node>> clusters) {
        int totalRecords = 0;
        int correctClassified = 0;
        for(List<Node> cluster: clusters) {
            HashMap<String,Integer> counts = new HashMap<String, Integer>();
            int maxCount = 0;
            int totalCount = cluster.size();
            for(Node node: cluster) {
                int count = 0;
                if(counts.containsKey(node.getLabel())) {
                    count = counts.get(node.getLabel());
                }
                count += 1;
                counts.put(node.getLabel(), count);
                if(maxCount <= count) {
                    maxCount = count;
                }
            }
            totalRecords += totalCount;
            correctClassified += maxCount;
        }
        System.out.println("(Total Nodes, Correct Classified = (" + totalRecords + ", " + correctClassified + ")");
    }

    public static void testData(List<String> ignoreList, String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line = null;
        
        while((line = reader.readLine()) != null) {
            if(!line.equals("")) {
                System.out.println("---------------------------------------------------------------");
                System.out.println(line);
                System.out.println("---------------------------------------------------------------");
                List<GoogleProductSearchResult> results = GoogleProductSearch.searchProducts(line, ignoreList);
                TreeSet<String> set = new TreeSet<String>();
                for(GoogleProductSearchResult result : results) {
                	if(set.add(result.getTitle().toLowerCase())) {
                	    System.out.println(result.getTitle());
                	}
                }
            }
        }
        reader.close();
    }

}
