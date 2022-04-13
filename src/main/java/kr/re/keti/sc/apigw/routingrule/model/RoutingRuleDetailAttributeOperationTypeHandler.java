package kr.re.keti.sc.apigw.routingrule.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class RoutingRuleDetailAttributeOperationTypeHandler extends EnumTypeHandler<RoutingRuleDetailAttributeOperation> {

	public RoutingRuleDetailAttributeOperationTypeHandler(Class<RoutingRuleDetailAttributeOperation> type) {
		super(type);
	}
	
	@Override
	public void setParameter(PreparedStatement ps, int i, RoutingRuleDetailAttributeOperation parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			ps.setString(i, parameter.getCode());
		} else {
			ps.setString(i, null);
		}
	}
	
	@Override
	public RoutingRuleDetailAttributeOperation getResult(ResultSet rs, String columnName) throws SQLException {
		String result = rs.getString(columnName);
		try {
			return RoutingRuleDetailAttributeOperation.parseCodeString(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	@Override
	public RoutingRuleDetailAttributeOperation getResult(ResultSet rs, int columnIndex) throws SQLException {
		String result = rs.getString(columnIndex);
		try {
			return RoutingRuleDetailAttributeOperation.parseCodeString(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	@Override
	public RoutingRuleDetailAttributeOperation getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String result = cs.getString(columnIndex);
		try {
			return RoutingRuleDetailAttributeOperation.parseCodeString(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

}
