package kr.re.keti.sc.apigw.routingrule.model;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;


public class StringArrayListTypeHandler extends BaseTypeHandler<ArrayList<String>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, ArrayList<String> parameter, JdbcType jdbcType)
			throws SQLException {
		if (parameter == null || parameter.size() == 0) {
			ps.setArray(i, null);
			return;
		}

		String[] parameterStringArray = new String[parameter.size()];
		for (int j = 0; j < parameter.size(); j++) {
			parameterStringArray[j] = parameter.get(j);
		}

		ps.setArray(i, ps.getConnection().createArrayOf("varchar", parameterStringArray));
	}

	@Override
	public ArrayList<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getArrayListFromSqlArray(rs.getArray(columnName));
	}

	@Override
	public ArrayList<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getArrayListFromSqlArray(rs.getArray(columnIndex));
	}

	@Override
	public ArrayList<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getArrayListFromSqlArray(cs.getArray(columnIndex));
	}

	private ArrayList<String> getArrayListFromSqlArray (Array array) throws SQLException {
	if (array == null) {
		return null;
	}
	
	String [] dataStringArray = (String []) array.getArray();
	if (dataStringArray == null) {
		return null;
	}
	
	ArrayList <String> dataStringArrayList = new ArrayList<String> ();
	for (int i=0;i < dataStringArray.length;i++) {
		dataStringArrayList.add(dataStringArray[i]);
	}
	
	return dataStringArrayList;
}

//	@Override
//	public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
//			throws SQLException {
//		
//		if(parameter == null || parameter.size() == 0) {
//			ps.setArray(i, null);
//			return;
//		}
//		
//		String [] parameterStringArray = new String [parameter.size()];
//		for (int j=0;j<parameter.size();j++) {
//			parameterStringArray[j] = parameter.get(j);
//		}
//		
//		ps.setArray(i, ps.getConnection().createArrayOf("varchar", parameterStringArray));
//	}
//
//	@Override
//	public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
//		return getArrayListFromSqlArray(rs.getArray(columnName));
//	}
//
//	@Override
//	public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//		return getArrayListFromSqlArray(rs.getArray(columnIndex));
//	}
//
//	@Override
//	public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//		return getArrayListFromSqlArray(cs.getArray(columnIndex));
//	}
//	
//	private List<String> getArrayListFromSqlArray (Array array) throws SQLException {
//		if (array == null) {
//			return null;
//		}
//		
//		String [] dataStringArray = (String []) array.getArray();
//		if (dataStringArray == null) {
//			return null;
//		}
//		
//		List <String> dataStringArrayList = new ArrayList<String> ();
//		for (int i=0;i < dataStringArray.length;i++) {
//			dataStringArrayList.add(dataStringArray[i]);
//		}
//		
//		return dataStringArrayList;
//	}
	
}
