/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.memsensitive.model.entity;

import cz.certicon.routing.model.entity.Coordinates;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface PathBuilder<T, G> {

    public void addEdge( G graph, long edgeId, boolean isForward, List<Coordinates> coordinates, double length, double time );

    public void addCoordinates( Coordinates coordinates );

    public void addLength( double length );

    public void addTime( double time );

    public T build();
}