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
		
		Article art1=new Article("Thing about bees","All known things about bees");
		Article art2=new Article("Thing about cats","All known things about cats");
		
		root.addSection(one);
		root.addSection(two);
		root.addSection(three);
		root.addSection(four);
		
		one.addSection(one_one);
		one_one.addArticle(art1);
		one_one.addArticle(art2);
		
	}
	
	@Test
	public void beAbleToSaveAndLoadTree() {
		System.out.println("Saving tree");
		SectionDAO.beginTransaction();
		SectionDAO.save(root);
		SectionDAO.commitTransaction();

		System.out.println("Loading tree");
		SectionDAO.beginTransaction();
		Section result=SectionDAO.load();
		SectionDAO.commitTransaction();
		
		
		System.out.println("checking tree");
		recursiveCheck(root,result);
		
	}
	
	public void recursiveCheck(Section root1, Section root2){
		assertNotNull(root1);
		assertNotNull(root2);
		assertEquals(root1.getId(), root2.getId());
		assertEquals(root1.getShortName(), root2.getShortName());
		assertEquals(root1.getFullName(), root2.getFullName());
		
		
		if (root1.getArticles()!=null && root2.getArticles()!=null){
			assertEquals(root1.getArticles().size(), root2.getArticles().size());
			if(root1.getArticles().size()>0 && root2.getArticles().size()>0){
				for(int i=0;i<root1.getArticles().size();i++){
					assertEquals(root1.getArticles().get(i), root2.getArticles().get(i));
				}
			}
		}
		
		
		if (root1.getSections()!=null && root2.getSections()!=null){
			assertEquals(root1.getSections().size(), root2.getSections().size());
			if (root1.getSections().size()>0 && root2.getSections().size()>0){
				for (int i=0;i<root1.getSections().size();i++){
					recursiveCheck(root1.getSections().get(i), root2.getSections().get(i));
				}
			}
		}
		
		/*assertArrayEquals(root1.getArticles().toArray(), root2.getArticles().toArray());
		for (int i=0;i<root1.getSections().size();i++){
			recursiveCheck(root1.getSections().get(i), root2.getSections().get(i));
		}*/
	}
}
