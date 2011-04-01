/**
 * @file NaturalField.java
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

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @brief Text field which only accept natural number.
 */
public class NaturalField extends JTextField {

	private static final long serialVersionUID = -4923755274608244338L;

	public NaturalField() {
		super( "4483" );
	}

	public NaturalField( long number ) {
		super( String.valueOf( number ) );
	}

	@Override
	protected Document createDefaultModel() {
		return new PlainDocument() {
			private static final long serialVersionUID = -3237520404172699543L;
			@Override
			public void insertString( int offs, String str, AttributeSet a ) throws BadLocationException {
				if( str == null ) {
					return;
				}
				for( char c : str.toCharArray() ) {
					if( !Character.isDigit( c ) ) {
						return;
					}
				}
				super.insertString(offs, str, a);
			}
		};
	}

	public void setLong( long number ) {
		this.setText( String.valueOf( number ) );
	}

	public long toLong() {
		try {
			return Long.parseLong( super.getText() );
		} catch (NumberFormatException e) {
			LogDialog.getErrorLog().log( e.getMessage() );
			return 0L;
		}
	}

}
