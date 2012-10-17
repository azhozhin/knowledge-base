
public class Article {
	
	private String shortName;
	private String fullName;
	private String text;
	private Section section;
	
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
	
}
