import static org.junit.Assert.*;

import org.junit.Test;


public class ArticleShould {

	@Test
	public void beConstructedByShortName(){
		Article a=new Article("shortname1");
		assertEquals("shortname1", a.getShortName());
	}
	
	@Test
	public void beConstructedByShourtNameAndFullName(){
		Article a=new Article("shortname2", "long full name");
		assertEquals("shortname2", a.getShortName());
		assertEquals("long full name", a.getFullName());
	}
	
	@Test
	public void haveEmptyTextWhenConstructed(){
		Article a=new Article("shortname3");
		assertEquals("", a.getText());
	}
	
	@Test 
	public void haveAbilityToModifyItsText(){
		Article a=new Article("shortname4");

		a.setText("123");
		assertEquals("123", a.getText());
	}
	
	@Test
	public void beAbleToDetermineParentSectionName(){
		Section s=new Section("section0");
		
		Article a=new Article("Article about some thing");
		s.addArticle(a);
		
		assertEquals("section0",a.getSection().getShortName());
	}
}
