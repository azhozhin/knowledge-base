package domain;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import domain.Article;
import domain.Section;


public class SectionShould {

	Section root=new Section();
	Section s1=new Section("1");
	Section s2=new Section("2");
	Section s1s1=new Section("1_1");
	Section s1s1s1=new Section("1_1_1");
	Section s1s1s2=new Section("1_1_2");
	Section s2s1=new Section("2_1");
	Section s2s2=new Section("2_2");
	Section s2s2s1=new Section("2_2_1");
	Section s3=new Section("3");
	
	@Before
	public void setUp(){
		root.addSection(s1);
		root.addSection(s2);
		root.addSection(s3);
		
		s1.addSection(s1s1);
		s1s1.addSection(s1s1s1);
		s1s1.addSection(s1s1s2);
		
		s2.addSection(s2s1);
		s2.addSection(s2s2);
		s2s2.addSection(s2s2s1);
	}
	
	@Test
	public void beAbleToConstructByShortName(){
		Section s=new Section("shortname");
		assertEquals("shortname", s.getShortName());
	}
	
	@Test
	public void beAbleToConstructByShortNameAndFullName(){
		Section s=new Section("shortname", "long full name");
		assertEquals("shortname", s.getShortName());
		assertEquals("long full name",s.getFullName());
	}
	
	@Test
	public void storeAtricles() {
		Section s=new Section("section 1");
		Article a1=new Article("art1");
		Article a2=new Article("art2");
		
		s.addArticle(a1);
		s.addArticle(a2);
		
		List<Article> storedAtricles=s.getArticles();
		
		assertEquals(a1, storedAtricles.get(0));
		assertEquals(a2, storedAtricles.get(1));
	}
	
	@Test 
	public void storeOtherSections(){
		Section s=new Section("section 1");
		
		Section subs1=new Section("section 2");
		Section subs2=new Section("section 3");
		
		s.addSection(subs1);
		s.addSection(subs2);
		
		List<Section> storedSections=s.getSections();
		
		assertEquals(subs1, storedSections.get(0));
		assertEquals(subs2, storedSections.get(1));
	}

	@Test
	public void beAbleToConstructItsNumberInHierarchy(){
		assertEquals("", root.getHierarchyNumber());
		assertEquals("1", s1.getHierarchyNumber());
		assertEquals("2", s2.getHierarchyNumber());
		assertEquals("3", s3.getHierarchyNumber());
		assertEquals("1.1", s1s1.getHierarchyNumber());
		assertEquals("1.1.2", s1s1s2.getHierarchyNumber());
		assertEquals("2.1", s2s1.getHierarchyNumber());
		assertEquals("2.2.1", s2s2s1.getHierarchyNumber());
	}
	
	@Test 
	public void beAbleToConstructPathInHierarchy(){
		assertEquals("", root.getHierarchyPath());
		assertEquals("1", s1.getHierarchyPath());
		assertEquals("2", s2.getHierarchyPath());
		assertEquals("2 / 2_1", s2s1.getHierarchyPath());
		assertEquals("1 / 1_1 / 1_1_2", s1s1s2.getHierarchyPath());
		assertEquals("2 / 2_2 / 2_2_1", s2s2s1.getHierarchyPath());
	}
	

	@Test
	public void notBeAllowedToBePlacedToTwoDifferentSections(){
		Section root=new Section();
		Section s1=new Section("sect1");
		Section s2=new Section("sect2");
		root.addSection(s1);
		root.addSection(s2);
		
		// this should be not allowed
		s1.addSection(s2);
		
		assertEquals(root, s1.getParent());
		assertEquals(root, s2.getParent());
	}
}
