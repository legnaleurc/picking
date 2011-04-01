/**
 * @file Configuration.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @brief Configuration utility.
 */
public class Configuration implements Serializable {

	private static final long serialVersionUID = -6422237746611091558L;
	private static final File file_ = new File( System.getProperty( "user.home" ) + File.separator + ".packing" );
	private static Configuration self_;
	private HashMap< String, Object > data_;

	static {
		try {
			ObjectInputStream fin = new ObjectInputStream( new FileInputStream( Configuration.file_ ) );
			Configuration.self_ = (Configuration) fin.readObject();
			if( Configuration.self_.data_ == null ) {
				throw new Exception( "Configuration format is mismatched." );
			}
			fin.close();
		} catch (FileNotFoundException e) {
			Configuration.self_ = new Configuration();
			try {
				Configuration.sync();
			} catch (Exception e1) {
				LogDialog.getErrorLog().log( e1.getMessage() );
			}
		} catch (Exception e) {
			LogDialog.getErrorLog().log( e.getMessage() );
			LogDialog.getErrorLog().log( "Rolling back to default value." );
			Configuration.self_ = new Configuration();
		}
	}

	/**
	 * @brief Get configuration.
	 * @param key configuration key
	 * @return configuration value
	 * @note Will return null if no such key.
	 */
	public static Object get( String key ) {
		return Configuration.self_.data_.get( key );
	}

	private static boolean isWindows() {
		return System.getProperty( "os.name" ).startsWith( "Windows" );
	}

	/**
	 * @brief Set configuration.
	 * @param key configuration key
	 * @param value configuration value
	 */
	public static void set( String key, Object value ) {
		synchronized (Configuration.self_) {
			Configuration.self_.data_.put(key, value);
		}
	}

	/**
	 * @brief Synchronize to file.
	 * @throws InterruptedException if Windows command has been interrupted
	 * @throws IOException file writing error
	 */
	public static void sync() throws InterruptedException, IOException {
		if( Configuration.isWindows() && Configuration.file_.exists() && Configuration.file_.isHidden() ) {
			Runtime.getRuntime().exec( String.format( "ATTRIB -H \"%s\"" , Configuration.file_.getAbsolutePath()) ).waitFor();
		}
		try {
			ObjectOutputStream fout = new ObjectOutputStream( new FileOutputStream( Configuration.file_ ) );
			fout.writeObject( Configuration.self_ );
			fout.close();
		} catch (FileNotFoundException e) {
			LogDialog.getErrorLog().log( e.getMessage() );
		}
		if( Configuration.isWindows() && !Configuration.file_.isHidden() ) {
			Runtime.getRuntime().exec( String.format( "ATTRIB +H \"%s\"" , Configuration.file_.getAbsolutePath()) ).waitFor();
		}
	}

	private Configuration() {
		this.data_ = new HashMap< String, Object >();
		this.data_.put( "limit", 4483L );
		this.data_.put( "unit", 2 );
		this.data_.put( "k3b_export_lower_bound", 4000L );
		this.data_.put( "k3b_export_bound_unit", 2 );
		this.data_.put( "debug", false );
		this.data_.put( "hidden", false );
	}

}
