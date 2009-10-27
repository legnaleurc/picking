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

import javax.swing.JComboBox;

public class UnitUtility {
	
	private static final String[] unit = new String[4];
	
	static {
		unit[0] = "B";
		unit[1] = "KB";
		unit[2] = "MB";
		unit[3] = "GB";
	}
	
	public static long extract( long value, int eng ) {
		return (long) (value * Math.pow( 1024, eng ));
	}
	
	public static String toString( long value, int eng ) {
		return String.format( "%d %s", (long)( value / Math.pow( 1024, eng ) ), unit[eng] );
	}
	
	public static String toString( long value ) {
		double size = value;
		int pow = 0;
		while( size >= 1024 ) {
			++pow;
			size /= 1024;
		}
		return String.format( "%.3f %s", size, unit[pow] );
	}
	
	public static JComboBox createComboBox() {
		JComboBox tmp = new JComboBox();
		for( String u : unit ) {
			tmp.addItem( u );
		}
		return tmp;
	}

}
