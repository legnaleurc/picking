/**
 * @file Main.java
 * @author Wei-Cheng Pan
 * 
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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.foolproofproject.picking.cli.CommandLine;
import org.foolproofproject.picking.gui.LogDialog;
import org.foolproofproject.picking.gui.MainWindow;

/**
 * @brief Entry class.
 */
public class Main {

	/**
	 * @brief Entry point.
	 * @param args arguments
	 */
	public static void main(String[] args) {
		if( args.length == 0 ) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
					} catch( Exception e ) {
						LogDialog.getErrorLog().log( e.getMessage() );
					}
					
					MainWindow self = new MainWindow( "PicKing" );
					self.setVisible(true);
				}
			} );
		} else {
			CommandLine.parse( args );
		}
	}

}
