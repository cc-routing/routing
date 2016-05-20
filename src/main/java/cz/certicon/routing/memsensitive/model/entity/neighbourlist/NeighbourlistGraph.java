/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.memsensitive.model.entity.neighbourlist;

import cz.certicon.routing.memsensitive.model.entity.Graph;
import cz.certicon.routing.utils.EffectiveUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class NeighbourlistGraph implements Graph {

    private final int edgeSources[];
    private final int edgeTargets[];
    private final double edgeLengths[];
    private final int[][] incomingEdges;
    private final int[][] outgoingEdges;
    private final int[] nodePredecessorsPrototype;
    private final double[] nodeDistancesPrototype;
    private final boolean[] nodeClosedPrototype;
    private final long[] nodeOrigIds;
    private final long[] edgeOrigIds;

    private static final double DISTANCE_DEFAULT = Double.MAX_VALUE;
    private static final boolean CLOSED_DEFAULT = false;
    private static final int PREDECESSOR_DEFAULT = -1;

    private final Map<Long, Integer> fromOrigNodesMap;
    private final Map<Long, Integer> fromOrigEdgesMap;

    public NeighbourlistGraph( int nodeCount, int edgeCount ) {
        this.edgeSources = new int[edgeCount];
        this.edgeTargets = new int[edgeCount];
        this.edgeLengths = new double[edgeCount];
        this.nodePredecessorsPrototype = new int[nodeCount];
        this.nodeDistancesPrototype = new double[nodeCount];
        this.nodeClosedPrototype = new boolean[nodeCount];
        this.incomingEdges = new int[nodeCount][];
        this.outgoingEdges = new int[nodeCount][];
        this.nodeOrigIds = new long[nodeCount];
        this.edgeOrigIds = new long[edgeCount];
        this.fromOrigEdgesMap = new HashMap<>();
        this.fromOrigNodesMap = new HashMap<>();
        EffectiveUtils.fillArray( nodePredecessorsPrototype, PREDECESSOR_DEFAULT );
        EffectiveUtils.fillArray( nodeDistancesPrototype, DISTANCE_DEFAULT );
        EffectiveUtils.fillArray( nodeClosedPrototype, CLOSED_DEFAULT );
    }

    @Override
    public void setSource( int edge, int source ) {
        edgeSources[edge] = source;
    }

    @Override
    public void setTarget( int edge, int target ) {
        edgeTargets[edge] = target;
    }

    @Override
    public void setLength( int edge, double length ) {
        edgeLengths[edge] = length;
    }

    @Override
    public void setIncomingEdges( int node, int[] incomingEdges ) {
        this.incomingEdges[node] = incomingEdges;
    }

    @Override
    public void setOutgoingEdges( int node, int[] outgoingEdges ) {
        this.outgoingEdges[node] = outgoingEdges;
    }

    @Override
    public void resetNodePredecessorArray( int[] nodePredecessors ) {
        System.arraycopy( nodePredecessorsPrototype, 0, nodePredecessors, 0, nodePredecessors.length );
    }

    @Override
    public void resetNodeDistanceArray( double[] nodeDistances ) {
        System.arraycopy( nodeDistancesPrototype, 0, nodeDistances, 0, nodeDistances.length );
    }

    @Override
    public void resetNodeClosedArray( boolean[] nodeClosed ) {
        System.arraycopy( nodeClosedPrototype, 0, nodeClosed, 0, nodeClosed.length );
    }

    @Override
    public int[] getIncomingEdges( int node ) {
        return incomingEdges[node];
    }

    @Override
    public int[] getOutgoingEdges( int node ) {
        return outgoingEdges[node];
    }

    @Override
    public int getSource( int edge ) {
        return edgeSources[edge];
    }

    @Override
    public int getTarget( int edge ) {
        return edgeTargets[edge];
    }

    @Override
    public double getLength( int edge ) {
        return edgeLengths[edge];
    }

    @Override
    public long getEdgeOrigId( int edge ) {
        return edgeOrigIds[edge];
    }

    @Override
    public long getNodeOrigId( int node ) {
        return nodeOrigIds[node];
    }

    @Override
    public void setEdgeOrigId( int edge, long id ) {
        edgeOrigIds[edge] = id;
        fromOrigEdgesMap.put( id, edge );
    }

    @Override
    public void setNodeOrigId( int node, long id ) {
        nodeOrigIds[node] = id;
        fromOrigNodesMap.put( id, node );
    }

    @Override
    public int getNodeByOrigId( long nodeId ) {
        return fromOrigNodesMap.get( nodeId );
    }

    @Override
    public int getEdgeByOrigId( long edgeId ) {
        return fromOrigEdgesMap.get( edgeId );
    }

    @Override
    public int getNodeCount() {
        return nodeOrigIds.length;
    }

    @Override
    public int getEdgeCount() {
        return edgeOrigIds.length;
    }

    @Override
    public int getOtherNode( int edge, int node ) {
//        System.out.println( "#getOtherNode: edge = " + edge+ ", node = " + node + ", source = " + edgeSources[edge] + ", target = " + edgeTargets[edge] );
        int target = edgeTargets[edge];
        if ( target == node ) {
            return edgeSources[edge];
        }
        return target;
    }

    @Override
    public Iterator<Integer> getIncomingEdgesIterator( int node ) {
        return new IncomingIterator( node );
    }

    @Override
    public Iterator<Integer> getOutgoingEdgesIterator( int node ) {
        return new OutgoingIterator( node );
    }

    @Override
    public boolean isValidPredecessor( int predecessor ) {
        return predecessor != PREDECESSOR_DEFAULT;
    }

    private class IncomingIterator implements Iterator<Integer> {

        private final int node;
        private int position = -1;

        public IncomingIterator( int node ) {
            this.node = node;
        }

        @Override
        public boolean hasNext() { // ... position + 1 < lastTwoway[node] // create a helper array of last position of the twoway node so that it does not have to go through the whole array when determining, whether a valid edge follows
            return position + 1 < incomingEdges[node].length;
        }

        @Override
        public Integer next() {
            return incomingEdges[node][++position];
        }

    }

    private class OutgoingIterator implements Iterator<Integer> {

        private final int node;
        private int position = -1;

        public OutgoingIterator( int node ) {
            this.node = node;
        }

        @Override
        public boolean hasNext() { // see above, analogically
            return position + 1 < outgoingEdges[node].length;
        }

        @Override
        public Integer next() {
            return outgoingEdges[node][++position];
        }
    }

}