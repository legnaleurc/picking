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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.stream.XMLStreamException;
import org.foolproofproject.picking.K3BUtility;
import org.foolproofproject.picking.SmartFile;
import org.foolproofproject.picking.UnitUtility;

/**
 * Result widget.
 * 
 * @author Wei-Cheng Pan
 */
class ResultWidget extends JPanel {

	private static final long serialVersionUID = 3366458847085663811L; 
	private JTree resultTree_;
	private Hashtable< SmartFile, Long > table_;
	private JPopupMenu popup_;
	private DefaultMutableTreeNode selectedNode_;
	private JProgressBar progressBar_;
	private JList overflowList_;
	
	private class LabelNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = -3736698920372921805L;
		private int pow_;
		public LabelNode( Long size, int pow ) {
			super( size );
			this.pow_ = pow;
		}
		@Override
		public String toString() {
			return UnitUtility.toString( (Long)this.getUserObject(), this.pow_ );
		}
	}
	
	public ResultWidget() {
		// setup layout
		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		this.setBorder( BorderFactory.createTitledBorder( "Result" ) );
		
		// setup tabs
		JTabbedPane tabs = new JTabbedPane();
		this.add( tabs );
		
		// setup result tree
		this.resultTree_ = new JTree();
		this.resultTree_.setRootVisible( false );
		JScrollPane scroll = new JScrollPane( this.resultTree_ );
		tabs.addTab( "Result", scroll );
		
		// setup overflow list
		this.overflowList_ = new JList( new DefaultListModel() );
		scroll = new JScrollPane( this.overflowList_ );
		tabs.addTab( "Overflow", scroll );
		
		this.progressBar_ = new JProgressBar();
		this.add( this.progressBar_ );
		this.progressBar_.setVisible( false );
		
		this.selectedNode_ = null;
		
		// setup pop up context menu
		this.popup_ = new JPopupMenu();
		JMenuItem k3b = new JMenuItem( "Export to K3B project file" );
		k3b.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				if( ResultWidget.this.selectedNode_ == null ) {
					return;
				}
				File file = FileDialog.getSaveFileName( ResultWidget.this.resultTree_, new FileNameExtensionFilter( "K3B project", "k3b" ) );
				if( file != null ) {
					try {
						K3BUtility.export( file, ResultWidget.this.selectedNode_ );
					} catch (IOException e1) {
						LogDialog.getErrorLog().log( e1.getMessage() );
					} catch (XMLStreamException e1) {
						LogDialog.getErrorLog().log( e1.getMessage() );
					}
				}
			}
		} );
		this.popup_.add( k3b );
		
		// setup tool tip
		ToolTipManager.sharedInstance().registerComponent( this.resultTree_ );
		this.resultTree_.setCellRenderer( new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 8169622028702532699L;
			@Override
			public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
				DefaultTreeCellRenderer self = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );
				if( ResultWidget.this.table_ != null && leaf ) {
					DefaultMutableTreeNode node = ( DefaultMutableTreeNode )value;
					Object item = node.getUserObject();
					if( item instanceof SmartFile && ResultWidget.this.table_.containsKey( item ) ) {
						self.setToolTipText( UnitUtility.toString( ResultWidget.this.table_.get( item ) ) );
					}
				}
				return self;
			}
		} );
		ToolTipManager.sharedInstance().registerComponent( this.overflowList_ );
		this.overflowList_.setCellRenderer( new DefaultListCellRenderer() {
			private static final long serialVersionUID = -2642459895866586526L;
			@Override
			public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				if( ResultWidget.this.table_ != null ) {
					if( value instanceof SmartFile && ResultWidget.this.table_.containsKey( value ) ) {
						this.setToolTipText( UnitUtility.toString( ResultWidget.this.table_.get( value ) ) );
					}
				}
				return this;
			}
		} );
		
		// setup mouse listener
		this.resultTree_.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent e ) {
				if( e.getButton() != MouseEvent.BUTTON3 ) {
					return;
				}
				TreePath tp = ResultWidget.this.resultTree_.getPathForLocation( e.getX(), e.getY() );
				if( tp != null && tp.getPathCount() == 2 ) {
					ResultWidget.this.selectedNode_ = ( DefaultMutableTreeNode )tp.getLastPathComponent();
					ResultWidget.this.popup_.show( ResultWidget.this.resultTree_, e.getX(), e.getY());
				}
			}
		} );
		
		this.table_ = null;
		this.clear();
	}
	
	private void expandAll_() {
		this.expand_( new TreePath( this.getRoot_() ) );
	}
	
	private void expand_( TreePath path ) {
		this.resultTree_.expandPath( path );
		TreeNode node = ( TreeNode )path.getLastPathComponent();
		for( Enumeration< ? > e = node.children(); e.hasMoreElements(); ) {
			this.expand_( path.pathByAddingChild( e.nextElement() ) );
		}
	}
	
	public void clear() {
		( ( DefaultTreeModel )this.resultTree_.getModel() ).setRoot( null );
	}
	
	public void addResult( long size, int eng, Vector< SmartFile > items ) {
		DefaultMutableTreeNode node = new LabelNode( size, eng );
		for( SmartFile item : items ) {
			node.add( new DefaultMutableTreeNode( item ) );
		}
		this.getRoot_().add( node );
		DefaultTreeModel model = (DefaultTreeModel) this.resultTree_.getModel();
		model.reload();
		
		this.progressBar_.setValue( this.progressBar_.getValue() + node.getChildCount() );
	}
	
	public void addOverflow( Vector< SmartFile > overflow ) {
		DefaultListModel model = (DefaultListModel) this.overflowList_.getModel();
		for( SmartFile file : overflow ) {
			model.addElement( file );
		}
		
		this.progressBar_.setValue( this.progressBar_.getValue() + overflow.size() );
	}
	
	private DefaultMutableTreeNode getRoot_() {
		DefaultTreeModel model = ( DefaultTreeModel )this.resultTree_.getModel();
		DefaultMutableTreeNode root = ( DefaultMutableTreeNode )model.getRoot();
		if( root == null ) {
			root = new DefaultMutableTreeNode( "Results" );
			model.setRoot( root );
		}
		return root;
	}
	
	public void save( PrintStream fout ) {
		if( fout != null ) {
			this.savePath_( new TreePath( this.getRoot_() ), 0, fout );
		}
	}
	
	private void savePath_( TreePath path, int indent, PrintStream fout ) {
		DefaultMutableTreeNode node = ( DefaultMutableTreeNode )path.getLastPathComponent();
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < indent; ++i ) {
			sb.append( '\t' );
		}
		sb.append( node );
		fout.println( sb.toString() );
		for( Enumeration< ? > e = node.children(); e.hasMoreElements(); ) {
			this.savePath_( path.pathByAddingChild( e.nextElement() ), indent + 1, fout );
		}
	}
	
	public void exportK3BProjectsTo( File dout ) {
		Long k3bBound = UnitUtility.extract( (Long)Configuration.get( "k3b_export_lower_bound" ), (Integer)Configuration.get( "k3b_export_bound_unit" ) );
		Vector< DefaultMutableTreeNode > tmp = new Vector< DefaultMutableTreeNode >();
		for( Enumeration< ? > e = this.getRoot_().children(); e.hasMoreElements(); ) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			if( node instanceof LabelNode ) {
				Long size = (Long)node.getUserObject();
				if( size >= k3bBound ) {
					tmp.add( node );
				}
			}
		}
		String template = String.format( "%%0%dd.k3b", String.valueOf( tmp.size() ).length() );
		for( int i = 0; i < tmp.size(); ++i ) {
			try {
				K3BUtility.export( new File( dout, String.format( template, i ) ), tmp.get( i ) );
			} catch (IOException e) {
				LogDialog.getErrorLog().log( e.getMessage() );
			} catch (XMLStreamException e) {
				LogDialog.getErrorLog().log( e.getMessage() );
			}
		}
	}
	
	public void openProgress( Hashtable< SmartFile, Long > table ) {
		this.clear();
		this.table_ = table;
		this.progressBar_.setMaximum( table.size() );
		this.progressBar_.setValue( 0 );
		this.progressBar_.setVisible( true );
	}
	
	public void closeProgress() {
		progressBar_.setVisible( false );
		progressBar_.setValue( 0 );
		progressBar_.setMaximum( 0 );
		this.expandAll_();
	}

}
