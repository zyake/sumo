package zyake.libs.sumo.mappers;

import zyake.libs.sumo.SQL;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CollectionMapper implements SQL.RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> map(ResultSet resultSet) throws SQLException, MappingFailedException {
        Map<String, Object> resultMap = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for ( int i = 1 ; i <= metaData.getColumnCount() ; i ++ ) {
            String colName = metaData.getColumnName(i);
            resultMap.put(colName.toUpperCase(),  resultSet.getObject(i));
        }

        return Collections.unmodifiableMap(resultMap);
    }
}
