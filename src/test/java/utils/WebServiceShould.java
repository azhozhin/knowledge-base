package utils;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utils.Link;
import utils.TreeNode;
import utils.WebService;

import domain.Article;
import domain.Section;

public class WebServiceShould {
	Section root;
	WebService ws=new WebService();

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
	public void transformRootSectionToEmptyMenu(){
		TreeNode<Link> t=ws.recursiveTransform(new Section());
		assertEquals("", t.getData().getTitle());
		assertEquals("#link#", t.getData().getHref());
	}
	
	@Test
	public void transformOneLevelSectionToOneLevelList(){
		
		Section s0=new Section();
		Section s1=new Section("s1");
		Section s2=new Section("s2");
		s0.addSection(s1);
		s0.addSection(s2);
		TreeNode<Link> t=ws.recursiveTransform(s0);
		assertEquals(2, t.getChildren().size());
		assertEquals("1. s1", t.getChildren().get(0).getData().getTitle());
		assertEquals("2. s2", t.getChildren().get(1).getData().getTitle());
		
	}
	
	@Test
	public void transformComplexSectionTree(){
		TreeNode<Link> t=ws.recursiveTransform(root);
		
		assertNotNull(t.getChildren());
		assertEquals(4, t.getChildren().size());
		
		assertEquals("1. section 1", t.getChildren().get(0).getData().getTitle());
		assertEquals("2. section 2", t.getChildren().get(1).getData().getTitle());
		assertEquals("3. section 3", t.getChildren().get(2).getData().getTitle());
		assertEquals("4. section 4", t.getChildren().get(3).getData().getTitle());
		
		List<TreeNode<Link>> one_one_one=t.getChildren().get(0).getChildren().get(0).getChildren();
		assertEquals("1.1.1. ... misc", one_one_one.get(0).getData().getTitle());
		assertEquals("Misc things", one_one_one.get(0).getChildren().get(0).getData().getTitle());
	}

}
