package dao;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import domain.Section;

import utils.HibernateUtil;


public class SectionDAO {

	public static void beginTransaction() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	}

	public static void commitTransaction() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().commit();
	}

	public static void save(Section root) {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();
		session.save(root);
	}
	
	public static Section load() {
		Session session=HibernateUtil.getSessionFactory().getCurrentSession();

		// load full tree
		//Query q=session.createQuery("from Section s left join fetch s.sections left join fetch s.articles order by s.parent asc");
		Query q=session.createQuery("from Section s");
		@SuppressWarnings("unchecked")
		List<Section> result=(List<Section>)q.list();

		return result.get(0);
		
	}

}
