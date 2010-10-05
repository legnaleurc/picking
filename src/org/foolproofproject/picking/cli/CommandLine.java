/**
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
package org.foolproofproject.picking.cli;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.foolproofproject.Pack;
import org.foolproofproject.picking.Performer;
import org.foolproofproject.picking.SmartFile;
import org.foolproofproject.picking.UnitUtility;

/**
 * Command line utility.
 * 
 * @author Wei-Cheng Pan
 */
public class CommandLine {
	
	private static final Pattern pattern = Pattern.compile( "(\\d+)((K|M|G)?B?)" );
	
	/**
	 * Parse command line arguments.
	 * 
	 * @param args arguments
	 */
	public static void parse( String[] args ) {
		if( args.length < 1 || args.length > 2 ) {
			CommandLine.printUsage();
			return;
		}
		Matcher m = pattern.matcher( args[0].toUpperCase() );
		if( !m.matches() ) {
			CommandLine.printUsage();
			return;
		}
		
		long limit = Long.parseLong( m.group( 1 ) );
		String unit = m.group( 2 );
		int eng = 0;
		if( unit.startsWith( "G" ) ) {
			eng = 3;
		} else if( unit.startsWith( "M" ) ) {
			eng = 2;
		} else if( unit.startsWith( "K" ) ) {
			eng = 1;
		} else {
			;
		}

		File path = new File( ( args.length < 2 ) ? "." : args[1] );
		if( !path.isDirectory() ) {
			path = new File( "." );
		}

		CommandLine.perform( path.listFiles(), UnitUtility.extract( limit, eng ), eng );	
	}
	
	private static void perform( File[] files, long limit, int eng ) {
		Performer p = new Performer( limit, files );
		
		while( !p.noItem() ) {
			Pack< SmartFile > pair = p.call();
			System.out.println( UnitUtility.toString( pair.getScore(), eng ) + ":" );
			for( SmartFile item : pair.getItems() ) {
				System.out.println( "\t" + item );
			}
			p.remove( pair.getItems() );
		}
		
		if( !p.noOverflow() ) {
			System.out.println( "Overflow:" );
			for( SmartFile item : p.getOverflow() ) {
				System.out.println( "\t" + item );
			}
		}
	}
	
	private static void printUsage() {
		System.out.println( "Usage: <limit[(K|M|G)[B]]> <directory>" );
	}

}
