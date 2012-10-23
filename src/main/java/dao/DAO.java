package dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Article;
import domain.Section;
import utils.HibernateUtil;


public class DAO {
	static Logger logger = LoggerFactory.getLogger(DAO.class);

	public static void beginTransaction() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	public static void commitTransaction() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().commit();
	}

	/**
	 * Save given section to database 
	 * @param section - section to save
	 */
	public static void saveSection(Section section) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.save(section);
	}
	
	/**
	 * Merge section to hibernate session
	 * @param root - root section to merge
	 * @return merged root section
	 */
	public static Section mergeSection(Section root) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		root=(Section)session.merge(root);
		return root;
	}
	
	/**
	 * Loads all sections and all articles (WARNING: may eat a lot of memory, use with care)
	 * @return loaded section with all subsections and articles
	 */
	public static Section loadAllSectons() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();

		// load full tree
		Query q=session.createQuery("from Section s left join fetch s.sections left join fetch s.articles");
		@SuppressWarnings("unchecked")
		List<Section> result=(List<Section>)q.list();

		Section root=null;
		for (Section s:result) {
			if (s.getParent()==null){
				root=s;
				break;
			}
		}
		return root;
	}

	/**
	 * Removes all sections and articles from database
	 */
	public static void removeAllSections() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		
		Query q=session.createQuery("from Section");
		@SuppressWarnings("unchecked")
		List<Section> sections=(List<Section>)q.list();
		for(Section s:sections){
			session.delete(s);
		}
		
	}

	/**
	 * Load one level of hierarchy from ROOT of section 
	 * @return section and one level of subsections and one level of articles
	 */
	public static Section loadSectionRootLevel() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		Query q=session.createQuery("from Section s left join fetch s.sections left join fetch s.articles where s.parent is null");
		Section result=(Section)q.uniqueResult();
		return result;
	}
	
	/**
	 * Load one level of hierarchy from given section (NOTE: you should use it everywhere)
	 * @param currentSection - section to load its contents
	 * @return section with loaded subsections and articles
	 */
	public static Section loadSectionOneLevel(Section currentSection) {
		org.hibernate.Hibernate.initialize(currentSection.getSections());
		org.hibernate.Hibernate.initialize(currentSection.getArticles());
		return currentSection;
	}

	/**
	 * Find section by its id
	 * @param id - id of section to search
	 * @return section with given id, or null if there is no such section
	 */
	public static Section findSectionById(Long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Section result=(Section)session.get(Section.class, id);
		return result;
	}

	
	/**
	 * Find section by its shortname in database
	 * @param shortName - which we will ind
	 * @return section with given shortname, or null if there is no such section
	 */
	public static Section findSectionByShortName(String shortName) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		Section result =(Section) session.createQuery("from Section s where s.shortName=:shortName").setParameter("shortName", shortName).uniqueResult();
		return result;
	}

	/**
	 * Removes section from database
	 * It clearly removes current section from parent subsections and updates parent
	 * @param section - section to remove
	 */
	public static void deleteSection(Section section) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Section parent=section.getParent();
		if (parent!=null){
			parent.removeSection(section);
			session.update(parent);
		}
		session.delete(section);
	}

	/**
	 * Load article from database
	 * @param currentArticle - article to load
	 * @return fresh instance of article from database
	 */
	public static Article loadArticle(Article currentArticle) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		Query q=session.createQuery("from Article a where a=:article").setParameter("article", currentArticle);
		Article result=(Article)q.uniqueResult();
		return result;
	}
	
	/**
	 * Save given article to database
	 * @param article - article to save
	 */
	public static void saveArticle(Article article) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.save(article);
		
	}

	/**
	 * Merges current article to hibernate session
	 * @param article - given article
	 * @return - merged article
	 */
	public static Article mergeArticle(Article article){
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		article=(Article)session.merge(article);
		return article;
	}
	
	/**
	 * Removes article from database
	 * It clearly removes current article from parent articles collection and updates parent section
	 * @param article
	 */
	public static void deleteArticle(Article article){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Section parent=article.getSection();
		if (parent!=null){
			parent.removeArticle(article);
			session.update(parent);
		}
		
		session.delete(article);
		
	}

	public static void updateSection(Section section) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.update(section);
	}

	public static void updateArticle(Article article) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.update(article);
	}

}
