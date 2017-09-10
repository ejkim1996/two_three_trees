import java.io.*;
import java.util.*;

public class Hijacking_the_Fees {

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner input = new Scanner(System.in);
        int numOfInput = input.nextInt();
        TwoThreeTree tree = new TwoThreeTree();        
        
        for (int i = 0; i < numOfInput + 1; i++) {
			String query = input.nextLine();
			String[] data = query.split(" ");

			if (data[0].equals("1")) {
				tree.insert(data[1], Integer.parseInt(data[2]), tree.root, tree.height);
			}
			else if (data[0].equals("2")) {
				String smaller = "";
	            String bigger = "";
	            if (data[1].compareTo(data[2]) < 0) {
	                smaller = data[1];
	                bigger = data[2];
	            }
	            else {
	                smaller = data[2];
	                bigger = data[1];
	            }
	            addRange(smaller, bigger, tree.root, tree.height, Integer.parseInt(data[3]));
			}
			else if (data[0].equals("3")) {
				System.out.println(search(data[1], tree.root, tree.height));
			}
		}
        input.close();
    }
    
    private static int search(String key, Node node, int height) {
		if (height > 0) {
			int returned;
			InternalNode p = (InternalNode)(node);
			if (key.compareTo(p.child0.guide) <= 0) {
				returned = search(key, p.child0, height - 1);
			}
			else if (key.compareTo(p.child1.guide) <= 0 || p.child2 == null) {
				returned = search(key, p.child1, height - 1);
			}
			else {
				returned = search(key, p.child2, height - 1);
			}
			
			if (returned == -1) {
				return -1;
			}
			else {
				return returned + p.value;
			}
		}
		else {
			LeafNode p = (LeafNode)(node);
			if (key.equals(p.guide)) {
				return p.value;
			}
			else {
				return -1;
			}
		}
	}
    
    private static void addRange(String smaller, String bigger, Node node, int height, int increment) {
    	if (height > 0) {
			Node leftChild = node;
			Node rightChild = node;
			while(!(leftChild instanceof LeafNode)) {
				leftChild = ((InternalNode)leftChild).child0;
			}
			while(!(rightChild instanceof LeafNode)) {
				if(((InternalNode)rightChild).child2 == null) {
					rightChild = ((InternalNode)rightChild).child1;
				}
				else {
					rightChild = ((InternalNode)rightChild).child2;
				}
			}
			if(leftChild.guide.compareTo(smaller) >= 0 && rightChild.guide.compareTo(bigger) <= 0) {
				node.value += increment;
				return;
			}
    		InternalNode p = (InternalNode)(node);
    		if (p.child2 != null && smaller.compareTo(p.child2.guide) > 0) {
    			;
    		}
    		else if (smaller.compareTo(p.child1.guide) > 0) {
    			if (p.child2 != null) {
        			addRange(smaller, bigger, p.child2, height - 1, increment);
        		}
    		}
    		else if (smaller.compareTo(p.child0.guide) > 0) {
    			addRange(smaller, bigger, p.child1, height - 1, increment);
    			if (p.child2 != null) {
        			addRange(smaller, bigger, p.child2, height - 1, increment);
        		}
    		}
    		else if (bigger.compareTo(p.child1.guide) <= 0) {
    			addRange(smaller, bigger, p.child0, height - 1, increment);
    			addRange(smaller, bigger, p.child1, height - 1, increment);
    		}
    		else if (bigger.compareTo(p.child0.guide) <= 0) {
    			addRange(smaller, bigger, p.child0, height - 1, increment);
    		}
    		else {
    			addRange(smaller, bigger, p.child0, height - 1, increment);
        		addRange(smaller, bigger, p.child1, height - 1, increment);
        		if (p.child2 != null) {
        			addRange(smaller, bigger, p.child2, height - 1, increment);
        		}
    		}
    	}
		else {
			LeafNode p = (LeafNode)(node);
			if (smaller.compareTo(p.guide) <= 0 && bigger.compareTo(p.guide) >= 0) {
				p.value += increment;
			}
		}
    }
}

class Node {
	String guide;
	int value;
	// guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
	Node child0, child1, child2;
	// child0 and child1 are always non-null
	// child2 is null iff node has only 2 children
}

class LeafNode extends Node {
	// guide points to the key
	//	int added;
}

class TwoThreeTree {
	Node root;
	int height;

	public Node[] insert(String key, int value, Node node, int height) {
		Node[] nodeArray = new Node[2];
		if (node == null) {
			LeafNode newNode = new LeafNode();
			newNode.guide = key;
			newNode.value = value;
			root = newNode;
			this.height = 0;
			return nodeArray;
		}
		if (height == 0) {
			LeafNode newNode = new LeafNode();
			newNode.guide = key;
			newNode.value = value;
			InternalNode newRoot = new InternalNode();
			if (key.compareTo(root.guide) <= 0) {
				newRoot.child0 = newNode;
				newRoot.child1 = root;
			}
			else {
				newRoot.child0 = root;
				newRoot.child1 = newNode;
			}
			root = newRoot;
			updateGuide(root);
			this.height++;
			return nodeArray;
		}

		if (height == 1) {
			InternalNode p = (InternalNode)(node);
			addToChild(p);
			if (p.child2 != null) {
				InternalNode n1 = new InternalNode();
				InternalNode n2 = new InternalNode();
				LeafNode newLeaf = new LeafNode();
				newLeaf.guide = key;
				newLeaf.value = value;

				if (newLeaf.guide.compareTo(p.child0.guide) <= 0) {
					n1.child0 = newLeaf;
					n1.child1 = p.child0;
					n2.child0 = p.child1;
					n2.child1 = p.child2;
				}
				else if (newLeaf.guide.compareTo(p.child1.guide) <= 0) {
					n1.child0 = p.child0;
					n1.child1 = newLeaf;
					n2.child0 = p.child1;
					n2.child1 = p.child2;
				}
				else if (newLeaf.guide.compareTo(p.child2.guide) <= 0) {
					n1.child0 = p.child0;
					n1.child1 = p.child1;
					n2.child0 = newLeaf;
					n2.child1 = p.child2;
				}
				else {
					n1.child0 = p.child0;
					n1.child1 = p.child1;
					n2.child0 = p.child2;
					n2.child1 = newLeaf;
				}

				updateGuide(n1);
				updateGuide(n2);

				if (p == root) {
					InternalNode newRoot = new InternalNode();
					newRoot.child0 = n1;
					newRoot.child1 = n2;
					root = newRoot;
					updateGuide(root);
					this.height++;
				}

				nodeArray[0] = n1;
				nodeArray[1] = n2;

				return nodeArray;
			}
			else {
				LeafNode newLeaf = new LeafNode();
				newLeaf.guide = key;
				newLeaf.value = value;
				InternalNode newParent = new InternalNode();
				if (newLeaf.guide.compareTo(p.child0.guide) <= 0) {
					newParent.child2 = p.child1;
					newParent.child1 = p.child0;
					newParent.child0 = newLeaf;
				}
				else if (newLeaf.guide.compareTo(p.child1.guide) <= 0) {
					newParent.child2 = p.child1;
					newParent.child1 = newLeaf;
					newParent.child0 = p.child0;
				}
				else {
					newParent.child2 = newLeaf;
					newParent.child1 = p.child1;
					newParent.child0 = p.child0;
				}
				updateGuide(newParent);

				if (p == root) {
					root = newParent;
					updateGuide(root);
				}
				nodeArray[0] = newParent;
//				nodeArray[1] = null;
				return nodeArray;
			}
		}

		else {
			//insert key value pair in appropriate child
			InternalNode p = (InternalNode)(node);
			addToChild(p);
			Node[] returnedArray = new Node[2];
			if (key.compareTo(p.child0.guide) <= 0) {
				returnedArray = insert(key, value, p.child0, height - 1);
			}
			else if (key.compareTo(p.child1.guide) <= 0 || p.child2 == null) {
				returnedArray = insert(key, value, p.child1, height - 1);
			}
			else {
				returnedArray = insert(key, value, p.child2, height - 1);
			}

			if (returnedArray[1] != null) {
//				Node[] arrayToReturn = new Node[2];
				if (p.child2 != null) {
					InternalNode n1 = new InternalNode();
					InternalNode n2 = new InternalNode();

					if (returnedArray[1].guide.compareTo(p.child0.guide) <= 0) {
						n1.child0 = returnedArray[0];
						n1.child1 = returnedArray[1];
						n2.child0 = p.child1;
						n2.child1 = p.child2;
					}
					else if (returnedArray[1].guide.compareTo(p.child1.guide) <= 0) {
						n1.child0 = p.child0;
						n1.child1 = returnedArray[0];
						n2.child0 = returnedArray[1];
						n2.child1 = p.child2;
					}
					else {
						n1.child0 = p.child0;
						n1.child1 = p.child1;
						n2.child0 = returnedArray[0];
						n2.child1 = returnedArray[1];
					}

					updateGuide(n1);
					updateGuide(n2);


					if (p == root) {
						InternalNode newParent = new InternalNode();
						newParent.child0 = n1;
						newParent.child1 = n2;
						root = newParent;
						updateGuide(root);
						this.height++;
					}
					nodeArray[0] = n1;
					nodeArray[1] = n2;
					return nodeArray;
				}
				else {
					InternalNode newParent = new InternalNode();
					if (returnedArray[1].guide.compareTo(p.child0.guide) <= 0) {
						newParent.child2 = p.child1;
						newParent.child0 = returnedArray[0];
						newParent.child1 = returnedArray[1];
					}
					else {
						newParent.child0 = p.child0;
						newParent.child1 = returnedArray[0];
						newParent.child2 = returnedArray[1];
					}
					updateGuide(newParent);

					if (p == root) {
						root = newParent;
						updateGuide(root);
					}
					nodeArray[0] = newParent;
//					nodeArray[1] = null;
					return nodeArray;
				}
			}
			else {
				InternalNode newParent = new InternalNode();
				if (p.child2 != null) {
					if (returnedArray[0].guide.compareTo(p.child0.guide) <= 0) {
						newParent.child0 = returnedArray[0];
						newParent.child1 = p.child1;
						newParent.child2 = p.child2;
					}
					else if (returnedArray[0].guide.compareTo(p.child1.guide) <= 0) {
						newParent.child0 = p.child0;
						newParent.child1 = returnedArray[0];
						newParent.child2 = p.child2;
					}
					else {
						newParent.child0 = p.child0;
						newParent.child1 = p.child1;
						newParent.child2 = returnedArray[0];
					}

				}
				else {
					if (returnedArray[0].guide.compareTo(p.child0.guide) <= 0) {
						newParent.child0 = returnedArray[0];
						newParent.child1 = p.child1;
					}
					else {
						newParent.child0 = p.child0;
						newParent.child1 = returnedArray[0];
					}
				}
				updateGuide(newParent);

				if (p == root) {
					root = newParent;
					updateGuide(root);
				}

				nodeArray[0] = newParent;
//				nodeArray[1] = null;
				return nodeArray;
			}
		}
	}

	private void updateGuide(Node node) {
		InternalNode p = (InternalNode)(node);
		if (p.child2 != null) {
			p.guide = p.child2.guide;
		}
		else {
			p.guide = p.child1.guide;
		}
	}
	
	private void addToChild(Node node) {
		InternalNode p = (InternalNode)node;
		int added = p.value;
		p.child0.value += added;
		p.child1.value += added;
		if(p.child2 != null) {
			p.child2.value += added;
		}
	}
}