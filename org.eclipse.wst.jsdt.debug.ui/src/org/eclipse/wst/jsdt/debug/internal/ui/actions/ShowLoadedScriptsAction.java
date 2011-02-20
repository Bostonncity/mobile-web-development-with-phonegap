package org.eclipse.wst.jsdt.debug.internal.ui.actions;

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.jsdt.debug.internal.ui.PreferencesManager;

/**
 * Menu action to show loaded scripts in the debug view
 * 
 * @since 1.0
 */
public class ShowLoadedScriptsAction implements IViewActionDelegate {
	
	IViewPart fView = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		fView = view;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		PreferencesManager.getManager().showLoadedScripts(action.isChecked());
		IDebugView dview = (IDebugView) fView.getAdapter(IDebugView.class);
		dview.getViewer().refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		action.setChecked(PreferencesManager.getManager().showLoadedScripts());
	}
}
