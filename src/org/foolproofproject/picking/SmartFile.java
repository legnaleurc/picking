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

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * File wrapper for file utility.
 * 
 * @author Wei-Cheng Pan
 */
public class SmartFile extends File {

	private static final long serialVersionUID = -498630998152519151L;
	
	/**
	 * Named constructor.
	 * 
	 * @param file Source object
	 * @return A new instance.
	 */
	public static SmartFile fromFile( File file ) {
		return new SmartFile( file.getPath() );
	}

	/**
	 * Construct with path.
	 * 
	 * @param pathname Path
	 */
	public SmartFile(String pathname) {
		super(pathname);
	}
	
	/**
	 * Use getName() instead of getAbsolutePath().
	 */
	@Override
	public String toString() {
		return this.getName();
	}
	@Override
	public SmartFile[] listFiles() {
		return this.listFiles( (FileFilter)null );
	}
	@Override
	public SmartFile[] listFiles( FileFilter filter ) {
		File[] original = super.listFiles( filter );
		if( original == null ) {
			return null;
		}
		Arrays.sort( original );
		SmartFile[] tmp = new SmartFile[original.length];
		for( int i = 0; i < tmp.length; ++i ) {
			tmp[i] = SmartFile.fromFile( original[i] );
		}
		return tmp;
	}
	@Override
	public SmartFile getParentFile() {
		File parent = super.getParentFile();
		if( parent == null ) {
			return null;
		} else {
			return SmartFile.fromFile( parent );
		}
	}
	@Override
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
	 * Recursively calculate directory size.
	 * Overloaded version of {@link #getTotalSize(FileFilter)} for convince.
	 * 
	 * @return Total size of directory or file.
	 */
	public long getTotalSize() {
		return this.getTotalSize( null );
	}
	/**
	 * Recursively calculate directory size with file filter.
	 * 
	 * @param filter File filter
	 * @return Total size of directory or file.
	 */
	public long getTotalSize( FileFilter filter ) {
		if( this.isDirectory() ) {
			long sum = this.length();
			SmartFile[] files = this.listFiles( filter );
			if( files != null ) {
				for( SmartFile file : files ) {
					sum += file.getTotalSize( filter );
				}
			}
			return sum;
		} else {
			return this.length();
		}
	}

}
