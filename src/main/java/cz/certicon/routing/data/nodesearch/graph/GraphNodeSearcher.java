/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data.nodesearch.graph;

import cz.certicon.routing.application.algorithm.Distance;
import cz.certicon.routing.application.algorithm.DistanceFactory;
import cz.certicon.routing.application.algorithm.EdgeData;
import cz.certicon.routing.data.nodesearch.NodeSearcher;
import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.model.entity.Edge;
import cz.certicon.routing.model.entity.Graph;
import cz.certicon.routing.model.entity.Node;
import cz.certicon.routing.model.entity.common.SimpleEdgeData;
import cz.certicon.routing.utils.CoordinateUtils;
import cz.certicon.routing.utils.DoubleComparator;
import cz.certicon.routing.utils.GeometryUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class GraphNodeSearcher implements NodeSearcher {

    private static final double EPS = 10E-9;

    private Graph graph;

    public GraphNodeSearcher( Graph graph ) {
        this.graph = graph;
    }

    @Override
    public Map<Node.Id, Distance> findClosestNodes( Coordinates coordinates, DistanceFactory distanceFactory, NodeSearcher.SearchFor searchFor ) throws IOException {
        List<Edge> closestEdges = new ArrayList<>();
        Coordinates closestCoords = null;
        double distance = Double.MAX_VALUE;
        for ( Edge edge : graph.getEdges() ) {
            double currentMin = Double.MAX_VALUE;
            Coordinates currentCoords = null;
            for ( Coordinates coordinate : edge.getCoordinates() ) {
                double tmp = CoordinateUtils.calculateDistance( coordinate, coordinates );
                if ( DoubleComparator.isLowerThan( tmp, currentMin, EPS ) ) {
                    currentMin = tmp;
                    currentCoords = coordinate;
                }
            }
            if ( DoubleComparator.isLowerThan( currentMin, distance, EPS ) ) {
                distance = currentMin;
                closestEdges.clear();
                closestEdges.add( edge );
                closestCoords = currentCoords;
            } else if ( DoubleComparator.isEqualTo( currentMin, distance, EPS ) ) {
                closestEdges.add( edge );
            }
        }
        if ( !closestEdges.isEmpty() ) {
            Map<Node.Id, Distance> distanceMap = new HashMap<>();
            for ( Edge edge : closestEdges ) {
                EdgeData edgeData = new SimpleEdgeData( edge.getSpeed(), edge.getAttributes().isPaid(), edge.getAttributes().getLength() );
                Coordinates start = edge.getCoordinates().get( 0 );
                Coordinates end = edge.getCoordinates().get( edge.getCoordinates().size() - 1 );
                double lengthFromStart = 0;
                double lengthToEnd = 0;
                double tmpLength = 0;
                for ( Coordinates coordinate : edge.getCoordinates() ) {
                    tmpLength += CoordinateUtils.calculateDistance( start, coordinate );
                    if ( coordinate.equals( closestCoords ) ) {
                        lengthFromStart = tmpLength;
                        tmpLength = 0;
                    }
                }
                lengthToEnd = tmpLength;
                double length;
                Node.Id nodeId;
                if ( searchFor.equals( SearchFor.SOURCE ) ) {
                    nodeId = edge.getTargetNode().getId();
                    length = ( edge.getSourceNode().getCoordinates().equals( start ) ) ? lengthToEnd : lengthFromStart;
                } else {
                    nodeId = edge.getSourceNode().getId();
                    length = ( edge.getSourceNode().getCoordinates().equals( start ) ) ? lengthFromStart : lengthToEnd;
                }
                distanceMap.put( nodeId, distanceFactory.createFromEdgeDataAndLength( edgeData, length ) );
            }
            return distanceMap;
        } else {
            return null;
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph( Graph graph ) {
        this.graph = graph;
    }

}