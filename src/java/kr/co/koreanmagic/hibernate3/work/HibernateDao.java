package kr.co.koreanmagic.hibernate3.work;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;

import kr.co.koreanmagic.hibernate3.work.NativeWork;
import kr.co.koreanmagic.web.dao.Dao;

public interface HibernateDao<T, P extends Serializable> extends Dao<T, P> {
	
	<V> V findByCriteria(boolean isArray, Criterion...criterion);
	<V> V doWork(NativeWork<V> work);
	<V> V query(boolean isArray, String query);
}
