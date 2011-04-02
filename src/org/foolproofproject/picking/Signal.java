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

import java.util.ArrayList;

public class Signal {

	public static interface Slot {
		void call( Object sender, Object ... arg );
	}

	private Object sender_;
	private ArrayList< Slot > slots_;

	public Signal( Object sender ) {
		this.sender_ = sender;
		this.slots_ = new ArrayList< Slot >();
	}

	public synchronized Boolean connect( Slot slot ) {
		if( slot == null || this.slots_.indexOf( slot ) >= 0 ) {
			return false;
		}
		this.slots_.add( slot );
		return true;
	}

	public synchronized void disconnect() {
		this.slots_.clear();
	}

	public synchronized Boolean disconnect( Slot slot ) {
		return this.slots_.remove( slot );
	}

	public synchronized void emit( Object ... args ) {
		for( Slot slot : this.slots_ ) {
			slot.call( this.sender_, args );
		}
	}

}
