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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Hashtable;

import org.junit.BeforeClass;
import org.junit.Test;

public class PackTest {
	
	private static Hashtable< Object, Long > table = new Hashtable< Object, Long >();
	private static long limit = 0L;
	
	private static Hashtable< Object, Long > generateTestCase( int size, long seed ) {
		Hashtable< Object, Long > h = new Hashtable< Object, Long >();
		for( int i = 0; i < size; ++i ) {
			long tmp = ( long )Math.floor( ( 0.5 + Math.random() * 2.5 * seed ) );
			h.put( Integer.toString( i ), tmp);
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
		Pack bfs = Pack.pickSmall( limit, table );
		System.out.println( bfs );
		Pack dfs = Pack.pickSmall2( limit, table );
		System.out.println( dfs );
		assertTrue( bfs.equals( dfs ) );
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
