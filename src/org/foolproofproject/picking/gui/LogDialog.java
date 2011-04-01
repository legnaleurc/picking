/**
 * @file LogDialog.java
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

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @brief Log dialog.
 */
public class LogDialog extends JDialog {

	private static final long serialVersionUID = -8893137565616271012L;
	private static LogDialog error_ = new LogDialog( "Error Log" );
	private static LogDialog debug_ = new LogDialog( "Debug Log" );
	/**
	 * @brief Get debug log widget.
	 */
	public static LogDialog getDebugLog() {
		return LogDialog.debug_;
	}

	/**
	 * @brief Get error log widget.
	 */
	public static LogDialog getErrorLog() {
		return LogDialog.error_;
	}

	private JTextArea textArea_;

	private LogDialog( String title ) {
		super( ( MainWindow )null );
		this.setSize( 320, 240 );
		this.setTitle( title );
		this.setLocationRelativeTo( null );

		Container pane = this.getContentPane();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ) );

		JPanel main = new JPanel();
		pane.add( main );
		main.setLayout( new BoxLayout( main, BoxLayout.Y_AXIS ) );

		this.textArea_ = new JTextArea();
		this.textArea_.setEditable( false );

		JScrollPane scroll = new JScrollPane( this.textArea_ );
		main.add( scroll );
	}

	/**
	 * @brief Write a log message.
	 * @param msg log message
	 */
	public void log( String msg ) {
		synchronized( this.textArea_ ) {
			this.textArea_.append( msg + "\n" );
		}
	}

}
