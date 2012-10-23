package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import domain.Article;
import domain.Section;

public class Utils {

	public static TreeNode makeTreeRoot(Section rootSection){
		return new DefaultTreeNode(new EntityHolder(0L,"section","",rootSection), null);
	}
	
	// Loads current Section contents into current TreeNode
	public static void loadNodeSection(TreeNode node, Section sect){
		for (Section s:sect.getSections()){
			EntityHolder sectionHolder = new EntityHolder(s.getId(),"section",Utils.formatSectionTitle(s), s);
			new DefaultTreeNode(sectionHolder, node);
		}
		for (Article a:sect.getArticles()){
			EntityHolder articleHolder=new EntityHolder(a.getId(), "article", Utils.formatArticleTitle(a), a);
			new DefaultTreeNode(articleHolder, node);
		}
	}

	public static Section findPersistentSection(Section persistentSection, Section currentSection) {

		if (currentSection.getId().equals(persistentSection.getId())){
			return persistentSection;
		}
		for(Section s:persistentSection.getSections()){
			Section r=findPersistentSection(s, currentSection);
			if (r!=null){
				return r;
			}
		}
		return null;
	}
	
	public static Article findPersistentArticle(Section persistentSection, Article currentArticle) {
		// look in articles
		for(Article a:persistentSection.getArticles()){
			if (a.getId().equals(currentArticle.getId())){
				return a;
			}
		}
		// recursively look in subsections
		for (Section s:persistentSection.getSections()){
			Article a=findPersistentArticle(s, currentArticle);
			if (a!=null){
				return a;
			}
		}
		return null;
	}
	
	public static TreeNode findSectionInTree(TreeNode treeNode, Section currentSection) {
		EntityHolder eh=(EntityHolder)treeNode.getData();
		
		if (eh.getType()=="section"){
			
			Section section=(Section)eh.getRef();
		
			if (currentSection.getId().equals(section.getId())){
				return treeNode;
			}
			for(TreeNode tn:treeNode.getChildren()){
				TreeNode r=findSectionInTree(tn, currentSection);
				if (r!=null){
					return r;
				}
			}
		}
		return null;
	}
	
	public static TreeNode findSectionInTreeByName(TreeNode treeNode, String name) {
		EntityHolder eh=(EntityHolder)treeNode.getData();
		
		if (eh.getType()=="section"){
			
			Section section=(Section)eh.getRef();
		
			if (name.equals(section.getShortName())){
				return treeNode;
			}
			for(TreeNode tn:treeNode.getChildren()){
				TreeNode r=findSectionInTreeByName(tn, name);
				if (r!=null){
					return r;
				}
			}
		}
		return null;
	}
	
	public static TreeNode findArticleInTree(TreeNode treeNode,	Article currentArticle) {
		EntityHolder eh=(EntityHolder)treeNode.getData();
		
		if (eh.getType()=="section"){
			
			for(TreeNode tn:treeNode.getChildren()){
				TreeNode r=findArticleInTree(tn, currentArticle);
				if (r!=null){
					return r;
				}
			}
		}else{
			Article article=(Article)eh.getRef();
			if (article.getId().equals(currentArticle.getId())){
				return treeNode;
			}
		}

		return null;
	}
	
	public static TreeNode findArticleInTreeByName(TreeNode treeNode, String name) {
		EntityHolder eh=(EntityHolder)treeNode.getData();
		
		if (eh.getType()=="section"){
			
			for(TreeNode tn:treeNode.getChildren()){
				TreeNode r=findArticleInTreeByName(tn, name);
				if (r!=null){
					return r;
				}
			}
		}else{
			Article article=(Article)eh.getRef();
			if (name.equals(article.getShortName())){
				return treeNode;
			}
		}

		return null;
	}
	
	public static void reconstructChildrenTitles(TreeNode treeNode){
		for(TreeNode tn:treeNode.getChildren()){
			EntityHolder eh=(EntityHolder)tn.getData();
			if (eh.getType()=="section"){
				Section s=(Section)eh.getRef();
				// fix current subling title
				eh.setShortName(Utils.formatSectionTitle(s));
				// fix recursive sublings titles
				reconstructChildrenTitles(tn);
			}
		}
	}
	
	public static void shakeChildrenTitles(TreeNode treeNode){
		List<EntityHolder> sectionItems=new ArrayList<>();
		List<EntityHolder> articleItems=new ArrayList<>();
		boolean secondpart=false;
		boolean mixed=false;
		for (TreeNode tn:treeNode.getChildren()){
			EntityHolder eh=(EntityHolder)tn.getData();
			if (eh.getType()=="section"){
				sectionItems.add(eh);
				if (secondpart==true){
					mixed=true;
				}
			}else{
				secondpart=true;
				articleItems.add(eh);
			}
		}
		if (mixed){
			treeNode.getChildren().clear();
			for(EntityHolder eh:sectionItems){
				new DefaultTreeNode(eh,treeNode);
			}
			for(EntityHolder eh:articleItems){
				new DefaultTreeNode(eh,treeNode);
			}
		}
	}
	
	public static String formatSectionTitle(Section section){
		String a=section.getHierarchyNumber();
		String b=section.getShortName();
		if (a.equals("")){
			return "";
		}else{
			return a+". "+b;
		}
	}
	
	public static String formatArticleTitle(Article article){
		return article.getShortName();
	}
	
	public static String makeBreadCrumb(EntityHolder eh){
		List<String> links=new ArrayList<String>();
		Section traverse;
		String result;
		if (eh.getType()=="section"){
			traverse=(Section)eh.getRef();
		}else{
			traverse=((Article)eh.getRef()).getSection();
		}
		
		while(traverse.getParent()!=null){
			links.add(traverse.getHierarchyNumber()+". "+traverse.getShortName());
			traverse=traverse.getParent();
		}
		StringBuilder sb=new StringBuilder();
		Collections.reverse(links);
		for(String l:links){
			sb.append(" / ");
			sb.append(l);
		}
		result="Home"+sb.toString();

		return result;
	}
	
	public static void print(TreeNode treeNode, int step){
		EntityHolder eh=(EntityHolder)treeNode.getData();
		for(int i=0;i<step;i++){
			System.out.print("\t");
		}
		System.out.println(eh);
		for (TreeNode tn:treeNode.getChildren()){
			print(tn, step+1);
		}
	}

}
