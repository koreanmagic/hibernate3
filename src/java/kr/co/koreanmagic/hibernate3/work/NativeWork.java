package kr.co.koreanmagic.hibernate3.work;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

public interface NativeWork<T> {

	T execute(Connection connection) throws SQLException;
	
}
