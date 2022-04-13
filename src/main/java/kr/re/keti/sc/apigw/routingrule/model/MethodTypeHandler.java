package kr.re.keti.sc.apigw.routingrule.model;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.http.HttpMethod;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MethodTypeHandler extends BaseTypeHandler<HttpMethod> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, HttpMethod httpMethod, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,httpMethod.name());
    }

    @Override
    public HttpMethod getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String method = resultSet.getString(s);
        if(method == null) {
            return null;
        }
        HttpMethod resolveMethod = HttpMethod.resolve(method);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + method + "]");
        }
        return resolveMethod;
    }

    @Override
    public HttpMethod getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String method = resultSet.getString(i);
        if(method == null) {
            return null;
        }
        HttpMethod resolveMethod = HttpMethod.resolve(method);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + method + "]");
        }
        return resolveMethod;
    }

    @Override
    public HttpMethod getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String method = callableStatement.getString(i);
        if(method == null) {
            return null;
        }
        HttpMethod resolveMethod = HttpMethod.resolve(method);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + method + "]");
        }
        return resolveMethod;
    }
}
