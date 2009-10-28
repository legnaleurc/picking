/**
 * @file FileDialog.java
 * @author Wei-Cheng Pan
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
package org.foolproofproject.picking.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @brief File dialog utility.
 */
public class FileDialog {
	
	/**
	 * @brief Get existing directory.
	 * @param parent parent widget
	 * @return selected directory
	 */
	public static File getExistingDirectory( Component parent ) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter( new FileFilter() {
			@Override
			public boolean accept( File f ) {
				return f.isDirectory();
			}
			@Override
			public String getDescription() {
				return "Directories";
			}
		} );
		fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		
		switch( fc.showOpenDialog( parent ) ) {
		case JFileChooser.APPROVE_OPTION:
			return fc.getSelectedFile();
		default:
			return null;
		}
	}
	
	/**
	 * @brief Get save file name.
	 * @param parent parent widget
	 * @param filter file name filter
	 * @return selected file
	 */
	public static File getSaveFileName( Component parent, FileFilter filter ) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter( filter );
		
		switch( fc.showSaveDialog( parent ) ) {
		case JFileChooser.APPROVE_OPTION:
			return fc.getSelectedFile();
		default:
			return null;
		}
	}

}
