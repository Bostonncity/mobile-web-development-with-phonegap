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
package org.eclipse.wst.jsdt.debug.internal.ui;


import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * A {@link JavaScriptImageDescriptor} consists of a main icon and several adornments. The adornments
 * are computed according to flags set on creation of the descriptor.
 * 
 * @since 1.0
 */
public class JavaScriptImageDescriptor extends CompositeImageDescriptor {
	
	/** Flag to render the installed breakpoint adornment */
	public final static int INSTALLED = 1 << 0;
	/** Flag to render the entry method breakpoint adornment */
	public final static int ENTRY =	1 << 1;
	/** Flag to render the exit method breakpoint adornment */
	public final static int EXIT = 1 << 2;
	/** Flag to render the enabled breakpoint adornment */
	public final static int ENABLED = 1 << 3;
	/** Flag to render the conditional breakpoint adornment */
	public final static int CONDITIONAL = 1 << 4;
	/** Flag to render the scoped breakpoint adornment */
	public final static int SCOPED = 1 << 5;

	private Image fBaseImage;
	private int fFlags;
	private Point fSize;
	
	/**
	 * Create a new JDIImageDescriptor.
	 * 
	 * @param baseImage an image used as the base image
	 * @param flags flags indicating which adornments are to be rendered
	 * 
	 */
	public JavaScriptImageDescriptor(Image baseImage, int flags) {
		setBaseImage(baseImage);
		setFlags(flags);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
	 */
	protected Point getSize() {
		if (fSize == null) {
			ImageData data= getBaseImage().getImageData();
			setSize(new Point(data.width, data.height));
		}
		return fSize;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof JavaScriptImageDescriptor)){
			return false;
		}
			
		JavaScriptImageDescriptor other= (JavaScriptImageDescriptor)object;
		return (getBaseImage().equals(other.getBaseImage()) && getFlags() == other.getFlags());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getBaseImage().hashCode() | getFlags();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
	 */
	protected void drawCompositeImage(int width, int height) {
		ImageData bg= getBaseImage().getImageData();
		if (bg == null) {
			bg = DEFAULT_IMAGE_DATA;
		}
		drawImage(bg, 0, 0);
		drawOverlays();
	}

	private ImageData getImageData(String imageDescriptorKey) {
		return JavaScriptImageRegistry.getSharedImage(imageDescriptorKey).getImageData();
	}
	/**
	 * Add any overlays to the image as specified in the flags.
	 */
	protected void drawOverlays() {
		int flags = getFlags();
		int x = 0;
		int y = 0;
		ImageData data = null;
		if ((flags & INSTALLED) != 0) {
			x = 0;
			y = getSize().y;
			if ((flags & ENABLED) != 0) {
				data = getImageData(ISharedImages.IMG_OVR_INSTALLED);
			} else {
				data = getImageData(ISharedImages.IMG_OVR_INSTALLED_DISABLED);
			}
				
			y -= data.height;
			drawImage(data, x, y);
		}
		if ((flags & SCOPED) != 0) {
			if ((flags & ENABLED) != 0) {
				data = getImageData(ISharedImages.IMG_OVR_SCOPED);
			} else {
				data = getImageData(ISharedImages.IMG_OVR_SCOPED_DISABLED);
			}
			x = 0;
			y = getSize().y;
			y-= data.height;
			drawImage(data, x, y);
		}
		if ((flags & CONDITIONAL) != 0) {
			if ((flags & ENABLED) != 0) {
				data = getImageData(ISharedImages.IMG_OVR_CONDITIONAL);
			} else {
				data = getImageData(ISharedImages.IMG_OVR_CONDITIONAL_DISABLED);
			}
			x = 0;
			y = 0;
			drawImage(data, x, y);
		}
		if ((flags & ENTRY) != 0) {
			x = getSize().x;
			y = 0;
			if ((flags & ENABLED) != 0) {
				data = getImageData(ISharedImages.IMG_OVR_ENTRY);
			} else {
				data = getImageData(ISharedImages.IMG_OVR_ENTRY_DISABLED);
			}
			x -= data.width;
			x = x - 2;
			drawImage(data, x, y);
		}
		if ((flags & EXIT)  != 0){
			x = getSize().x;
			y = getSize().y;
			if ((flags & ENABLED) != 0) {
				data = getImageData(ISharedImages.IMG_OVR_EXIT);
			} else {
				data = getImageData(ISharedImages.IMG_OVR_EXIT_DISABLED);
			}
			x -= data.width;
			x = x - 2;
			y -= data.height;
			drawImage(data, x, y);
		}
	}
	
	protected Image getBaseImage() {
		return fBaseImage;
	}

	protected void setBaseImage(Image baseImage) {
		fBaseImage = baseImage;
	}

	protected int getFlags() {
		return fFlags;
	}

	protected void setFlags(int flags) {
		fFlags = flags;
	}

	protected void setSize(Point size) {
		fSize = size;
	}
}
