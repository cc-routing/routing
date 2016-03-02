/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.data.coordinates;

import cz.certicon.routing.model.entity.Coordinates;
import cz.certicon.routing.model.entity.Edge;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public interface CoordinatesWriter {

    public CoordinatesWriter open() throws IOException;

    public CoordinatesWriter write( Edge edge, List<Coordinates> coordinates ) throws IOException;

    public CoordinatesWriter close() throws IOException;
}