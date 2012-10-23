package utils;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import dao.DAO;
import domain.Article;
import domain.Section;

public class WebAppHelper {

	public static void saveArticle(TreeNode rootTreeNode, Section rootSection, Article article){
		// update tree
		TreeNode currentTreeNode=Utils.findArticleInTree(rootTreeNode, article);
		EntityHolder eh=(EntityHolder)currentTreeNode.getData();
		eh.setShortName(Utils.formatArticleTitle(article));
		
		// update in db
		DAO.beginTransaction();
		DAO.updateArticle(article);
		DAO.commitTransaction();
	}
	
	public static void saveSection(TreeNode rootTreeNode, Section rootSection, Section section){
		// update tree
		TreeNode currentTreeNode=Utils.findSectionInTree(rootTreeNode, section);
		EntityHolder eh=(EntityHolder)currentTreeNode.getData();
		eh.setShortName(Utils.formatSectionTitle(section));

		// update in db
		DAO.beginTransaction();
		DAO.updateSection(rootSection);
		DAO.updateSection(section);
		DAO.commitTransaction();
	}
	
	public static void deleteArticle(TreeNode rootTreeNode, Section rootSection, Article article){
		// Remove section from tree
		TreeNode currentTreeNode=Utils.findArticleInTree(rootTreeNode, article);
		TreeNode parentTreeNode=currentTreeNode.getParent();
		parentTreeNode.getChildren().remove(currentTreeNode);

		// Remove section from db
		DAO.beginTransaction();
		DAO.updateSection(rootSection);
		Article currentPersistentArticle=Utils.findPersistentArticle(rootSection,article);
		Section persistentParent=currentPersistentArticle.getSection();
		persistentParent.removeArticle(currentPersistentArticle);
		DAO.saveSection(persistentParent);
		DAO.deleteArticle(currentPersistentArticle);
		DAO.commitTransaction();
	}

	public static void deleteSection(TreeNode treeRoot, Section rootSection, Section section){
		// Remove section from tree
		TreeNode currentTreeNode=Utils.findSectionInTree(treeRoot, section);
		TreeNode parentTreeNode=currentTreeNode.getParent();
		parentTreeNode.getChildren().remove(currentTreeNode);

		// Remove section from db
		DAO.beginTransaction();
		DAO.updateSection(rootSection);
		Section currentPersistentSection=Utils.findPersistentSection(rootSection,section);
		Section persistentParent=currentPersistentSection.getParent();
		persistentParent.removeSection(currentPersistentSection);
		DAO.saveSection(persistentParent);
		DAO.deleteSection(currentPersistentSection);
		DAO.commitTransaction();

		Utils.reconstructChildrenTitles(parentTreeNode);
	}

	public static void insertArticle(TreeNode treeRoot, Section rootSection, Section targetSection, Article sourceArticle){
		TreeNode currentTreeNode=Utils.findSectionInTree(treeRoot, targetSection);

		// add article to db
		DAO.beginTransaction();
		DAO.updateSection(rootSection);
		Section currentPersistentSection=Utils.findPersistentSection(rootSection,targetSection);

		Article newPersistentArticle=new Article(sourceArticle.getShortName(),sourceArticle.getFullName(),sourceArticle.getText());
		currentPersistentSection.addArticle(newPersistentArticle);
		DAO.saveArticle(newPersistentArticle);
		DAO.updateSection(currentPersistentSection);
		DAO.commitTransaction();
		
		// Add section to tree
		EntityHolder newEntity=new EntityHolder(newPersistentArticle.getId(), "article", Utils.formatArticleTitle(newPersistentArticle), newPersistentArticle);
		new DefaultTreeNode(newEntity,currentTreeNode);
	}

	public static void insertSection(TreeNode treeRoot, Section rootSection, Section targetSection, Section sourceSection, boolean asSubling){

		TreeNode currentTreeNode=Utils.findSectionInTree(treeRoot, targetSection);
		TreeNode parentTreeNode=currentTreeNode.getParent();
		// add section to db
		DAO.beginTransaction();
		DAO.updateSection(rootSection);

		// create new section
		Section newPersistentSection=new Section(sourceSection.getShortName(),sourceSection.getFullName());

		if (asSubling){
			//EntityHolder eh=(EntityHolder)currentTreeNode.getData();
			//Section persistentSection=Utils.findPersistentSection(rootSection, (Section)eh.getRef());
			Section persistentSection=Utils.findPersistentSection(rootSection, targetSection);
			persistentSection.addSection(newPersistentSection);
			DAO.saveSection(persistentSection);
		}else{
			//EntityHolder ehparent=(EntityHolder)parentTreeNode.getData();
			//Section parentPersistentSection=Utils.findPersistentSection(rootSection,(Section)ehparent.getRef());
			Section parentPersistentSection=Utils.findPersistentSection(rootSection,targetSection.getParent());
			parentPersistentSection.addSection(newPersistentSection);
			DAO.saveSection(parentPersistentSection);
		}

		DAO.saveSection(newPersistentSection);
		DAO.commitTransaction();

		EntityHolder newEntity=new EntityHolder(newPersistentSection.getId(), "section", Utils.formatSectionTitle(newPersistentSection), newPersistentSection);
		// Add section to tree
		if (asSubling){
			new DefaultTreeNode(newEntity,currentTreeNode);
		}else{
			new DefaultTreeNode(newEntity,parentTreeNode);
		}
		
		// Shake tree nodes
		if (asSubling){
			Utils.shakeChildrenTitles(currentTreeNode);
		}else{
			Utils.shakeChildrenTitles(parentTreeNode);
		}
	}


}
