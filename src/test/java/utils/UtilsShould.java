package utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.primefaces.model.TreeNode;

import domain.Article;
import domain.Section;

public class UtilsShould {
	
	private Section rootInRAM;
	private TreeNode treeRoot;

	private Section newSectionHolder;
	private Long id=1L;
	
	@Before
	public void setUp() throws Exception {
		rootInRAM=new Section();
		Section one   = new Section(id++,"section 1"); 
		Section two   = new Section(id++,"section 2"); 
		Section three = new Section(id++,"section 3"); 
		Section four  = new Section(id++,"section 4"); 
		
		rootInRAM.addSection(one);
		rootInRAM.addSection(two);
		rootInRAM.addSection(three);
		rootInRAM.addSection(four);
		
		Section one_one=new Section(id++,"section 1.1"); 
		one.addSection(one_one);
		
		Section one_one_one=new Section(id++,"section 1.1.1"); 
		one_one.addSection(one_one_one);
		
		Article art0=new Article(id++,"Paper one in section 1","Long title of paper one in section 1","<h2>title</h2> some text goes <b>here</b>"); 
		one.addArticle(art0);
		
		Article art1=new Article(id++,"Thing about bees in section 1.1"); 
		one_one.addArticle(art1);
		
		Article art2=new Article(id++,"Thing about cats in section 1.1");
		one_one.addArticle(art2);
		
		Article art4=new Article(id++,"Thing about misc things in section 1.1"); 
		one_one.addArticle(art4);
		
		Article art3=new Article(id++,"Misc things in section 1.1.1");
		one_one_one.addArticle(art3);
		
		newSectionHolder=new Section(id++,"section holder");
		Section s=new Section(id++,"newSection");
		newSectionHolder.addSection(s);
		
		treeRoot=Utils.makeTreeRoot(rootInRAM);
		Utils.loadNodeSection(treeRoot, rootInRAM);
		Utils.loadNodeSection(treeRoot.getChildren().get(0), rootInRAM.getSections().get(0));
		Utils.loadNodeSection(treeRoot.getChildren().get(0).getChildren().get(0), rootInRAM.getSections().get(0).getSections().get(0));
		Utils.loadNodeSection(treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(0), rootInRAM.getSections().get(0).getSections().get(0).getSections().get(0));
	}

	@Test
	public void beAbleToMakeTreeRoot(){
		Section root=new Section();
		TreeNode result=Utils.makeTreeRoot(root);
		EntityHolder eh=(EntityHolder)result.getData();
		assertEquals("",eh.getShortName());
		assertSame(root,(Section)eh.getRef());
		assertEquals("section", eh.getType());
	}
	
	@Test 
	public void beAbleToLoadNodeSection(){
		assertEquals(4, treeRoot.getChildCount());
		assertEquals("1. section 1", ((EntityHolder)treeRoot.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)treeRoot.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)treeRoot.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)treeRoot.getChildren().get(3).getData()).getShortName());
		TreeNode one_one=treeRoot.getChildren().get(0).getChildren().get(0);
		assertEquals("1.1. section 1.1", ((EntityHolder)one_one.getData()).getShortName());
		TreeNode one_one_one=one_one.getChildren().get(0);
		assertEquals("1.1.1. section 1.1.1", ((EntityHolder)one_one_one.getData()).getShortName());
	}
	
	@Test
	public void beAbleToFindPersistentSection() {
		Section toFind=new Section(6L,"section 1.1.1");
		Section result=Utils.findPersistentSection(rootInRAM, toFind);
		assertNotNull(result);
		assertEquals(toFind.getId(), result.getId());
		assertEquals(toFind.getShortName(), result.getShortName());
		assertEquals(toFind.getFullName(), result.getFullName());
		
		Section toFindWrongId=new Section(7L,"section 1.1.1");
		result=Utils.findPersistentSection(rootInRAM, toFindWrongId);
		assertNull(result);
	}

	@Test
	public void beAbleToFindPersistentArticle() {
		Article toFind=new Article(11L,"Misc things in section 1.1.1");
		Article result=Utils.findPersistentArticle(rootInRAM, toFind);
		assertNotNull(result);
		assertEquals(toFind.getId(), result.getId());
		assertEquals(toFind.getShortName(), result.getShortName());
		assertEquals(toFind.getFullName(), result.getFullName());
		
		Article toFindWrongId=new Article(12L, "Misc things in section 1.1.1");
		result=Utils.findPersistentArticle(rootInRAM, toFindWrongId);
		assertNull(result);
	}

	@Test
	public void beAbleToFindSectionInTree() {
		Section toFind=new Section(6L,"section 1.1.1");
		TreeNode result=Utils.findSectionInTree(treeRoot, toFind);
		assertNotNull(result);
		EntityHolder eh=(EntityHolder)result.getData();
		Section foundSection=(Section)eh.getRef();
		assertEquals(toFind.getId(), foundSection.getId());
		assertEquals(toFind.getShortName(), foundSection.getShortName());
		assertEquals(toFind.getFullName(), foundSection.getFullName());
	}
	
	@Test
	public void beAbleToFindSectionInTreeByName() {
		TreeNode result=Utils.findSectionInTreeByName(treeRoot, "section 1.1.1");
		assertNotNull(result);
		EntityHolder eh=(EntityHolder)result.getData();
		Section foundSection=(Section)eh.getRef();
		assertEquals("section 1.1.1", foundSection.getShortName());
	}

	@Test
	public void beAbleToFindArticleInTree() {
		Article toFind=new Article(11L, "Misc things in section 1.1.1");
		TreeNode result=Utils.findArticleInTree(treeRoot, toFind);
		assertNotNull(result);
		EntityHolder eh=(EntityHolder)result.getData();
		Article foundArticle=(Article)eh.getRef();
		assertEquals(toFind.getId(), foundArticle.getId());
		assertEquals(toFind.getShortName(), foundArticle.getShortName());
		assertEquals(toFind.getFullName(), foundArticle.getFullName());
	}
	
	@Test
	public void beAbleToFindArticleInTreeByName() {
		TreeNode result=Utils.findArticleInTreeByName(treeRoot, "Misc things in section 1.1.1");
		assertNotNull(result);
		EntityHolder eh=(EntityHolder)result.getData();
		Article foundArticle=(Article)eh.getRef();
		assertEquals("Misc things in section 1.1.1", foundArticle.getShortName());
	}

	
	
	@Test
	public void beAbleToReconstructChildrenTitles() {
		TreeNode section1Node=treeRoot.getChildren().get(0);
		Section section1=rootInRAM.getSections().get(0);
		// remove from TreeNode 
		treeRoot.getChildren().remove(section1Node);
		// remove from sections
		rootInRAM.removeSection(section1);
		Utils.reconstructChildrenTitles(treeRoot);
		assertEquals(3, rootInRAM.getSections().size());
		assertEquals(3, treeRoot.getChildren().size());
		
		assertEquals("1. section 2", ((EntityHolder)treeRoot.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 3", ((EntityHolder)treeRoot.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 4", ((EntityHolder)treeRoot.getChildren().get(2).getData()).getShortName());
	}

	@Test
	public void beAbleToFormatSectionTitle() {
		assertEquals("", Utils.formatSectionTitle(rootInRAM));
		assertEquals("1. section 1", Utils.formatSectionTitle(rootInRAM.getSections().get(0)));
		assertEquals("2. section 2", Utils.formatSectionTitle(rootInRAM.getSections().get(1)));
		assertEquals("3. section 3", Utils.formatSectionTitle(rootInRAM.getSections().get(2)));
		assertEquals("1.1. section 1.1", Utils.formatSectionTitle(rootInRAM.getSections().get(0).getSections().get(0)));
		assertEquals("1.1.1. section 1.1.1", Utils.formatSectionTitle(rootInRAM.getSections().get(0).getSections().get(0).getSections().get(0)));
	}

	@Test
	public void beAbleToFormatArticleTitle() {
		assertEquals("short name", Utils.formatArticleTitle(new Article("short name")));
		assertEquals("", Utils.formatArticleTitle(new Article("")));
	}

	@Test
	public void beAbleToMakeBreadCrumb() {
		// get section 1.1.1
		EntityHolder eh=(EntityHolder)treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData();
		String breadCrumb=Utils.makeBreadCrumb(eh);
		assertEquals("Home / 1. section 1 / 1.1. section 1.1 / 1.1.1. section 1.1.1", breadCrumb);
		
		// get article in section 1.1.1
		Article article=new Article(11L);
		
		eh=(EntityHolder)Utils.findArticleInTree(treeRoot, article).getData();
		breadCrumb=Utils.makeBreadCrumb(eh);
		assertEquals("Home / 1. section 1 / 1.1. section 1.1 / 1.1.1. section 1.1.1", breadCrumb);
	}
	
	@Test
	public void beAbleToShakeTreeWithoutModification(){
		Utils.shakeChildrenTitles(treeRoot);
		assertEquals("1. section 1", ((EntityHolder)treeRoot.getChildren().get(0).getData()).getShortName());
		assertEquals("2. section 2", ((EntityHolder)treeRoot.getChildren().get(1).getData()).getShortName());
		assertEquals("3. section 3", ((EntityHolder)treeRoot.getChildren().get(2).getData()).getShortName());
		assertEquals("4. section 4", ((EntityHolder)treeRoot.getChildren().get(3).getData()).getShortName());
	}
	
	@Test
	public void beAbleToShakeTreeWithModification(){
		
		Utils.loadNodeSection(treeRoot.getChildren().get(0).getChildren().get(0), newSectionHolder);
		Utils.shakeChildrenTitles(treeRoot.getChildren().get(0).getChildren().get(0));
		assertEquals("1.1.1. section 1.1.1", ((EntityHolder)treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(0).getData()).getShortName());
		// this should only shake, not reconstruct titles
		assertEquals("1. newSection", ((EntityHolder)treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(1).getData()).getShortName());
		assertEquals("Thing about bees in section 1.1", ((EntityHolder)treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(2).getData()).getShortName());
		assertEquals("Thing about cats in section 1.1", ((EntityHolder)treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(3).getData()).getShortName());
		assertEquals("Thing about misc things in section 1.1", ((EntityHolder)treeRoot.getChildren().get(0).getChildren().get(0).getChildren().get(4).getData()).getShortName());
	}

	
}
