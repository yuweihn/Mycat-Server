package io.mycat.backend.mysql.nio.handler;


import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import java.util.List;


/**
 * 中间结果处理器
 * @author huangyiming
 *
 * @param <T>
 */
public interface MiddlerResultHandler<T> {
	List<SQLCharExpr> getResult();
	void add(T t );
	String getDataType();
	void secondEexcute();
}
