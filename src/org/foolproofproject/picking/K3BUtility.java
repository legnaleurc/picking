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
package org.foolproofproject.picking;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * Some functionality of K3B.
 * @author Wei-Cheng Pan
 */
public class K3BUtility {

	private static XMLOutputFactory xmlFactory_ = XMLOutputFactory.newInstance();
	/**
	 * Export items to K3B project.
	 * @param file Exporting file
	 * @param node A tree node contents itmes
	 * @throws IOException file writing error
	 * @throws XMLStreamException XML serialization error
	 */
	public static void export( File file, QTreeWidgetItem node ) throws IOException, XMLStreamException {
		K3BUtility k3b = new K3BUtility( node );

		k3b.zout_ = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( file ) ) );

		k3b.zout_.putNextEntry( new ZipEntry( "mimetype" ) );
		k3b.zout_.write( "application/x-k3b".getBytes( "UTF-8" ) );
		k3b.zout_.closeEntry();

		k3b.zout_.putNextEntry( new ZipEntry( "maindata.xml" ) );
		k3b.writeXML();
		k3b.zout_.closeEntry();

		k3b.zout_.close();
	}
	private QTreeWidgetItem node_;
	private ZipOutputStream zout_;
	private XMLStreamWriter xout_;

	private K3BUtility( QTreeWidgetItem node ) {
		this.node_ = node;
		this.zout_ = null;
		this.xout_ = null;
	}

	private void writeK3BFilesNode( File file ) throws XMLStreamException {
		if( file.isDirectory() ) {
			this.xout_.writeStartElement( "directory" );
			this.xout_.writeAttribute( "name", file.toString() );
			for( File child : file.listFiles() ) {
				this.writeK3BFilesNode( child );
			}
			this.xout_.writeEndElement();
		} else {
			this.xout_.writeStartElement( "file" );
			this.xout_.writeAttribute( "name", file.toString() );
			this.xout_.writeStartElement( "url" );
			this.xout_.writeCharacters( file.getAbsolutePath() );
			this.xout_.writeEndElement();
			this.xout_.writeEndElement();
		}
	}

	private void writeXML() throws UnsupportedEncodingException, XMLStreamException {
		Calendar now = Calendar.getInstance();
		String title = String.format( "%02d%02d%02d", now.get( Calendar.YEAR ) % 100, now.get( Calendar.MONTH ) + 1, now.get( Calendar.DATE ) );

		this.xout_ = K3BUtility.xmlFactory_.createXMLStreamWriter( new OutputStreamWriter( this.zout_, "UTF-8" ) );
		this.xout_.writeStartDocument( "UTF-8", "1.0" );
		this.xout_.writeDTD( "<!DOCTYPE k3b_dvd_project>" );
		this.xout_.writeStartElement( "k3b_dvd_project" );

		this.xout_.writeStartElement( "general" );
		this.xout_.writeStartElement( "writing_mode" );
		this.xout_.writeCharacters( "auto" );
		this.xout_.writeEndElement();
		this.xout_.writeEmptyElement( "dummy" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "on_the_fly" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEmptyElement( "only_create_images" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "remove_images" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEndElement();

		this.xout_.writeStartElement( "options" );
		this.xout_.writeEmptyElement( "rock_ridge" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEmptyElement( "joliet" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEmptyElement( "udf" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "joliet_allow_103_characters" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEmptyElement( "iso_allow_lowercase" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_allow_period_at_begin" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_allow_31_char" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEmptyElement( "iso_omit_version_numbers" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_omit_trailing_period" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_max_filename_length" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_relaxed_filenames" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_no_iso_translate" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_allow_multidot" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "iso_untranslated_filenames" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "follow_symbolic_links" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "create_trans_tbl" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "hide_trans_tbl" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeStartElement( "iso_level" );
		this.xout_.writeCharacters( "2" );
		this.xout_.writeEndElement();
		this.xout_.writeEmptyElement( "discard_symlinks" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "discard_broken_symlinks" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "preserve_file_permissions" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "force_input_charset" );
		this.xout_.writeAttribute( "activated", "no");
		this.xout_.writeEmptyElement( "do_not_cache_inodes" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeStartElement( "input_charset" );
		this.xout_.writeCharacters( "iso8859-1" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "whitespace_treatment" );
		this.xout_.writeCharacters( "noChange" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "whitespace_replace_string" );
		this.xout_.writeCharacters( "_" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "data_track_mode" );
		this.xout_.writeCharacters( "auto" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "multisession" );
		this.xout_.writeCharacters( "auto" );
		this.xout_.writeEndElement();
		this.xout_.writeEmptyElement( "verify_data" );
		this.xout_.writeAttribute( "activated", "yes");
		this.xout_.writeEndElement();

		this.xout_.writeStartElement( "header" );
		this.xout_.writeStartElement( "volume_id" );
		this.xout_.writeCharacters( title );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "volume_set_id" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "volume_set_size" );
		this.xout_.writeCharacters( "1" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "volume_set_number" );
		this.xout_.writeCharacters( "1" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "system_id" );
		this.xout_.writeCharacters( "LINUX" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "application_id" );
		this.xout_.writeCharacters( "K3B THE CD KREATOR (C) 1998-2006 SEBASTIAN TRUEG AND THE K3B TEAM" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "publisher" );
		this.xout_.writeEndElement();
		this.xout_.writeStartElement( "preparer" );
		this.xout_.writeEndElement();
		this.xout_.writeEndElement();

		this.xout_.writeStartElement( "files" );
		for( int i = 0; i < this.node_.childCount(); ++i ) {
			QTreeWidgetItem child = this.node_.child( i );
			this.writeK3BFilesNode( (File) child.data( 0, Qt.ItemDataRole.UserRole ) );
		}
		this.xout_.writeEndElement();

		this.xout_.writeEndElement();
		this.xout_.writeEndDocument();
		this.xout_.flush();
		this.xout_.close();
	}

}
