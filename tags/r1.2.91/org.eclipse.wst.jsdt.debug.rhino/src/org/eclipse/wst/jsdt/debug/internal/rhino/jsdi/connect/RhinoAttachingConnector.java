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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.AttachingConnector;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.*;

/**
 * Rhino {@link Connector} implementation that attaches to a running debug process
 * 
 * @since 1.0
 * @see Connector
 * @see AttachingConnector
 */
public class RhinoAttachingConnector implements AttachingConnector {

	/**
	 * Describes the <code>localhost</code> hostname i.e. the <code>127.0.0.1</code> IP address
	 */
	public static final String LOCALHOST = "localhost"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.AttachingConnector#attach(java.util.Map)
	 */
	public VirtualMachine attach(Map arguments) throws IOException {
		TransportService service = new SocketTransportService();
		String host = (String) arguments.get(HostArgument.HOST);
		String port = (String) arguments.get(PortArgument.PORT);
		Connection c = service.attach(host + ":" + Integer.parseInt(port), 10000, 10000); //$NON-NLS-1$
		DebugSession session = new DebugSession(c);
		return new VirtualMachineImpl(session);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector#defaultArguments()
	 */
	public Map defaultArguments() {
		Map result = new HashMap();
		result.put(HostArgument.HOST, new HostArgument(LOCALHOST));
		result.put(PortArgument.PORT, new PortArgument(9000));
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector#description()
	 */
	public String description() {
		return Messages.RhinoAttachingConnector_description;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector#name()
	 */
	public String name() {
		return Messages.RhinoAttachingConnector_name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector#id()
	 */
	public String id() {
		return "rhino.attaching.connector"; //$NON-NLS-1$
	}
}