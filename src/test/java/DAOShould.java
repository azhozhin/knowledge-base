import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class DAOShould {

	Section root;
	
	@Before
	public void setUp(){
		root=new Section();
		Section one=new Section("section 1");
		Section two=new Section("section 2");
		Section three=new Section("section 3");
		Section four=new Section("section 4");
		
		Section one_one=new Section("Things about ...");
		Section one_one_one=new Section("... misc");
		
		Article art1=new Article("Thing about bees","All known things about bees", "here goes some long long text... about bees");
		Article art2=new Article("Thing about cats","All known things about cats", "here goes some long long text... about cats");
		Article art3=new Article("Misc things","Long named misc things", "here goes some long long text... about misc things");
		
		root.addSection(one);
		root.addSection(two);
		root.addSection(three);
		root.addSection(four);
		
		one.addSection(one_one);
		one_one.addArticle(art1);
		one_one.addArticle(art2);
		
		one_one.addSection(one_one_one);
		one_one_one.addArticle(art3);
	}
	

	@Test
	public void beAbleToSaveAndLoadComplexTree() {
		SectionDAO.beginTransaction();
		SectionDAO.save(root);
		SectionDAO.commitTransaction();
		
		SectionDAO.beginTransaction();
		Section result=SectionDAO.load();
		SectionDAO.commitTransaction();
		
		assertNotSame(root, result);
		
		recursiveCheck(root,result);
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

}
