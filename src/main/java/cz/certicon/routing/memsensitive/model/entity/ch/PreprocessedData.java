/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.memsensitive.model.entity.ch;

import cz.certicon.routing.memsensitive.model.entity.Graph;
import gnu.trove.iterator.TIntIterator;
import java.util.Arrays;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class PreprocessedData {

    private final int[] ranks;
    private final int[][] incomingShortcuts;
    private final int[][] outgoingShortcuts;
    private final int[] sources;
    private final int[] targets;

    /* more memory, more efficiency */
    private final int[] startEdges;
    private final int[] endEdges;

    public PreprocessedData( int nodeCount, int edgeCount, int shortcutCount ) {
        this.ranks = new int[nodeCount];
        this.incomingShortcuts = new int[nodeCount][];
        this.outgoingShortcuts = new int[nodeCount][];
        this.sources = new int[shortcutCount];
        this.targets = new int[shortcutCount];

        this.startEdges = new int[shortcutCount];
        this.endEdges = new int[shortcutCount];
    }

    public PreprocessedData( int[] ranks, int[][] incomingShortcuts, int[][] outgoingShortcuts, int[] sources, int[] targets, int[] startEdges, int[] endEdges ) {
        this.ranks = ranks;
        this.incomingShortcuts = incomingShortcuts;
        this.outgoingShortcuts = outgoingShortcuts;
        this.sources = sources;
        this.targets = targets;
        this.startEdges = startEdges;
        this.endEdges = endEdges;
    }

    public void setRank( int node, int rank ) {
        ranks[node] = rank;
    }

    public void setIncomingShortcuts( int node, int[] incomingShortcuts ) {
        this.incomingShortcuts[node] = incomingShortcuts;
    }

    public void setOutgoingShortcuts( int node, int[] outgoingShortcuts ) {
        this.outgoingShortcuts[node] = outgoingShortcuts;
    }

    public void setShortcutSource( int shortcut, int node ) {
        sources[shortcut] = node;
    }

    public void setShortcutTarget( int shortcut, int node ) {
        targets[shortcut] = node;
    }

    public void setStartEdge( int shortcut, int edge ) {
        startEdges[shortcut] = edge;
    }

    public void setEndEdge( int shorcut, int edge ) {
        endEdges[shorcut] = edge;
    }

    public int[] getRanks() {
        return ranks;
    }

    public int getRank( int rank ) {
        return ranks[rank];
    }

    public int[][] getIncomingShortcuts() {
        return incomingShortcuts;
    }

    public int[] getIncomingShortcuts( int node ) {
        return incomingShortcuts[node];
    }

    public int[][] getOutgoingShortcuts() {
        return outgoingShortcuts;
    }

    public int[] getOutgoingShortcuts( int node ) {
        return outgoingShortcuts[node];
    }

    public int[] getSources() {
        return sources;
    }

    public int getShortcutCount() {
        return sources.length;
    }

    public int getSource( int shortcut ) {
        return sources[shortcut];
    }

    public int getSource( int edge, Graph graph ) {
        if ( edge < graph.getEdgeCount() ) {
            return graph.getSource( edge );
        }
        return sources[edge - graph.getEdgeCount()];
    }

    public int[] getTargets() {
        return targets;
    }

    public int getTarget( int shortcut ) {
        return targets[shortcut];
    }

    public int getTarget( int edge, Graph graph ) {
        if ( edge < graph.getEdgeCount() ) {
            return graph.getTarget( edge );
        }
        return targets[edge - graph.getEdgeCount()];
    }

    public int getStartEdge( int shortcut ) {
        return startEdges[shortcut];
    }

    public int getEndEdge( int shortcut ) {
        return endEdges[shortcut];
    }

    public float getLength( int shortcut, Graph graph ) {
        int start = startEdges[shortcut];
        int end = endEdges[shortcut];
        float length = 0;
        if ( start >= graph.getEdgeCount() ) {
            length += getLength( start - graph.getEdgeCount(), graph );
        } else {
            length += graph.getLength( start );
        }
        if ( end >= graph.getEdgeCount() ) {
            length += getLength( end - graph.getEdgeCount(), graph );
        } else {
            length += graph.getLength( end );
        }
        return length;
    }

    public TIntIterator getIncomingEdgesIterator( int node, Graph graph ) {
        return new IncomingIterator( graph, node );
    }

    public TIntIterator getOutgoingEdgesIterator( int node, Graph graph ) {
        return new OutgoingIterator( graph, node );
    }

    private class IncomingIterator implements TIntIterator {

        private final int node;
        private final Graph graph;
        private int position = -1;

        public IncomingIterator( Graph graph, int node ) {
            this.node = node;
            this.graph = graph;
        }

        @Override
        public boolean hasNext() { // ... see note at NeighbourListGraph
            return position + 1 < graph.getIncomingEdges( node ).length + incomingShortcuts[node].length;
        }

        @Override
        public int next() {
            if ( position + 1 < graph.getIncomingEdges( node ).length ) {
                return graph.getIncomingEdges( node )[++position];
            } else {
                return incomingShortcuts[node][++position - graph.getIncomingEdges( node ).length];
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class OutgoingIterator implements TIntIterator {

        private final int node;
        private int position = -1;
        private final Graph graph;

        public OutgoingIterator( Graph graph, int node ) {
            this.node = node;
            this.graph = graph;
        }

        @Override
        public boolean hasNext() { // see above, analogically
            return position + 1 < graph.getOutgoingEdges( node ).length + outgoingShortcuts[node].length;
        }

        @Override
        public int next() {
            if ( position + 1 < graph.getOutgoingEdges( node ).length ) {
                return graph.getOutgoingEdges( node )[++position];
            } else {
                return outgoingShortcuts[node][++position - graph.getOutgoingEdges( node ).length];
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.hashCode( this.ranks );
        hash = 53 * hash + Arrays.deepHashCode( this.incomingShortcuts );
        hash = 53 * hash + Arrays.deepHashCode( this.outgoingShortcuts );
        hash = 53 * hash + Arrays.hashCode( this.sources );
        hash = 53 * hash + Arrays.hashCode( this.targets );
        hash = 53 * hash + Arrays.hashCode( this.startEdges );
        hash = 53 * hash + Arrays.hashCode( this.endEdges );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final PreprocessedData other = (PreprocessedData) obj;
        if ( !Arrays.equals( this.ranks, other.ranks ) ) {
            return false;
        }
        if ( !Arrays.deepEquals( this.incomingShortcuts, other.incomingShortcuts ) ) {
            return false;
        }
        if ( !Arrays.deepEquals( this.outgoingShortcuts, other.outgoingShortcuts ) ) {
            return false;
        }
        if ( !Arrays.equals( this.sources, other.sources ) ) {
            return false;
        }
        if ( !Arrays.equals( this.targets, other.targets ) ) {
            return false;
        }
        if ( !Arrays.equals( this.startEdges, other.startEdges ) ) {
            return false;
        }
        if ( !Arrays.equals( this.endEdges, other.endEdges ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder isb = new StringBuilder();
        isb.append( "[" );
        for ( int[] i : incomingShortcuts ) {
            isb.append( Arrays.toString( i ) ).append( "," );
        }
        isb.append( "]" );
        StringBuilder osb = new StringBuilder();
        osb.append( "[" );
        for ( int[] i : outgoingShortcuts ) {
            osb.append( Arrays.toString( i ) ).append( "," );
        }
        osb.append( "]" );
        return "PreprocessedData{"
                + "ranks=" + Arrays.toString( ranks )
                + ", incomingShortcuts=" + isb.toString()
                + ", outgoingShortcuts=" + osb.toString()
                + ", sources=" + Arrays.toString( sources )
                + ", targets=" + Arrays.toString( targets )
                + ", startEdges=" + Arrays.toString( startEdges )
                + ", endEdges=" + Arrays.toString( endEdges ) + '}';
    }

}
