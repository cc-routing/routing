/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.application.algorithm;

import cz.certicon.routing.model.entity.Edge;
import cz.certicon.routing.model.entity.Node;

/**
 * The root interface for distance factories - classes for creating
 * {@link Distance} objects.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface DistanceFactory {

    /**
     * Creates instance with the value of zero.
     *
     * @return created {@link Distance} instance
     */
    public Distance createZeroDistance();

    /**
     * Creates instance with the value of infinity.
     *
     * @return created {@link Distance} instance
     */
    public Distance createInfiniteDistance();

//    /**
//     * Creates instance with the value of a given double number.
//     *
//     * @param distance abstract distance represented in double
//     * @return created {@link Distance} instance
//     */
//    public Distance createFromDouble( double distance );
    
    /**
     * Creates an instance using attributes of the edge (its data)
     * 
     * @param edgeData {@link EdgeData} container
     * @return an instance of {@link Distance}
     */
    public Distance createFromEdgeData(EdgeData edgeData);
    
    /**
     * Creates an instance using approximate distance calculation from the two given nodes.
     * 
     * @param a the first {@link Node}
     * @param b the second {@link Node}
     * @return an instance of {@link Distance}
     */
    public Distance createApproximateFromNodes(Node a, Node b);
}
