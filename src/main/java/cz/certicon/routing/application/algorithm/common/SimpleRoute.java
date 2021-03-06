/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.application.algorithm.common;

import cz.certicon.routing.application.algorithm.Route;
import cz.certicon.routing.model.basic.Pair;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Basic implementation of the {@link Route} interface.
 *
 * @author Michael Blaha {@literal <michael.blaha@certicon.cz>}
 */
public class SimpleRoute implements Route {

    private final LinkedList<Pair<Long, Boolean>> edges;
    private final long source;
    private final long target;

    public SimpleRoute( LinkedList<Pair<Long, Boolean>> edges, long source, long target ) {
        this.edges = edges;
        this.source = source;
        this.target = target;
    }

    @Override
    public Iterator<Pair<Long, Boolean>> getEdgeIterator() {
        return edges.iterator();
    }

    @Override
    public long getTarget() {
        return target;
    }

    @Override
    public long getSource() {
        return source;
    }

}
