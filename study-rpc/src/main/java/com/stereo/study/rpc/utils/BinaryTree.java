package com.stereo.study.rpc.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * B树 算法
 * 
 * @author stereo
 * 
 * @param <T>
 */
public class BinaryTree<T extends Comparable<T>> {
	class Node {
		T data;
		Node left;
		Node right;

		Node(T data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			return data.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return data.equals(obj);
		}
	}

	Node root = null;

	public BinaryTree(T t) {
		root = new Node(t);
	}

	public void insert(T data) {
		if (root == null)
			root = new Node(data);
		else
			insert(root, data);
	}

	public void insert(Node node, T data) {
		if (node.data.compareTo(data) == 1 || node.data.compareTo(data) == 0) {
			if (node.left == null)
				node.left = new Node(data);
			else
				insert(node.left, data);
		} else {
			if (node.right == null)
				node.right = new Node(data);
			else
				insert(node.right, data);
		}
	}

	public List<T> preorder() {
		if (root != null) {
			List<T> list = new ArrayList<T>();
			preorder(root, list);
			return list;
		}
		return null;
	}

	public void preorder(Node node, List<T> list) {
		if (node != null) {
			list.add(node.data);
			preorder(node.left, list);
			preorder(node.right, list);
		}
	}

	public List<T> inorder() {
		if (root != null) {
			List<T> list = new ArrayList<T>();
			inorder(root, list);
			return list;
		}
		return null;
	}

	public void inorder(Node node, List<T> list) {
		if (node != null) {
			inorder(node.left, list);
			list.add(node.data);
			inorder(node.right, list);
		}
	}

	public List<T> postorder() {
		if (root != null) {
			List<T> list = new ArrayList<T>();
			postorder(root, list);
			return list;
		}
		return null;
	}

	public void postorder(Node node, List<T> list) {
		if (node != null) {
			postorder(node.left, list);
			postorder(node.right, list);
			list.add(node.data);
		}
	}

	public T searchMin() {
		Node node = root;
		while (node.left != null)
			node = node.left;
		return node.data;
	}

	public boolean exist(T data) {
		return exist(root, data);
	}

	public boolean exist(Node node, T data) {
		boolean isexist = false;
		if (node.equals(data)) {
			isexist = true;
		} else {
			if (node.data.compareTo(data) == 1
					|| node.data.compareTo(data) == 0)
				isexist = exist(node.left, data);
			else
				isexist = exist(node.right, data);
		}
		return isexist;
	}

	static class IntEntry implements Comparable<IntEntry> {

		public int data;

		IntEntry(int data) {
			this.data = data;
		}

		@Override
		public int compareTo(IntEntry o) {
			if (data > o.data)
				return 1;
			else if (data < o.data)
				return -1;
			else
				return 0;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this.data == ((IntEntry) obj).data)
				return true;
			return false;
		}

		@Override
		public String toString() {
			return String.valueOf(data);
		}
	}

	public static void main(String[] args) {
		Random random = new Random();
		IntEntry entry = new IntEntry(1000);
		BinaryTree<IntEntry> binaryTree = new BinaryTree<IntEntry>(entry);
		for (int i = 0; i < 1000; i++) {
			binaryTree.insert(new IntEntry(random.nextInt(Integer.MAX_VALUE)));
		}
		System.err.println(binaryTree.exist(entry));
		System.err.println(binaryTree.preorder());
		System.err.println(binaryTree.inorder());
		System.err.println(binaryTree.postorder());
	}
}