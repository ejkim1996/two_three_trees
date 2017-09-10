import java.io.*;
import java.util.*;

public class Fords_Dilemma {

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner input = new Scanner(System.in);
        int numOfInput = input.nextInt();
        TwoThreeTree tree = new TwoThreeTree();        
        
        for (int i = 0; i < numOfInput + 1; i++) {
            String query = input.nextLine();
            if (query.length() == 0) {
                continue;
            }
            String[] data = query.split(" ");
            
            tree.insert(data[0], Integer.parseInt(data[1]), tree.root, tree.height);
        }
        
        numOfInput = input.nextInt();
        for (int i = 0; i < numOfInput + 1; i++) {
            String query = input.nextLine();
            if (query.length() == 0) {
                continue;
            }
            String[] data = query.split(" ");
            String smaller = "";
            String bigger = "";
            if (data[0].compareTo(data[1]) < 0) {
                smaller = data[0];
                bigger = data[1];
            }
            else {
                smaller = data[1];
                bigger = data[0];
            }
            printRange(smaller, bigger, tree.root, tree.height);
        }
        
    }
    
    private static void printRange(String smaller, String bigger, Node node, int height) {
    	if (height > 0) {
    		InternalNode p = (InternalNode)(node);
    		if (p.child2 != null && smaller.compareTo(p.child2.guide) > 0) {
    			;
    		}
    		else if (smaller.compareTo(p.child1.guide) > 0) {
    			if (p.child2 != null) {
        			printRange(smaller, bigger, p.child2, height - 1);
        		}
    		}
    		else if (smaller.compareTo(p.child0.guide) > 0) {
    			printRange(smaller, bigger, p.child1, height - 1);
    			if (p.child2 != null) {
        			printRange(smaller, bigger, p.child2, height - 1);
        		}
    		}
    		else if (bigger.compareTo(p.child1.guide) <= 0) {
    			printRange(smaller, bigger, p.child0, height - 1);
    			printRange(smaller, bigger, p.child1, height - 1);
    		}
    		else if (bigger.compareTo(p.child0.guide) <= 0) {
    			printRange(smaller, bigger, p.child0, height - 1);
    		}
    		else {
    			printRange(smaller, bigger, p.child0, height - 1);
        		printRange(smaller, bigger, p.child1, height - 1);
        		if (p.child2 != null) {
        			printRange(smaller, bigger, p.child2, height - 1);
        		}
    		}
    	}
		else {
			LeafNode p = (LeafNode)(node);
			if (smaller.compareTo(p.guide) <= 0 && bigger.compareTo(p.guide) >= 0) {
				System.out.println(p.guide + " " + p.value);
			}
		}
    }
}

class Node {
	String guide;
	// guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
	Node child0, child1, child2;
	// child0 and child1 are always non-null
	// child2 is null iff node has only 2 children
}

class LeafNode extends Node {
	// guide points to the key
	int value;
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
				root = newRoot;
			}
			else {
				newRoot.child0 = root;
				newRoot.child1 = newNode;
				root = newRoot;
			}
			updateGuide(newRoot);
			this.height++;
			return nodeArray;
		}

		if (height == 1) {
			InternalNode p = (InternalNode)(node);
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
				nodeArray[1] = null;
				return nodeArray;
			}
		}

		else {
			//insert key value pair in appropriate child
			InternalNode p = (InternalNode)(node);
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
				Node[] arrayToReturn = new Node[2];
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
						updateGuide(newParent);
						root = newParent;
						updateGuide(root);
						this.height++;
					}
					arrayToReturn[0] = n1;
					arrayToReturn[1] = n2;
					return arrayToReturn;
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
					arrayToReturn[0] = newParent;
					arrayToReturn[1] = null;
					return arrayToReturn;
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
				nodeArray[1] = null;
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
}