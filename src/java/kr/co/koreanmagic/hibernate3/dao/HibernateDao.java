package kr.co.koreanmagic.hibernate3.dao;

import java.io.Serializable;

import kr.co.koreanmagic.hibernate3.work.NativeWork;
import kr.co.koreanmagic.web.dao.Dao;

import org.hibernate.criterion.Criterion;

public interface HibernateDao<T, P extends Serializable> extends Dao<T, P> {
	
	<V> V findByCriteria(boolean isArray, Criterion...criterion);
	<V> V doWork(NativeWork<V> work);
	<V> V query(boolean isArray, String query);
}
