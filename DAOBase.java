/**
 *
 */
package mobi.softpay.pvi.portal.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.softpay.pvi.portal.model.Projection;
import mobi.softpay.pvi.portal.model.ProjectionType;
import mobi.softpay.pvi.portal.model.SearchFormBase;
import mobi.softpay.pvi.portal.model.Sort;
import mobi.softpay.pvi.portal.security.AuthorizedService;
import mobi.softpay.pvi.common.beans.BasicTable;
import mobi.softpay.pvi.common.beans.Department;
import mobi.softpay.pvi.common.beans.Users;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;



public abstract class DAOBase<T extends BasicTable> {    
    
    
	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSessionFactory() {
		return this.sessionFactory.getCurrentSession();
	}

	protected DateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
	protected DateFormat hourFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");

	private Class<T> entityClass;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	public DAOBase() {
		this.entityClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Session currentSession() {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (HibernateException he) {
			session = sessionFactory.openSession();
		}
		return session;
	}

	public Criteria getCriteria() {
		Criteria criteria = currentSession().createCriteria(entityClass);
		return criteria;
	}

	public Criteria getCriteriaByEntity(Class clazz) {
		Criteria criteria = currentSession().createCriteria(clazz);
		return criteria;
	}

	public Criteria getCriteriaByEntitySet(Class clazz,String set) {
		Criteria criteria = currentSession().createCriteria(clazz,set);
		return criteria;
	}

	public T save(T t) {
		if (t.getId() != null) {
			currentSession().update(t);
		} else {
			currentSession().save(t);
		}
		return t;
	}

	public void delete(T t) {
		currentSession().delete(t);
	}

	//@SuppressWarnings("unchecked")
	public T findByPk(Serializable id) {
		return (T) currentSession().get(entityClass, id);
	}

	//@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return getCriteria().list();
	}

	protected long countBySearchForm(Criteria criteria, SearchFormBase searchForm){
		if (searchForm.isGroupProjection()) {
			this.applyProjection(criteria, searchForm);
			return criteria.list().size();
		}
		criteria.setProjection(Projections.countDistinct("id"));
		Object rt = criteria.uniqueResult();
		if(rt==null) return 0l;
		return (long)rt;
	}

	protected void applyProjection(Criteria criteria, SearchFormBase searchForm) {
		// projection
		if (!searchForm.getProjectionList().isEmpty()) {
			ProjectionList projectionList = Projections.projectionList();
			for (Projection p : searchForm.getProjectionList()) {
				if (p.getType().equals(ProjectionType.GROUP)) {
					projectionList.add(
							Projections.groupProperty(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.COUNT)) {
					projectionList.add(Projections.count(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.SUM)) {
					projectionList.add(Projections.sum(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.PROPERTY)) {
					projectionList.add(Projections.property(p.getProperty()),
							p.getAlias());
				}
			}
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Transformers.aliasToBean(entityClass));
		}
	}

	public void applySearchFormToCriteria(Criteria criteria,
			SearchFormBase searchForm) {

		criteria.setMaxResults(searchForm.getPageSize());
		criteria.setFirstResult(searchForm.getPageIndex() * searchForm.getPageSize());

		// projection
		if (!searchForm.getProjectionList().isEmpty()) {
			ProjectionList projectionList = Projections.projectionList();
			for (Projection p : searchForm.getProjectionList()) {
				if (p.getType().equals(ProjectionType.GROUP)) {
					projectionList.add(
							Projections.groupProperty(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.COUNT)) {
					projectionList.add(Projections.rowCount(), p.getAlias());
				}
                                if(p.getType().equals(ProjectionType.COUNT_DISTINCT)){
                                    projectionList.add(Projections.countDistinct(p.getProperty()), p.getAlias());
                                }
				if (p.getType().equals(ProjectionType.SUM)) {
					projectionList.add(Projections.sum(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.PROPERTY)) {
					projectionList.add(Projections.property(p.getProperty()),
							p.getAlias());
				}
			}
			if(searchForm.isGroupProjection() && !searchForm.hasGroupkey()){

			}
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Transformers.aliasToBean(entityClass));
		}else{
                    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                }

		// sort
		if (!searchForm.getSortList().isEmpty()) {
			for (Sort sort : searchForm.getSortList()) {
				if (sort.isAsc()) {
					criteria.addOrder(Order.asc(sort.getProperty()));
				} else {
					criteria.addOrder(Order.desc(sort.getProperty()));
				}
			}
		} else {
			if (!searchForm.isGroupProjection()) {
				criteria.addOrder(Order.desc("id"));
			}
		}
	}

	public void applySearchFormToCriteriaToGetDTO(Criteria criteria,
			SearchFormBase searchForm, Class transformBeanClass) {
		criteria.setMaxResults(searchForm.getPageSize());
		criteria.setFirstResult(searchForm.getPageIndex()
				* searchForm.getPageSize());

		ProjectionList projectionList = Projections.projectionList();

		// projection
		if (!searchForm.getProjectionList().isEmpty()) {
			for (Projection p : searchForm.getProjectionList()) {
				if (p.getType().equals(ProjectionType.GROUP)) {
					projectionList.add(
							Projections.groupProperty(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.COUNT)) {
					projectionList.add(Projections.count(p.getProperty()),
							p.getAlias());
				}
                                if(p.getType().equals(ProjectionType.COUNT_DISTINCT)){
                                    projectionList.add(Projections.countDistinct(p.getProperty()), p.getAlias());
                                }
				if (p.getType().equals(ProjectionType.SUM)) {
					projectionList.add(Projections.sum(p.getProperty()),
							p.getAlias());
				}
				if (p.getType().equals(ProjectionType.PROPERTY)) {
					projectionList.add(Projections.property(p.getProperty()),
							p.getAlias());
				}
			}
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Transformers
					.aliasToBean(transformBeanClass));
		}

		// sort
		if (!searchForm.getSortList().isEmpty()) {
			for (Sort sort : searchForm.getSortList()) {
				if (sort.isAsc()) {
					criteria.addOrder(Order.asc(sort.getProperty()));
				} else {
					criteria.addOrder(Order.desc(sort.getProperty()));
				}
			}
		} else {
			if (!searchForm.isGroupProjection()) {
				criteria.addOrder(Order.desc("id"));
			}
		}
	}
}
