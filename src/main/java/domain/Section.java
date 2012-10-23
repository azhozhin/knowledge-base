package domain;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


@Entity
public class Section {
	
	@Id
	@Column(name="section_id")
	@GeneratedValue
	private Long id;

	@Column(name="short_name")
	private String shortName;

	@Column(name="full_name")
	private String fullName;

	@ManyToOne(optional=true)
	@Cascade({CascadeType.SAVE_UPDATE})
	@JoinColumn(name="parent_section_id" )
	private Section parent;

	@OneToMany
	@Cascade({CascadeType.ALL})
	@JoinTable(	name="section_article",
				joinColumns=@JoinColumn(name="section_id"),
				inverseJoinColumns=@JoinColumn(name="article_id") )
	@OrderColumn
	private List<Article> articles;

	@OneToMany
	@Cascade({CascadeType.ALL}) //, CascadeType.DELETE_ORPHAN
	@JoinTable( name="section_section",
				joinColumns=@JoinColumn(name="section_id"),
				inverseJoinColumns=@JoinColumn(name="subsection_id"))
	@OrderColumn
	private List<Section> sections;

	public Section(){
		this(null,"","");
	}
	
	public Section(Long id){
		this(id,"","");
	}

	public Section(String shortName){
		this(null,shortName,"");
	}

	public Section(Long id,String shortName){
		this(id,shortName,"");
	} 
	
	public Section(String shortName, String fullName){
		this(null, shortName, fullName);
	}

	public Section(Long id,String shortName, String fullName){
		this.id=id;
		this.shortName=shortName;
		this.fullName=fullName;
		this.parent=null;
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
	
	public void removeSection(Section subSection){
		subSection.setParent(null);
		sections.remove(subSection);
		// FIXME: Hibernate bug with unique constraint violation
		// http://stackoverflow.com/questions/8773311/unique-constraint-violation-with-ordered-hibernate-list
		sections=new ArrayList<>(sections); 
	}

	public void removeArticle(Article article) {
		article.setSection(null);
		articles.remove(article);
		// FIXME: Hibernate bug with unique constraint violation
		// http://stackoverflow.com/questions/8773311/unique-constraint-violation-with-ordered-hibernate-list
		articles=new ArrayList<>(articles);
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

	public String getHierarchyNumber() {
		
		//if (parent==null || parent.getSections().indexOf(this)==-1){
		if (parent==null){
			return "";
		}
		List<Section> parentChilds=parent.getSections();
		String myHierarchyNumber=String.valueOf(parentChilds.indexOf(this)+1);
		if (parent.parent==null){
			return myHierarchyNumber;
		}else{
			String parentHierarchyNumber=parent.getHierarchyNumber();
			return parentHierarchyNumber+"."+myHierarchyNumber;
		}
	}

	public String getHierarchyPath() {
		if (parent==null){
			return "";
		}
		String myName=getShortName();
		if (parent.parent==null){
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
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (parent!=null){
			return "Section [id=" + id + ", shortName=" + shortName + ", fullName="	+ fullName + ", parent="+parent.id+"]";
		}else{
			return "Section [id=" + id + ", shortName=" + shortName + ", fullName="	+ fullName + ", parent=null]";
		}
	}

	

	


}
