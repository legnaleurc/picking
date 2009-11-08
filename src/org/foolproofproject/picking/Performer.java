/**
 * @file Performer.java
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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Map.Entry;

import org.foolproofproject.Pack;

/**
 * @brief Algorithm performer.
 */
public class Performer {
	
	/**
	 * @brief Store picking result.
	 */
	public class Result {
		private long size;
		private Vector< SmartFile > items;
		private Result( long size, Vector< SmartFile > files ) {
			this.size = size;
			Collections.sort( files );
			this.items = files;
		}
		public long getSize() {
			return size;
		}
		public Vector< SmartFile > getItems() {
			return items;
		}
	}
	
	private final long limit;
	private long size;
	private Hashtable< SmartFile, Long > items, table;
	private Vector< SmartFile > overflow;
	
	/**
	 * @brief Constructor.
	 * @param limit combination maximum size
	 * @param files all files
	 */
	public Performer( long limit, File[] files ) {
		this.limit = limit;
		size = 0L;
		items = new Hashtable< SmartFile, Long >();
		table = new Hashtable< SmartFile, Long >();
		overflow = new Vector< SmartFile >();
		
		for( File f : files ) {
			SmartFile file = new SmartFile( f );
			put( file, file.getTotalSize() );
		}
	}
	
	/**
	 * @brief Pick once.
	 * @return Result
	 */
	public Result once() {
		if( size < limit ) {	// need not to pick, directly return result
			return new Result( size, new Vector< SmartFile >( items.keySet() ) );
		}
		Hashtable< Object, Long > tmpTable = new Hashtable< Object, Long >();
		for( Entry< SmartFile, Long > e : items.entrySet() ) {
			tmpTable.put( e.getKey(), e.getValue() );
		}
		Pack r = Pack.pick( limit, tmpTable );
		Vector< SmartFile > tmp = new Vector< SmartFile >();
		for( Object o : r.getItems() ) {
			tmp.add( ( SmartFile )o );
		}
		return new Result( r.getSize(), tmp );
	}
	
	/**
	 * @brief Remove items by given keys.
	 * @param keys item keys
	 */
	public void remove( Vector< SmartFile > keys ) {
		for( SmartFile key : keys ) {
			size -= items.get( key );
			items.remove( key );
		}
	}
	
	public Hashtable< SmartFile, Long > getTable() {
		return table;
	}
	public boolean noItem() {
		return items.isEmpty();
	}
	public boolean noOverflow() {
		return overflow.isEmpty();
	}
	public Vector< SmartFile > getOverflow() {
		return overflow;
	}
	
	private void put( SmartFile key, Long value ) {
		if( value < limit ) {
			size += value;
			items.put( key, value );
		} else {
			overflow.add( key );
		}
		table.put( key, value );
	}

}
