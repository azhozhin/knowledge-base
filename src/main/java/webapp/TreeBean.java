package webapp;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import config.ConfigLoader;

import utils.EntityHolder;
import utils.Utils;
import utils.WebAppHelper;

import dao.DAO;
import domain.Article;
import domain.Section;

/**
 * TreeBean represents elements binding to JSF page 
 */

@SessionScoped
@ManagedBean
public class TreeBean implements Serializable {
	
	Logger logger = LoggerFactory.getLogger(TreeBean.class);

	private static final long serialVersionUID = 1L;

	private TreeNode treeRoot;
	private Section rootSection;

	private boolean articleRendered;
	private Section currentSection;
	private Section newSection;
	private Article currentArticle;
	private Article newArticle;
	
	private TreeNode selectedNode;
	
	private String searchString="";
	private List<Article> searchResult=new ArrayList<>();
	
	private String breadCrumb="";
	
	private List<Section> thisSectionSubSections;
	private List<Article> thisSectionArticles;
	
	public TreeBean() {
		reloadTree();
		newSection=new Section("");
		newArticle=new Article("");
	}
	
	public void reloadTree(){
		
		DAO.getInstance().beginTransaction();
		rootSection= DAO.getInstance().loadSectionRootLevel();
		DAO.getInstance().commitTransaction();
		
		treeRoot = Utils.makeTreeRoot(rootSection);
		
		if (rootSection==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cannot load any section","There is no section to load"));
		}else{
			Utils.loadNodeSection(treeRoot, rootSection);
		}
	}  

	public void onNodeSelect(NodeSelectEvent event) {
		Section currentNodeSection;
		
		TreeNode treeNode=event.getTreeNode();
		EntityHolder entityHolder=(EntityHolder)treeNode.getData();
		
		if (entityHolder.getType()=="section"){
			currentNodeSection=(Section)entityHolder.getRef();
			
			// we have no child nodes, lets try load some
			if (treeNode.getChildCount()==0){
				DAO.getInstance().beginTransaction();
				DAO.getInstance().updateSection(rootSection);
				currentNodeSection=Utils.findPersistentSection(rootSection,currentNodeSection);
				currentNodeSection=DAO.getInstance().loadSectionOneLevel(currentNodeSection);
				DAO.getInstance().commitTransaction();
				
				Utils.loadNodeSection(event.getTreeNode(), currentNodeSection);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Loaded new content"));
				
			}
			
			articleRendered=false;
			currentSection=currentNodeSection;
			currentArticle=null;
			thisSectionArticles=currentNodeSection.getArticles();
			thisSectionSubSections=currentNodeSection.getSections();
		}else{
			Article currentNodeArticle=(Article)entityHolder.getRef();
			currentNodeSection=currentNodeArticle.getSection();

			DAO.getInstance().beginTransaction();
			currentArticle=DAO.getInstance().loadArticle(currentNodeArticle);
			DAO.getInstance().commitTransaction();
			
			articleRendered=true;
			currentSection=null;
			thisSectionArticles=null;
			thisSectionSubSections=null;
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Loaded article"));
		}
		
	}
	
	public void saveArticle(){
		WebAppHelper.saveArticle(treeRoot, rootSection, currentArticle);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Article saved"));
	}  
	
	public void saveSection(){
		WebAppHelper.saveSection(treeRoot, rootSection, currentSection);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Section saved"));
	}
	
	public void deleteSection(){
		if (selectedNode!=null){
			EntityHolder eh=(EntityHolder)selectedNode.getData();
			if (eh.getType()=="section"){
				selectedNode = selectedNode.getParent();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Section deleted"+currentSection));
				WebAppHelper.deleteSection(treeRoot, rootSection, currentSection);
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select section"));
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select anything"));
		}
	}
	
	public void newSectionSameLevel(){
		if (selectedNode!=null){
			EntityHolder eh=(EntityHolder)selectedNode.getData();
			if (eh.getType()=="section"){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Section created"));
				WebAppHelper.insertSection(treeRoot, rootSection, currentSection, newSection, false);

			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select section"));
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select anything"));
		}
	}
	
	public void newSectionSubling(){
		if (selectedNode!=null){
			EntityHolder eh=(EntityHolder)selectedNode.getData();
			if (eh.getType()=="section"){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Section created"));
				WebAppHelper.insertSection(treeRoot, rootSection, currentSection, newSection, true);
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select section"));
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select anything"));
		}
	}
	
	public void saveNewArticle(){
		if (selectedNode!=null){
			EntityHolder eh=(EntityHolder)selectedNode.getData();
			if (eh.getType()=="section"){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Article created"));
				WebAppHelper.insertArticle(treeRoot, rootSection, currentSection, newArticle);
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selected wrong type"));
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select anything"));
		}
	}

	public void deleteArticle(){
		if (selectedNode!=null){
			EntityHolder eh=(EntityHolder)selectedNode.getData();
			if (eh.getType()=="article"){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Section deleted"+currentArticle));
				WebAppHelper.deleteArticle(treeRoot, rootSection, currentArticle);
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select article"));
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You should select anything"));
		}
	}

	public void upload(FileUploadEvent event) { 
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(event.getFile().getFileName() + " is uploaded."));
		logger.info(event.getFile().getFileName() + " is uploaded.");
		ConfigLoader configLoader;
		try {
			configLoader = new ConfigLoader();
			Section newRootSection=configLoader.parseFromFile(event.getFile().getFileName());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data sucessfully parsed"));
			logger.info("Successfully parsed");
			
			DAO.getInstance().beginTransaction();
			DAO.getInstance().removeAllSections();
			DAO.getInstance().saveSection(newRootSection);
			DAO.getInstance().commitTransaction();
			
			reloadTree();
			
		} catch (ParserConfigurationException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exception! ", "ParserConfigurationException"));
			logger.info(e.toString());
		} catch (SAXException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exception! ", "SAXException"));
			logger.info(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exception! ", "IOException"));
			logger.info(e.toString());
			e.printStackTrace();
		}
		
	}
	
	public void reindex(){
		try{
			DAO.getInstance().beginTransaction();
			DAO.getInstance().reindex();
			DAO.getInstance().commitTransaction();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Reindexing started"));
		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exception!", "Exception"));
			logger.info(e.toString());
		}
	}
	
	public void search(){
		try{
			searchResult=DAO.getInstance().articleFullTextSearch(searchString);
			logger.info(searchString);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Search done."));
		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exception!", "Exception"));
			logger.info(e.toString());
		}
	}
	
	public TreeNode getRoot() {
		return treeRoot;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	public String getBreadCrumb() {
		if (selectedNode==null){
			return "";
		}
		
		EntityHolder eh=(EntityHolder)selectedNode.getData();
		breadCrumb=Utils.makeBreadCrumb(eh);
		
		return breadCrumb;
	}

	public void setBreadCrumb(String breadCrumb) {
		this.breadCrumb = breadCrumb;
	}

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}

	public boolean isArticleRendered() {
		return articleRendered;
	}

	public void setArticleRendered(boolean articleRendered) {
		this.articleRendered = articleRendered;
	}

	public Section getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(Section currentSection) {
		this.currentSection = currentSection;
	}

	public Section getNewSection() {
		return newSection;
	}

	public void setNewSection(Section newSection) {
		this.newSection = newSection;
	}

	public Article getNewArticle() {
		return newArticle;
	}

	public void setNewArticle(Article newArticle) {
		this.newArticle = newArticle;
	}

	public List<Section> getThisSectionSubSections() {
		return thisSectionSubSections;
	}

	public void setThisSectionSubSections(List<Section> thisSectionSubSections) {
		this.thisSectionSubSections = thisSectionSubSections;
	}

	public List<Article> getThisSectionArticles() {
		return thisSectionArticles;
	}

	public void setThisSectionArticles(List<Article> thisSectionArticles) {
		this.thisSectionArticles = thisSectionArticles;
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public List<Article> getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(List<Article> searchResult) {
		this.searchResult = searchResult;
	}
}
