/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.ui.launching;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.BooleanArgument;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.SelectedArgument;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.StringArgument;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.launching.ILaunchConstants;
import org.eclipse.wst.jsdt.debug.internal.ui.IHelpContextIds;
import org.eclipse.wst.jsdt.debug.internal.ui.ISharedImages;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptImageRegistry;
import org.eclipse.wst.jsdt.debug.internal.ui.Messages;
import org.eclipse.wst.jsdt.debug.internal.ui.SWTFactory;

/**
 * Tab to allow connection information to be specified
 * 
 * @since 1.0
 */
public class JavaScriptConnectTab extends AbstractLaunchConfigurationTab implements IPropertyChangeListener {
	
	Text description = null;
	Combo connectorcombo = null;
	Connector selectedconnector = null;
	Group argumentsgroup = null;
	HashMap editormap = new HashMap();
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);
		//connectors combo
		Group group = SWTFactory.createGroup(comp, Messages.connector, 1, 2, GridData.FILL_HORIZONTAL);
		this.connectorcombo = SWTFactory.createCombo(group, SWT.FLAT | SWT.BORDER | SWT.READ_ONLY, 1, null);
		GridData gd = (GridData) this.connectorcombo.getLayoutData();
		gd.grabExcessHorizontalSpace = true;
		this.connectorcombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleConnectorSelected();
			}
		});
		this.description = SWTFactory.createText(group, SWT.WRAP | SWT.READ_ONLY, 1);
		gd = (GridData) this.description.getLayoutData();
		gd.heightHint = 30;
		List connectors = JavaScriptDebugPlugin.getConnectionsManager().getConnectors();
		Connector connector = null;
		for (int i = 0; i < connectors.size(); i++) {
			connector = (Connector)connectors.get(i);
			this.connectorcombo.add(connector.name());
			this.connectorcombo.setData(connector.name(), connector);
		}
		
		this.argumentsgroup = SWTFactory.createGroup(comp, Messages.connector_properties, 2, 2, GridData.FILL_HORIZONTAL);
		this.argumentsgroup.setVisible(false);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(comp, IHelpContextIds.CONNECT_TAB);
	}

	/**
	 * Returns the {@link Connector} based on the selection of the combo
	 * @return the selected combo
	 */
	Connector getSelectedConnector() {
		if(this.connectorcombo.getItemCount() > 0) {
			int idx = this.connectorcombo.getSelectionIndex();
			idx = (idx < 0 ? 0 : idx);
			String name = this.connectorcombo.getItem(idx);
			return (Connector) this.connectorcombo.getData(name);
		}
		return null;
	}
	
	/**
	 * Handles creating the UI for the connector selected
	 */
	void handleConnectorSelected() {
		Connector connect = getSelectedConnector();
		if(connect == null || connect.equals(this.selectedconnector)) {
			//nothing changed
			return;
		}
		this.selectedconnector = connect;
		String desc = this.selectedconnector.description();
		if(desc != null) {
			this.description.setText(desc);
		}
		else {
			this.description.setText(Messages.no_description_provided);
		}
		this.editormap.clear();
		//get rid of the old controls
		Control[] children = this.argumentsgroup.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].dispose();
		}
		PreferenceStore store = new PreferenceStore();
		Entry entry = null;
		Argument argument = null;
		FieldEditor editor = null;
		String key = null;
		for (Iterator iter = this.selectedconnector.defaultArguments().entrySet().iterator(); iter.hasNext();) {
			entry = (Entry) iter.next();
			key = (String) entry.getKey();
			argument = (Argument) entry.getValue();
			if(argument instanceof IntegerArgument) {
				//create an int editor
				store.setDefault(argument.name(), ((IntegerArgument)argument).intValue());
				editor = new IntegerFieldEditor(argument.name(), argument.label(), this.argumentsgroup);
			}
			else if(argument instanceof BooleanArgument) {
				//create boolean editor
				store.setDefault(argument.name(), ((BooleanArgument)argument).booleanValue());
				editor = new BooleanFieldEditor(argument.name(), argument.label(), this.argumentsgroup);
				editor.fillIntoGrid(argumentsgroup, 2);
			}
			else if(argument instanceof StringArgument) {
				//create String editor
				store.setDefault(argument.name(), argument.value());
				editor = new StringFieldEditor(argument.name(), argument.label(), this.argumentsgroup);
			}
			else if(argument instanceof SelectedArgument) {
				//create list selection editor
				List choices = ((SelectedArgument)argument).choices();
				String[][] namesAndValues = new String[choices.size()][2];
				int count = 0;
				for (Iterator iter2 = choices.iterator(); iter2.hasNext();) {
					String choice = (String)iter2.next();
					namesAndValues[count][0] = choice;
					namesAndValues[count][1] = choice;
					count++;
				}
				store.setDefault(argument.name(), argument.value());
				editor = new ComboFieldEditor(argument.name(), argument.label(), namesAndValues, this.argumentsgroup);
			}
			if(editor != null) {
				editor.setPreferenceStore(store);
				editor.loadDefault();
				editor.setPropertyChangeListener(this);
				this.editormap.put(key, editor);
				editor = null;
			}
		}
		//reset margins to undo FieldEditor bogosity
		GridLayout gd = (GridLayout) this.argumentsgroup.getLayout();
		gd.marginHeight = 5;
		gd.marginWidth = 5;
		this.argumentsgroup.getParent().layout(true);
		this.argumentsgroup.setVisible(true);
		this.argumentsgroup.layout(true);
		updateLaunchConfigurationDialog();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		Iterator keys = this.editormap.keySet().iterator();
		Connector connector = getSelectedConnector();
		if(connector == null) {
			return false;
		}
		while (keys.hasNext()) {
			String key = (String)keys.next();
			Argument arg = (Argument)connector.defaultArguments().get(key);
			FieldEditor editor = (FieldEditor)this.editormap.get(key);
			if (editor instanceof StringFieldEditor) {
				String value = ((StringFieldEditor)editor).getStringValue();
				if (!arg.isValid(value)) {
					setErrorMessage(NLS.bind(Messages.the_argument_0_is_not_valid, new String[] {arg.name()})); 
					return false;
				}		
			}
		}		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return Messages.connect;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_CONNECT);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String connectorid = configuration.getAttribute(ILaunchConstants.CONNECTOR_ID, (String)null);
			if(connectorid != null) {
				Connector connector = JavaScriptDebugPlugin.getConnectionsManager().getConnector(connectorid);
				if(connector != null) {
					int idx = this.connectorcombo.indexOf(connector.name());
					if(idx > -1) {
						this.connectorcombo.select(idx);
						handleConnectorSelected();
						Map argmap = configuration.getAttribute(ILaunchConstants.ARGUMENT_MAP, (Map)null);
						if(argmap != null) {
							Entry entry = null;
							Argument argument = null;
							String key = null;
							FieldEditor editor = null;
							for (Iterator iter = argmap.entrySet().iterator(); iter.hasNext();) {
								entry = (Entry) iter.next();
								key = (String) entry.getKey();
								argument = (Argument) connector.defaultArguments().get(key);
								editor = (FieldEditor)this.editormap.get(key);
								if (argument != null && editor != null) {
									String value = (String)argmap.get(key);
									if (argument instanceof StringArgument || argument instanceof SelectedArgument) {
										editor.getPreferenceStore().setValue(key, value);
									} 
									else if (argument instanceof BooleanArgument) {
										editor.getPreferenceStore().setValue(key, Boolean.valueOf(value).booleanValue());
									}
									else if (argument instanceof IntegerArgument) {
										editor.getPreferenceStore().setValue(key, new Integer(value).intValue());
									}
									editor.load();
								}
							}
						}
					}
					else {
						this.connectorcombo.select(0);
						handleConnectorSelected();
					}
				}
			}
		}
		catch(CoreException ce) {
			//ignore
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		Connector connector = getSelectedConnector();
		if(connector != null) {
			configuration.setAttribute(ILaunchConstants.CONNECTOR_ID, connector.id());
			Entry entry = null;
			FieldEditor editor = null;
			String key = null;
			HashMap argmap = new HashMap();
			Argument argument = null;
			for (Iterator iter = this.editormap.entrySet().iterator(); iter.hasNext();) {
				entry = (Entry) iter.next();
				editor = (FieldEditor) entry.getValue();
				if(!editor.isValid()) {
					return;
				}
				key = (String) entry.getKey();
				argument = (Argument) connector.defaultArguments().get(key);
				if(argument == null) {
					continue;
				}
				editor.store();
				if (argument instanceof StringArgument || argument instanceof SelectedArgument) {
					argmap.put(key, editor.getPreferenceStore().getString(key));
				}
				else if (argument instanceof BooleanArgument) {
					argmap.put(key, Boolean.valueOf(editor.getPreferenceStore().getBoolean(key)).toString());
				} 
				else if (argument instanceof IntegerArgument) {
					argmap.put(key, new Integer(editor.getPreferenceStore().getInt(key)).toString());
				}
			}
			configuration.setAttribute(ILaunchConstants.ARGUMENT_MAP, argmap);
		}
		else {
			configuration.removeAttribute(ILaunchConstants.ARGUMENT_MAP);
			configuration.removeAttribute(ILaunchConstants.CONNECTOR_ID);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		List connectors = JavaScriptDebugPlugin.getConnectionsManager().getConnectors();
		if(!connectors.isEmpty()) {
			configuration.setAttribute(ILaunchConstants.CONNECTOR_ID, ((Connector)connectors.get(0)).id());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		updateLaunchConfigurationDialog();
	}
}
