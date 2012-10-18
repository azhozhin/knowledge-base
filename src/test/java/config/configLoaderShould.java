package config;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import config.ConfigLoader;

import domain.Article;
import domain.Section;


public class configLoaderShould {

	String data=new StringBuilder()
	.append("<?xml version='1.0' encoding='UTF-8'?>")
	.append("<sections>")
	.append("	<section shortname='section one' fullname='this is long title for section one'>")
	.append("		<articles>")
	.append("			<article shortname='Beggining' fullname='In the beggining, Far far away'>")
	.append("			<![CDATA[")
	.append("Here goes some text")
	.append("			]]>")
	.append("			</article>")
	.append("		</articles>")
	.append("		<sections>")
	.append("			<section shortname='section 1.1' fullname='long title for section 1.1'>")
	.append("			</section>")
	.append("			<section shortname='section 1.2' fullname='long title for section 1.2'>")
	.append("			</section>")
	.append("		</sections>")
	.append("	</section>")
	.append("</sections>")
	.toString();
	
	ConfigLoader configLoader;
	Section tree;
	
	@Before
	public void setUp(){
		configLoader=new ConfigLoader();
		configLoader.setStringSource(data);
		tree=configLoader.parseFromSource();
	}
	
	@Test
	public void beAbleLoadOneSectionData(){
		assertNotNull(tree.getSections());
		assertEquals(1, tree.getSections().size());
		
		Section one=tree.getSections().get(0);
		assertEquals("section one", one.getShortName());
		assertEquals("this is long title for section one", one.getFullName());
	}
	
	@Test 
	public void beAbleLoadArticles(){
		Article art=tree.getSections().get(0).getArticles().get(0);
		
		assertNotNull(art);
		assertEquals("Beggining", art.getShortName());
		assertEquals("In the beggining, Far far away", art.getFullName());
		assertEquals("Here goes some text", art.getText().trim());
	}
	
	@Test
	public void beAbleLoadSubsections(){
		Section one=tree.getSections().get(0);
		
		List<Section> subsections=one.getSections();
		assertEquals(2, subsections.size());
		assertEquals("section 1.1", subsections.get(0).getShortName());
		assertEquals("section 1.2", subsections.get(1).getShortName());
	}

}
