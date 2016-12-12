/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cornell.qatarmed.planrnaseq;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author pankajkumar
 */
public class StartHelp extends VerticalLayout {
    String startString = "<b> MetaRNA-Seq provides easy browsing, searching and annotating "
            + "of meta-date RNA-Studies at study level. "
            + "Most of the details about any RNA-Seq study are provided through "
            + " a single click and in the same window. MetRNA-Seq provides consensus "
            + "summary for any RNA-Seq study by digesting all biosample, experiment and run "
            + " in any particular study. In addition, MetaRNA-Seq provides the hierarchical data "
            + "structures of a study in tree-like structure. Meta-data of a RNA-Seq study "
            + "in MetaRNA-Seq can be annotated and searched by annotated fields the RNA-Seq "
            + "study such as disease type, time-series, and case-control, replicate type, "
            + "customized annotation and so on. </b>"
           
            + "<br></br> Click on any study in the table on the left "
            + "to get Study details.  You can also annotate the clicked study"
            + "for quickly searching it in future using guided search. <br></br>"
            + "Studies highligheted in <span style=\"background-color:rgb(255,140,0)\">orange</span> "
            + " are the ones for which annotation are ongoing <br></br>"
             + "Studies highligheted in <span style=\"background-color:rgb(0,255,0)\">green</span> "
            + " are the ones for which annotation are completed <br></br>"
            + "Please contact <b>Pankaj Kumar</b> at <b>pankajxyz@gmail.com </b>"
            + "if you have comment, suggestion or if you find any bug.<br><br>"
            + "<b>How to cite this ?</b> <br>"
            + "Publication under process";
    Label startLabel = new Label(startString, ContentMode.HTML);
    
    public StartHelp(){
        addComponent(startLabel);
    }
   
    
}
