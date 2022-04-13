package kr.re.keti.sc.apigw.routingrule.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class RoutingRuleDetailTypeTypeHandler extends EnumTypeHandler<RoutingRuleDetailType> {

	public RoutingRuleDetailTypeTypeHandler(Class<RoutingRuleDetailType> type) {
		super(type);
	}
	
	@Override
	public void setParameter(PreparedStatement ps, int i, RoutingRuleDetailType parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			ps.setString(i, parameter.getCode());
		} else {
			ps.setString(i, null);
		}
	}
	
	@Override
	public RoutingRuleDetailType getResult(ResultSet rs, String columnName) throws SQLException {
		String result = rs.getString(columnName);
		try {
			return RoutingRuleDetailType.parseCodeString(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	@Override
	public RoutingRuleDetailType getResult(ResultSet rs, int columnIndex) throws SQLException {
		String result = rs.getString(columnIndex);
		try {
			return RoutingRuleDetailType.parseCodeString(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	@Override
	public RoutingRuleDetailType getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String result = cs.getString(columnIndex);
		try {
			return RoutingRuleDetailType.parseCodeString(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

}
