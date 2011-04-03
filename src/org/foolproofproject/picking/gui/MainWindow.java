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
package org.foolproofproject.picking.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.foolproofproject.Pack;
import org.foolproofproject.picking.Performer;
import org.foolproofproject.picking.Signal;
import org.foolproofproject.picking.UnitUtility;

/**
 * Main window.
 *
 * @author Wei-Cheng Pan
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 6869079478547863579L;
	private FileList list_;
	private NaturalField limit_;
	private ResultWidget result_;
	private JComboBox unit_;
	private JDialog about_;
	private Preference preference_;
	private DirectoryTree tree_;
	private JCheckBox hidden_;

	public MainWindow( String title ) {
		super( title );

		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setSize( 800, 600 );
		this.setLocationRelativeTo( null );

		Container pane = this.getContentPane();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ) );

		this.initViewPort_();
		this.initControlPanel_();
		this.initPreference_();
		this.initAbout_();

		this.initMenuBar_();

		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing( WindowEvent e ) {
				try {
					Configuration.sync();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog( e.getComponent(), e1.getMessage(), "Error on saving configuration!", JOptionPane.ERROR_MESSAGE );
				}
			}
		} );
	}

	private void initAbout_() {
		this.about_ = new JDialog( this );
		this.about_.setTitle( "About PacKing" );
		this.about_.setSize( 320, 240 );
		this.about_.setLocationRelativeTo( this );

		Container pane = this.about_.getContentPane();
		pane.setLayout( new BoxLayout( pane, BoxLayout.Y_AXIS ) );

		JPanel center = new JPanel();
		pane.add( center );
		center.setLayout( new BoxLayout( center, BoxLayout.Y_AXIS ) );
		center.setMaximumSize( new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE ) );

		center.add( new JLabel( "Version: 0.2.3" ) );
		center.add( new JLabel( "Author: legnaleurc (FoolproofProject)" ) );
		center.add( new JLabel( "License: LGPLv3 or later" ) );
		center.add( new JLabel( "e-mail: legnaleurc@gmail.com" ) );
		center.add( new JLabel( "blog: http://legnaleurc.blogspot.com/" ) );

		JPanel bottom = new JPanel();
		pane.add( bottom );
		bottom.setLayout( new GridLayout( 1, 1 ) );

		JButton ok = new JButton( "OK" );
		bottom.add( ok );
		ok.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				MainWindow.this.about_.setVisible( false );
			}
		} );
	}

	private void initControlPanel_() {
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 1, 3 ) );

		JPanel limitPanel = new JPanel();
		panel.add( limitPanel );
		limitPanel.setLayout( new GridLayout( 1, 2 ) );
		limitPanel.setBorder( BorderFactory.createTitledBorder( "Limit" ) );

		this.limit_ = new NaturalField();
		limitPanel.add( this.limit_ );

		this.unit_ = UnitUtility.createComboBox();
		limitPanel.add( this.unit_ );

		JPanel viewPanel = new JPanel();
		panel.add( viewPanel );
		viewPanel.setLayout( new GridLayout( 1, 1 ) );
		viewPanel.setBorder( BorderFactory.createTitledBorder( "View" ) );

		this.hidden_ = new JCheckBox( "Hidden" );
		viewPanel.add( this.hidden_ );
		this.hidden_.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.tree_.setHiddenVisible( MainWindow.this.hidden_.isSelected() );
				MainWindow.this.tree_.refresh();
			}
		} );

		JButton start = new JButton( "Start" );
		panel.add( start );
		start.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				new Thread( new Runnable() {
					@Override
					public void run() {
						MainWindow.this.perform();
					}
				} ).start();
			}
		} );

		Container pane = this.getContentPane();
		pane.add( panel );

		this.read();
	}

	private void initMenuBar_() {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar( menuBar );

		JMenu file = new JMenu( "File" );
		menuBar.add( file );
		file.setMnemonic( KeyEvent.VK_F );

		JMenuItem save = new JMenuItem( "Save Result" );
		file.add( save );
		save.setMnemonic( KeyEvent.VK_S );
		save.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK ) );
		save.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.save();
			}
		} );

		JMenuItem export = new JMenuItem( "Export to K3B" );
		file.add( export );
		export.setMnemonic( KeyEvent.VK_E );
		export.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK ) );
		export.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = FileDialog.getExistingDirectory( (Component)e.getSource() );
				if( file != null ) {
					if( !file.exists() ) {
						JOptionPane.showMessageDialog( MainWindow.this, "`" + file.getAbsolutePath() + "\' dose not exists.", "Open Error", JOptionPane.ERROR_MESSAGE );
						return;
					}
					int succeed = MainWindow.this.result_.exportK3BProjectsTo( file );
					JOptionPane.showMessageDialog( MainWindow.this, "Exported " + succeed + " K3B project(s).", "Done", JOptionPane.INFORMATION_MESSAGE );
				}
			}
		} );

		JMenu edit = new JMenu( "Edit" );
		menuBar.add( edit );
		edit.setMnemonic( KeyEvent.VK_E );

		JMenuItem refresh = new JMenuItem( "Refresh" );
		edit.add( refresh );
		refresh.setMnemonic( KeyEvent.VK_R );
		refresh.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ) );
		refresh.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.tree_.refresh();
			}
		} );

		JMenuItem preferences = new JMenuItem( "Preferences" );
		edit.add( preferences );
		preferences.setMnemonic( KeyEvent.VK_P );
		preferences.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.preference_.exec( MainWindow.this.limit_.toLong(), MainWindow.this.unit_.getSelectedIndex(), MainWindow.this.hidden_.isSelected() );
			}
		} );

		JMenu help = new JMenu( "Help" );
		menuBar.add( help );
		help.setMnemonic( KeyEvent.VK_H );

		JMenuItem debugLog = new JMenuItem( "Debug Log" );
		help.add( debugLog );
		debugLog.setMnemonic( KeyEvent.VK_D );
		debugLog.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogDialog.getDebugLog().setVisible( true );
			}
		} );

		JMenuItem errorLog = new JMenuItem( "Error Log" );
		help.add( errorLog );
		errorLog.setMnemonic( KeyEvent.VK_E );
		errorLog.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogDialog.getErrorLog().setVisible( true );
			}
		} );

		JMenuItem about = new JMenuItem( "About ..." );
		help.add( about );
		about.setMnemonic( KeyEvent.VK_A );
		about.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.about_.setVisible( true );
			}
		} );
	}

	private void initPreference_() {
		this.preference_ = new Preference( this );
	}

	private void initViewPort_() {
		JPanel central = new JPanel();
		central.setLayout( new GridLayout( 1, 3 ) );
		central.setMaximumSize( new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE ) );

		this.tree_ = new DirectoryTree();
		central.add( this.tree_ );

		this.list_ = new FileList();
		central.add( this.list_ );

		this.tree_.onSelectionChanged().connect( new Signal.Slot() {
			@Override
			public void call( Object sender, Object ... args ) {
				MainWindow.this.list_.setItems( (File[])args[0] );
			}
		} );
		this.list_.onMouseDoubleClicked().connect( new Signal.Slot() {
			@Override
			public void call( Object sender, Object ... args ) {
				MainWindow.this.tree_.open( (File)args[0] );
			}
		} );

		this.result_ = new ResultWidget();
		central.add( this.result_ );

		Container pane = this.getContentPane();
		pane.add( central );

		this.tree_.open( new File( System.getProperty( "user.home" ) ) );
	}

	public void perform() {
		Performer p = new Performer( UnitUtility.extract( this.limit_.toLong(), this.unit_.getSelectedIndex() ), this.list_.getSelectedFiles() );

		this.result_.openProgress( p.getTable() );

		if( !p.noOverflow() ) {
			this.result_.addOverflow( p.getOverflow() );
		}

		while( !p.noItem() ) {
			Pack< File > r = p.call();
			this.result_.addResult( r.getScore(), this.unit_.getSelectedIndex(), r.getItems() );
			p.remove( r.getItems() );
		}

		this.result_.closeProgress();
	}

	public void read() {
		this.limit_.setLong( (Long) Configuration.get( "limit" ) );
		this.unit_.setSelectedIndex( (Integer) Configuration.get( "unit" ) );
		this.hidden_.setSelected( (Boolean) Configuration.get( "hidden" ) );
	}

	public void save() {
		File file = FileDialog.getSaveFileName( this, new FileNameExtensionFilter( "Plain Text", "txt" ));
		if( file != null ) {
			try {
				PrintStream fout = new PrintStream( file, "UTF-8" );
				this.result_.save( fout );
				fout.close();
			} catch (FileNotFoundException e) {
				LogDialog.getErrorLog().log( e.getMessage() );
			} catch (UnsupportedEncodingException e) {
				LogDialog.getErrorLog().log( e.getMessage() );
			}
		}
	}

}
