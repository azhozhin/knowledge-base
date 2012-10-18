import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

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
		// sort by Section.root property - this should only one
		Query q=session.createQuery("from Section s order by s.root desc");
		List<Section> result=(List<Section>)q.list();
	
		return result.get(0);
	}

}
