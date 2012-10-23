package utils;

/**
 *  EntityHolder is special class for holding Article or Section in PrimeFaces.TreeNode
 * 
 *  TreeNode represents tree-like menu for navigation over sections and articles
 */
public class EntityHolder {
	/**
	 * id of stored entity 
	 */
	private Long id;
	
	/**
	 * type of stored entity "section" or "article"
	 */
	private String type;
	
	/**
	 * calculated from stored entity 
	 */
	private String shortName;
	
	/**
	 * reference to stored entity 
	 */
	private Object ref;
	
	public EntityHolder(Long id, String type, String shortName, Object ref) {
		this.id=id;
		this.type=type;
		this.shortName=shortName;
		this.ref=ref;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public Object getRef() {
		return ref;
	}
	
	public void setRef(Object ref) {
		this.ref = ref;
	}
	
	@Override
	public String toString() {
		return "EntityHolder [id=" + id + ", type=" + type + ", shortName="
				+ shortName + "]";
	}

}
