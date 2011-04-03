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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

public class CleanUpUtility {

	public static void export( File file, DefaultMutableTreeNode node ) throws FileNotFoundException {
		PrintStream fout = new PrintStream( file );
		fout.println( "#! /bin/sh\n" );
		for( Enumeration< ? > e = node.children(); e.hasMoreElements(); ) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			File sm = ( File )child.getUserObject();
			fout.printf( "rm -rf '%s'\n", sm.getAbsolutePath() );
		}
		fout.print( "\nrm -rf $0\n" );
		fout.close();
		file.setExecutable( true );
	}

}
