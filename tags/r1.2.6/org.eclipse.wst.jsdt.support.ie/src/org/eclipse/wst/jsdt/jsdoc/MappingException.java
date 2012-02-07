package org.eclipse.wst.jsdt.jsdoc;
/*
 * Created on Apr 24, 2006
 *
 * Bradley Childs (childsb@us.ibm.com)
 * Copyright IBM 2006.
 *
 * Error thrown for any mapping related issues.
 */
public class MappingException extends Exception {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public MappingException() {
        super();
    }

    public MappingException(String ex) {
        super(ex);
    }
}
