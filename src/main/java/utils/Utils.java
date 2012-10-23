package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import domain.Article;
import domain.Section;

/**
 * Utils class hold misc function for manipulating trees of Sections or PrimeFaces.TreeNodes
 */

public class Utils {

	/**
	 * Creates PrimeFaces.TreeNode root from rootSection 
	 * @param rootSection Section which will be root of tree, usualy it is empty Section
	 * @return PrimeFaces.TreeNode root
	 */
	public static TreeNode makeTreeRoot(Section rootSection){
		return new DefaultTreeNode(new EntityHolder(0L,"section","",rootSection), null);
	}
	
	// Loads current Section contents into current TreeNode
	/**
	 * Loads one level of section contents, i.e. sections and articles to given TreeNode
	 * @param node PrimeFaces.TreeNode to load in
	 * @param section section with some contents
	 */
	public static void loadNodeSection(TreeNode node, Section section){
		for (Section s:section.getSections()){
			EntityHolder sectionHolder = new EntityHolder(s.getId(),"section",Utils.formatSectionTitle(s), s);
			new DefaultTreeNode(sectionHolder, node);
		}
		for (Article a:section.getArticles()){
			EntityHolder articleHolder=new EntityHolder(a.getId(), "article", Utils.formatArticleTitle(a), a);
			new DefaultTreeNode(articleHolder, node);
		}
	}

	/**
	 * Finds given section in persistent section tree, used id for comparison 
	 * @param persistentSection root of section tree to search through, should be in sync with database
	 * @param currentSection section we want to find
	 * @return section from tree
	 */
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

	/**
	 * Finds given article in persistent section tree, used id for comparison 
	 * @param persistentSection root of section tree to search through, should be in sync with database
	 * @param currentArticle article we want to find
	 * @return article from tree
	 */
	public static Article findPersistentArticle(Section persistentSection, Article currentArticle) {
		for(Article a:persistentSection.getArticles()){
			if (a.getId().equals(currentArticle.getId())){
				return a;
			}
		}
		for (Section s:persistentSection.getSections()){
			Article a=findPersistentArticle(s, currentArticle);
			if (a!=null){
				return a;
			}
		}
		return null;
	}
	
	/**
	 * Finds given section in PrimeFaces.TreeNode, used id for comparison 
	 * @param treeNode TreeNode to search in
	 * @param currentSection we want to find
	 * @return TreeNode which contains given section
	 */
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
	
	/**
	 * Finds given article in PrimeFaces.TreeNode, used id for comparison
	 * @param treeNode TreeNode to search in
	 * @param currentArticle we want to find
	 * @return TreeNode which contains given artive
	 */
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
	
	/**
	 * Finds given section name in PrimeFaces.TreeNode, used shortName for comparison
	 * @param treeNode TreeNode to search in
	 * @param shortName given shortname we want to find
	 * @return TreeNode which contains section with given name 
	 */
	public static TreeNode findSectionInTreeByName(TreeNode treeNode, String shortName) {
		EntityHolder eh=(EntityHolder)treeNode.getData();
		
		if (eh.getType()=="section"){
			
			Section section=(Section)eh.getRef();
		
			if (shortName.equals(section.getShortName())){
				return treeNode;
			}
			for(TreeNode tn:treeNode.getChildren()){
				TreeNode r=findSectionInTreeByName(tn, shortName);
				if (r!=null){
					return r;
				}
			}
		}
		return null;
	}
	

	/**
	 * Finds given article name in PrimeFaces.TreeNode, used shortName for comparison
	 * @param treeNode TreeNode to search in
	 * @param shortName given shortname we want to find
	 * @return TreeNode which contaibs article with given name
	 */
	public static TreeNode findArticleInTreeByName(TreeNode treeNode, String shortName) {
		EntityHolder eh=(EntityHolder)treeNode.getData();
		
		if (eh.getType()=="section"){
			
			for(TreeNode tn:treeNode.getChildren()){
				TreeNode r=findArticleInTreeByName(tn, shortName);
				if (r!=null){
					return r;
				}
			}
		}else{
			Article article=(Article)eh.getRef();
			if (shortName.equals(article.getShortName())){
				return treeNode;
			}
		}

		return null;
	}
	
	/**
	 * Recursive reconstruct titles of child nodes in PrimeFaces.TreeNode
	 * its updates EntityHolder short name which placed in tree nodes 
	 * @param treeNode root of tree to reconstruct names
	 */
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
	
	/**
	 * Guess right order of child nodes of PrimeFaces.TreeNode node
	 * "Right" means all sections should go before articles in list of children
	 * If it not true, then toss them in "right" order
	 * @param treeNode target node to shake its children 
	 */
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
	
	/**
	 * Format section title to be placed in TreeNode via EntityHolder
	 * use hierarchy number and shortname of section
	 * @param section given section
	 * @return string with name
	 */
	public static String formatSectionTitle(Section section){
		String a=section.getHierarchyNumber();
		String b=section.getShortName();
		if (a.equals("")){
			return "";
		}else{
			return a+". "+b;
		}
	}
	
	/**
	 * Format article title to be placed in TreeNode via EntityHolder
	 * use shortname of article
	 * @param article
	 * @return
	 */
	public static String formatArticleTitle(Article article){
		return article.getShortName();
	}

	/**
	 * Make breadcrumb path for given entityHolder
	 * It get traverses parents one-by-one before reach root of section tree  
	 * @param eh current entity holder
	 * @return string represent path to element
	 */
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
	
	/**
	 * Recursive print PrimeFaces.TreeNode
	 * @param treeNode given tree
	 */
	public static void print(TreeNode treeNode){
		print(treeNode,0);
	}
	/**
	 * Recursive print PrimeFaces.TreeNode
	 * @param treeNode given tree
	 * @param depth given depth 
	 */
	private static void print(TreeNode treeNode, int depth){
		EntityHolder eh=(EntityHolder)treeNode.getData();
		for(int i=0;i<depth;i++){
			System.out.print("\t");
		}
		System.out.println(eh);
		for (TreeNode tn:treeNode.getChildren()){
			print(tn, depth+1);
		}
	}

}
