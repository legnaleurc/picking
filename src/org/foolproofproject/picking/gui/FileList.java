/**
 * @file FileList.java
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
package org.foolproofproject.picking.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @brief File list widget.
 */
public class FileList extends JPanel {

	private static final long serialVersionUID = -5296371739711677521L;
	private JList view_;
	private JLabel items_;
	private Observable mouseDoubleClicked_;

	public FileList() {
		this.mouseDoubleClicked_ = new Observable() {
			@Override
			public void notifyObservers( Object arg ) {
				this.setChanged();
				super.notifyObservers( arg );
			}
		};

		this.view_ = new JList();
		JScrollPane scroll = new JScrollPane( this.view_ );
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		this.setBorder( BorderFactory.createTitledBorder( "File List" ) );
		this.add( scroll );

		this.view_.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				if( e.getClickCount() == 2 ) {
					int index = FileList.this.view_.locationToIndex( e.getPoint() );
					if( index >= 0 ) {
						File file = ( File )FileList.this.view_.getModel().getElementAt( index );
						System.err.println( "java sucks!!" );
						FileList.this.mouseDoubleClicked_.notifyObservers( file );
					}
				}
			}
		} );

		this.items_ = new JLabel( "0" );
		this.view_.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				FileList.this.items_.setText( String.valueOf( FileList.this.view_.getSelectedIndices().length ) );
			}
		} );
		this.add( this.items_ );
	}

	public File[] getSelectedFiles() {
		Object[] selection = this.view_.getSelectedValues();
		if( selection == null ) {
			return null;
		}

		File[] tmp = new File[selection.length];
		for( int i = 0; i < tmp.length; ++i ) {
			tmp[i] = (File)selection[i];
		}
		return tmp;
	}

	public Observable onMouseDoubleClicked() {
		return this.mouseDoubleClicked_;
	}

	public void setItems( File[] files ) {
		if( files != null ) {
			this.view_.setListData( files );
		} else {
			this.view_.removeAll();
		}
	}

}
