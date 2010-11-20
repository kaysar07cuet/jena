/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.script;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.assembler.AssemblerVocab;

public class ScriptVocab
{
    // A command is ...
    // a dataset + a query + an output format
    static final String NS = AssemblerVocab.getURI() ;
    // Types 
    public static final Resource CommandLineType                = type(NS, "Cmd") ;
    public static final Resource ScriptType                     = type(NS, "Script") ;
    public static final Resource DatasetAssemblerType           = type(NS, "Dataset") ;

    //public static final Resource CommandAssemblerType           = type(NS, "Command") ;

    private static boolean initialized = false ; 
    static { init() ; }
    
    public static void init()
    {
        if ( initialized )
            return ;
        assemblerClass(CommandLineType,               new CmdDescAssembler()) ;
        assemblerClass(ScriptType,                    new ScriptAssembler()) ;
        initialized = true ;
    }
    
    private static void assemblerClass(Resource r, Assembler a)
    {
        Assembler.general.implementWith(r, a) ;
        //**assemblerAssertions.add(r, RDFS.subClassOf, JA.Object) ;
    }
    
    private static Resource type(String namespace, String localName)
    { return ResourceFactory.createResource(namespace+localName) ; }

    private static Property property(String namespace, String localName)
    { return ResourceFactory.createProperty(namespace+localName) ; }
}

/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */