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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.foolproofproject.Pack;

/**
 * Algorithm performer.
 * @author Wei-Cheng Pan
 */
public class Performer {

	private final long limit_;
	private HashMap< File, Long > table_;
	private ArrayList< File > overflow_, items_;

	/**
	 * Constructor.
	 * @param limit Combination maximum size
	 * @param files All files
	 */
	public Performer( long limit, File[] files ) {
		this.limit_ = limit;
		this.items_ = new ArrayList< File >();
		this.table_ = new HashMap< File, Long >();
		this.overflow_ = new ArrayList< File >();

		for( File file : files ) {
			this.put_( file, FileUtility.getTotalSize( file ) );
		}
	}

	/**
	 * Pick once.
	 * @return Result.
	 */
	public Pack< File > call() {
		return Pack.pick( this.limit_, this.table_ );
	}

	public ArrayList< File > getOverflow() {
		return this.overflow_;
	}

	public HashMap< File, Long > getTable() {
		return this.table_;
	}
	public boolean noItem() {
		return this.items_.isEmpty();
	}
	public boolean noOverflow() {
		return this.overflow_.isEmpty();
	}
	private void put_( File key, long value ) {
		if( value < this.limit_ ) {
			this.items_.add( key );
		} else {
			this.overflow_.add( key );
		}
		this.table_.put( key, value );
	}

	/**
	 * Remove items by given keys.
	 * @param keys Item keys
	 */
	public void remove( List< File > keys ) {
		this.items_.removeAll( keys );
		for( File key : keys ) {
			this.table_.remove( key );
		}
	}

}
