/**
 * @file DirectoryTree.java
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

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.foolproofproject.picking.Signal;

/**
 * Directory tree widget.
 */
public class DirectoryTree extends JPanel {

	private class CustomFilter implements FileFilter {
		private boolean directoryOnly_;
		public CustomFilter( boolean directoryOnly ) {
			this.directoryOnly_ = directoryOnly;
		}
		@Override
		public boolean accept(File file) {
			boolean a = this.directoryOnly_ ? file.isDirectory() : true;
			boolean b = DirectoryTree.this.viewHidden_ ? true : !file.isHidden();
			return a && b;
		}
	}
	private class DirectoryTreeModel implements TreeModel {

		private File root_;

		public DirectoryTreeModel( File root ) {
			this.root_ = root;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public File getChild(Object parent, int index) {
			File[] children = ( ( File )parent ).listFiles( new CustomFilter( true ) );
			if( children == null || ( index >= children.length ) ) {
				return null;
			}
			return children[index];
		}

		@Override
		public int getChildCount(Object parent) {
			File[] children = ( ( File )parent ).listFiles( new CustomFilter( true ) );
			if( children == null ) {
				return 0;
			}
			return children.length;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			File[] children = ( ( File )parent ).listFiles( new CustomFilter( true ) );
			if( children == null ) {
				return -1;
			}
			File target = ( File )child;
			for( int i = 0; i < children.length; ++i ) {
				if( target.equals( children[i] ) ) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public File getRoot() {
			return this.root_;
		}

		@Override
		public boolean isLeaf(Object node) {
			return ( ( File )node ).listFiles( new CustomFilter( true ) ) == null;
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			// TODO Auto-generated method stub

		}

	}
	private static final long serialVersionUID = -8724999594568776949L;
	private JTabbedPane tabWidget_;
	private boolean viewHidden_;
	private Signal selectionChanged_;

	public DirectoryTree() {
		this.selectionChanged_ = new Signal( this );

		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		this.setBorder( BorderFactory.createTitledBorder( "Directory Tree" ) );
		this.viewHidden_ = (Boolean) Configuration.get( "hidden" );

		this.tabWidget_ = new JTabbedPane();
		this.add( this.tabWidget_ );
		this.createTabs_();
		this.tabWidget_.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				DirectoryTree.this.dispatch_( DirectoryTree.this.getCurrentTree_() );
			}
		} );
	}

	private JScrollPane createRootTab_( File root ) {
		JTree view = new JTree( new DirectoryTreeModel( root ) );
		view.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		view.addTreeSelectionListener( new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DirectoryTree.this.dispatch_( ( JTree )e.getSource() );
			}
		} );
		view.setCellRenderer( new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = -8226412998997225459L;
			@Override
			public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
				super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
				this.setText( ( ( File )value ).getName() );
				return this;
			}
		} );
		return new JScrollPane( view );
	}

	private void createTabs_() {
		for( File root : File.listRoots() ) {
			this.tabWidget_.addTab( root.getPath(), this.createRootTab_( root ) );
		}
	}

	private void dispatch_( JTree tree ) {
		if( tree != null ) {
			TreePath selection = tree.getSelectionPath();
			if( selection != null ) {
				File file = ( File )selection.getLastPathComponent();
				File[] items = file.listFiles( new CustomFilter( false ) );
				Arrays.sort( items );
				this.selectionChanged_.emit( (Object)items );
			}
		}
	}

	private JTree getCurrentTree_() {
		JScrollPane current = ( JScrollPane )this.tabWidget_.getSelectedComponent();
		if( current == null ) {
			return null;
		}
		JTree tree = ( JTree )current.getViewport().getView();
		return tree;
	}

	public Signal onSelectionChanged() {
		return this.selectionChanged_;
	}

	public void open( File file ) {
		if( file.isDirectory() ) {
			File root = file;
			ArrayList< File > list = new ArrayList< File >();
			while( root.getParentFile() != null ) {
				list.add( 0, root );
				root = root.getParentFile();
			}
			list.add( 0, root );
			File[] roots = File.listRoots();
			for( int i = 0; i < roots.length; ++i ) {
				if( roots[i].equals( root ) ) {
					this.tabWidget_.setSelectedIndex( i );
					break;
				}
			}

			TreePath path = new TreePath( list.toArray() );
			this.getCurrentTree_().setSelectionPath( path );
		}
	}

	public void refresh() {
		// dump state
		HashMap< File, TreePath > sel = new HashMap< File, TreePath >();
		File curRoot = null;
		for( int i = 0; i < this.tabWidget_.getTabCount(); ++i ) {
			JScrollPane tab = ( JScrollPane )this.tabWidget_.getComponentAt( i );
			JTree tree = ( JTree )tab.getViewport().getView();
			File root = ( File )tree.getModel().getRoot();
			TreePath path = tree.getSelectionPath();
			if( path != null ) {
				sel.put( root, path );
			}

			if( this.tabWidget_.getSelectedIndex() == i ) {
				curRoot = root;
			}
		}

		// clear tabs
		this.tabWidget_.removeAll();
		this.createTabs_();

		// restore state
		for( int i = 0; i < this.tabWidget_.getTabCount(); ++i ) {
			JScrollPane tab = ( JScrollPane )this.tabWidget_.getComponentAt( i );
			JTree tree = ( JTree )tab.getViewport().getView();
			File root = ( File )tree.getModel().getRoot();
			TreePath path = sel.get( root );
			if( path != null ) {
				tree.setSelectionPath( path );
			}
			if( root.equals( curRoot ) ) {
				this.tabWidget_.setSelectedIndex( i );
				this.dispatch_( tree );
			}
		}
	}

	public void setHiddenVisible( boolean visible ) {
		this.viewHidden_ = visible;
	}

}
