/**
 * @file SmartFile.java
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

import java.io.File;
import java.io.FileFilter;
import java.net.URI;

/**
 * @brief File wrapper for file utility.
 */
public class SmartFile extends File {

	private static final long serialVersionUID = -498630998152519151L;

	public SmartFile(String pathname) {
		super(pathname);
	}

	public SmartFile(URI uri) {
		super(uri);
	}

	public SmartFile(String parent, String child) {
		super(parent, child);
	}

	public SmartFile(File parent, String child) {
		super(parent, child);
	}
	
	public SmartFile( File file ) {
		super( file.getPath() );
	}
	
	/**
	 * @brief Use getName() instead of getAbsolutePath().
	 */
	public String toString() {
		return this.getName();
	}
	
	public SmartFile[] listFiles() {
		return listFiles( (FileFilter)null );
	}
	
	public SmartFile[] listFiles( FileFilter filter ) {
		File[] original = super.listFiles( filter );
		if( original == null ) {
			return null;
		}
		SmartFile[] tmp = new SmartFile[original.length];
		for( int i = 0; i < tmp.length; ++i ) {
			tmp[i] = new SmartFile( original[i] );
		}
		return tmp;
	}
	
	public SmartFile getParentFile() {
		File parent = super.getParentFile();
		if( parent == null ) {
			return null;
		} else {
			return new SmartFile( parent );
		}
	}
	
	public int compareTo( File that ) {
		if( this.isDirectory() && !that.isDirectory() ) {
			return -1;
		} else if( !this.isDirectory() && that.isDirectory() ) {
			return 1;
		} else {
			return super.compareTo( that );
		}
	}
	
	/**
	 * @brief Recursively calculate directory size.
	 * @return directory total size
	 */
	public long getTotalSize() {
		if( isDirectory() ) {
			long sum = length();
			SmartFile[] files = listFiles();
			if( files != null ) {
				for( SmartFile file : files ) {
					sum += file.getTotalSize();
				}
			}
			return sum;
		} else {
			return length();
		}
	}

}
