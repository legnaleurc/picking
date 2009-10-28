/**
 * @file Pack.java
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
package org.foolproofproject;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * @brief Storing picking result.
 * 
 * This class calculates the maximum combination of given objects table.<br/>
 * To do this you should collect objects and their values, and store them to a
 * <strong>Hashtable&lt;Object,Long&gt;</strong> as a table. Then decide a
 * maximum value of combinations of objects.<br/>
 * Simply use pick() to perform algorithm.<br/>
 * If the amount of items is less then 16, then it will use brute force to
 * find an optimal solution. Or it will use heuristic algorithm to do it.<br/>
 */
public class Pack {

	private long size;
	private Vector< Object > items;
	
	/**
	 * @brief Default constructor.
	 * 
	 * Will initialize size to 0, items as an empty Vector.
	 */
	private Pack() {
		size = 0L;
		items = new Vector< Object >();
	}
	/**
	 * @brief Constructor.
	 * @param size total value of @p items
	 * @param items selected objects
	 */
	private Pack( long size, Vector< Object > items ) {
		this.size = size;
		this.items = items;
	}
	public String toString() {
		return "("+size+":"+items+")";
	}
	public long getSize() {
		return size;
	}
	public Vector< Object > getItems() {
		return items;
	}
	
	/**
	 * @brief Genetic algorithm.
	 */
	private static class GeneticAlgorithm {
		
		/**
		 * @brief One cell
		 */
		private class Cell implements Comparable< Cell > {
			
			private Hashtable< Object, Boolean > table;
			private Long size;
			
			public Cell( Hashtable< Object, Boolean > table, Long size ) {
				this.table = table;
				this.size = size;
			}
			public Cell( Cell cell ) {
				this.table = new Hashtable< Object, Boolean >( cell.table );
				this.size = new Long( cell.size );
			}
			public boolean canToggle( Object key, Long value, Long limit ) {
				return ( table.get( key ) || size + value < limit );
			}
			public void toggle( Object key, Long value ) {
				boolean tmp = table.get( key );
				table.put( key, !tmp );
				if( tmp ) {
					size -= value;
				} else {
					size += value;
				}
			}
			public Hashtable< Object, Boolean > getTable() {
				return table;
			}
			public Long getSize() {
				return size;
			}
			public String toString() {
				return String.format( "(%d,%s)", size, table.keySet() );
			}
			@Override
			public int compareTo(Cell rhs) {
				return rhs.size.compareTo( size );
			}
		}
		
		private Long limit;
		private Hashtable< Object, Long > items;
		private Vector< Cell > population;
		
		public GeneticAlgorithm( Long limit, Hashtable< Object, Long > items ) {
			this.limit = limit;
			this.items = items;
			
			population = new Vector< Cell >();
			for( int i = 0; i < items.size(); ++i ) {
				population.add( generatePopulation() );
			}
			Collections.sort( population );
		}
		
		public Pack perform() {
			while( !canStop() ) {
				crossOver();
				mutation();
				Collections.sort( population );
				population.subList( items.size(), population.size() ).clear();
			}
			
			Cell survivor = population.get( 0 );
			Pack result = new Pack( survivor.getSize(), new Vector< Object >() );
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
			for( Entry< Object, Long > e : items.entrySet() ) {
				if( e.getValue() + sum >= limit || Math.random() * 2 < 1.0 ) {
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
		 * 
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
		 */
		private int selectParent() {
			int n = items.size();
			int b = 2 * n - 1;
			double c = Math.random();
			double d = ( b * b - 1 ) * ( 1 - c ) + 1;
			double k = ( b - Math.sqrt( d ) ) / 2;
			return ( int )Math.floor( k );
		}
		
		private Boolean canStop() {
			Cell head = population.firstElement();
			Cell tail = population.lastElement();
			return head.getSize().equals( tail.getSize() );
		}
		
		private void crossOver() {
			final int length = population.size();
			for( int i = 0; i < length; ++i ) {
				Cell new1 = new Cell( population.get( i ) );
				Cell new2 = new Cell( population.get( selectParent() ) );
				for( Entry< Object, Long > e : items.entrySet() ) {
					if( new1.getTable().get( e.getKey() ) == new2.getTable().get( e.getKey() ) ) {
						continue;
					}
					if( !new1.canToggle( e.getKey(), e.getValue(), limit ) || !new2.canToggle( e.getKey(), e.getValue(), limit ) ) {
						continue;
					}
					if( Math.random() < 0.5 ) {
						new1.toggle( e.getKey(), e.getValue() );
						new2.toggle( e.getKey(), e.getValue() );
					}
				}
				population.add( new1 );
				population.add( new2 );
			}
		}
		
		private void mutation() {
			final int length = population.size();
			for( int i = 0; i < length; ++i ) {
				Cell cell = population.get( i );
				for( Entry< Object, Long > e : items.entrySet() ) {
					if( cell.canToggle( e.getKey(), e.getValue(), limit) && Math.random() * items.size() < 1.0 ) {
						cell.toggle( e.getKey(), e.getValue() );
					}
				}
			}
		}
		
	}
	
	/**
	 * @brief Main pick function.
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 * 
	 * If table size is greater then or equal to 16, it will use heuristic
	 * algorithm.
	 */
	public static Pack pick( Long limit, Hashtable< Object, Long > items ) {
		if( items.size() < 16 ) {
			return pickSmall( limit, items );
		} else {
			return pickLarge( limit, items );
		}
	}
	
	/**
	 * @brief Back-end to pick using brute force.
	 * @param limit maximum value of combinations
	 * @param items object value table
	 * @return solution
	 */
	public static Pack pickLarge( Long limit, Hashtable< Object, Long > items ) {
		GeneticAlgorithm ga = new GeneticAlgorithm( limit, items );
		return ga.perform();
	}
	
	/**
	 * @brief Back-end to pick using heuristic algorithm.
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
				Long newSize = p.getSize() + e.getValue();
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
			if( p.getSize() >= max.getSize() ) {
				max = p;
			}
		}
		return max;
	}

}
