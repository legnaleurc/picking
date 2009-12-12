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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import org.foolproofproject.Pack;

/**
 * Algorithm performer.
 * 
 * @author Wei-Cheng Pan
 */
public class Performer {
	
	/**
	 * Store picking result.
	 * 
	 * @author Wei-Cheng Pan
	 */
	public class Result {
		private long value_;
		private Vector< SmartFile > files_;
		private Result( long value, Vector< SmartFile > files ) {
			this.value_ = value;
			Collections.sort( files );
			this.files_ = files;
		}
		public long getValue() {
			return this.value_;
		}
		public Vector< SmartFile > getFiles() {
			return this.files_;
		}
	}
	
	private final long limit_;
	private long value_;
	private Hashtable< SmartFile, Long > table_;
	private Vector< SmartFile > overflow_, items_;
	
	/**
	 * Constructor.
	 * 
	 * @param limit combination maximum size
	 * @param files all files
	 */
	public Performer( long limit, File[] files ) {
		this.limit_ = limit;
		this.value_ = 0L;
		this.items_ = new Vector< SmartFile >();
		this.table_ = new Hashtable< SmartFile, Long >();
		this.overflow_ = new Vector< SmartFile >();
		
		for( File f : files ) {
			SmartFile file = SmartFile.fromFile( f );
			this.put_( file, file.getTotalSize() );
		}
	}
	
	/**
	 * Pick once.
	 * 
	 * @return Result.
	 */
	public Result once() {
		if( this.value_ < this.limit_ ) {	// no need to pick, directly return result
			return new Result( this.value_, this.items_ );
		}
		Hashtable< Object, Long > tmpTable = new Hashtable< Object, Long >();
		for( SmartFile item : this.items_ ) {
			tmpTable.put( item, this.table_.get( item ) );
		}
		Pack r = Pack.pick( this.limit_, tmpTable );
		Vector< SmartFile > tmp = new Vector< SmartFile >();
		for( Object o : r.getItems() ) {
			tmp.add( ( SmartFile )o );
		}
		return new Result( r.getValue(), tmp );
	}
	
	/**
	 * Remove items by given keys.
	 * 
	 * @param keys item keys
	 */
	public void remove( Vector< SmartFile > keys ) {
		this.items_.removeAll( keys );
		for( SmartFile key : keys ) {
			this.value_ -= this.table_.get( key );
		}
	}
	
	public Hashtable< SmartFile, Long > getTable() {
		return this.table_;
	}
	public boolean noItem() {
		return this.items_.isEmpty();
	}
	public boolean noOverflow() {
		return this.overflow_.isEmpty();
	}
	public Vector< SmartFile > getOverflow() {
		return this.overflow_;
	}
	
	private void put_( SmartFile key, long value ) {
		if( value < this.limit_ ) {
			this.value_ += value;
			this.items_.add( key );
		} else {
			this.overflow_.add( key );
		}
		this.table_.put( key, value );
	}

}
