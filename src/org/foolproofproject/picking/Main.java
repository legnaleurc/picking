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
package org.foolproofproject.picking;

import org.foolproofproject.picking.cli.CommandLine;
import org.foolproofproject.picking.gui.MainWindow;

import com.trolltech.qt.gui.QApplication;

/**
 * Entry class.
 *
 * @author Wei-Cheng Pan
 */
public class Main {

	/**
	 * Entry point.
	 *
	 * @param args Program arguments
	 */
	public static void main( String[] args ) {
		if( args.length == 0 ) {
			QApplication.initialize( args );

			MainWindow w = new MainWindow();
			w.show();

			QApplication.exec();
		} else {
			CommandLine.parse( args );
		}
	}

}
