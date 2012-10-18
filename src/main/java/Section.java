import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class Section {
	
	@Id
	@Column(name="section_id")
	@GeneratedValue
	private Long id;
	
	@Column(name="is_root")
	private boolean root=false;
	
	@Column(name="short_name")
	private String shortName;
	
	@Column(name="full_name")
	private String fullName;
	
	@ManyToOne
	@JoinColumn(name="section_id", insertable=false, updatable=false)
	private Section parent;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(
			name="section_article",
			joinColumns=@JoinColumn(name="section_id"),
			inverseJoinColumns=@JoinColumn(name="article_id")
	)
	@OrderColumn(name="article_index")
	private final List<Article> articles;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@OrderColumn(name="section_index")
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addArticle(Article a) {
		articles.add(a);
		a.setSection(this);
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void addSection(Section subSection) {
		if (subSection.parent==null){
			sections.add(subSection);
			subSection.parent=this;
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + (root ? 1231 : 1237);
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Section other = (Section) obj;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (root != other.root)
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	

}
