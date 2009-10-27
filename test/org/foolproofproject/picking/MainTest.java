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
package org.foolproofproject.picking;

import org.junit.Test;

public class MainTest {

	@Test
	public void testMain() {
		String[] args = new String[1];
		args[0] = "help";
		Main.main( args );
		
		args[0] = "ramdom";
		Main.main( args );
		
		args[0] = "1024K";
		Main.main( args );
		
		args = new String[2];
		args[0] = "random";
		args[1] = "dummy";
		Main.main( args );
		
		args[0] = "1024MB";
		args[1] = "src";
		Main.main( args );
		
		args = new String[3];
		args[0] = "1024GB";
		args[1] = "dummy";
		args[1] = "dummy";
		Main.main( args );
	}

}
