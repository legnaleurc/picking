package org.foolproofproject.picking;

import java.io.File;
import java.io.FileFilter;

public class FileUtility {

	public static Long getTotalSize( File file ) {
		return FileUtility.getTotalSize( file, null );
	}

	public static Long getTotalSize( File file, FileFilter filter ) {
		if( !file.isDirectory() ) {
			return file.length();
		}
		Long sum = file.length();
		File[] files = file.listFiles( filter );
		if( files != null ) {
			for( File f : files ) {
				sum += FileUtility.getTotalSize( f, filter );
			}
		}
		return sum;
	}

	private FileUtility() {
	}

	private FileUtility( FileUtility that ) {
	}

}
