package domain;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;


@Entity
public class Article {
	
	@Id
	@Column(name="article_id")
	@GeneratedValue
	private Long id;
	
	@Column(name="short_name")
	private String shortName;
	
	@Column(name="full_name")
	private String fullName;
	
	@Column(name="body_text")
	@Lob
	private String text;
	
	@ManyToOne
	private Section section;
	
	public Article(){
		this("","","");
	}
	
	public Article(String shortName) {
		this(shortName,shortName,"");
	}
	
	public Article(String shortName, String fullName){
		this(shortName,fullName,"");
	}

	public Article(String shortName, String fullName, String text) {
		this.shortName=shortName;
		this.fullName=fullName;
		this.text=text;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Section getSection() {
		return section;
	}
	
	public void setSection(Section section){
		this.section = section;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
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
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}


}
