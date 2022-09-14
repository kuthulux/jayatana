/*
 * Copyright (c) 2014 Jared González
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jarego.jayatana.basic;

import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jarego.jayatana.swing.SwingGlobalMenuWindow;

/**
 * Clase de adatador de GlobalMenu que permite encapsular el controlador de ventana
 * junto con los controles de menú nativo.
 * 
 * @author Jared González
 */
public abstract class GlobalMenuAdapter {
	private static final int SPINCOUNT = 200;
	private final GlobalMenuImp globalMenuImp;
	/**
	 * Variable de especificación de bloqueo de barra de menus
	 */
	private boolean lockedMenuBar = false;
	
	/**
	 * Varaible de especificación de retardo por espera de contrucción
	 * de menús.
	 */
	protected long approveRefreshWatcher = -1;
	/**
	 * Ventana asociada al menu global
	 */
	private Object window;
	/**
	 * Identificador de ventana
	 */
	private long windowXID;
	
	/**
	 * Iniciar controlador de menú global basado en un objeto ventana.
	 * 
	 * @param window ventana
	 */
	public GlobalMenuAdapter(Window window) {
		this(window, GlobalMenu.getWindowXID((Window)window));
	}
	
	public GlobalMenuAdapter(Object window, long windowXID) {
		this.window = window;
		this.windowXID = windowXID;
		this.globalMenuImp = new GlobalMenuImp(this);
	}
	
	/**
	 * Registra visualizador de bus de menu global. En caso de que el bus
	 * exista se invocará el método <code>register</code>.
	 */
	public void registerWatcher() {
		globalMenuImp.registerWatcher(windowXID);
	}
	/**
	 * Elimina el visualizador de bus de menu global. En cas de que el bus
	 * exista se invocará el método <code>unregister</code>.
	 */
	protected void unregisterWatcher() {
		globalMenuImp.unregisterWatcher(windowXID);
	}
	/**
	 * Este método regenera el visualizador de Bus, y debe ser usado si algun
	 * menu (de nivel 0) agregado directamente a la barra de menus cambia,
	 * puesto que Ubuntu tiene problemas con los métodos de agregar o eliminar estos.
	 */
	protected void refreshWatcher() {
		globalMenuImp.refreshWatcher(windowXID);
	}
	
	/**
	 * Agrega un nuevo menú de folder nativo sobre la barra de menús.
	 * 
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param visible estado de visibulidad del menú.
	 */
	protected void addMenu(int menuId, String label, boolean enabled, boolean visible) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.addMenu(windowXID, -1, menuId, secureString(label), lockedMenuBar ? false : enabled, visible);
	}
	/**
	 * Agrega un nuevo menú de folder nativo.
	 * 
	 * @param menuParentId identificador del menu padre, para especificar un menu directamente en la barra
	 * de menú el identificador del padre debe ser <code>-1</code>.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param visible estado de visibulidad del menú.
	 */
	protected void addMenu(int menuParentId, int menuId, String label, boolean enabled,
			boolean visible) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.addMenu(windowXID, menuParentId, menuId, secureString(label), enabled, visible);
	}
	/**
	 * Agrega un elemento de menú nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param modifiers modificador del acelerador del menú (CTRL, ALT o SHIFT).
	 * @param keycode acelerador del menú.
	 */
	protected void addMenuItem(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.addMenuItem(windowXID, menuParentId, menuId, secureString(label), enabled, modifiers, keycode);
	}
	/**
	 * Agrega un elemento de menú check nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param modifiers modificador del acelerador del menú (CTRL, ALT o SHIFT).
	 * @param keycode acelerador del menú.
	 * @param selected estado de selección del menú.
	 */
	protected void addMenuItemCheck(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode, boolean selected) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.addMenuItemCheck(windowXID, menuParentId, menuId, secureString(label), enabled,modifiers, keycode, selected);
	}
	/**
	 * Agrega un elemento de menú radio nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 * @param menuId identificador del menú.
	 * @param label etiqueta del menú.
	 * @param enabled estado de habilitación del menú.
	 * @param modifiers modificador del acelerador del menú (CTRL, ALT o SHIFT).
	 * @param keycode acelerador del menú.
	 * @param selected estado de selección del menú.
	 */
	protected void addMenuItemRadio(int menuParentId, int menuId, String label, boolean enabled,
			int modifiers, int keycode, boolean selected) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.addMenuItemRadio(windowXID, menuParentId, menuId, secureString(label), enabled, modifiers, keycode, selected);
	}
	/**
	 * Agrega un elmemento de menú de separador nativo.
	 * 
	 * @param menuParentId identificador del menú padre.
	 */
	protected void addSeparator(int menuParentId) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.addSeparator(windowXID, menuParentId);
	}
	/**
	 * Actualización de estado del menú nativo.
	 * 
	 * @param menuId identificador de menu
	 * @param label nuevo valor de etiqueta
	 * @param enabled nuevo valor de estado de habilitación del menú.
	 * @param visible nuevo valor de estado de visibilidad del menú.
	 */
	protected void updateMenu(int menuId, String label, boolean enabled, boolean visible) {
		if (approveRefreshWatcher != -1)
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		globalMenuImp.updateMenu(windowXID, menuId, secureString(label), enabled, visible);
	}
	
	/**
	 * Obtener la ventana.
	 * 
	 * @return objeto ventana.
	 */
	protected Object getWindow() {
		return window;
	}
	/**
	 * Obtener el identificador de ventana.
	 * 
	 * @return identificador de ventana.
	 */
	protected long getWindowXID() {
		return windowXID;
	}
	
	/**
	 * Obtener una cadena en caso de NULL
	 * @param text texto de etiqueta
	 * @return texto
	 */
	private static String secureString(String text) {
		if (text == null)
			return "";
		else
			return text;
	}
	
	/**
	 * Regenera menús directamente en la barra de menús.
	 */
	protected void refreshWatcherSafe() {
		if (approveRefreshWatcher == -1) {
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
			new Thread() {
				@Override
				public void run() {
					try {
						while (System.currentTimeMillis() < approveRefreshWatcher)
							Thread.sleep(100);
					} catch (InterruptedException e) {
						Logger.getLogger(SwingGlobalMenuWindow.class.getName()).log(
								Level.WARNING, "Can't wait approve rebuild", e);
					} finally {
						approveRefreshWatcher = -1;
						refreshWatcher();
					}
				}
			}.start();
		} else {
			approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
		}
	}
	
	/**
	 * Bloquear la barra de menus.
	 */
	public void lockMenuBar() {
		if (!lockedMenuBar) {
			lockedMenuBar = true;
			refreshWatcherSafe();
		}
	}
	
	/**
	 * Desbloquar la barra de menus.
	 */
	public void unlockMenuBar() {
		if (lockedMenuBar) {
			lockedMenuBar = false;
			refreshWatcherSafe();
		}
	}
	
	abstract protected void register(int state);
	abstract protected void unregister();
	abstract protected void menuActivated(int parentMenuId, int menuId);
	abstract protected void menuAboutToShow(int parentMenuId, int menuId);
	abstract protected void menuAfterClose(int parentMenuId, int menuId);
	
	private static class GlobalMenuImp extends GlobalMenu {
		private final GlobalMenuAdapter globalMenuAdapter;
		
		public GlobalMenuImp(GlobalMenuAdapter globalMenuAdapter) {
			this.globalMenuAdapter = globalMenuAdapter;
		}
		@Override
		protected void register(int state) {
			globalMenuAdapter.register(state);
		}
		@Override
		protected void unregister() {
			globalMenuAdapter.unregister();
		}
		@Override
		protected void menuActivated(int parentMenuId, int menuId) {
			globalMenuAdapter.menuActivated(parentMenuId, menuId);
		}
		@Override
		protected void menuAboutToShow(int parentMenuId, int menuId) {
			if (globalMenuAdapter.approveRefreshWatcher != -1)
				globalMenuAdapter.approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
			globalMenuAdapter.menuAboutToShow(parentMenuId, menuId);
		}
		@Override
		protected void menuAfterClose(int parentMenuId, int menuId) {
			if (globalMenuAdapter.approveRefreshWatcher != -1)
				globalMenuAdapter.approveRefreshWatcher = System.currentTimeMillis() + SPINCOUNT;
			globalMenuAdapter.menuAfterClose(parentMenuId, menuId);
		}
	}
}
