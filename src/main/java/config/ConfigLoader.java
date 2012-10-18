package config;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import domain.Article;
import domain.Section;


public class ConfigLoader {

	private String source;
	private Document dom=null;
	private Section rootSection;
	
	public void setStringSource(String data) {
		this.source=data;
	}

	public Section parseFromSource() {
		rootSection=new Section();
		
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db=dbf.newDocumentBuilder();
			
			InputStream is=new ByteArrayInputStream(source.getBytes());
			dom=db.parse(is);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(dom!=null){
			Element root=dom.getDocumentElement();
			List<Section> sections=loadSections(root);
			for(Section s:sections){
				rootSection.addSection(s);
			}
		}
		
		return rootSection;
	}

	private List<Section> loadSections(Node node){
		List<Section> result=new ArrayList<Section>();
		NodeList children = node.getChildNodes();
		for (int i=0;i<children.getLength();i++){
			Node n=children.item(i);
			if (n.getNodeType()==Node.ELEMENT_NODE){
				if ("section".equals(n.getNodeName().toLowerCase())){
					Section s=loadSection(n);
					result.add(s);
				}
			}
		}
		return result;
	}
	
	private Section loadSection(Node node) {
		Section result;
		
		String shortName=node.getAttributes().getNamedItem("shortname").getNodeValue();
		String fullName=node.getAttributes().getNamedItem("fullname").getNodeValue();
		
		result=new Section(shortName, fullName);
		
		NodeList children=node.getChildNodes();
		for (int i=0;i<children.getLength();i++){
			Node n=children.item(i);
			if (n.getNodeType()==Node.ELEMENT_NODE){
				if ("articles".equals(n.getNodeName())){
					List<Article> articles=loadArticles(n);
					for(Article a:articles){
						result.addArticle(a);
					}
				}else if("sections".equals(n.getNodeName())){
					List<Section> subsections=loadSections(n);
					for(Section s:subsections){
						result.addSection(s);
					}
				}
			}
		}
		return result;
	}

	private List<Article> loadArticles(Node node) {
		List<Article> result=new ArrayList<Article>();
		NodeList children = node.getChildNodes();
		for (int i=0;i<children.getLength();i++){
			Node n=children.item(i);
			if (n.getNodeType()==Node.ELEMENT_NODE){
				if ("article".equals(n.getNodeName().toLowerCase())){
					Article a=loadArticle(n);
					result.add(a);
				}
			}
		}
		return result;
	}

	private Article loadArticle(Node node) {
		Article result;
		
		String shortName=node.getAttributes().getNamedItem("shortname").getNodeValue();
		String fullName=node.getAttributes().getNamedItem("fullname").getNodeValue();
		String text="";
		
		NodeList children = node.getChildNodes();
		for (int i=0;i<children.getLength();i++){
			Node n=children.item(i);
			if (n.getNodeType()==Node.CDATA_SECTION_NODE){
				text=n.getNodeValue();
				break;
			}
		}
		
		result=new Article(shortName, fullName, text);
		
		return result;
	}
	

}
