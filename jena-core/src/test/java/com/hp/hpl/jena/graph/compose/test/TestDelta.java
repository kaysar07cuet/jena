/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.graph.compose.test;

import junit.framework.TestSuite ;

import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.compose.Delta ;
import com.hp.hpl.jena.graph.test.AbstractTestGraph;

public class TestDelta extends AbstractTestGraph
{

    private static final String DEFAULT_TRIPLES = "x R y; p S q";

    public TestDelta( String name )
    {
        super( name );
    }

    public static TestSuite suite()
    {
        return new TestSuite( TestDelta.class );
    }

    @Override
    public Graph getGraph()
    {
        Graph gBase = graphWith( "" );
        return new Delta( gBase ); 
    }

    public void testDeltaMirrorsBase()
    {
        Graph base = graphWith( DEFAULT_TRIPLES );
        Delta delta = new Delta( base );
        assertIsomorphic(base, delta);
    }

    public void testAddGoesToAdditions()
    {
        Graph base = graphWith( DEFAULT_TRIPLES );
        Delta delta = new Delta( base );
        delta.add( triple( "x R z" ) );
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "x R z" ), delta.getAdditions());
        assertIsomorphic(graphWith( "" ), delta.getDeletions());
        assertIsomorphic(graphWith( DEFAULT_TRIPLES + "; x R z" ), delta);
    }

    public void testDeleteGoesToDeletions()
    {
        Graph base = graphWith( DEFAULT_TRIPLES );
        Delta delta = new Delta( base );
        delta.delete( triple( "x R y" ) );
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "x R y" ), delta.getDeletions());
        assertIsomorphic(graphWith( "p S q" ), delta);
    }

    public void testRedundantAddNoOp()
    {
        Graph base = graphWith( DEFAULT_TRIPLES );
        Delta delta = new Delta( base ) ;
        delta.add( triple( "x R y" ) ) ;
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "" ), delta.getAdditions());
        assertIsomorphic(graphWith( "" ), delta.getDeletions());
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), delta);
    }

    public void testRedundantDeleteNoOp()
    {
        Graph base = graphWith( DEFAULT_TRIPLES ) ;
        Delta delta = new Delta( base ) ;
        delta.delete( triple( "a T b" ) ) ;
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "" ), delta.getAdditions());
        assertIsomorphic(graphWith( "" ), delta.getDeletions());
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), delta);
    }

    public void testAddThenDelete()
    {
        Graph base = graphWith( DEFAULT_TRIPLES ) ;
        Delta delta = new Delta( base ) ;
        delta.add( triple( "a T b" ) ) ;
        delta.delete( triple( "a T b" ) ) ;
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "" ), delta.getAdditions());
        assertIsomorphic(graphWith( "" ), delta.getDeletions());
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), delta);
    }

    public void testDeleteThenAdd()
    {
        Graph base = graphWith( DEFAULT_TRIPLES ) ;
        Delta delta = new Delta( base ) ;
        delta.delete( triple( "p S q" ) ) ;
        delta.add( triple( "p S q" ) ) ;
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "" ), delta.getAdditions());
        assertIsomorphic(graphWith( "" ), delta.getDeletions());
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), delta);
    }

    public void testAddAndDelete()
    {
        Graph base = graphWith( DEFAULT_TRIPLES ) ;
        Delta delta = new Delta( base ) ;
        delta.delete( triple( "a T b" ) ) ;
        delta.add( triple( "x R z" ) ) ;
        delta.delete( triple( "p S q" ) ) ;
        delta.add( triple( "a T b" ) ) ;
        assertIsomorphic(graphWith( DEFAULT_TRIPLES ), base);
        assertIsomorphic(graphWith( "a T b; x R z" ), delta.getAdditions());
        assertIsomorphic(graphWith( "p S q" ), delta.getDeletions());
        assertIsomorphic(graphWith( "x R y ; x R z; a T b" ), delta);
    }

}
