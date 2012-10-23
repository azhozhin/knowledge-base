package dao;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dao.DAO;
import domain.Article;
import domain.Section;


public class DAOShould {

	Section rootInRAM;
	Section rootInDB;
	
	@Before
	public void setUp(){
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
	}
	
	@Test 
	public void beAbleLoadTreeStepByStep(){
		
		DAO.getInstance().beginTransaction();
		Section result0=DAO.getInstance().loadSectionRootLevel();
		DAO.getInstance().commitTransaction();
		
		for(Section s:result0.getSections()){
			System.out.println(s);
		}
		
		DAO.getInstance().beginTransaction();
		//result0=DAO.mergeSection(result0);
		DAO.getInstance().updateSection(result0);
		Section result1=DAO.getInstance().loadSectionOneLevel(result0.getSections().get(0));
		DAO.getInstance().commitTransaction();
		
		DAO.getInstance().beginTransaction();
		//result0=DAO.mergeSection(result0);
		DAO.getInstance().updateSection(result0);
		Section result2=DAO.getInstance().loadSectionOneLevel(result0.getSections().get(0).getSections().get(0));
		DAO.getInstance().commitTransaction();
		
		assertEquals(result0.getSections().get(0), result1);
		assertEquals(result1.getSections().get(0), result2);
		
		assertEquals("", result0.getHierarchyNumber());
		assertEquals("1", result0.getSections().get(0).getHierarchyNumber());
		assertEquals("2", result0.getSections().get(1).getHierarchyNumber());
		assertEquals("3", result0.getSections().get(2).getHierarchyNumber());
		assertEquals("4", result0.getSections().get(3).getHierarchyNumber());
		
		assertEquals("1.1", result1.getSections().get(0).getHierarchyNumber());
		assertEquals("1.1.1", result2.getSections().get(0).getHierarchyNumber());
	}
	

	@Test
	public void beAbleToSaveAndLoadComplexTree() {
		DAO.getInstance().beginTransaction();
		Section result=DAO.getInstance().loadAllSectons();
		DAO.getInstance().commitTransaction();
		
		assertEquals("1", result.getSections().get(0).getHierarchyNumber());
		assertEquals("2", result.getSections().get(1).getHierarchyNumber());
		assertEquals("3", result.getSections().get(2).getHierarchyNumber());
		assertEquals("4", result.getSections().get(3).getHierarchyNumber());
		
		assertEquals("1.1", result.getSections().get(0).getSections().get(0).getHierarchyNumber());
		
		recursiveCheck(rootInRAM,result);
	
	}
	
	@Test
	public void beAbleToLoadRootLevelFromRootHierarchy(){
		
		DAO.getInstance().beginTransaction();
		Section result=DAO.getInstance().loadSectionRootLevel();
		DAO.getInstance().commitTransaction();
		
		assertEquals(4, result.getSections().size());
		
		List<Section> sects=result.getSections();
		
		assertEquals("section 1", sects.get(0).getShortName());
		assertEquals("section 2", sects.get(1).getShortName());
		assertEquals("section 3", sects.get(2).getShortName());
		assertEquals("section 4", sects.get(3).getShortName());
		
		assertEquals(0, result.getArticles().size());
	}
	
	@Test
	public void beAbleToLoadOneLevelForConcreteSectionHierarchy(){
		
		DAO.getInstance().beginTransaction();
		Section from=DAO.getInstance().findSectionByShortName("section 1");

		Section result=DAO.getInstance().loadSectionOneLevel(from);
		DAO.getInstance().commitTransaction();
		
		assertEquals(1, result.getSections().size());
		assertEquals("section 1.1", result.getSections().get(0).getShortName());
		
		assertEquals(1, result.getArticles().size());
		assertEquals("Paper one in section 1", result.getArticles().get(0).getShortName());
	}
	
	
	public void recursiveCheck(Section root1, Section root2){
		assertNotNull(root1);
		assertNotNull(root2);
		assertEquals(root1.getId(), root2.getId());
		assertEquals(root1.getShortName(), root2.getShortName());
		assertEquals(root1.getFullName(), root2.getFullName());

		// check articles
		assertNotNull(root1.getArticles());
		assertNotNull(root2.getArticles());
		assertEquals(root1.getArticles().size(), root2.getArticles().size());
		
		if(root1.getArticles().size()>0 && root2.getArticles().size()>0){
			for(int i=0;i<root1.getArticles().size();i++){
				assertEquals(root1.getArticles().get(i), root2.getArticles().get(i));
			}
		}
		
		assertNotNull(root1.getSections());
		assertNotNull(root2.getSections());
		assertEquals(root1.getSections().size(), root2.getSections().size());
		
		// check subsections
		if (root1.getSections().size()>0 && root2.getSections().size()>0){
			for (int i=0;i<root1.getSections().size();i++){
				recursiveCheck(root1.getSections().get(i), root2.getSections().get(i));
			}
		}

	}

	@Test
	public void beAbleToDeleteSingleSectionOnRootLevel(){
		DAO.getInstance().beginTransaction();
		Section section3=DAO.getInstance().findSectionByShortName("section 3");
		DAO.getInstance().deleteSection(section3);
		DAO.getInstance().commitTransaction();
		
		DAO.getInstance().beginTransaction();
		Section result=DAO.getInstance().loadSectionRootLevel();
		DAO.getInstance().commitTransaction();
		
		assertNotNull(result);
		assertEquals(3, result.getSections().size());
		assertEquals("section 1", result.getSections().get(0).getShortName());
		assertEquals("section 2", result.getSections().get(1).getShortName());
		assertEquals("section 4", result.getSections().get(2).getShortName());
	}
	
	@Test
	public void beAbleToDeleteSingleSectionWithItContents(){
		Section result=null;
		
		DAO.getInstance().beginTransaction();
		Section result0=DAO.getInstance().loadSectionRootLevel();
		Section section1=result0.getSections().get(0);
		DAO.getInstance().deleteSection(section1);
		DAO.getInstance().commitTransaction();

		DAO.getInstance().beginTransaction();
		result=DAO.getInstance().loadSectionRootLevel();
		DAO.getInstance().commitTransaction();

		assertNotNull(result);
		assertEquals(3, result.getSections().size());
		assertEquals("section 2", result.getSections().get(0).getShortName());
		assertEquals("section 3", result.getSections().get(1).getShortName());
		assertEquals("section 4", result.getSections().get(2).getShortName());
		
	}
	@Test
	public void beAbleToDeleteSingleNestedSectionWithItContents(){
		Section result=null;
		
		DAO.getInstance().beginTransaction();
		Section result0=DAO.getInstance().loadSectionRootLevel();
		Section section1=result0.getSections().get(0).getSections().get(0);
		DAO.getInstance().deleteSection(section1);
		DAO.getInstance().commitTransaction();

		DAO.getInstance().beginTransaction();
		result=DAO.getInstance().loadAllSectons();
		DAO.getInstance().commitTransaction();

		assertNotNull(result);
		assertEquals(4, result.getSections().size());
		assertEquals("section 1", result.getSections().get(0).getShortName());
		assertEquals("section 2", result.getSections().get(1).getShortName());
		assertEquals("section 3", result.getSections().get(2).getShortName());
		assertEquals("section 4", result.getSections().get(3).getShortName());
		
		assertEquals(0, result.getSections().get(0).getSections().size());
		assertEquals(1, result.getSections().get(0).getArticles().size());
	}
}
