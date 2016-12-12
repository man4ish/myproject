/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.planrnaseq;

import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pankajkumar
 */
@Title("AnnotateRNAseq")
public class AnnotateRNAseq extends UI {

    private Table rnaseqTable = new Table();
    TextField studyName = new TextField("RNAseq Study Name");
    Tree tree = new Tree("RNA-Seq Projects");

    @Override
    protected void init(VaadinRequest request) {
        initLayout();
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initLayout() {

        /* Root of the user interface component tree is set */
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        setContent(splitPanel);

        /* Build the component tree */
        VerticalLayout leftLayout = new VerticalLayout();
        VerticalLayout rightLayout = new VerticalLayout();

        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(rightLayout);

        /*
         //make form asking parameters and add it to leftLaayout
         VerticalLayout formLayout = new VerticalLayout();
         // TextField studyName = new TextField("RNAseq Study Name");
         formLayout.addComponent(studyName);
         List replist = new ArrayList();
         ComboBox numReplicates = new ComboBox("Replicates", replist);
         formLayout.addComponent(numReplicates);
         leftLayout.addComponent(formLayout);
         */
        leftLayout.addComponent(tree);
        /* Set the contents in the left of the split panel to use all the space */
        leftLayout.setSizeFull();

        /*        VerticalLayout resultLayout = new VerticalLayout();
         rightLayout.addComponent(resultLayout);
         VerticalLayout chartLayout = new VerticalLayout();
         rightLayout.addComponent(chartLayout);
        
         chartLayout.setVisible(false);
         */
        initRNAseqTable();

    }

   

    private void initRNAseqTable() {
        SQLContainer rnaseqContainer = createMySQLContainer("bioproject_summary");
        rnaseqTable.setContainerDataSource(rnaseqContainer);
        //   rnaseqTable.setVisibleColumns(new String[] { studyName });
        rnaseqTable.setSelectable(true);
        rnaseqTable.setImmediate(true);
        studyName.setValue(rnaseqContainer.firstItemId().toString());
      //  List bioprojects = new ArrayList();
        // bioprojects = rnaseqContainer.getItemIds();
        for (int i = 0; i < rnaseqContainer.getItemIds().size(); i++) {
            tree.addItem(rnaseqContainer.getIdByIndex(i).toString());
          // tree.setParent(moon, planet);

        }

        rnaseqTable.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Object contactId = rnaseqTable.getValue();

//Binding data
//When a contact is selected from the list, we want to show that in our editor on the right. This is nicely done by the FieldGroup that binds all the fields to the corresponding Properties in our contact at once.                                if (contactId != null)
                //                           editorFields.setItemDataSource(rnaseqTable.getItem(contactId));
                //                  editorLayout.setVisible(contactId != null);
            }
        });
    }

    private static SQLContainer createMySQLContainer(String dataTable) {
        //  TableQuery query = null;
        SQLContainer container = null;
        try {
            System.out.println("1. Trying to connect to Mysql RNA database");
            SimpleJDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost:3306/rna", "rnaseq",
                    "rna", 2, 5);
          //  query = new TableQuery("customers", connectionPool);
            // query.setVersionColumn("id");

            // temp = new SQLContainer(query);
            /*
            container = new SQLContainer(new FreeformQuery(
                            "SELECT * FROM bioproject_summary",
                            connectionPool, "Bioproject"));
            
         */
            switch (dataTable) {
                case "bioproject_summary":
                    container = new SQLContainer(new FreeformQuery(
                            "SELECT * FROM bioproject_summary",
                            connectionPool, "Bioproject"));
                    break;
                case "sra_rnaseq":
                    container = new SQLContainer(new FreeformQuery(
                            "SELECT * FROM sra_rnaseq",
                            connectionPool, "DocId"));
                   
            }
                   

            System.out.println("my " + container.firstItemId());

            System.out.println("2. Trying to connect to Mysql RNA database");

        } catch (SQLException e) {
            System.out.println("some problem");
            e.printStackTrace();
        }
        return container;
    }
}