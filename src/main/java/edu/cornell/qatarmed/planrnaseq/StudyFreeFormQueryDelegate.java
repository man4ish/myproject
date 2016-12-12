/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.planrnaseq;

/**
 *
 * @author pak2013
 */



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.data.Container.Filter;

@SuppressWarnings("serial")
public class StudyFreeFormQueryDelegate implements FreeformStatementDelegate {

    private List<Filter> filters;
    private List<OrderBy> orderBys;
    private String query;

     public StudyFreeFormQueryDelegate(){
      // this.query = query; 
    }
    public StudyFreeFormQueryDelegate(String query){
       this.query = query; 
    }
    
   
    
    @Deprecated
    public String getQueryString(int offset, int limit)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Use getQueryStatement method.");
    }

    public StatementHelper getQueryStatement(int offset, int limit)
            throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
       // StringBuffer query = new StringBuffer("SELECT * FROM study_summary ");
         StringBuffer query = new StringBuffer(this.query);
        if (filters != null) {
            query.append(QueryBuilder.getWhereStringForFilters(
                    filters, sh));
        }
        query.append(getOrderByString());
        if (offset != 0 || limit != 0) {
            query.append(" LIMIT ").append(limit);
            query.append(" OFFSET ").append(offset);
        }
        sh.setQueryString(query.toString());
        System.out.println("my query is: " + query);
        return sh;
    }

    private String getOrderByString() {
        StringBuffer orderBuffer = new StringBuffer("");
        if (orderBys != null && !orderBys.isEmpty()) {
            orderBuffer.append(" ORDER BY ");
            OrderBy lastOrderBy = orderBys.get(orderBys.size() - 1);
            for (OrderBy orderBy : orderBys) {
                orderBuffer.append(SQLUtil.escapeSQL(orderBy.getColumn()));
                if (orderBy.isAscending()) {
                    orderBuffer.append(" ASC");
                } else {
                    orderBuffer.append(" DESC");
                }
                if (orderBy != lastOrderBy) {
                    orderBuffer.append(", ");
                }
            }
        }
        return orderBuffer.toString();
    }

    @Deprecated
    public String getCountQuery() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Use getCountStatement method.");
    }

    public StatementHelper getCountStatement()
            throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM study_summary ");
        if (filters != null) {
            query.append(QueryBuilder.getWhereStringForFilters(
                    filters, sh));
        }
        sh.setQueryString(query.toString());
        return sh;
    }

    public void setFilters(List<Filter> filters)
            throws UnsupportedOperationException {
        this.filters = filters;
    }

    public void setOrderBy(List<OrderBy> orderBys)
            throws UnsupportedOperationException {
        this.orderBys = orderBys;
    }

    public int storeRow(Connection conn, RowItem row) throws SQLException {
        PreparedStatement statement = null;
        if (row.getId() instanceof TemporaryRowId) {
            statement = conn
                    .prepareStatement("INSERT INTO study_summary VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            setRowValues(statement, row);
        } else {
            statement = conn
                    .prepareStatement("UPDATE PEOPLE SET FIRSTNAME = ?, LASTNAME = ?, COMPANY = ?, MOBILE = ?, WORKPHONE = ?, HOMEPHONE = ?, WORKEMAIL = ?, HOMEEMAIL = ?, STREET = ?, ZIP = ?, CITY = ?, STATE = ?, COUNTRY = ? WHERE ID = ?");
            setRowValues(statement, row);
            statement
                    .setInt(14, (Integer) row.getItemProperty("ID").getValue());
        }

        int retval = statement.executeUpdate();
        statement.close();
        return retval;
    }

    private void setRowValues(PreparedStatement statement, RowItem row)
            throws SQLException {
        statement.setString(1, (String) row.getItemProperty("FIRSTNAME")
                .getValue());
        statement.setString(2, (String) row.getItemProperty("LASTNAME")
                .getValue());
        statement.setString(3, (String) row.getItemProperty("COMPANY")
                .getValue());
        statement.setString(4, (String) row.getItemProperty("MOBILE")
                .getValue());
        statement.setString(5, (String) row.getItemProperty("WORKPHONE")
                .getValue());
        statement.setString(6, (String) row.getItemProperty("HOMEPHONE")
                .getValue());
        statement.setString(7, (String) row.getItemProperty("WORKEMAIL")
                .getValue());
        statement.setString(8, (String) row.getItemProperty("HOMEEMAIL")
                .getValue());
        statement.setString(9, (String) row.getItemProperty("STREET")
                .getValue());
        statement.setString(10, (String) row.getItemProperty("ZIP").getValue());
        statement
                .setString(11, (String) row.getItemProperty("CITY").getValue());
        statement.setString(12, (String) row.getItemProperty("STATE")
                .getValue());
        statement.setString(13, (String) row.getItemProperty("COUNTRY")
                .getValue());
    }

    public boolean removeRow(Connection conn, RowItem row)
            throws UnsupportedOperationException, SQLException {
        PreparedStatement statement = conn
                .prepareStatement("DELETE FROM study_summary WHERE Study = ?");
        //statement.setInt(1, (Integer) row.getItemProperty("ID").getValue());
        int rowsChanged = statement.executeUpdate();
        statement.close();
        return rowsChanged == 1;
    }

    @Deprecated
    public String getContainsRowQueryString(Object... keys)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Please use getContainsRowQueryStatement method.");
    }

    public StatementHelper getContainsRowQueryStatement(Object... keys)
            throws UnsupportedOperationException {
        // this function is not executed according to my tests. Not important
        //May be I will revisit this when needed
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(
          "SELECT * FROM study_summary WHERE Study = ?");
     //    StringBuffer query = new StringBuffer(this.query + " and Study = ?"); // this has no effect
        sh.addParameterValue(keys[0]);
        sh.setQueryString(query.toString());
         System.out.println("this query is: " + query);
        return sh;
    }
}