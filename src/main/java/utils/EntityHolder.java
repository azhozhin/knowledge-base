package utils;

public class EntityHolder {
	private Long id;
	private String type;
	private String shortName;
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
