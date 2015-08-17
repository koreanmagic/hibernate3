package kr.co.koreanmagic.hibernate3.service;

import java.sql.Date;
import java.time.LocalDate;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/*
 * 자주 사용되는 조회를 미리 만들어뒀다.
 */
public class RestrictionCommon {
	
	
	public static Criterion betweenDate(String propertyName, String start, String end) {
		return betweenDate(propertyName, LocalDate.parse(start), LocalDate.parse(end));
	}
	public static Criterion betweenDate(String propertyName, LocalDate start, LocalDate end) {
		return betweenDate(propertyName, Date.valueOf(start), Date.valueOf(end));
	}
	public static Criterion betweenDate(String propertyName, Date start, Date end) {
		return Restrictions.between(propertyName, start, end);
	}
	
	

}
