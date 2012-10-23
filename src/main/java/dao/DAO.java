package dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Article;
import domain.Section;
import utils.HibernateUtil;

/**
 * DAO class is singleton class for accessing data via hibernate 
 *
 */
public class DAO {
	static Logger logger = LoggerFactory.getLogger(DAO.class);
	private static DAO instance;
	
	private DAO(){
		
	}
	
	/**
	 * Get singleton instance of DAO
	 * @return instance
	 */
	public static synchronized DAO getInstance(){
		if (instance==null){
			instance=new DAO();
		}
		return instance;
	}

	/**
	 * Begin hibernate transaction
	 */
	public void beginTransaction() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	/**
	 * Commit hibernate transaction
	 */
	public void commitTransaction() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().commit();
	}

	/**
	 * Load one level of hierarchy from ROOT of section 
	 * @return section and one level of subsections and one level of articles
	 */
	public Section loadSectionRootLevel() {
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
	public Section loadSectionOneLevel(Section currentSection) {
		org.hibernate.Hibernate.initialize(currentSection.getSections());
		org.hibernate.Hibernate.initialize(currentSection.getArticles());
		return currentSection;
	}
	
	/**
	 * Loads all sections and all articles (WARNING: may eat a lot of memory, use with care)
	 * @return loaded section with all subsections and articles
	 */
	public Section loadAllSectons() {
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
	 * Save given section to database 
	 * @param section - section to save
	 */
	public void saveSection(Section section) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.save(section);
	}
	
	/**
	 * Merge section to hibernate session
	 * @param root - root section to merge
	 * @return merged root section
	 */
	public Section mergeSection(Section root) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		root=(Section)session.merge(root);
		return root;
	}

	/**
	 * Update section with hibernate session
	 * @param section
	 */

	public void updateSection(Section section) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.update(section);
	}
	


	/**
	 * Removes section from database
	 * It clearly removes current section from parent subsections and updates parent
	 * @param section - section to remove
	 */
	public void deleteSection(Section section) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Section parent=section.getParent();
		if (parent!=null){
			parent.removeSection(section);
			session.update(parent);
		}
		session.delete(section);
	}
	
	/**
	 * Removes all sections and articles from database
	 */
	public void removeAllSections() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		
		Query q=session.createQuery("from Section");
		@SuppressWarnings("unchecked")
		List<Section> sections=(List<Section>)q.list();
		for(Section s:sections){
			session.delete(s);
		}
		
	}

	

	/**
	 * Find section by its id
	 * @param id - id of section to search
	 * @return section with given id, or null if there is no such section
	 */
	public Section findSectionById(Long id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Section result=(Section)session.get(Section.class, id);
		return result;
	}
	
	/**
	 * Find section by its shortname in database
	 * @param shortName - which we will ind
	 * @return section with given shortname, or null if there is no such section
	 */
	public Section findSectionByShortName(String shortName) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		Section result =(Section) session.createQuery("from Section s where s.shortName=:shortName").setParameter("shortName", shortName).uniqueResult();
		return result;
	}

	

	/**
	 * Load article from database
	 * @param currentArticle - article to load
	 * @return fresh instance of article from database
	 */
	public Article loadArticle(Article currentArticle) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		Query q=session.createQuery("from Article a where a=:article").setParameter("article", currentArticle);
		Article result=(Article)q.uniqueResult();
		return result;
	}
	
	/**
	 * Save given article to database
	 * @param article - article to save
	 */
	public void saveArticle(Article article) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.save(article);
		
	}

	/**
	 * Merges current article to hibernate session
	 * @param article - given article
	 * @return - merged article
	 */
	public Article mergeArticle(Article article){
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		article=(Article)session.merge(article);
		return article;
	}
	
	/**
	 * Update article with hibernate session
	 * @param article - article to update
	 */

	public void updateArticle(Article article) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.update(article);
	}
	
	/**
	 * Removes article from database
	 * It clearly removes current article from parent articles collection and updates parent section
	 * @param article
	 */
	public void deleteArticle(Article article){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Section parent=article.getSection();
		if (parent!=null){
			parent.removeArticle(article);
			session.update(parent);
		}
		
		session.delete(article);
		
	}
	

	/**
	 * Manually reindex all entities with full text support 
	 * @throws InterruptedException
	 */
	public void reindex() throws InterruptedException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		fullTextSession.createIndexer().startAndWait();
	}

	/**
	 * Does fulltext search on Article entity
	 * @param searchString - input string with words
	 * @return list of found articles
	 */
	public List<Article> articleFullTextSearch(String searchString){
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		Transaction tx = fullTextSession.beginTransaction();

		QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( Article.class ).get();
		org.apache.lucene.search.Query query = qb.keyword().onFields("text").matching(searchString).createQuery();

		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.Query hibQuery =  fullTextSession.createFullTextQuery(query, Article.class);

		// execute search
		@SuppressWarnings("unchecked")
		List<Article> result = (List<Article>)hibQuery.list();
		  
		tx.commit();
		//session.close();
		return result;
	}
}
