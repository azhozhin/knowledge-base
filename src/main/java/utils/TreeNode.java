package utils;
import java.util.ArrayList;
import java.util.List;


class TreeNode<T>{
		private T data;
		private List<TreeNode<T>> children;
		private TreeNode<T> parent;
		
		public TreeNode(T rootData){
			this.data=rootData;
			this.children=new ArrayList<TreeNode<T>>();
			this.parent=null;
		}
		
		public T getData(){
			return data;
		}
		public void setData(T data){
			this.data=data;
		}
		
		public void addChild(TreeNode<T> child){
			child.parent=this;
			children.add(child);
		}
		public void addItemAsChild(T child){
			TreeNode<T> newChild=new TreeNode(child);
			newChild.parent=this;
			children.add(newChild);
		}
		
		public List<TreeNode<T>> getChildren(){
			return children;
		}
		public void setChildren(List<TreeNode<T>> children){
			this.children=children;
		}
		
}
