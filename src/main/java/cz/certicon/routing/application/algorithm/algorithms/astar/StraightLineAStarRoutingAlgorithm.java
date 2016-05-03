/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.application.algorithm.algorithms.astar;

import cz.certicon.routing.application.algorithm.*;
import cz.certicon.routing.application.algorithm.algorithms.AbstractRoutingAlgorithm;
import cz.certicon.routing.application.algorithm.datastructures.JgraphtFibonacciDataStructure;
import cz.certicon.routing.model.entity.*;
import cz.certicon.routing.utils.GraphUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * A* implementation of the routing algorithm, based on the node-flight-distance
 * heuristic function.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class StraightLineAStarRoutingAlgorithm extends AbstractRoutingAlgorithm {

    private NodeDataStructure<Node> nodeDataStructure;
    private final Map<Node.Id, Distance> distanceMap;

    public StraightLineAStarRoutingAlgorithm( Graph graph, GraphEntityFactory entityAbstractFactory, DistanceFactory distanceFactory ) {
        super( graph, entityAbstractFactory, distanceFactory );
        this.nodeDataStructure = new JgraphtFibonacciDataStructure();
        this.distanceMap = new HashMap<>();
    }

    public void setNodeDataStructure( NodeDataStructure nodeDataStructure ) {
        this.nodeDataStructure = nodeDataStructure;
    }

    @Override
    public Path route( Node.Id from, Node.Id to ) {
        // clear the data structure
        nodeDataStructure.clear();
//        Node nodeEqToFrom = from;
//        Node nodeEqToTo = to;
        // foreach node in G
        Map<Node.Id, Distance> targetNodeMap = new HashMap<>();
        for ( Node node : getGraph().getNodes() ) {
            node.setPredecessorEdge( null );
            if ( node.getId().equals( from ) ) {
                node.setDistance( getDistanceFactory().createZeroDistance() );
                nodeDataStructure.add( node, 0 );
            } else if ( node.getId().equals( to ) ) {
                node.setDistance( getDistanceFactory().createInfiniteDistance() );
                targetNodeMap.put( node.getId(), getDistanceFactory().createZeroDistance() );
            } else { // set distance to infinity
                node.setDistance( getDistanceFactory().createInfiniteDistance() );
            }
        }
        return route( targetNodeMap );
    }

    @Override
    public Path route( Map<Node.Id, Distance> from, Map<Node.Id, Distance> to ) {
        // clear the data structure
        nodeDataStructure.clear();
//        Node nodeEqToFrom = from;
//        Node nodeEqToTo = to;
        // foreach node in G
        for ( Node node : getGraph().getNodes() ) {
            node.setPredecessorEdge( null );
            if ( from.containsKey( node.getId() ) ) {
                Distance nodeDistance = from.get( node.getId() );
                node.setDistance( nodeDistance );
                nodeDataStructure.add( node, calculateDistance( getDistanceFactory(), node, to ).getEvaluableValue() );
            } else { // set distance to infinity
                node.setDistance( getDistanceFactory().createInfiniteDistance() );
            }
        }
        return route( to );
    }

    private Path route( Map<Node.Id, Distance> to ) {
        distanceMap.clear();
        // set source node distance to zero
        // while the data structure is not empty (or while the target node is not found)
        while ( !nodeDataStructure.isEmpty() ) {
            // extract node S with the minimal distance
            Node currentNode = nodeDataStructure.extractMin();
            if ( to.containsKey( currentNode.getId() ) ) {
                // build path from predecessors and return
                return GraphUtils.createPath( getGraph(), getEntityAbstractFactory(), currentNode );
            }
            // foreach neighbour T of node S
            for ( Edge edge : getGraph().getOutgoingEdgesOf( currentNode ) ) {
                if ( !getRoutingConfiguration().getEdgeValidator().validate( edge ) ) {
                    continue;
                }
                Node endNode = getGraph().getOtherNodeOf( edge, currentNode );
                // calculate it's distance S + path from S to T
                Distance tmpNodeDistance;
                if ( to.containsKey( currentNode.getId() ) ) {
                    tmpNodeDistance = getRoutingConfiguration().getDistanceEvaluator().evaluate( currentNode, edge, endNode, to.get( currentNode.getId() ) );
                } else {
                    tmpNodeDistance = getRoutingConfiguration().getDistanceEvaluator().evaluate( currentNode, edge, endNode );
                }
                // replace is lower than actual
                if ( tmpNodeDistance.isLowerThan( endNode.getDistance() ) ) {
                    endNode.setDistance( tmpNodeDistance );
                    endNode.setPredecessorEdge( edge );
                    nodeDataStructure.notifyDataChange( endNode, calculateDistance( getDistanceFactory(), endNode, to ).getEvaluableValue() );
                }
            }
        }
        return null;
    }

    private Distance calculateDistance( DistanceFactory distanceFactory, Node node, Map<Node.Id, Distance> target ) {
        Distance dist = distanceMap.get( node.getId() );
        if ( dist == null ) {
            Distance min = distanceFactory.createInfiniteDistance();
            for ( Map.Entry<Node.Id, Distance> entry : target.entrySet() ) {
                dist = distanceFactory.createApproximateFromCoordinates( node.getCoordinates(), getGraph().getNode( entry.getKey() ).getCoordinates() );
                if ( min.isGreaterThan( dist ) ) {
                    min = dist;
                }
            }
            dist = min;
            distanceMap.put( node.getId(), dist );
        }
        return node.getDistance().add( dist );
    }

}
