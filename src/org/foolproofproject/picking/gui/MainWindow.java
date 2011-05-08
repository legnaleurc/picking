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
package org.foolproofproject.picking.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.foolproofproject.Pack;
import org.foolproofproject.picking.CleanUpUtility;
import org.foolproofproject.picking.K3BUtility;
import org.foolproofproject.picking.UnitUtility;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QThreadPool;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView.SelectionMode;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileSystemModel;
import com.trolltech.qt.gui.QHeaderView;
import com.trolltech.qt.gui.QItemSelectionModel;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QProgressBar;
import com.trolltech.qt.gui.QTreeView;
import com.trolltech.qt.gui.QTreeWidgetItem;

public class MainWindow extends QMainWindow {

	private Ui_MainWindow ui_;
	private QFileSystemModel treeModel_;
	private QFileSystemModel listModel_;
	private QProgressBar progress_;
	private QMenu contextMenu_;
	private QTreeWidgetItem currentItem_;

	public MainWindow() {
		super();
		this.ui_ = new Ui_MainWindow();
		this.ui_.setupUi( this );

		this.treeModel_ = new QFileSystemModel( this );
		this.ui_.treeView.setModel( this.treeModel_ );
		this.treeModel_.setRootPath( QDir.rootPath() );
		QDir.Filters filter = this.treeModel_.filter();
		filter.clear( QDir.Filter.Files );
		this.treeModel_.setFilter( filter );

		this.ui_.treeView.setSelectionMode( SelectionMode.SingleSelection );
		QItemSelectionModel selection = this.ui_.treeView.selectionModel();
		selection.currentChanged.connect( this, "onTreeSelectionChanged_( QModelIndex )" );

		this.listModel_ = new QFileSystemModel( this );
		this.ui_.listView.setModel( this.listModel_ );
		this.listModel_.setRootPath( QDir.rootPath() );
		this.ui_.listView.setSelectionMode( SelectionMode.MultiSelection );
		this.ui_.listView.doubleClicked.connect( this, "onListDoubleClicked_( QModelIndex )" );

		this.ui_.treeView.header().setSectionHidden( 1, true );
		this.ui_.treeView.header().setSectionHidden( 2, true );
		this.ui_.treeView.header().setSectionHidden( 3, true );
		this.focusTreeItem_( this.treeModel_.index( QDir.homePath() ) );

		this.ui_.start.clicked.connect( this, "onStartPressed_()" );

		this.progress_ = new QProgressBar( this );
		this.ui_.statusbar.insertPermanentWidget( 0, this.progress_ );
		this.progress_.hide();

		this.ui_.treeView.setTextElideMode( Qt.TextElideMode.ElideNone );
		this.ui_.listView.setTextElideMode( Qt.TextElideMode.ElideNone );
		this.ui_.pack.setTextElideMode( Qt.TextElideMode.ElideNone );
		this.ui_.overflow.setTextElideMode( Qt.TextElideMode.ElideNone );

		this.ui_.pack.expanded.connect( this, "onTreeSizeChanged_( QModelIndex )" );
		this.ui_.pack.collapsed.connect( this, "onTreeSizeChanged_( QModelIndex )" );
		this.ui_.pack.header().setResizeMode( QHeaderView.ResizeMode.Interactive );

		this.ui_.treeView.expanded.connect( this, "onTreeSizeChanged_( QModelIndex )" );
		this.ui_.treeView.collapsed.connect( this, "onTreeSizeChanged_( QModelIndex )" );
		this.ui_.treeView.header().setResizeMode( QHeaderView.ResizeMode.Interactive );
		this.treeModel_.directoryLoaded.connect( this, "onDirectoryLoaded_( String )" );

		this.ui_.viewHidden.toggled.connect( this, "onViewHiddenToggled_( Boolean )" );

		this.ui_.action_About_Qt.triggered.connect( QApplication.instance(), "aboutQt()" );
		this.ui_.action_About_Qt_Jambi.triggered.connect( QApplication.instance(), "aboutQtJambi()" );
		this.ui_.action_Export_As_K3B_Project_File.triggered.connect( this, "exportAll_()" );

		this.ui_.pack.setContextMenuPolicy( Qt.ContextMenuPolicy.CustomContextMenu );
		this.ui_.pack.customContextMenuRequested.connect( this, "onContextMenuRequested_( QPoint )" );
		this.contextMenu_ = new QMenu( this );
		QAction action = new QAction( "Export to K3B", this );
		this.contextMenu_.addAction( action );
		action.triggered.connect( this, "onK3BAction_()" );
	}

	private void expandTreeItem_( QModelIndex index ) {
		if( index == null ) {
			return;
		}
		this.expandTreeItem_( index.parent() );
		this.ui_.treeView.expand( index );
	}

	@SuppressWarnings("unused")
	private void exportAll_() {
		int nPack = this.ui_.pack.topLevelItemCount();
		if( nPack <= 0 ) {
			return;
		}
		String template = String.format( "%%0%dd", String.valueOf( nPack ).length() );
		File path = new File( QFileDialog.getExistingDirectory( this, this.tr( "Choose Output Directory" ), QDir.homePath() ) );
		for( int i = 0; i < nPack; ++i ) {
			String baseName = String.format( template, i );
			QTreeWidgetItem root = this.ui_.pack.topLevelItem( i );
			try {
				K3BUtility.export( new File( path, baseName + ".k3b" ), root );
				CleanUpUtility.export( new File( path, baseName + ".sh" ), root );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void focusTreeItem_( QModelIndex index ) {
		this.expandTreeItem_( index );
		this.ui_.treeView.setCurrentIndex( index );
	}

	@SuppressWarnings("unused")
	private void onContextMenuRequested_( QPoint pos ) {
		QTreeWidgetItem item = this.ui_.pack.itemAt( pos );
		if( item != null ) {
			if( item.parent() != null ) {
				item = item.parent();
			}
			this.currentItem_ = item;
			this.contextMenu_.exec( this.ui_.pack.mapToGlobal( pos ) );
		}
	}

	@SuppressWarnings("unused")
	private void onDirectoryLoaded_( String path ) {
		this.resizeTreeHeader( this.ui_.treeView );
	}

	@SuppressWarnings("unused")
	private void onK3BAction_() {
		String filePath = QFileDialog.getSaveFileName( this, this.tr( "Choose File Path" ), QDir.homePath(), new QFileDialog.Filter( this.tr( "K3B Projects(*.k3b)" ) ) );
		try {
			K3BUtility.export( new File( filePath ), this.currentItem_ );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void onListDoubleClicked_( QModelIndex index ) {
		String filePath = this.listModel_.filePath( index );
		QModelIndex target = this.treeModel_.index( filePath );
		this.focusTreeItem_( target );
	}

	@SuppressWarnings("unused")
	private void onOverflowDetected_( File item ) {
		this.ui_.overflow.addItem( item.getName() );
	}

	@SuppressWarnings("unused")
	private void onPacked_( Pack< File > pack ) {
		QTreeWidgetItem root = new QTreeWidgetItem( this.ui_.pack );
		root.setText( 0, UnitUtility.toString( pack.getScore(), this.ui_.comboBox.currentIndex() ) );
		for( File file : pack.getItems() ) {
			QTreeWidgetItem child = new QTreeWidgetItem( root );
			child.setText( 0, file.getName() );
			child.setData( 0, Qt.ItemDataRole.UserRole, file );
			root.addChild( child );
		}
		this.ui_.pack.addTopLevelItem( root );
	}

	@SuppressWarnings("unused")
	private void onPackFinished_() {
		this.progress_.hide();
		this.ui_.pack.expandAll();
		this.resizeTreeHeader( this.ui_.pack );
	}

	@SuppressWarnings("unused")
	private void onProgressChanged_( Integer current, Integer maximum ) {
		this.progress_.setMaximum( maximum );
		this.progress_.setValue( current );
	}

	@SuppressWarnings("unused")
	private void onStartPressed_() {
		this.ui_.pack.clear();
		this.ui_.overflow.clear();

		Long limit = UnitUtility.extract( this.ui_.spinBox.value(), this.ui_.comboBox.currentIndex() );

		ArrayList< String > filePaths = new ArrayList< String >();
		for( QModelIndex index : this.ui_.listView.selectionModel().selectedRows( 0 ) ) {
			filePaths.add( this.listModel_.filePath( index ) );
		}
		this.progress_.show();

		PackingRunner runner = new PackingRunner( limit, filePaths );
		runner.overflowDetected.connect( this, "onOverflowDetected_( File )" );
		runner.packed.connect( this, "onPacked_( Pack )" );
		runner.progressChanged.connect( this, "onProgressChanged_( Integer, Integer )" );
		runner.finished.connect( this, "onPackFinished_()" );
		QThreadPool.globalInstance().start( runner );
	}

	@SuppressWarnings("unused")
	private void onTreeSelectionChanged_( QModelIndex index ) {
		String filePath = this.treeModel_.filePath( index );
		QModelIndex target = this.listModel_.index( filePath );
		this.ui_.listView.setRootIndex( target );
	}

	@SuppressWarnings("unused")
	private void onTreeSizeChanged_( QModelIndex index ) {
		this.resizeTreeHeader( (QTreeView) QSignalEmitter.signalSender() );
	}

	@SuppressWarnings("unused")
	private void onViewHiddenToggled_( Boolean viewHidden ) {
		QDir.Filters treeFilter = this.treeModel_.filter();
		QDir.Filters listFilter = this.listModel_.filter();
		if( viewHidden ) {
			treeFilter.set( QDir.Filter.Hidden );
			listFilter.set( QDir.Filter.Hidden );
		} else {
			treeFilter.clear( QDir.Filter.Hidden );
			listFilter.clear( QDir.Filter.Hidden );
		}
		this.treeModel_.setFilter( treeFilter );
		this.listModel_.setFilter( listFilter );
	}

	private void resizeTreeHeader( QTreeView tree ) {
		tree.header().resizeSection( 0, tree.sizeHintForColumn( 0 ) );
	}

}
