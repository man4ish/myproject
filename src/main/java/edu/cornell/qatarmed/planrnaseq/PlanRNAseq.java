/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.planrnaseq;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pankajkumar
 */

@Title("PlanRNAseq")
public class PlanRNAseq extends UI {

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
                
                //make form asking parameters and add it to leftLaayout
                VerticalLayout formLayout = new VerticalLayout();
                TextField studyName = new TextField("Name of RNAseq Study");
                formLayout.addComponent(studyName);
                List replist = new ArrayList();
                ComboBox numReplicates = new ComboBox("Replicates", replist);
                formLayout.addComponent(numReplicates);  
                leftLayout.addComponent(formLayout);
                /* Set the contents in the left of the split panel to use all the space */
		leftLayout.setSizeFull();
                
                
                VerticalLayout resultLayout = new VerticalLayout();
                rightLayout.addComponent(resultLayout);
                VerticalLayout chartLayout = new VerticalLayout();
                rightLayout.addComponent(chartLayout);
                
                chartLayout.setVisible(false);
                
                        
                
                
	
		

              
	}

}
