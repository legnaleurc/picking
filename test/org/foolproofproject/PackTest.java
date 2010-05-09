/*
 * PicKing, a file picker.
 * Copyright (C) 2009  Wei-Cheng Pan <legnaleurc@gmail.com>
 * 
 * This file is part of PicKing.
 *
 * PicKing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PicKing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.foolproofproject;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import org.junit.BeforeClass;
import org.junit.Test;

public class PackTest {
	
	private static Hashtable< Integer, Long > table = new Hashtable< Integer, Long >();
	private static long limit = 0L;
	
	private static Hashtable< Integer, Long > generateTestCase( int size, long seed ) {
		Hashtable< Integer, Long > h = new Hashtable< Integer, Long >();
		for( int i = 0; i < size; ++i ) {
			long tmp = ( long )Math.floor( ( 0.5 + Math.random() * 2.5 * seed ) );
			h.put( i, tmp);
		}
		return h;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int size = 16;
		long seed = ( long )Math.floor( Math.random() * 100 );
		table = generateTestCase( size, seed );
		limit = size * seed;
	}
	
	@Test
	public void testSmall() {
		Pack< Integer > bfs = Pack.pickSmall( limit, table );
		Collections.sort( bfs.getItems() );
		System.out.println( bfs );
		Pack< Integer > dfs = Pack.pickSmall2( limit, table );
		Collections.sort( dfs.getItems() );
		System.out.println( dfs );
		assertEquals( bfs, dfs );
	}

	@Test
	public void testPick() {
		long[] result = new long[10];
		for( int i = 0; i < 10; ++i ) {
			result[i] = Pack.pick( limit, table ).getValue();
		}
		Arrays.sort( result );
		for( int i = 0; i < 10; ++i ) {
			System.out.println( result[i] );
		}
	}

}
