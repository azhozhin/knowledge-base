package config;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import domain.Article;
import domain.Section;

/**
 * ConfigLoader is special purpose loader for parse data in xml format
 * There are sections and articles
 * section list is represented by <sections>...</sections>
 * article list is represented by <articles>...</articles>
 * section is represented by <section>...</section>
 * section can hold list of other sections
 * section can hold list of articles
 * article is represented by <article>...</article>
 * article can hold nothing
 */
public class ConfigLoader {

	private Document dom=null;

	private DocumentBuilder db;

	public ConfigLoader() throws ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		db=dbf.newDocumentBuilder();
	}

	/**
	 * Parses data from input string
	 * @param source  string with data in xml format
	 * @return parsed Section in tree form
	 * @throws SAXException
	 * @throws IOException
	 */
	public Section parseFromSource(String source) throws SAXException, IOException {
		InputStream is=new ByteArrayInputStream(source.getBytes());
		dom=db.parse(is);
		
		return loadAll(dom);
	}
	
	/**
	 * Parses data from external file
	 * @param filename  path to file
	 * @return parsed Section in tree form
	 * @throws SAXException
	 * @throws IOException
	 */
	public Section parseFromFile(String filename) throws SAXException, IOException{
		dom=db.parse(filename);

		return loadAll(dom);
	}

	/**
	 * Load all entities from dom
	 * @param dom complete dom object
	 * @return loaded section in tree form
	 */
	public Section loadAll(Document dom){
		Section rootSection=new Section();
		if(dom!=null){
			Element root=dom.getDocumentElement();
			List<Section> sections=loadSections(root);
			for(Section s:sections){
				rootSection.addSection(s);
			}
		}
		return rootSection;
	}

	/**
	 * Loads Sections list from <sections>...</sections> tags
	 * calls loadSection for every section occurrence 
	 * @param node current DOM node
	 * @return list of sections
	 */
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

	/**
	 * Loads One section from <section>...</section> tags
	 * Section have shortName and fullName coded as attributes
	 * Section can have subsections represented by <sections>...</sections>, calls loadSections
	 * Section can have articles represented by <articles>...</articles>, calls loadArticles
	 * @param node current DOM node
	 * @return section in tree form
	 */
	private Section loadSection(Node node) {
		Section result;

		String shortName;
		String fullName;
		
		Node shortNameNode=node.getAttributes().getNamedItem("shortname");
		Node fullNameNode=node.getAttributes().getNamedItem("fullname");

		shortName=shortNameNode!=null ? shortNameNode.getNodeValue() : "";
		fullName=fullNameNode!=null ? fullNameNode.getNodeValue() : "";

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

	/**
	 * Loads articles list from <articles>...</articles>, calls loadArticle 
	 * @param node current DOM node
	 * @return list of articles
	 */
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

	/**
	 * Loads one article from <article>...</article>
	 * Article have shortName and fullName coded as attributes
	 * Article can have text coded in <![CDATA[...]]> section  
	 * @param node current DOM node
	 * @return article
	 */
	private Article loadArticle(Node node) {
		Article result;
		
		String shortName;
		String fullName;

		Node shortNameNode=node.getAttributes().getNamedItem("shortname");
		Node fullNameNode=node.getAttributes().getNamedItem("fullname");
		
		shortName=shortNameNode!=null ? shortNameNode.getNodeValue() : "";
		fullName=fullNameNode!=null ? fullNameNode.getNodeValue() : "";
		
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
