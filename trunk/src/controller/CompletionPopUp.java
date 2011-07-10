package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
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

public abstract class CompletionPopUp{
	protected JList list = new JList(); 
	private JPopupMenu popup = new JPopupMenu(); 
	protected JTextComponent textComp; 
	private static final String COMPLETION = "COMPLETION"; //NOI18N 
	private boolean added;
	private char lastKey;
	private boolean rightKey, leftKey, backKey;
	public CompletionPopUp(JTextComponent comp){
	        textComp = comp; 
	        added = false;
	        textComp.putClientProperty(COMPLETION, this); 
	        JScrollPane scroll = new JScrollPane(list); 
	        scroll.setBorder(null); 
	 
	        list.setFocusable( false );
	        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	        scroll.getVerticalScrollBar().setFocusable( false ); 
	        scroll.getHorizontalScrollBar().setFocusable( false ); 
	 
	        popup.setBorder(BorderFactory.createLineBorder(Color.black)); 
	        popup.add(scroll); 
	 
	        if(textComp instanceof JTextArea){ 
	           
	            if(!added){
	            	textComp.getDocument().addDocumentListener(documentListener);
	            	textComp.addKeyListener(keyListener);
	            	popup.addPopupMenuListener(popupListener);
	            	added = true;
	            }
	        }
//	        textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED); 
	 
//	        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED); 
	        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW); 

	       
	        list.setRequestFocusEnabled(false);
	        
	        
	    }

    static Action acceptAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
            completer.popup.setVisible(false); 
            completer.acceptedListItem(e.getActionCommand()); 
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
//			if (e == KeyEvent.VK_LEFT){
//					show(textComp);
//			}
//	            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), JComponent.WHEN_FOCUSED); 
//	            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), JComponent.WHEN_FOCUSED); 
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			rightKey = e.getKeyCode() == KeyEvent.VK_RIGHT;
			leftKey = e.getKeyCode() == KeyEvent.VK_LEFT;
			if (leftKey || rightKey){
				show(textComp);
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			backKey = e.getKeyCode() == KeyEvent.VK_BACK_SPACE;
		}		
	};
	 PopupMenuListener popupListener = new PopupMenuListener(){ 
         public void popupMenuWillBecomeVisible(PopupMenuEvent e){
 	     
         } 

         public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ 
             textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
         } 

         public void popupMenuCanceled(PopupMenuEvent e){ 
         } 
     }; 
    public void showPopup(){ 
        popup.setVisible(false); 
        if(updateListData() && list.getModel().getSize()!=0){ 
//            if(!(textComp instanceof JTextField) && !added){
//                textComp.getDocument().addDocumentListener(documentListener);
//                textComp.addKeyListener(keyListener);
//                added = true;
//            }
//            textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED); 
//            textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            
            int size = list.getModel().getSize(); 
            for(int i=0;i<size;i++){//KeyEvent.VK_F1
            	 textComp.registerKeyboardAction(acceptAction, "F" + ( i+1 ), KeyStroke.getKeyStroke(112+i, 0),JComponent.WHEN_FOCUSED);
            }
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
            popup.show(textComp, x + 10, y + 13); //popup.show(textComp, 0, 230); 
        }else{
            popup.setVisible(false);
//	         textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)); 
//	         textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)); 
        }
        textComp.requestFocus(); 
    } 
 
   private static void show(JComponent tf){
	   CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
       if(tf.isEnabled()){ 
//           if(!completer.popup.isVisible()) 
//               completer.selectNextPossibleValue(); 
//           else 
               completer.showPopup(); 
       } 
   }
    static Action showAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            show(tf);
        } 
    }; 
 
//    static Action upAction = new AbstractAction(){ 
//        public void actionPerformed(ActionEvent e){ 
//            JComponent tf = (JComponent)e.getSource(); 
//            CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
//            if(tf.isEnabled()){ 
//                if(completer.popup.isVisible()) 
//                    completer.selectPreviousPossibleValue(); 
//            } 
//        } 
//    }; 
 
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
//    protected void selectNextPossibleValue(){ 
//        int si = list.getSelectedIndex(); 
// 
//        if(si < list.getModel().getSize() - 1){ 
//            list.setSelectedIndex(si + 1); 
//            list.ensureIndexIsVisible(si + 1); 
//        } 
//    } 
// 
    /** 
     * Selects the previous item in the list.  It won't change the selection if the 
     * currently selected item is already the first item. 
     */ 
//    protected void selectPreviousPossibleValue(){ 
//        int si = list.getSelectedIndex(); 
// 
//        if(si > 0){ 
//            list.setSelectedIndex(si - 1); 
//            list.ensureIndexIsVisible(si - 1); 
//        } 
//    } 
 
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

	public boolean isLeftKey() {
		return leftKey;
	}

	public void setLeftKey(boolean leftKey) {
		this.leftKey = leftKey;
	}

	public boolean isRightKey() {
		return rightKey;
	}

	public void setRightKey(boolean rightKey) {
		this.rightKey = rightKey;
	}

	public boolean isBackKey() {
		return backKey;
	}

	public void setBackKey(boolean backKey) {
		this.backKey = backKey;
	} 
}
