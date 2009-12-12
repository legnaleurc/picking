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

import java.util.Arrays;
import java.util.Hashtable;

import org.junit.BeforeClass;
import org.junit.Test;

public class PackTest {
	
	private static Hashtable< Object, Long > table = new Hashtable< Object, Long >();
	private static long limit = 0L;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		long totalSize = 0L;
		int tableSize = (int) (Math.random() * 128) + 16;
		for( int i = 0; i < tableSize; ++i ) {
			long single = (long) (Math.random() * 1073741824);
			table.put( Integer.toString( i ), single );
			totalSize += single;
		}
		limit = totalSize / 3;
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
