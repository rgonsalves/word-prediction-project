package utilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.TreeNode;

public class TrieNode<T> {
	Collection<TrieNode<T>> childNodes;
	private Character nodeKey;
	private T nodeValue;
	private boolean terminal;
	private Map<Character, TrieNode<T>> children = new HashMap<Character, TrieNode<T>>();

	public Character getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(Character nodeKey) {
		this.nodeKey = nodeKey;
	}

	public T getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(T nodeValue) {
		this.nodeValue = nodeValue;
	}

	public boolean isTerminal() {
		return terminal;
	}

	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
	}

	public Map<Character, TrieNode<T>> getChildren() {
		return children;
	}

	public void setChildren(Map<Character, TrieNode<T>> children) {
		this.children = children;
	}

	public Collection<TrieNode<T>> getChildNodes() {
		return children.values();
	}
}
