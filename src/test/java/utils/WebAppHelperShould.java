package utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.primefaces.model.TreeNode;

import dao.DAO;
import domain.Article;
import domain.Section;

public class WebAppHelperShould {

	private Section rootInRAM;
	private TreeNode rootTreeNode;
	private Section rootFromDB;
	
	@Before
	public void setUp() throws Exception {
		rootInRAM=new Section();
		Section one   = new Section("section 1"); 
		Section two   = new Section("section 2"); 
		Section three = new Section("section 3"); 
		Section four  = new Section("section 4"); 
		
		rootInRAM.addSection(one);
		rootInRAM.addSection(two);
		rootInRAM.addSection(three);
		rootInRAM.addSection(four);
		
		Section one_one=new Section("section 1.1"); 
		one.addSection(one_one);
		
		Section one_one_one=new Section("section 1.1.1"); 
		one_one.addSection(one_one_one);
		
		Article art0=new Article("Paper one in section 1","Long title of paper one in section 1","<h2>title</h2> some text goes <b>here</b>"); 
		one.addArticle(art0);
		
		Article art1=new Article("Thing about bees in section 1.1"); 
		one_one.addArticle(art1);
		
		Article art2=new Article("Thing about cats in section 1.1");
		one_one.addArticle(art2);
		
		Article art4=new Article("Thing about misc things in section 1.1"); 
		one_one.addArticle(art4);
		
		Article art3=new Article("Misc things in section 1.1.1");
		one_one_one.addArticle(art3);
		
		DAO.getInstance().beginTransaction();
		DAO.getInstance().removeAllSections();
		DAO.getInstance().saveSection(rootInRAM);
		DAO.getInstance().commitTransaction();
		
		DAO.getInstance().beginTransaction();
		rootFromDB=DAO.getInstance().loadAllSectons();
		DAO.getInstance().commitTransaction();
		
		rootTreeNode=Utils.makeTreeRoot(rootFromDB);
		Utils.loadNodeSection(rootTreeNode, rootFromDB);
		Utils.loadNodeSection(rootTreeNode.getChildren().get(0), rootFromDB.getSections().get(0));
		Utils.loadNodeSection(rootTreeNode.getChildren().get(0).getChildren().get(0), rootFromDB.getSections().get(0).getSections().get(0));
		Utils.loadNodeSection(rootTreeNode.getChildren().get(0).getChildren().get(0).getChildren().get(0), rootFromDB.getSections().get(0).getSections().get(0).getSections().get(0));
		
	}

	@Test
	public void beAbleToDeleteArticle() {
		TreeNode articleNode=Utils.findArticleInTreeByName(rootTreeNode, "Thing about bees in section 1.1");
		
		
		EntityHolder eh=(EntityHolder)articleNode.getData();
		Article a=(Article)eh.getRef();
		
		assertEquals(3, rootFromDB.getSections().get(0).getSections().get(0).getArticles().size());
		
		WebAppHelper.deleteArticle(rootTreeNode, rootFromDB, a);
		
		assertEquals(2, rootFromDB.getSections().get(0).getSections().get(0).getArticles().size());
		
		assertEquals(1, rootFromDB.getSections().get(0).getSections().get(0).getSections().size());
		assertEquals(3, rootTreeNode.getChildren().get(0).getChildren().get(0).getChildCount());
		TreeNode tn1=rootTreeNode.getChildren().get(0).getChildren().get(0).getChildren().get(0);
		TreeNode tn2=rootTreeNode.getChildren().get(0).getChildren().get(0).getChildren().get(1);
		TreeNode tn3=rootTreeNode.getChildren().get(0).getChildren().get(0).getChildren().get(2);
		
		assertEquals("1.1.1. section 1.1.1",((EntityHolder)tn1.getData()).getShortName()); 
		assertEquals("Thing about cats in section 1.1",((EntityHolder)tn2.getData()).getShortName());
		assertEquals("Thing about misc things in section 1.1",((EntityHolder)tn3.getData()).getShortName());
	}

	@Test
	public void beAbleToDeleteSection() {
		TreeNode sectionNode=Utils.findSectionInTreeByName(rootTreeNode, "section 2");
		
		EntityHolder eh=(EntityHolder)sectionNode.getData();
		Section section=(Section)eh.getRef();
		
		// old state
		assertEquals(4, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		
		assertEquals(4, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
		
		WebAppHelper.deleteSection(rootTreeNode, rootFromDB, section);
		
		// new state
		assertEquals(3, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(2).getShortName());
		
		assertEquals(3, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 3", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 4", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
	}

	

	@Test
	public void beAbleToInsertSectionAsSubling() {

		// Old state
		assertEquals(4, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		
		assertEquals(4, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
		
		//WebAppHelper.insertSection(rootTreeNode, rootFromDB, section);
		Section section2=rootFromDB.getSections().get(1);
		Section newSection=new Section("subling of section 2");
		
		WebAppHelper.insertSection(rootTreeNode, rootFromDB, section2, newSection, true);
	
		// New state
		assertEquals(4, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("subling of section 2", rootFromDB.getSections().get(1).getSections().get(0).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		
		assertEquals(4, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("2.1. subling of section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getChildren().get(0).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
	}
	
	@Test
	public void beAbleToInsertSectionOnSameLevel() {
		// Old state
		assertEquals(4, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		
		assertEquals(4, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
		
		Section section2=rootFromDB.getSections().get(1);
		Section newSection=new Section("new section 5");
		
		WebAppHelper.insertSection(rootTreeNode, rootFromDB, section2, newSection, false);
	
		// New state
		assertEquals(5, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		assertEquals("new section 5", rootFromDB.getSections().get(4).getShortName());
		
		assertEquals(5, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
		assertEquals("5. new section 5", ((EntityHolder)rootTreeNode.getChildren().get(4).getData()).getShortName());
	}
	
	@Test
	public void beAbleToInsertArticleInAnyPlace() {

		// Old state
		assertEquals(4, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		
		assertEquals(0, rootFromDB.getArticles().size());
		
		assertEquals(4, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
		
		Section rootSection=rootFromDB;
		Article art=new Article("new article");
		
		WebAppHelper.insertArticle(rootTreeNode, rootFromDB, rootSection, art);
		
		// New state
		assertEquals(4, rootFromDB.getSections().size());
		assertEquals("section 1", rootFromDB.getSections().get(0).getShortName());
		assertEquals("section 2", rootFromDB.getSections().get(1).getShortName());
		assertEquals("section 3", rootFromDB.getSections().get(2).getShortName());
		assertEquals("section 4", rootFromDB.getSections().get(3).getShortName());
		
		assertEquals(1, rootFromDB.getArticles().size());
		assertEquals("new article", rootFromDB.getArticles().get(0).getShortName());
		
		assertEquals(5, rootTreeNode.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)rootTreeNode.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)rootTreeNode.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)rootTreeNode.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)rootTreeNode.getChildren().get(3).getData()).getShortName());
		assertEquals("new article", ((EntityHolder)rootTreeNode.getChildren().get(4).getData()).getShortName());
		
	}

}
