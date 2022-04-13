package kr.re.keti.sc.apigw.routingrule.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class BooleanTypeHandler extends BaseTypeHandler<Boolean> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
			throws SQLException {
		if (parameter != null) {
			if (parameter == true) {
				ps.setString(i, "Y");
			} else {
				ps.setString(i, "N");
			}
		} else {
			ps.setString(i, "Y");
		}
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String result = rs.getString(columnName);
		if (result != null && result.equalsIgnoreCase("N")) {
			return false;
		}
		return true;
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String result = rs.getString(columnIndex);
		if (result != null && result.equalsIgnoreCase("N")) {
			return false;
		}
		return true;
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String result = cs.getString(columnIndex);
		if (result != null && result.equalsIgnoreCase("N")) {
			return false;
		}
		return true;
	}
}