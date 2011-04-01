/**
 * @file Preference.java
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.foolproofproject.picking.UnitUtility;

/**
 * @brief Preference widget.
 */
public class Preference extends JDialog {

	private static final long serialVersionUID = -3910072899938489788L;
	private JComboBox unit_;
	private NaturalField limit_;
	private JCheckBox debug_;
	private MainWindow parent_;
	private JCheckBox hidden_;
	private NaturalField k3bLB_;
	private JComboBox k3bUnit_;

	public Preference(MainWindow window) {
		super(window);

		this.parent_ = window;
		this.setTitle("Preferences");

		Container pane = this.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		JPanel limits = new JPanel();
		pane.add(limits);
		limits.setLayout(new GridLayout(1, 2));
		limits.setBorder(BorderFactory.createTitledBorder("Limit"));

		this.limit_ = new NaturalField();
		limits.add(this.limit_);

		this.unit_ = UnitUtility.createComboBox();
		limits.add(this.unit_);

		JPanel k3b = new JPanel();
		pane.add(k3b);
		k3b.setBorder(BorderFactory.createTitledBorder("K3B"));
		k3b.setLayout(new BoxLayout(k3b, BoxLayout.X_AXIS));

		k3b.add(new JLabel("Save upper then"));
		this.k3bLB_ = new NaturalField(4000);
		k3b.add(this.k3bLB_);
		this.k3bUnit_ = UnitUtility.createComboBox();
		k3b.add(this.k3bUnit_);
		k3b.add(new JLabel("results."));

		JPanel misc = new JPanel();
		pane.add(misc);
		misc.setLayout(new GridLayout(2, 1));
		misc.setBorder(BorderFactory.createTitledBorder("Miscellaneous"));

		this.hidden_ = new JCheckBox("View Hidden Files");
		misc.add(this.hidden_);
		this.hidden_.setSelected((Boolean) Configuration.get("hidden"));
		this.debug_ = new JCheckBox("Log debug information");
		misc.add(this.debug_);
		this.debug_.setSelected((Boolean) Configuration.get("debug"));

		JPanel buttons = new JPanel();
		pane.add(buttons);
		buttons.setLayout(new GridLayout(1, 3));

		JButton ok = new JButton("OK");
		buttons.add(ok);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Preference.this.toConf();
				Preference.this.parent_.read();
				Preference.this.setVisible(false);
			}
		});
		JButton apply = new JButton("Apply");
		buttons.add(apply);
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Preference.this.toConf();
				Preference.this.parent_.read();
			}
		});
		JButton cancel = new JButton("Cancel");
		buttons.add(cancel);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Preference.this.setVisible(false);
			}
		});

		this.pack();
		this.setLocationRelativeTo(this.parent_);
	}

	public void exec(Long size, int index, boolean hidden) {
		this.limit_.setText(size.toString());
		this.unit_.setSelectedIndex(index);
		this.hidden_.setSelected(hidden);

		this.k3bLB_.setText(Configuration.get("k3b_export_lower_bound").toString());
		this.k3bUnit_.setSelectedIndex((Integer) Configuration.get("k3b_export_bound_unit"));

		this.setVisible(true);
	}

	private void toConf() {
		Configuration.set("limit", this.limit_.toLong());
		Configuration.set("unit", this.unit_.getSelectedIndex());
		Configuration.set("debug", this.debug_.isSelected());
		Configuration.set("hidden", false);
		Configuration.set("k3b_export_lower_bound", this.k3bLB_.toLong());
		Configuration.set("k3b_export_bound_unit", this.k3bUnit_.getSelectedIndex());
	}

}
