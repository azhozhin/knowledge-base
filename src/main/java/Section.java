import java.util.ArrayList;
import java.util.List;


public class Section {
	private boolean root=false;
	private String shortName;
	private String fullName;
	private Section parent;
	private final List<Article> articles;
	private final List<Section> sections;
	
	public Section(){
		// construct root
		this("","");
		this.root=true;
	}
	
	public Section(String shortName){
		this(shortName,shortName);
	}
	
	public Section(String shortName, String fullName){
		this.shortName=shortName;
		this.fullName=fullName;
		articles=new ArrayList<Article>();
		sections=new ArrayList<Section>();
	}
	
	public void addArticle(Article a) {
		articles.add(a);
		a.setSection(this);
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void addSection(Section subSection) {
		sections.add(subSection);
		subSection.parent=this;
	}

	public List<Section> getSections() {
		return sections;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setParent(Section parent){
		this.parent=parent;
	}
	
	public Section getParent(){
		return parent;
	}
	
	public boolean isRoot() {
		return root;
	}
	
	public String getHierarchyNumber() {
		String myHierarchyNumber=String.valueOf(getParent().getSections().indexOf(this)+1);
		if (getParent().isRoot()){
			return myHierarchyNumber;
		}else{
			String parentHierarchyNumber=getParent().getHierarchyNumber();
			return parentHierarchyNumber+"."+myHierarchyNumber;
		}
	}

	public String getHierarchyPath() {
		String myName=getShortName();
		if (getParent().isRoot()){
			return myName;
		}else{
			String parentName=getParent().getHierarchyPath();
			return parentName + " / " + myName;
		}
	}


}
