package webapp;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import utils.Link;

import dao.SectionDAO;
import domain.Article;
import domain.Section;

@SessionScoped
@ManagedBean
public class TreeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private TreeNode root;

	private TreeNode selectedNode;

	public TreeBean() {
		root = new DefaultTreeNode("Root", null);
		SectionDAO.beginTransaction();
		Section rootSection= SectionDAO.load();
		SectionDAO.commitTransaction();
		recursiveLoad(root, rootSection);
	}
	
	private void recursiveLoad(TreeNode node, Section sect){
		for (Section s:sect.getSections()){
			TreeNode n=new DefaultTreeNode(s.getShortName(),node);
			recursiveLoad(n, s);
		}
		for (Article a:sect.getArticles()){
			TreeNode n=new DefaultTreeNode(a.getShortName(),node);
		}
	}

	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void onNodeExpand(NodeExpandEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void onNodeSelect(NodeSelectEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());

		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
