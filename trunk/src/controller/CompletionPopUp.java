package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import view.PredictionPanel;
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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import view.PredictionPanel;

public abstract class CompletionPopUp{
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
	        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	        scroll.getVerticalScrollBar().setFocusable( false ); 
	        scroll.getHorizontalScrollBar().setFocusable( false ); 
	 
	        popup.setBorder(BorderFactory.createLineBorder(Color.black)); 
	        popup.add(scroll); 
	 
	        if(textComp instanceof JTextArea){ 
	           
	            if(!added){
	            	textComp.addCaretListener(caretListener);
	            	textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
//	            	textComp.registerKeyboardAction(undoAction, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
	            	
	            	added = true;
	            }
	        }
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
 
    CaretListener caretListener = new CaretListener() {
		
		@Override
		public void caretUpdate(CaretEvent e) {
			
			show(textComp);
//			String text = textComp.getText();
//			if(PredictionPanel.getMainPanel().getSize().getWidth() > textComp.getText().length())
//				textComp.setText(text + '\n');
		}
	};

	

    public void showPopup(){ 
        popup.setVisible(false); 
        if(updateListData() && list.getModel().getSize()!=0){ 
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
        }
        textComp.requestFocus(); 
    } 
 
   private static void show(JComponent tf){
	   CompletionPopUp completer = (CompletionPopUp)tf.getClientProperty(COMPLETION); 
       if(tf.isEnabled()){ 
    	   completer.showPopup(); 
       } 
   }
    static Action showAction = new AbstractAction(){ 
        public void actionPerformed(ActionEvent e){ 
            JComponent tf = (JComponent)e.getSource(); 
            show(tf);
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
}
