/*
 * Author: Aron Sajan Philip
 **/



import java.util.LinkedList;
import java.util.Queue;

public class AVLTreeImplementation {

	public static void main(String[] args) {
		try
		
		{

		AVLTree obj = new AVLTree();
		obj.insert(100);
		obj.insert(40);
		obj.insert(500);
		obj.insert(29);

		obj.BFSDisplay();
		obj.insert(30);
		obj.BFSDisplay();
                obj.insert(292);
		obj.insert(1200);
		obj.insert(1310);
		obj.BFSDisplay();
		obj.insert(1375);
		obj.BFSDisplay();
		obj.insert(1220);
		obj.BFSDisplay();
		obj.insert(1800);
		obj.BFSDisplay();
                obj.DeleteNode(1220);
		obj.BFSDisplay();
		obj.insert(2000);
		obj.insert(35);
		obj.BFSDisplay();
		obj.DeleteNode(1375);
		obj.BFSDisplay();
	
		}
		catch(Exception Ex)
		{
			System.out.println("Exception : "+Ex.getMessage());
		}
		
		
		

	
	}

}

class AVLTree {
	Node root;

	private enum BalanceCase {
		LeftOfLeft, RightOfLeft, LeftOfRight, RightOfRight
	}
	
	
	public void BFSDisplay()
	{
		Queue<Node> queue=new LinkedList<Node>();
		queue.add(root);
		while(!queue.isEmpty())
		{
		  Node currentNode=queue.remove();
		  System.out.print(currentNode.data+" ");
		  if(currentNode.left!=null)
		  {
			  queue.add(currentNode.left);
		  }
		  
		  if(currentNode.right!=null)
		  {
			  queue.add(currentNode.right);
		  }
		  
		}

		System.out.println("");
	}

	public void insert(int data) {
		Node newNode = new Node();
		newNode.data = data;
		Node current = root;
		Node prev = null;
		if (current == null) {
			root = newNode;
		} else {
			while (current != null) {
				if (data < current.data) {
					prev = current;
					current = current.left;
					if (current == null) {
						prev.left = newNode;

					}
				} else if (data > current.data) {
					prev = current;
					current = current.right;
					if (current == null) {
						prev.right = newNode;

					}
				}

				newNode.parent = prev;
			}
		}

		/*
		 * Check tree balance if delta(left subtree, rightSubtree)>1 then
		 * balance the tree
		 */

		Node ImbalanceNode = treeImbalanceAtNode(newNode.parent);
		
		if (ImbalanceNode != null) {
			// Balance tree using rotation
			System.out.println("Imbalance at node " + ImbalanceNode.data + " after inserting new node " + newNode.data);
			System.out.println("Re-balancing the tree...");
			BalanceSubtree(ImbalanceNode, newNode);

		}

	}
	
	
	public void DeleteNode(int key)throws Exception
	{
		Node targetNode = getNode(key);
		Node ImbalanceSuspectedNode=null;
		if(targetNode==null)
		{
			throw new Exception("Key value "+key+" not found");
		}
		else
		{
			//Deletion of root node(which has no children) or leaf node
			if((targetNode.left==null)&&(targetNode.right==null))
			{
				ImbalanceSuspectedNode=targetNode.parent;
					switch(targetNode.getChildType())
					{
						case LeftChild:
							targetNode.parent.left=null;
						break;
						case RightChild:
							targetNode.parent.right=null;
							break;
						case NotApplicable:
							root=null;
							break;
					}

			}
			else
			{
				
				//Case when there is a predecessor
							
				if(targetNode.left!=null)
				{
					Node pred=targetNode.getPredecessor();
				
					switch(targetNode.getChildType())
					{
					case LeftChild:
						targetNode.parent.left=pred;
						break;
					case RightChild:
						targetNode.parent.right=pred;
						break;
					case NotApplicable:
						root=pred;
						break;
					}
					
					if(pred!=targetNode.left)
					{
						ImbalanceSuspectedNode=pred.parent;
						switch (pred.getChildType()) {
						case LeftChild:
							pred.parent.left=pred.left;
							break;

						case RightChild:
							pred.parent.right=pred.left;
							break;
						
						}
						
						if(pred.left!=null)
						{
							pred.left.parent=pred.parent;
						}
						pred.left=targetNode.left;
						targetNode.left.parent=pred;
					}
					else
					{
						ImbalanceSuspectedNode=pred;
					}
					
					pred.parent=targetNode.parent;
					
					pred.right=targetNode.right;
					if(targetNode.right!=null)
					{
						targetNode.right.parent=pred;
					}
					
					

				}
				else
				{
					//Case when there is no predecessor but a right child
					switch(targetNode.getChildType())
					{
					case LeftChild: targetNode.parent.left=targetNode.right;
					break;
					case RightChild: targetNode.parent.right=targetNode.right;
					break;
					case NotApplicable: root=root.right;
					
					}
					targetNode.right.parent=targetNode.parent;
					ImbalanceSuspectedNode=targetNode.parent;
				}
				
			}
			
				
		}
		
		/*
		 * Re-balancing the tree if necessary after deletion. The method checks for 
		 * imbalances till the root node and re-balances the tree (if necessary) in every case
		 */
		
		int balanceCount=0;
		
		while(ImbalanceSuspectedNode!=null)
		{
			
			Node Parent=ImbalanceSuspectedNode.parent;
			Node ImbalanceNode=treeImbalanceAtNode(ImbalanceSuspectedNode);
			if(ImbalanceNode!=null)
			{
				++balanceCount;
				System.out.println("Balancing tree at node "+ImbalanceNode.data+" following deletion of node "+key);
				Node HighestDepthLeaf = ImbalanceNode.getLeafAtHighestDepth();
				BalanceSubtree(ImbalanceNode,HighestDepthLeaf);
			}
			
			ImbalanceSuspectedNode=Parent;
		}
		
		if(balanceCount>0)
		{
			System.out.println("Tree balanced after "+balanceCount+" Tree-Balance operations following deletion of node "+key);
		}
	}
	
	
	private Node getNode(int key)
	{
		
		Node current=root;
		while(current!=null)
		{
			if(current.data==key)
			{
				return current;
			}
			
			if(key<current.data)
			{
				current=current.left;
			}
			
			if(key>current.data)
			{
				current=current.right;
			}
		}
		
		throw null;
	}

	private void BalanceSubtree(Node UnbalancedNode, Node newNode) {

		try {
			BalanceCase balancType = IdentifyBalanceCase(UnbalancedNode, newNode);

			switch (balancType) 
			{
			case LeftOfLeft:

				// Requires single right rotation
				
				//Left child of the unbalanced node
				Node leftChild=UnbalancedNode.left;
				
				switch(UnbalancedNode.getChildType())
				{
					case LeftChild:
					
						//Connecting the parent of unbalanced node to its left child
						UnbalancedNode.parent.left=leftChild;
						leftChild.parent=UnbalancedNode.parent;
						
						
					break;
					case RightChild:
					
						//Connecting the parent of unbalanced node to its left child
						UnbalancedNode.parent.right=leftChild;
						leftChild.parent=UnbalancedNode.parent;
						
						
					break;
					case NotApplicable:
						
						
						leftChild.parent=null;
						root=leftChild;
						
					
					break;
				}
				
				//Right child of the left child of the unbalanced node (can be null)
				Node childRightChild=leftChild.right;
				
				//Unbalanced node will become the right child of its left child
				leftChild.right=UnbalancedNode;
				UnbalancedNode.parent=leftChild;
				
				//The right child of the left child of the unbalanced node will become the left child of the unbalanced node
				UnbalancedNode.left=childRightChild;
				if(childRightChild!=null)
				{
					childRightChild.parent=UnbalancedNode;
				}
				

				break;
			case RightOfLeft:

				// Requires double rotation
				
				
				// ---- 1st Rotation towards left ---- //
				//Left child of the unbalanced node
				Node LC = UnbalancedNode.left;
				
				//Right child of the left child of the unbalanced node
				Node RCLC = LC.right;
				
				//Left Child of the Right Child of the Left Child of the unbalanced node (can be null)
				
				Node LCRCLC = RCLC.left;
				
				
				UnbalancedNode.left=RCLC;
				RCLC.parent=UnbalancedNode;
				
				RCLC.left=LC;
				LC.parent=RCLC;
				
				LC.right=LCRCLC;
				if(LCRCLC!=null)
				{
					LCRCLC.parent=LC;
				}
				
				// --- End of first rotation --- //
				
				//----Second rotation towards right ---- //
				
				LC=UnbalancedNode.left;
				
				//Right child of LC
				RCLC=LC.right;
				
				switch(UnbalancedNode.getChildType())
				{
				case LeftChild:
					
					UnbalancedNode.parent.left=LC;
					LC.parent=UnbalancedNode.parent;
					
					break;
				case RightChild:
					
					UnbalancedNode.parent.right=LC;
					LC.parent=UnbalancedNode.parent;
					
					break;
				case NotApplicable:
					
					LC.parent=null;
					root=LC;
					
					break;
				}
				
		       LC.right=UnbalancedNode;
		       UnbalancedNode.parent=LC;
		       
		       UnbalancedNode.left=RCLC;
		       if(RCLC!=null)
		       {
		    	   RCLC.parent=UnbalancedNode;
		       }
				
				

				break;
			case LeftOfRight:

				// Requires double rotation
				
				// ---- 1st Rotation towards right ---- //
				//Right child of the unbalanced node
				Node RC = UnbalancedNode.right;
				
				//Left child of the right child of the unbalanced node
				Node LCRC = RC.left;
				
				//Right Child of the left Child of the right Child of the unbalanced node (can be null)
				
				Node RCLCRC = LCRC.right;
				
				UnbalancedNode.right=LCRC;
				LCRC.parent=UnbalancedNode;
				
				LCRC.right=RC;
				RC.parent=LCRC;
				
				RC.left=RCLCRC;
				if(RCLCRC!=null)
				{
					RCLCRC.parent=RC;
				}
				
				// --- End of first rotation --- //
				
				//----Second rotation towards left ---- //
				
				RC=UnbalancedNode.right;
				
				//Left child of RC
				LCRC=RC.left;
				
				switch(UnbalancedNode.getChildType())
				{
				case LeftChild:
					
					UnbalancedNode.parent.left=RC;
					RC.parent=UnbalancedNode.parent;
					
					break;
				case RightChild:
					
					UnbalancedNode.parent.right=RC;
					RC.parent=UnbalancedNode.parent;
					
					break;
				case NotApplicable:
					
					RC.parent=null;
					root=RC;
					break;
				}
				
		       RC.left=UnbalancedNode;
		       UnbalancedNode.parent=RC;
		       
		       UnbalancedNode.right=LCRC;
		       if(LCRC!=null)
		       {
		    	   LCRC.parent=UnbalancedNode;
		       }
				

				break;
			case RightOfRight:

				// Requires single left-rotation
				
				//Right Child of the unbalanced node
				Node rightChild=UnbalancedNode.right;
				
				
				switch (UnbalancedNode.getChildType()) 
				{
					case LeftChild:
						

						//Connecting parent of the unbalanced node to its right child
						UnbalancedNode.parent.left=rightChild;
						rightChild=UnbalancedNode.parent;

					break;
					case RightChild:
						
						//Connecting parent of the unbalanced node to its right child
						UnbalancedNode.parent.right=rightChild;
						rightChild.parent=UnbalancedNode.parent;
						
						
					break;
					case NotApplicable:
											
						//Setting the parent of the right child of the unbalanced node to null since unbalanced node is the root
						rightChild.parent=null;

						//Making the root as the right child of the unbalanced node
						root=rightChild;
						

					break;
				}
				
				
				//Left Child of the right child (can be null)
				Node childLeftChild=rightChild.left; 
				
				//Making unbalanced node the left subtree of its right child
				rightChild.left=UnbalancedNode;
				UnbalancedNode.parent=rightChild;
				
				
				//Left Child of the right child becomes the right subtree of the unbalanced node
				UnbalancedNode.right=childLeftChild;
				if(childLeftChild!=null)
				{
					childLeftChild.parent=UnbalancedNode;
				}
			
				
				break;
			}

		} catch (Exception Ex) {
			System.out.println("Exception : " + Ex.getMessage());
		}

	}

	private BalanceCase IdentifyBalanceCase(Node UnbalancedNode, Node newNode) throws Exception {
		/*
		 * 1.) Check which of the following case of balancing applies
		 * (X=>UnbalancedNode) 
		 * -> An insertion in the left subtree of the left
		 * child of X, 
		 * -> An insertion in the right subtree of the left child of
		 * X, 
		 * -> An insertion in the left subtree of the right child of X, or 
		 * -> An insertion in the right subtree of the right child of X
		 */

		// Case 1 - Left of Left
		boolean isPresent = false;
		if (UnbalancedNode.left != null) {
			isPresent = UnbalancedNode.left.LeftSubtreeContains(newNode);
			if (isPresent) {
				return BalanceCase.LeftOfLeft;
			} else {
				isPresent = UnbalancedNode.left.RightSubtreeContains(newNode);
				if (isPresent) {
					return BalanceCase.RightOfLeft;
				}
			}
		}

		if (UnbalancedNode.right != null) {
			isPresent = UnbalancedNode.right.LeftSubtreeContains(newNode);
			if (isPresent) {
				return BalanceCase.LeftOfRight;
			} else {
				isPresent = UnbalancedNode.right.RightSubtreeContains(newNode);
				if (isPresent) {
					return BalanceCase.RightOfRight;
				}
			}
		}

		if (!isPresent) {
			throw new Exception(newNode.data + " is does not belong to any of the subtrees of " + UnbalancedNode.data);
		}

		return null;

	}

	private Node treeImbalanceAtNode(Node targetNode) {
		if (targetNode == null) {
			return null;
		}
		int LHeight, RHeight;
		LHeight = 0;
		RHeight = 0;
		if (targetNode.left != null) {
			LHeight = targetNode.left.getHeight() + 1;
		}
		if (targetNode.right != null) {
			RHeight = targetNode.right.getHeight() + 1;
		}

		int delta = Math.abs(LHeight - RHeight);
		if (delta > 1) {
			return targetNode;
		} else {
			return treeImbalanceAtNode(targetNode.parent);
		}

	}

}

enum ChildType {
	LeftChild, RightChild, NotApplicable
}

class Node {
	int data;
	Node left, right;
	Node parent;

	public Node getPredecessor() {
		Node predecessor = this.left;
		Node prev = this;
		while (predecessor != null) {
			prev = predecessor;
			predecessor = predecessor.right;
		}
		predecessor = prev;
		return predecessor;
	}

	public int getHeight() {
		int height = 0;
		int LHeight = computeHeight(this.left);
		int RHeight = computeHeight(this.right);
		height = (LHeight > RHeight) ? LHeight : RHeight;
		return height;
	}

	private int computeHeight(Node node) {
		if (node == null) {
			return 0;
		}
		int LHeight = computeHeight(node.left);
		int RHeight = computeHeight(node.right);
		int height = (LHeight > RHeight) ? LHeight : RHeight;
		return (1 + height);
	}

	public ChildType getChildType() {
		ChildType type = null;
		if (this.parent != null) {
			if (this.data == this.parent.left.data) {
				type = ChildType.LeftChild;
			} else {
				type = ChildType.RightChild;
			}
		} else {
			type = ChildType.NotApplicable;
		}

		return type;
	}

	public boolean LeftSubtreeContains(Node targetNode) {
		return subtreeHasNode(this.left, targetNode);
	}

	public boolean RightSubtreeContains(Node targetNode) {
		return subtreeHasNode(this.right, targetNode);
	}

	private boolean subtreeHasNode(Node subtreeNode, Node targetNode) {
		if (subtreeNode == null) {
			return false;
		}

		if (subtreeNode.data == targetNode.data) {
			return true;
		}

		boolean nodePresent = subtreeHasNode(subtreeNode.left, targetNode);
		if (!nodePresent) {
			nodePresent = subtreeHasNode(subtreeNode.right, targetNode);
		}

		return nodePresent;

	}
	
	public Node getLeafAtHighestDepth()
	{
		HighestDepthLeafInfo info=highestDepthLeaf(this);
		return info.Leaf;
	}
	
	private HighestDepthLeafInfo highestDepthLeaf(Node node)
	{
		HighestDepthLeafInfo info;
		
		if(node==null)
		{
			info=new HighestDepthLeafInfo();
			info.height=0;
			return info;
		}
		
		if((node.left==null)&&(node.right==null))
		{
			info=new HighestDepthLeafInfo();
			info.height=1;
			info.Leaf=node;
			return info;
		}
		
		HighestDepthLeafInfo LInfo=highestDepthLeaf(node.left);
		HighestDepthLeafInfo RInfo=highestDepthLeaf(node.right);
		info=(LInfo.height>RInfo.height)?LInfo:RInfo;
		info.height++;
		return info;
		
	}

}


class HighestDepthLeafInfo
{
	int height;
	Node Leaf;
}
