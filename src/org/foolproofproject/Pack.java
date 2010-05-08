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
package org.foolproofproject;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * <p>Calculates the maximum combination of given objects table.</p>
 * <p>To do this you should collect objects and their values, and store them
 * to a <code>Hashtable&lt;Object,Long&gt;</code> as a table. Then decide a
 * maximum value of combinations of objects.</p>
 * <p>Simply use {@link #pick(Long, Hashtable)} to perform algorithm.</p>
 * <p>If the amount of items is less then 16, then it will use brute force
 * (i.e. {@link #pickSmall(Long, Hashtable)}) to find an optimal solution. Or
 * it will use heuristic algorithm (i.e. {@link #pickLarge(Long, Hashtable)})
 * to do it.</p>
 * 
 * @author Wei-Cheng Pan
 */
public class Pack {

	private long value_;
	private Vector< Object > items_;
	
	/**
	 * Default constructor.
	 * Will initialize value to 0, items as an empty Vector.
	 */
	private Pack() {
		this.value_ = 0L;
		this.items_ = new Vector< Object >();
	}
	/**
	 * Constructor.
	 * 
	 * @param value total value of items
	 * @param items selected objects
	 */
	private Pack( long value, Vector< Object > items ) {
		this.value_ = value;
		this.items_ = items;
	}
	private Pack add( long value, Object key ) {
		Vector< Object > tmp = new Vector< Object >( this.items_ );
		tmp.add( key );
		return new Pack( this.value_ + value, tmp );
	}
	@Override
	public String toString() {
		return "(" + this.value_ + ":" + this.items_ + ")";
	}
	/**
	 * Get value.
	 * 
	 * @return Total value of items.
	 */
	public long getValue() {
		return this.value_;
	}
	/**
	 * Get items list.
	 * 
	 * @return Items list.
	 */
	public Vector< Object > getItems() {
		return this.items_;
	}
	
	/**
	 * Genetic algorithm.
	 * For internal usage only.
	 */
	private static class GeneticAlgorithm {
		
		/**
		 * A cell which represents a possible combination.
		 */
		private static class Cell implements Comparable< Cell > {
			
			/// Table of item selection.
			private Hashtable< Object, Boolean > table_;
			/// Total value
			private Long value_;
			
			/**
			 * Constructor.
			 * 
			 * @param table Item selection table
			 * @param value Total value
			 */
			public Cell( Hashtable< Object, Boolean > table, Long value ) {
				this.table_ = table;
				this.value_ = value;
			}
			/**
			 * Copy constructor.
			 * 
			 * @param that Copy source
			 */
			public Cell( Cell that ) {
				this.table_ = new Hashtable< Object, Boolean >( that.table_ );
				this.value_ = that.value_;
			}
			/**
			 * Check if can toggle.
			 * 
			 * @param key Toggle target
			 * @param value Value of target
			 * @param limit Maximum limit
			 * @return true if can toggle.
			 */
			public boolean canToggle( Object key, Long value, Long limit ) {
				return ( this.table_.get( key ) || this.value_ + value < limit );
			}
			/**
			 * Toggle item selection.
			 * 
			 * @param key Toggle target
			 * @param value Value of target
			 */
			public void toggle( Object key, Long value ) {
				boolean tmp = this.table_.get( key );
				this.table_.put( key, !tmp );
				if( tmp ) {
					this.value_ -= value;
				} else {
					this.value_ += value;
				}
			}
			/**
			 * Get item selection table.
			 * 
			 * @return Selection table.
			 */
			public Hashtable< Object, Boolean > getTable() {
				return this.table_;
			}
			/**
			 * Get total value.
			 * 
			 * @return Total value.
			 */
			public Long getValue() {
				return this.value_;
			}
			@Override
			public String toString() {
				return String.format( "(%d,%s)", this.value_, this.table_.keySet() );
			}
			@Override
			public int compareTo( Cell rhs ) {
				return rhs.value_.compareTo( this.value_ );
			}
		}
		
		private Long limit_;
		private Hashtable< Object, Long > table_;
		private Vector< Cell > population_;
		
		public GeneticAlgorithm( Long limit, Hashtable< Object, Long > items ) {
			this.limit_ = limit;
			this.table_ = items;
			
			this.population_ = new Vector< Cell >();
			for( int i = 0; i < items.size(); ++i ) {
				this.population_.add( this.generatePopulation() );
			}
			Collections.sort( this.population_ );
		}
		
		public Pack perform() {
			while( !this.canStop() ) {
				this.crossOver();
				this.mutation();
				Collections.sort( this.population_ );
				this.population_.subList( this.table_.size(), this.population_.size() ).clear();
			}
			
			Cell survivor = this.population_.get( 0 );
			Pack result = new Pack( survivor.getValue(), new Vector< Object >() );
			for( Entry< Object, Boolean > e : survivor.getTable().entrySet() ) {
				if( e.getValue() ) {
					result.getItems().add( e.getKey() );
				}
			}
			return result;
		}
		
		private Cell generatePopulation() {
			Hashtable< Object, Boolean > cell = new Hashtable< Object, Boolean >();
			Long sum = 0L;
			for( Entry< Object, Long > e : this.table_.entrySet() ) {
				if( e.getValue() + sum >= this.limit_ || Math.random() * 2 < 1.0 ) {
					cell.put( e.getKey(), false );
				} else {
					cell.put( e.getKey(), true );
					sum += e.getValue();
				}
			}
			return new Cell( cell, sum );
		}
		
		/**
		 * Select parent index.
		 * <pre>
		 * N := total size
		 * 
		 * Plain:
		 * int i = 0;
		 * for( ; Math.random() * ( N - i ) >= 2.0; ++i );
		 * return i;
		 * 
		 * PDF:
		 * P( X = i ) = 2(N-1-i)/(N(N-1))
		 * 
		 * CDF:
		 * P( X <= i ) = i(2N-1-i)/(N(N-1)) = C
		 * b = 2N-1
		 * d = b^2 - 4CN(N-1)
		 * i = (b-d^0.5)/2
		 * </pre>
		 */
		private int selectParent() {
			int n = this.table_.size();
			int b = 2 * n - 1;
			double c = Math.random();
			double d = ( b * b - 1 ) * ( 1 - c ) + 1;
			double k = ( b - Math.sqrt( d ) ) / 2;
			return ( int )Math.floor( k );
		}
		
		private Boolean canStop() {
			Cell head = this.population_.firstElement();
			Cell tail = this.population_.lastElement();
			return head.getValue().equals( tail.getValue() );
		}
		
		private void crossOver() {
			final int length = this.population_.size();
			for( int i = 0; i < length; ++i ) {
				Cell new1 = new Cell( this.population_.get( i ) );
				Cell new2 = new Cell( this.population_.get( selectParent() ) );
				for( Entry< Object, Long > e : this.table_.entrySet() ) {
					if( new1.getTable().get( e.getKey() ) == new2.getTable().get( e.getKey() ) ) {
						continue;
					}
					if( !new1.canToggle( e.getKey(), e.getValue(), this.limit_ ) || !new2.canToggle( e.getKey(), e.getValue(), this.limit_ ) ) {
						continue;
					}
					if( Math.random() < 0.5 ) {
						new1.toggle( e.getKey(), e.getValue() );
						new2.toggle( e.getKey(), e.getValue() );
					}
				}
				this.population_.add( new1 );
				this.population_.add( new2 );
			}
		}
		
		private void mutation() {
			final int length = this.population_.size();
			for( int i = 0; i < length; ++i ) {
				Cell cell = this.population_.get( i );
				for( Entry< Object, Long > e : table_.entrySet() ) {
					if( cell.canToggle( e.getKey(), e.getValue(), this.limit_) && Math.random() * this.table_.size() < 1.0 ) {
						cell.toggle( e.getKey(), e.getValue() );
					}
				}
			}
		}
		
	}
	
	/**
	 * Main pick function.
	 * If table size is greater then or equal to 16, it will use heuristic
	 * algorithm.
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static Pack pick( Long limit, Hashtable< Object, Long > items ) {
		if( items.size() < 16 ) {
			return Pack.pickSmall( limit, items );
		} else {
			return Pack.pickLarge( limit, items );
		}
	}
	
	/**
	 * Back-end to pick using brute force.
	 * The complexity is O(2^n).
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static Pack pickLarge( Long limit, Hashtable< Object, Long > items ) {
		GeneticAlgorithm ga = new GeneticAlgorithm( limit, items );
		return ga.perform();
	}
	
	public static Pack pickSmall2( Long limit, Hashtable< Object, Long > items ) {
		return new Recursion( limit, items ).perform( 0, new Pack() );
	}
	
	private static class Recursion {
		
		private Vector< Object > keys_;
		private Vector< Long > values_;
		private Long limit_;
		
		public Recursion( Long limit, Hashtable< Object, Long > items ) {
			this.keys_ = new Vector< Object >( items.keySet() );
			this.values_ = new Vector< Long >( items.values() );
			this.limit_ = limit;
		}
		
		public Pack perform( int n, Pack p ) {
			if( n == this.keys_.size() || ( p.value_ + this.values_.get( n ) ) > this.limit_ ) {
				return p;
			} else {
				Pack a = this.perform( n + 1, p );
				Pack b = this.perform( n + 1, p.add( this.values_.get( n ), this.keys_.get( n ) ) );
				if( a.value_ > b.value_ ) {
					return a;
				} else {
					return b;
				}
			}
		}
		
	}
	
	/**
	 * Back-end to pick using heuristic algorithm.
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static Pack pickSmall( Long limit, Hashtable< Object, Long > items ) {
		Vector< Pack > table = new Vector< Pack >();
		table.add( new Pack() );
		
		for( Entry< Object, Long> e : items.entrySet() ) {
			Vector< Pack > tmp = new Vector< Pack >();
			for( Pack p : table ) {
				Long newSize = p.getValue() + e.getValue();
				if( newSize <= limit ) {
					Vector< Object > newDirs = new Vector< Object >( p.getItems() );
					newDirs.add( e.getKey() );
					tmp.add( new Pack( newSize, newDirs ) );
				}
			}
			table.addAll(table.size(), tmp);
		}
		
		Pack max = new Pack();
		for( Pack p : table ) {
			if( p.getValue() >= max.getValue() ) {
				max = p;
			}
		}
		return max;
	}

}
