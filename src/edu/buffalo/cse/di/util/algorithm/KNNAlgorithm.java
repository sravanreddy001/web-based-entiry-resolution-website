/**
 * 
 */
package edu.buffalo.cse.di.util.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.buffalo.cse.di.util.SimilarityScore;
import edu.buffalo.cse.di.util.SimilarityScore.SimilarityType;
import edu.buffalo.cse.di.util.entity.Node;

/**
 * @author sravanku@buffalo.edu
 */
public class KNNAlgorithm {
    
    private double threshold;
    private int kValue;
    private List<Node> nodes = new ArrayList<Node>();
    private NodeDistanceComparator nodeDistanceComparator = 
            new NodeDistanceComparator();
    private Map<Node, PriorityQueue<NodeDistance>> nodeDistances = 
            new HashMap<Node, PriorityQueue<NodeDistance>>();
    
    int[] array;
    
    private class NodeDistance {
        int nodeId;
        double distance;

        public NodeDistance(int nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        /**
         * Auto generated code. Do not worry about this.
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            long temp;
            temp = Double.doubleToLongBits(distance);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + nodeId;
            return result;
        }

        /**
         * Auto generated code. Do not worry about this.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NodeDistance other = (NodeDistance) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (Double.doubleToLongBits(distance) != Double
                    .doubleToLongBits(other.distance))
                return false;
            // To guarentee the contract of the comparator.
            /*if (nodeId != other.nodeId)
                return false; */
            return true;
        }

        private KNNAlgorithm getOuterType() {
            return KNNAlgorithm.this;
        }
        
        
    }
    
    private class NodeDistanceComparator implements Comparator<NodeDistance> {
        @Override
        public int compare(NodeDistance o1, NodeDistance o2) {
            double diff = o1.distance - o2.distance;
            if(diff < 0) {
                return -1;
            }
            else if(diff == 0.0) {
                return 0;
            }
            else {
                return 1;
            }
        }
        
    }
    
    
    public KNNAlgorithm(List<Node> nodes, int kValue) {
        this(nodes, kValue, 0.5);
    }
    
    public KNNAlgorithm(List<Node> nodes, int kValue,double threshold) {
        this.nodes = nodes;
        this.kValue = kValue;
        this.threshold = threshold;
    }
    
    /**
     * Node distances is updated after this function is evaluated.
     * Only the nodes that have distance >= threshold are included.
     * @param type 
     */
    private void evaluateDistances(SimilarityType type) {
        for( int i=0; i<nodes.size(); i++ ) {
            PriorityQueue<NodeDistance> distances = new PriorityQueue<NodeDistance>(10 , nodeDistanceComparator);
            for( int j=0; j< nodes.size(); j++ ) {
                if( i==j ) {
                    continue;
                }
                else {
                    double distance = getDistanceBetweenNodes(nodes.get(i), nodes.get(j),type);
                    if(distance >= this.threshold){ 
                        distances.add(new NodeDistance(j, distance));
                    }
                }
            }
            nodeDistances.put(nodes.get(i), distances);
        }
    }
    
    public static double getDistanceBetweenNodes(Node node1, Node node2, SimilarityType type) {
        // TODO complete this code by calculating jaccard similarity and
        // TODO weight assignment for different attributes in the total similarity.
        // this is a very basic implementation.
    	double searchStringDistance = SimilarityScore.getJaccardSimilarty(node1.getString(), node2.getString());
    	
    	if(type == SimilarityType.JACCARD) {
	    	int urlThreshold = ((node1.getUrls().size() < node2.getUrls().size()) ? node1.getUrls().size() : node2.getUrls().size() ) / 2;
	        
	        double titleDistance = SimilarityScore.simScoreForTitles(node1.getTitles(), node2.getTitles());
	        double urlsDistance = SimilarityScore.simScoreForURLS(node1.getUrls(), node2.getUrls(), urlThreshold);
	        
	        //double textDistance = SimilarityScore.getCustomSimilarity(node1.getText(), node2.getText());
	        
	        // Equal ratio initially. Similarity Type doesn't matter now.
	        //return 0.33 * ( titleDistance + urlsDistance + searchStringDistance );
	        
	        //return 0.2 * titleDistance + 0.2 * urlsDistance + 0.6 * searchStringDistance;
	        
	        //System.out.println("(" + titleDistance + ", " + urlsDistance + ", " + searchStringDistance +") -- " + node1.getString() + " -- " + node2.getString());
	        return 0.4 * titleDistance + 0.4 * urlsDistance + 0.2 * searchStringDistance;
    	}
    	else {
    		return searchStringDistance;
    	}
        //return searchStringDistance;
        
        /*if(type == SimilarityType.JACCARD) {
            return SimilarityScore.getJaccardSimilarty(node1.getString(), node2.getString());
        }
        else if(type == SimilarityType.CUSTOM) {
            return SimilarityScore.getCustomSimilarity(node1.getString(), node2.getString());
        }*/
        //throw new IllegalArgumentException("Invalid SimilarityType argument passed");
    }

    public List<List<Node>> generateClusters(SimilarityType type) {
        
        evaluateDistances(type); // Evaluate distances
        
        array = new int[nodes.size()];
        for(int i = 0; i<array.length; i++) {
            array[i] = 0;
        }
        List<List<Node>> clusters = new ArrayList<List<Node>>();
        for(int i=0; i< nodes.size(); i++) {
            if(array[i] == 0 ) {
                List<Node> cluster = getCluster(nodes.get(i)); 
                for(Node node: cluster ) {
                    array[nodes.indexOf(node)] = 1;
                }
                clusters.add(cluster);
            }
        }
        return clusters;
    }
    
    private List<Node> getCluster(Node node) {
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(node);
        List<Node> returnList = new ArrayList<Node>();
        while ( !queue.isEmpty() ) {
            Node firstNode = queue.removeFirst();
            if(array[nodes.indexOf(firstNode)] == 1) { // Already added to cluster
                continue;
            }
            returnList.add(firstNode);
            PriorityQueue<NodeDistance> nearNodes = nodeDistances.get(node);
            for(int i=0; i<kValue && !nearNodes.isEmpty(); i++) {
                queue.add(nodes.get(nearNodes.remove().nodeId));
            }
        }
        return returnList;
    }
}