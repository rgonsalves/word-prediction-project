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


public abstract class CompletionPopUp{
	protected JList list = new JList(); 
	private JPopupMenu popup = new JPopupMenu(); 
	protected JTextComponent textComp; 
	private static final String COMPLETION = "COMPLETION"; //NOI18N 
	private boolean added;
	private char lastKey;
	
	 public CompletionPopUp(){//for the static methods (js)
	    	
	    }
	public CompletionPopUp(JTextComponent comp){
	        textComp = comp; 
	        added = false;
	      
	        textComp.putClientProperty(COMPLETION, this); 
	        JScrollPane scroll = new JScrollPane(list); 
	        scroll.setBorder(null); 
	 
	        list.setFocusable( false );
	        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        scroll.getVerticalScrollBar().setFocusable( false ); 
	        scroll.getHorizontalScrollBar().setFocusable( false ); 
	 
	        popup.setBorder(BorderFactory.createLineBorder(Color.black)); 
	        popup.add(scroll); 
	 
//	        if(textComp instanceof JTextArea){ 
	           
	            if(!added){
	            	textComp.addCaretListener(caretListener);
	            	textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
      	
	            	added = true;
	            }
//	        }
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
