
public class WebService {

	public TreeNode<Link> getMenu() {
		TreeNode<Link> result=new TreeNode<Link>(new Link("",""));
		
		Section root=SectionDAO.load();
		
		result=recursiveTransform(root);
		return result;
	}
	
	public TreeNode<Link> recursiveTransform(Section root){
		Link rootLink=new Link(constructSectionMenuName(root),constructSectionMenuLink(root));
		TreeNode<Link> result=new TreeNode<Link>(rootLink);
		for (Section s:root.getSections()){
			TreeNode<Link> sectionTree=recursiveTransform(s);
			result.addChild(sectionTree);
		}
		for (Article a:root.getArticles()){
			Link artLink=new Link(constructArticleMenuName(a),constructArticleMenuLink(a));
			result.addItemAsChild(artLink);
		}
		return result;
	}

	public String constructSectionMenuName(Section from){
		if (from.getParent()==null){
			return "";
		}else{
			return from.getHierarchyNumber()+". "+from.getShortName();
		}
	}
	public String constructSectionMenuLink(Section from){
		return "#link#";
	}
	
	public String constructArticleMenuName(Article a){
		return a.getShortName();
	}
	
	public String constructArticleMenuLink(Article a){
		return "#link#";
	}


}
