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
package org.eclipse.wst.jsdt.debug.core.jsdi.connect;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Describes an extensible connector for JSDI.
 * 
 * Clients can implement or extend this interface.
 * 
 * @since 1.0
 */
public interface Connector {
	
	/**
	 * Default description of an argument that this {@link Connector} can support
	 */
	public interface Argument extends Serializable {
		/**
		 * A human readable description of the {@link Argument}.<br>
		 * <br>
		 * This method can return <code>null</code>
		 * 
		 * @return the description of the {@link Argument} or <code>null</code>
		 */
		public String description();

		/**
		 * The simple label of the {@link Argument}.<br>
		 * <br>
		 * This method can return <code>null</code>
		 * 
		 * @return the label for this {@link Argument} or <code>null</code>
		 */
		public String label();

		/**
		 * Returns <code>true</code> if this {@link Argument} is required <code>false</code> otherwise.
		 * 
		 * @return <code>true</code> if this {@link Argument} is required <code>false</code> otherwise
		 */
		public boolean mustSpecify();

		/**
		 * The name of the {@link Argument}.<br>
		 * <br>
		 * This method can return <code>null</code>
		 * 
		 * @return the name or the {@link Argument} or <code>null</code>
		 */
		public String name();

		/**
		 * Returns if the given value is valid with-respect-to this {@link Argument}
		 * 
		 * @param value the value to test for validity, <code>null</code> is accepted
		 * 
		 * @return <code>true</code> if the {@link Argument} is valid <code>false</code> otherwise
		 */
		public boolean isValid(String value);

		/**
		 * Sets the value of the {@link Argument} to be the given value.
		 * 
		 * @param value the new value to set, <code>null</code> is accepted
		 */
		public void setValue(String value);

		/**
		 * Returns the value of the {@link Argument}.<br>
		 * <br>
		 * This method can return <code>null</code>
		 * 
		 * @return the string value of the {@link Argument} or <code>null</code>
		 */
		public String value();
	}

	/**
	 * Integer specialization of an {@link Argument}
	 * 
	 * @see Argument
	 */
	public interface IntegerArgument extends Connector.Argument {
		/**
		 * Returns the integer value of the {@link Argument}
		 * 
		 * @return the integer value
		 */
		public int intValue();

		/**
		 * If the integer value is valid with-respect-to this argument.
		 * 
		 * @param intValue the value to test
		 * @return <code>true</code> if the given value if valid <code>false</code> otherwise
		 */
		public boolean isValid(int intValue);

		/* (non-Javadoc)
		 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#isValid(java.lang.String)
		 */
		public boolean isValid(String value);

		/**
		 * Returns the maximum value that this {@link Argument} will accept as being valid.
		 * 
		 * @return the maximum value for this argument
		 */
		public int max();

		/**
		 * Returns the minimum value that this {@link Argument} will accept as being valid.
		 * 
		 * @return the minimum value for this argument
		 */
		public int min();

		/**
		 * Sets the integer value of this {@link Argument}.
		 * 
		 * @param intValue the new value to set
		 */
		public void setValue(int intValue);
	}

	/**
	 * Boolean specialization of an {@link Argument}
	 * 
	 * @see Argument
	 */
	public interface BooleanArgument extends Connector.Argument {
		/**
		 * Returns the boolean value for this {@link Argument}.
		 * 
		 * @return the boolean value
		 */
		public boolean booleanValue();

		/* (non-Javadoc)
		 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#isValid(java.lang.String)
		 */
		public boolean isValid(String value);

		/**
		 * Sets the new boolean value for this {@link Argument}.
		 * 
		 * @param booleanValue the new value
		 */
		public void setValue(boolean booleanValue);
	}

	/**
	 * String specialization of an {@link Argument}
	 * 
	 * @see Argument
	 */
	public interface StringArgument extends Connector.Argument {
		/* (non-Javadoc)
		 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#isValid(java.lang.String)
		 */
		public boolean isValid(String arg1);
	}

	/**
	 * Specialization of {@link Argument} whose value is a String selected from a list of choices.
	 */
	public interface SelectedArgument extends Connector.Argument {
		/**
		 * The list of choices for this connector
		 * 
		 * @return the complete list of choices for ths connector
		 */
		public List choices();

		/* (non-Javadoc)
		 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector.Argument#isValid(java.lang.String)
		 */
		public boolean isValid(String value);
	}

	/**
	 * Returns the {@link Map} of default {@link Argument}s for this connector or an
	 * empty {@link Map} if no {@link Argument}s have been specified.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the {@link Map} of default {@link Argument}s or an empty {@link Map}, never <code>null</code>
	 */
	public Map defaultArguments();

	/**
	 * A human readable description of this {@link Connector}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the description of this {@link Connector} or <code>null</code>
	 */
	public String description();

	/**
	 * The name of this {@link Connector}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the name of the {@link Connector}
	 */
	public String name();

	/**
	 * The unique identifier for this {@link Connector}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the identifier of this {@link Connector} never <code>null</code>
	 */
	public String id();
}
