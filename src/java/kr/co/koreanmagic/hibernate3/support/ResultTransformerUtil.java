package kr.co.koreanmagic.hibernate3.support;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class ResultTransformerUtil {

	
	/*
	 *  일반 빈을 받아 SELECT 스칼라쿼리를 생성해서 반환한다.
	 *  
	 *  단 필드에 애노테이션 @Column(name=V) 붙어있어야 한다.
	 *  @Transient 애노테이션이 있으면 무시한다.
	 */
	public static SQLQuery createScala(Session session, Class<?> bean) {
		
		StringBuilder sb = new StringBuilder("SELECT ");
		Map<String, String> nameMap = new HashMap<>();
		Column columnAnno = null;
		Transient pass = null;
		
		String fieldName = null,
				tableName = bean.getAnnotation(Table.class).name();
		
		
		
		try {
			
			Field[] fields = bean.getDeclaredFields();
			
			for(Field field : fields) {
				
				fieldName = field.getName();
				
				columnAnno = field.getAnnotation(Column.class);
				pass = field.getAnnotation(Transient.class);
				
				if(pass == null) {
					if(columnAnno != null)
						nameMap.put(columnAnno.name(), fieldName);
					else
						nameMap.put(fieldName, fieldName);
				} 
				
				columnAnno = null; pass = null;
				
			}
			
			createScala(sb, nameMap).append(" FROM ").append(tableName);
			return addScala(session.createSQLQuery(sb.toString()), nameMap);
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private static StringBuilder createScala(StringBuilder sb, Map<String, String> map) {

		for(Entry<String, String> entry : map.entrySet())
			sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
		
		return sb.delete(sb.length() - ", ".length(), sb.length());
	}
	
	private static SQLQuery addScala(SQLQuery query, Map<String, String> map) {
		for(Entry<String, String> entry : map.entrySet())
			query.addScalar(entry.getValue());
		return query;
	}
}
