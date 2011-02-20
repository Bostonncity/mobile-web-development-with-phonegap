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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.connect;

import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument;

/**
 * Implementation of an {@link IntegerArgument} that describes the port to try connecting to
 * 
 * @since 1.0
 */
public class PortArgument implements IntegerArgument {

	private static final long serialVersionUID = -1954469572907116388L;
	private int port;

	/**
	 * The port attribute name
	 */
	public static final String PORT = "port"; //$NON-NLS-1$

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public PortArgument(int port) {
		setValue(port);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument#intValue()
	 */
	public int intValue() {
		return port;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument#isValid(int)
	 */
	public boolean isValid(int intValue) {
		return intValue > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument#max()
	 */
	public int max() {
		return Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument#min()
	 */
	public int min() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument#setValue(int)
	 */
	public void setValue(int port) {
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#description()
	 */
	public String description() {
		return Messages.PortArgument_description;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.IntegerArgument#isValid(java.lang.String)
	 */
	public boolean isValid(String value) {
		try {
			int intValue = Integer.parseInt(value);
			return isValid(intValue);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#label()
	 */
	public String label() {
		return Messages.PortArgument_label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#mustSpecify()
	 */
	public boolean mustSpecify() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#name()
	 */
	public String name() {
		return PORT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		try {
			int intValue = Integer.parseInt(value);
			setValue(intValue);
		} catch (NumberFormatException nfe) {
			// re-throw IllegalArgumentException
			throw new IllegalArgumentException(nfe.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#value()
	 */
	public String value() {
		return Integer.toString(port);
	}
}