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

package org.apache.jena.hadoop.rdf.io.input.readers;

import java.util.Iterator;

import org.apache.jena.hadoop.rdf.types.TripleWritable;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.tokens.Tokenizer;
import com.hp.hpl.jena.graph.Triple;

/**
 * An abstract record reader for line based triple formats
 * 
 * 
 * 
 */
public abstract class AbstractLineBasedTripleReader extends AbstractLineBasedNodeTupleReader<Triple, TripleWritable> {

    @Override
    protected Iterator<Triple> getIterator(String line, ParserProfile profile) {
        Tokenizer tokenizer = getTokenizer(line);
        return getTriplesIterator(tokenizer, profile);
    }

    @Override
    protected TripleWritable createInstance(Triple t) {
        return new TripleWritable(t);
    }
    
    protected abstract Tokenizer getTokenizer(String line);

    protected abstract Iterator<Triple> getTriplesIterator(Tokenizer tokenizer, ParserProfile profile);

}
