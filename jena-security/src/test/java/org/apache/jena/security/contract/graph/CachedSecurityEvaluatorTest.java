/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.security.contract.graph;

import org.apache.jena.security.SecurityEvaluator;
import org.apache.jena.security.StaticSecurityEvaluator;
import org.apache.jena.security.impl.CachedSecurityEvaluator;
import org.junit.Test;
import static org.junit.Assert.*;

public class CachedSecurityEvaluatorTest {

	private StaticSecurityEvaluator securityEvaluator;
	private SecurityEvaluator cachedEvaluator;
	
	public CachedSecurityEvaluatorTest() {
		securityEvaluator = new StaticSecurityEvaluator( "bob" );
		cachedEvaluator = new CachedSecurityEvaluator( securityEvaluator, "ted" );
		
	}
	
	@Test
	public void testGetPrincipal()
	{
		assertEquals( "bob", securityEvaluator.getPrincipal());
		assertEquals( "ted", cachedEvaluator.getPrincipal());
	}

}
