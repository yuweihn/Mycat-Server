package io.mycat.backend.mysql.nio.handler;


import java.util.List;


/**
 * 查询分解后的第二部处理
 * @author huangyiming
 *
 */
public interface SecondHandler {
	void doExecute(List params);
}
