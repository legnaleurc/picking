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
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * <p>Calculates the maximum combination of given objects table.</p>
 * <p>To do this you should collect objects and their values, and store them
 * to a <code>Hashtable&lt;T,Long&gt;</code> as a table. Then decide a
 * maximum score of combinations of objects.</p>
 * <p>Simply use {@link #pick} to perform algorithm.</p>
 * <p>If the amount of items is less then 16, then it will use brute force
 * (i.e. {@link #pickSmall}) to find an optimal solution. Or
 * it will use heuristic algorithm (i.e. {@link #pickLarge}) to do it.</p>
 * 
 * @author Wei-Cheng Pan
 */
public class Pack< T > {

	private Long score_;
	private Vector< T > items_;
	
	/**
	 * Main pick function.
	 * If table size is greater then or equal to 16, it will use heuristic
	 * algorithm.
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static< T > Pack< T > pick( Long limit, Hashtable< T, Long > items ) {
		if( items.size() < 16 ) {
			return Pack.pickSmall( limit, items );
		} else {
			return Pack.pickLarge( limit, items );
		}
	}
	
	/**
	 * Back-end to pick using heuristic algorithm.
	 * The complexity is O(2^n).
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static< T > Pack< T > pickLarge( Long limit, Hashtable< T, Long > items ) {
		GeneticAlgorithm< T > ga = new GeneticAlgorithm< T >( limit, items );
		return ga.perform();
	}

	/**
	 * Back-end to pick using DFS.
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static< T > Pack< T > pickSmall( Long limit, Hashtable< T, Long > items ) {
		return new Recursion< T >( limit, items ).perform( 0, new Pack< T >() );
	}
	
	/**
	 * Default constructor.
	 * Will initialize value to 0, items as an empty Vector.
	 */
	private Pack() {
		this.score_ = 0L;
		this.items_ = new Vector< T >();
	}
	/**
	 * Constructor.
	 * 
	 * @param score total value of items
	 * @param items selected objects
	 */
	private Pack( long score, Vector< T > items ) {
		this.score_ = score;
		this.items_ = items;
	}
	private Pack< T > add( T key, Long value ) {
		Vector< T > tmp = new Vector< T >( this.items_ );
		tmp.add( key );
		return new Pack< T >( this.score_ + value, tmp );
	}
	/**
	 * Returns the score and picked items in this pack.
	 */
	@Override
	public String toString() {
		return "(" + this.score_ + ":" + this.items_ + ")";
	}
	/**
	 * Get score.
	 * 
	 * @return Total score of items.
	 */
	public Long getScore() {
		return this.score_;
	}
	/**
	 * Get items list.
	 * 
	 * @return Items list.
	 */
	public Vector< T > getItems() {
		return this.items_;
	}
	
	/**
	 * Genetic algorithm.
	 * For internal usage only.
	 */
	private static class GeneticAlgorithm< T > {
		
		/**
		 * A cell which represents a possible combination.
		 */
		private static class Cell< T > implements Comparable< Cell< T > > {
			
			/// Table of item selection.
			private Hashtable< T, Boolean > table_;
			/// Total value
			private Long value_;
			
			/**
			 * Constructor.
			 * 
			 * @param table Item selection table
			 * @param value Total value
			 */
			public Cell( Hashtable< T, Boolean > table, Long value ) {
				this.table_ = table;
				this.value_ = value;
			}
			/**
			 * Copy constructor.
			 * 
			 * @param that Copy source
			 */
			public Cell( Cell< T > that ) {
				this.table_ = new Hashtable< T, Boolean >( that.table_ );
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
			public boolean canToggle( T key, Long value, Long limit ) {
				return ( this.table_.get( key ) || this.value_ + value < limit );
			}
			/**
			 * Toggle item selection.
			 * 
			 * @param key Toggle target
			 * @param value Value of target
			 */
			public void toggle( T key, Long value ) {
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
			public Hashtable< T, Boolean > getTable() {
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
			public int compareTo( Cell< T > rhs ) {
				return rhs.value_.compareTo( this.value_ );
			}
		}
		
		private Long limit_;
		private Hashtable< T, Long > table_;
		private Vector< Cell< T > > population_;
		
		public GeneticAlgorithm( Long limit, Hashtable< T, Long > items ) {
			this.limit_ = limit;
			this.table_ = items;
			
			this.population_ = new Vector< Cell< T > >();
			for( int i = 0; i < items.size(); ++i ) {
				this.population_.add( this.generatePopulation() );
			}
			Collections.sort( this.population_ );
		}
		
		public Pack< T > perform() {
			while( !this.canStop() ) {
				this.crossOver();
				this.mutation();
				Collections.sort( this.population_ );
				this.population_.subList( this.table_.size(), this.population_.size() ).clear();
			}
			
			Cell< T > survivor = this.population_.get( 0 );
			Pack< T > result = new Pack< T >( survivor.getValue(), new Vector< T >() );
			for( Entry< T, Boolean > e : survivor.getTable().entrySet() ) {
				if( e.getValue() ) {
					result.getItems().add( e.getKey() );
				}
			}
			return result;
		}
		
		private Cell< T > generatePopulation() {
			Hashtable< T, Boolean > cell = new Hashtable< T, Boolean >();
			Long sum = 0L;
			for( Entry< T, Long > e : this.table_.entrySet() ) {
				if( e.getValue() + sum >= this.limit_ || Math.random() * 2 < 1.0 ) {
					cell.put( e.getKey(), false );
				} else {
					cell.put( e.getKey(), true );
					sum += e.getValue();
				}
			}
			return new Cell< T >( cell, sum );
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
			Cell< T > head = this.population_.firstElement();
			Cell< T > tail = this.population_.lastElement();
			return head.getValue().equals( tail.getValue() );
		}
		
		private void crossOver() {
			final int length = this.population_.size();
			for( int i = 0; i < length; ++i ) {
				Cell< T > new1 = new Cell< T >( this.population_.get( i ) );
				Cell< T > new2 = new Cell< T >( this.population_.get( selectParent() ) );
				for( Entry< T, Long > e : this.table_.entrySet() ) {
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
				Cell< T > cell = this.population_.get( i );
				for( Entry< T, Long > e : table_.entrySet() ) {
					if( cell.canToggle( e.getKey(), e.getValue(), this.limit_) && Math.random() * this.table_.size() < 1.0 ) {
						cell.toggle( e.getKey(), e.getValue() );
					}
				}
			}
		}
		
	}
	
	private static class Recursion< T > {
		
		private Vector< T > keys_;
		private Vector< Long > values_;
		private Long limit_;
		
		public Recursion( Long limit, Hashtable< T, Long > items ) {
			Vector< Entry< T, Long > > tmp = new Vector< Entry< T, Long > >( items.entrySet() );
			Collections.sort( tmp, new Comparator< Entry< T, Long > >() {
				@Override
				public int compare( Entry<T, Long> r, Entry<T, Long> l ) {
					return l.getValue().compareTo( r.getValue() );
				}
			} );
			
			this.keys_ = new Vector< T >();
			this.values_ = new Vector< Long >();
			for( Entry< T, Long > e : tmp ) {
				this.keys_.add( e.getKey() );
				this.values_.add( e.getValue() );
			}
			
			this.limit_ = limit;
		}
		
		public Pack< T > perform( int n, Pack< T > p ) {
			if( p.score_ > this.limit_) {
				return new Pack< T >();
			} else if( n == this.keys_.size() ) {
				return p;
			} else {
				Pack< T > a = this.perform( n + 1, p );
				Pack< T > b = this.perform( n + 1, p.add( this.keys_.get( n ) , this.values_.get( n ) ) );
				if( a.score_ > b.score_ ) {
					return a;
				} else {
					return b;
				}
			}
		}
		
	}
	
	/**
	 * Back-end to pick using BFS.
	 * 
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	/*
	@Deprecated
	public static< T > Pack< T > pickSmall2( Long limit, Hashtable< T, Long > items ) {
		Vector< Pack< T > > table = new Vector< Pack< T > >();
		table.add( new Pack< T >() );
		
		for( Entry< T, Long> e : items.entrySet() ) {
			Vector< Pack< T > > tmp = new Vector< Pack< T > >();
			for( Pack< T > p : table ) {
				Long newSize = p.getScore() + e.getValue();
				if( newSize <= limit ) {
					Vector< T > newDirs = new Vector< T >( p.getItems() );
					newDirs.add( e.getKey() );
					tmp.add( new Pack< T >( newSize, newDirs ) );
				}
			}
			table.addAll(table.size(), tmp);
		}
		
		Pack< T > max = new Pack< T >();
		for( Pack< T > p : table ) {
			if( p.getScore() >= max.getScore() ) {
				max = p;
			}
		}
		return max;
	}
	*/

}
