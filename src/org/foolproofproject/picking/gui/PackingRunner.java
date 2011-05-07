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
package org.foolproofproject.picking.gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.foolproofproject.Pack;
import org.foolproofproject.picking.FileUtility;

import com.trolltech.qt.core.QRunnable;

public class PackingRunner extends QRunnable {

	private List< String > items_;
	private Long limit_;

	public Signal1< File > overflowDetected = new Signal1< File >();
	public Signal1< Pack< File > > packed = new Signal1< Pack< File > >();
	public Signal0 finished = new Signal0();

	public PackingRunner( Long limit, List< String > items ) {
		this.limit_ = limit;
		this.items_ = items;
	}

	@Override
	public void run() {
		HashMap< File, Long > table = new HashMap< File, Long >();
		for( String filePath : this.items_ ) {
			File file = new File( filePath );
			Long size = FileUtility.getTotalSize( file );
			if( size <= this.limit_ ) {
				table.put( file, size );
			} else {
				this.overflowDetected.emit( file );
			}
		}

		while( !table.isEmpty() ) {
			Pack< File > pack = Pack.pick( this.limit_, table );
			this.packed.emit( pack );
			for( File item : pack.getItems() ) {
				table.remove( item );
			}
		}

		this.finished.emit();
	}

}
