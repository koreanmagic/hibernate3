package kr.co.koreanmagic.hibernate3.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import kr.co.koreanmagic.hibernate3.work.NativeWork;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/*
 * 엔터티 클래스 형정보와, SessionFactory만 주입받으면
 * 하이버네이트3의 기본 API를 모두 사용할 수 있게 만들어주는 abstract Class
 */
public abstract class AbstractHibernate3Dao<T, P extends Serializable> implements HibernateDao<T, P> {

	private Class<T> persistentClass;
	
	protected abstract SessionFactory getFactory();
	
	protected AbstractHibernate3Dao(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}
	
	@Override
	public Class<T> getPersistentClass() {
		return this.persistentClass;
	}
	
	@Override
	public T findById(P id) {
		return findById(id, false);
	}
	
	@SuppressWarnings("unchecked")
	public T findById(P id, boolean readOnly) {
		Session session = currentSession(); 
		session.setDefaultReadOnly(readOnly);
		return (T)session.get(getPersistentClass(), id );
	}

	@Override
	public List<T> findAll() {
		return findByCriteria(false);
	}

	
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String... excludeProperty) {
		Criteria criteria = currentSession().createCriteria(getPersistentClass());
		Example example = Example.create(exampleInstance);
		for(String exclude : excludeProperty)
			example.excludeProperty(exclude);
		criteria.add(example);
		return criteria.list();
	}

	@Override
	public T makePersistent(T entity) {
		currentSession().saveOrUpdate(entity);
		return entity;
	}

	@Override
	public void makeTransient(T entity) {
		currentSession().delete(entity);
	}
	
	@Override
	public void refresh(T entity) {
		currentSession().refresh(entity);
	}

	@Override
	public void flush() {
		currentSession().flush();
	}

	@Override
	public void clear() {
		currentSession().clear();
	}
	
	@Override
	public long rowCount() {
		return (long)criteria().setProjection(Projections.rowCount()).uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public<V> V query(boolean isArray, String query) {
		if(isArray)
			return (V)currentSession().createQuery(query).list();
		return (V)currentSession().createQuery(query).uniqueResult();
	}
	
	@Override
	public List<T> getList(int start, int limit, String orderBy) {
		return getList(criteria(), start, limit, orderBy);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getList(Criteria criteria, int start, int limit, String orderBy) {
		
		criteria.setFirstResult(start).setMaxResults(limit);
		
		System.out.println(orderBy);
		
		if(orderBy != null) {
			Order order = null;
			
			if(orderBy.startsWith("<")) { // DESC (내림차순 - 큰값부터/하파타 순)
				orderBy = orderBy.substring(1);
				order = Order.desc(orderBy);
			}
			else if (orderBy.startsWith(">")) { // ASC (오름차순 - 작은값부터/가나다순) ※기본값
				orderBy = orderBy.substring(1);
				order = Order.asc(orderBy);
			}
			else order = Order.asc(orderBy);	// ※기본값 ASC
			
			criteria.addOrder(order);
		} 
		
		return criteria.list();
	}
	
	
	/* ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒  Hibernate API  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ */
	
	
	
	protected Session currentSession() {
		return getFactory().getCurrentSession();
	}
	protected Session openSession() {
		return getFactory().openSession();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public<V> V findByCriteria(boolean isUnique, Criterion...criterion) {
		Criteria criteria = criteria();
		
		for(Criterion c : criterion)
			criteria.add(c);
		return isUnique ? (V)criteria.uniqueResult() : (V)criteria.list();
	}
	

	protected Criteria criteria() {
		return currentSession().createCriteria(getPersistentClass());
	}
	
	/* Connection을 직접받아서 작업하는 NativeWork */
	@Override
	@SuppressWarnings("unchecked")
	public<V> V doWork(NativeWork<V> work) {
		final Object[] obj = new Object[1];
		
		currentSession().doWork(con -> {
			obj[0] = work.execute(con);
		});
		return (V)obj[0];
	}

	
}
