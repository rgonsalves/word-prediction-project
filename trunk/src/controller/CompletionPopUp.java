package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public abstract class CompletionPopUp {
	protected JList list = new JList(); 
	private JPopupMenu popup = new JPopupMenu(); 
	protected JTextComponent textComp; 
	private static final String COMPLETION = "COMPLETION"; //NOI18N 
	private boolean added;
	private char lastKey;
	public CompletionPopUp(JTextComponent comp){
	        textComp = comp; 
	        added = false;
	        textComp.putClientProperty(COMPLETION, this); 
	        JScrollPane scroll = new JScrollPane(list); 
	        scroll.setBorder(null); 
	 
	        list.setFocusable( false ); 
	        scroll.getVerticalScrollBar().setFocusable( false ); 
	        scroll.getHorizontalScrollBar().setFocusable( false ); 
	 
	        popup.setBorder(BorderFactory.createLineBorder(Color.black)); 
	        popup.add(scroll); 
	 
	        if(textComp instanceof JTextArea){ 
	            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED); 
	            if(!added){
	            	textComp.getDocument().addDocumentListener(documentListener);
	            	textComp.addKeyListener(keyListener);
	            	added = true;
	            }
	        }else 
	            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED); 
	 
	        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED); 
	        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED); 
	 
	        popup.addPopupMenuListener(new PopupMenuListener(){ 
	            public void popupMenuWillBecomeVisible(PopupMenuEvent e){ 
	            } 
	 
	            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ 
	                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)); 
	            } 
	 
	            public void popupMenuCanceled(PopupMenuEvent e){ 
	            } 
	        }); 
	        list.setRequestFocusEnabled(false);
	        
	        
	    }

    static Action acceptAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
            completer.popup.setVisible(false); 
            completer.acceptedListItem((String)completer.list.getSelectedValue()); 
        } 
    }; 
 
    DocumentListener documentListener = new DocumentListener(){ 
        public void insertUpdate(DocumentEvent e){ 
            showPopup(); 
        } 
 
        public void removeUpdate(DocumentEvent e){ 
            showPopup(); 
        } 
 
        public void changedUpdate(DocumentEvent e){} 
    }; 
   KeyListener keyListener =new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent e) {
			lastKey = e.getKeyChar();
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	};
    private void showPopup(){ 
        popup.setVisible(false); 
        if(updateListData() && list.getModel().getSize()!=0){ 
            if(!(textComp instanceof JTextField) && !added){
                textComp.getDocument().addDocumentListener(documentListener);
                textComp.addKeyListener(keyListener);
                added = true;
            }
            textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED); 
            int size = list.getModel().getSize(); 
            list.setVisibleRowCount(size<10 ? size : 10); 
            int x = 0;
            int y = 0;
            try{ 
                int pos = Math.min(textComp.getCaret().getDot(), textComp.getCaret().getMark()); 
                x = textComp.getUI().modelToView(textComp, pos).x;
                y = textComp.getUI().modelToView(textComp, pos).y; 
            } catch(BadLocationException e){ 
                // this should never happen!!! 
                e.printStackTrace(); 
            } 
            popup.show(textComp, x + 10, y + 13); 
        }else 
            popup.setVisible(false); 
        textComp.requestFocus(); 
    } 
 
    static Action showAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
            if(tf.isEnabled()){ 
                if(completer.popup.isVisible()) 
                    completer.selectNextPossibleValue(); 
                else 
                    completer.showPopup(); 
            } 
        } 
    }; 
 
    static Action upAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
            if(tf.isEnabled()){ 
                if(completer.popup.isVisible()) 
                    completer.selectPreviousPossibleValue(); 
            } 
        } 
    }; 
 
    static Action hidePopupAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
            if(tf.isEnabled()) 
                completer.popup.setVisible(false); 
        } 
    }; 
 
    /** 
     * Selects the next item in the list.  It won't change the selection if the 
     * currently selected item is already the last item. 
     */ 
    protected void selectNextPossibleValue(){ 
        int si = list.getSelectedIndex(); 
 
        if(si < list.getModel().getSize() - 1){ 
            list.setSelectedIndex(si + 1); 
            list.ensureIndexIsVisible(si + 1); 
        } 
    } 
 
    /** 
     * Selects the previous item in the list.  It won't change the selection if the 
     * currently selected item is already the first item. 
     */ 
    protected void selectPreviousPossibleValue(){ 
        int si = list.getSelectedIndex(); 
 
        if(si > 0){ 
            list.setSelectedIndex(si - 1); 
            list.ensureIndexIsVisible(si - 1); 
        } 
    } 
 
    // update list model depending on the data in textfield 
    protected abstract boolean updateListData(); 
 
    // user has selected some item in the list. update textfield accordingly... 
    protected abstract void acceptedListItem(String selected);

	public char getLastKey() {
		return lastKey;
	}

	public void setLastKey(char lastKey) {
		this.lastKey = lastKey;
	} 
}
