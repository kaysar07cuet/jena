/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.layout2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sdb.SDBException;
import com.hp.hpl.jena.sdb.core.*;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlColumn;
import com.hp.hpl.jena.sdb.core.sqlexpr.SqlExpr;
import com.hp.hpl.jena.sdb.core.sqlnode.SqlNode;
import com.hp.hpl.jena.sdb.core.sqlnode.SqlRestrict;
import com.hp.hpl.jena.sdb.core.sqlnode.SqlTable;
import com.hp.hpl.jena.sdb.engine.compiler.QC;
import com.hp.hpl.jena.sdb.engine.compiler.QuadBlock;
import com.hp.hpl.jena.sdb.engine.compiler.QuadBlockCompilerTriple;
import com.hp.hpl.jena.sdb.engine.compiler.SDBConstraint;
import com.hp.hpl.jena.sdb.store.NodeTableDesc;
import com.hp.hpl.jena.sdb.store.TripleTableDesc;

public abstract class QuadBlockCompiler2 extends QuadBlockCompilerTriple
{
    // Slot typing.
    // Result handling.
    
    private static Log log = LogFactory.getLog(QuadBlockCompiler2.class) ;
    
    private Generator genNodeResultAlias = Gensym.create(Aliases.NodesResultAliasBase) ;

    List<Node> constants = new ArrayList<Node>() ;
    List<Var>  vars = new ArrayList<Var>() ;
    protected TripleTableDesc tripleTableDesc ;
    protected NodeTableDesc   nodeTableDesc ;
    
    
    public QuadBlockCompiler2(SDBRequest request)
    { 
        super(request) ;
        tripleTableDesc = request.getStore().getTripleTableDesc() ;
        nodeTableDesc = request.getStore().getNodeTableDesc() ;
    }

    @Override
    final protected SqlNode start(QuadBlock quads)
    {
        classify(quads, constants, vars) ;
        addMoreConstants(constants) ;
        SqlNode sqlNode = insertConstantAccesses(request, constants) ;
        return sqlNode ;
    }

    // Hook for specialized engines.
    protected void addMoreConstants(Collection<Node> constants)
    {}

    @Override
    final protected SqlNode finish(SqlNode sqlNode, QuadBlock quads)
    { return sqlNode ; }
    
    protected abstract 
    SqlNode insertConstantAccesses(SDBRequest request, Collection<Node> constants) ;

    private SqlNode addRestrictions(SDBRequest request,
                                    SqlNode sqlNode,
                                    List<SDBConstraint> constraints)
    {
        if ( constraints.size() == 0 )
            return sqlNode ;

        // Add all value columns
        for ( SDBConstraint c : constraints )
        {
            @SuppressWarnings("unchecked")
            Set<Var> vars = c.getExpr().getVarsMentioned() ;
            for ( Var v : vars )
            {
                // For Variables used in this SQL constraint, make sure the value is available.  
                ScopeEntry e = sqlNode.getIdScope().findScopeForVar(v) ;  // tripleTableCol
                
                if ( e == null )
                {
                    // Not in scope.
                    log.info("Var not in scope for value of expression: "+v) ;
                    continue ;
                }
                SqlColumn tripleTableCol = e.getColumn() ;    

                // Value table column
                SqlTable nTable =   new SqlTable(nodeTableDesc.getTableName(), genNodeResultAlias.next()) ;
                SqlColumn colId =   new SqlColumn(nTable, nodeTableDesc.getIdColName()) ;
                SqlColumn colLex =  new SqlColumn(nTable, nodeTableDesc.getLexColName()) ;
//                SqlColumn colType = new SqlColumn(nTable, nodeTableDesc.getTypeColName()) ;

                nTable.setValueColumnForVar(v, colLex) ;        // ASSUME lexical/string form needed 
                nTable.setIdColumnForVar(v, colId) ;            // Id scope => join
                sqlNode = QC.innerJoin(request, sqlNode, nTable) ;
            }

            SqlExpr sqlExpr = c.compile(sqlNode.getNodeScope()) ;
            sqlNode = SqlRestrict.restrict(sqlNode, sqlExpr) ;
        }
        return sqlNode ;
    }

    @Override
    final protected SqlTable accessTriplesTable(String alias)
    {
        return new SqlTable(tripleTableDesc.getTableName(), alias) ;
    }
    
    private static void classify(QuadBlock quadBlock, Collection<Node> constants, Collection<Var>vars)
    {
        for ( Quad quad : quadBlock )
        {
            if ( ! quad.getGraph().equals(Quad.defaultGraph) )
            {
                log.fatal("Non-default graph") ;
                throw new SDBException("Non-default graph") ;
            }
            if ( false )
                // Not quadding currently.
                acc(constants, vars, quad.getGraph()) ;
            acc(constants, vars, quad.getSubject()) ;
            acc(constants, vars, quad.getPredicate()) ;
            acc(constants, vars, quad.getObject()) ;
        }
    }

    private static void acc(Collection<Node>constants,  Collection<Var>vars, Node node)
    { 
        // ?? node.isConcrete()
        if ( node.isLiteral() || node.isBlank() || node.isURI() )
        {
            constants.add(node) ;
            return ;
        }
        if ( Var.isVar(node) )
        {
            vars.add(Var.alloc(node)) ;
            return ;
        }
        if ( node.isVariable() )
        {
            log.warn("Node_Varable but not a Var; bodged") ;
            vars.add(Var.alloc(node)) ;
            return ;
        }
        log.fatal("Unknown Node type: "+node) ;
        throw new SDBException("Unknown Node type: "+node) ;
    }
    
    //private static 
}

/*
 * (c) Copyright 2006, 2007 Hewlett-Packard Development Company, LP
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