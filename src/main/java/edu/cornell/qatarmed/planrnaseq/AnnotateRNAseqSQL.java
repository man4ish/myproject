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
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author pankajkumar
 */
@Title("AnnotateRNAseq")
public class AnnotateRNAseqSQL extends UI  {

    private Table bioprojectSummaryTable = new Table();
    TextField studyName = new TextField("RNAseq Study Name");
    // Tree tree = new Tree("RNA-Seq Projects");
    Tree tree = new Tree();
    FormLayout myform = new FormLayout();
    FormLayout rightTopForm = new FormLayout();
    FormLayout rightTopAnnotationForm = new FormLayout();
    TabSheet rightBottomTabsheet = new TabSheet();
    TabSheet rightTopTabsheet = new TabSheet();
    Table biosampleSummaryTable = new Table();
    TextField searchField = new TextField();
    Button searchButton = new Button("Search");
    SQLContainer rnaseqContainer;
    String[] list_of_diseases = new String[]{"cancer", "diabetes", "obesity", "cardiovascular", "arthritis",
        "alzheimer"};

    @Override
    protected void init(VaadinRequest request) {
        initLayout();
        initSearch();
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initLayout() {

        /* Root of the user interface component tree is set */
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        setContent(splitPanel);

        /* Build the component tree */
        VerticalLayout leftLayout = new VerticalLayout();
        VerticalSplitPanel rightSplitPanel = new VerticalSplitPanel();
        //  VerticalSplitPanel leftSplitPanel = new VerticalSplitPanel();

        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(rightSplitPanel);

        VerticalLayout rightTopLayout = new VerticalLayout();
        // rightTopLayout.addComponent(rightTopForm);
        rightTopTabsheet.setSizeFull();
        rightTopLayout.addComponent(rightTopTabsheet);
        rightTopTabsheet.addTab(rightTopForm, "Study Details");
        rightTopTabsheet.addTab(rightTopAnnotationForm, "Annotate");
        rightTopLayout.setSizeFull();

        rightSplitPanel.addComponent(rightTopLayout);

        HorizontalSplitPanel rightBottomLayout = new HorizontalSplitPanel();
        // HorizontalLayout rightBottomLayout = new HorizontalLayout();
        VerticalLayout rightBottomLeftLayout = new VerticalLayout();
        VerticalLayout rightBottomRightLayout = new VerticalLayout();
        rightBottomLayout.addComponent(rightBottomLeftLayout);
        rightBottomLayout.addComponent(rightBottomRightLayout);
        //  rightBottomLayout.setExpandRatio(rightBottomLeftLayout, 1);
        //  rightBottomLayout.setExpandRatio(rightBottomRightLayout, 3);
        rightBottomLayout.setSplitPosition(30f, Unit.PERCENTAGE);

        rightBottomLayout.setSizeFull();
        rightBottomTabsheet.setSizeFull();

        rightSplitPanel.addComponent(rightBottomLayout);

        splitPanel.setSplitPosition(50f, Unit.PERCENTAGE);
        //   rightSplitPanel.setWidth("20%");

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
        HorizontalLayout leftTopLayout = new HorizontalLayout();
        leftLayout.addComponent(leftTopLayout);
        leftTopLayout.addComponent(searchField);
        leftTopLayout.addComponent(searchButton);
        leftTopLayout.setWidth("100%");
        searchField.setWidth("100%");

        leftTopLayout.setExpandRatio(searchField, 1);
        leftLayout.addComponent(bioprojectSummaryTable);
        // leftLayout.setExpandRatio(searchField, 0);
        leftLayout.setExpandRatio(bioprojectSummaryTable, 1);
        bioprojectSummaryTable.setSizeFull();
        /* Set the contents in the left of the split panel to use all the space */
        leftLayout.setSizeFull();

        /*        VerticalLayout resultLayout = new VerticalLayout();
         rightLayout.addComponent(resultLayout);
         VerticalLayout chartLayout = new VerticalLayout();
         rightLayout.addComponent(chartLayout);
        
         chartLayout.setVisible(false);
         */
        rightBottomLeftLayout.addComponent(tree);

        rightBottomRightLayout.addComponent(rightBottomTabsheet);
        rightBottomTabsheet.addTab(myform, "Selected Biosample");
        myform.setSizeFull();
        VerticalLayout rbTabBiosampleSummaryLayout = new VerticalLayout(); // Right bottom Biosample Summary
        rightBottomTabsheet.addTab(rbTabBiosampleSummaryLayout, "All Biosamples");
        rbTabBiosampleSummaryLayout.addComponent(biosampleSummaryTable);
        rbTabBiosampleSummaryLayout.setSizeFull();

        initDataAndSubcomponent();
        rightTopLayout.setSizeFull();
        rightBottomRightLayout.setSizeFull();

    }

    private void initDataAndSubcomponent() {
        rnaseqContainer = createMySQLContainer("study_summary", "dummy");
        bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
        //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
        bioprojectSummaryTable.setSelectable(true);
        bioprojectSummaryTable.setImmediate(true);
        bioprojectSummaryTable.setColumnReorderingAllowed(true);
        bioprojectSummaryTable.setSortEnabled(true);
        bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "name", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases"});
        //bioprojectSummaryTable.setVisibleColumns(new Object[] { "firstName", "lastName", "department", "phoneNumber", "street", "city", "zipCode" });
        studyName.setValue(rnaseqContainer.firstItemId().toString());
        bioprojectSummaryTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            int custom_annotation_counter = 0;

            public void itemClick(ItemClickEvent event) {
                //<editor-fold defaultstate="collapsed" desc="filling study details on right panel">

                Object selectedStudyObject = event.getItemId();
                bioprojectSummaryTable.select(selectedStudyObject);
                tree.removeAllItems();
                rightTopForm.removeAllComponents();
                rightTopAnnotationForm.removeAllComponents();
                myform.removeAllComponents();

                String selectedStudy = selectedStudyObject.toString();
                String studyTitle = (String) bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "title").getValue();
                String studyName = (String) bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "name").getValue();
                String studyNumsample = String.valueOf(bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "Numsample").getValue());
                String studyNumexp = String.valueOf(bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "Numexp").getValue());
                String studyNumrun = String.valueOf(bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "Numrun").getValue());
                String studyAvgspots = String.valueOf(bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "Avgspots").getValue());
                String studyAvgbases = String.valueOf(bioprojectSummaryTable.getContainerProperty(selectedStudyObject, "avgbases").getValue());
                tree.addItem(selectedStudy);
                tree.setItemCaption(selectedStudy, "Study: " + selectedStudy);
                SQLContainer tempContainer = createMySQLContainer("study_extdb", selectedStudy);  // In this table I will chaeck for the manual annotation status
                String extdbid = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("extdb").getValue().toString();
                Label labelStudyAcc = new Label("<b>SRA Study Accession : </b>" + selectedStudy, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyAcc);
                Label labelStudyTitle = new Label("<b>Study Title: </b>" + studyTitle, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyTitle);
                Label labelStudyName = new Label("<b>Study Name: </b>" + studyName, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyName);
                Label labelStudyNumsample = new Label("<b>Total number of samples: </b>" + studyNumsample, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyNumsample);
                Label labelStudyNumexp = new Label("<b>Total number of experiments (each experiment uses any one of the samples): </b>" + studyNumexp, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyNumexp);
                Label labelStudyNumrun = new Label("<b>Total number of runs ( an experiment can have multiple runs) : </b>" + studyNumrun, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyNumrun);
                Label labelStudyAvgspots = new Label("<b>Avg number of spots or reads (per run): </b>" + studyAvgspots, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyAvgspots);
                Label labelStudyAvgbases = new Label("<b>Avg number of bases (per run): </b>" + studyAvgbases, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyAvgbases);
                if (extdbid.startsWith("PRJ")) {
                    //<editor-fold defaultstate="collapsed" desc="if PRJ">
                    tempContainer = createMySQLContainer("bioproject_details", extdbid);
                    String bioproject_accession = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("BioprojectAccession").getValue().toString();
                    String bioproject_id = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("BioprojectId").getValue().toString();
                    String bioproject_name = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("Name").getValue().toString();
                    String bioproject_title = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("Title").getValue().toString();
                    String bioproject_description = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("Description").getValue().toString();
                    String bioproject_capture = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("Capture").getValue().toString();
                    String bioproject_material = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("Material").getValue().toString();
                    String bioproject_method = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("MethodType").getValue().toString();
                    String bioproject_datatype = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("DataType").getValue().toString();
                    String bioproject_sampleScope = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("SampleScope").getValue().toString();
                    System.out.println(bioproject_description);
                    Label labelBioprojectAccession = new Label("<b>Bioproject Accession : </b>" + bioproject_accession, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectAccession);
                    Label labelBioprojectId = new Label("<b>Bioproject Id : </b>" + bioproject_id, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectId);
                    Label labelBioprojectTitle = new Label("<b>Bioproject Title : </b>" + bioproject_title, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectTitle);
                    Label labelBioprojectName = new Label("<b>Bioproject Name : </b>" + bioproject_name, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectName);
                    Label labelBioprojectDescription = new Label("<b>Bioproject Description : </b>" + bioproject_description, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectDescription);
                    Label labelBioprojectCapture = new Label("<b>Bioproject Capture : </b>" + bioproject_capture, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectCapture);
                    Label labelBioprojectMaterial = new Label("<b>Bioproject  Material : </b>" + bioproject_material, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectMaterial);
                    Label labelBioprojectMethod = new Label("<b>Bioproject Method : </b>" + bioproject_method, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectMethod);
                    Label labelBioprojectDatatype = new Label("<b>Bioproject Data Type : </b>" + bioproject_datatype, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectDatatype);
                    Label labelBioprojectSampleScope = new Label("<b>Bioproject Sample Scope : </b>" + bioproject_sampleScope, ContentMode.HTML);
                    rightTopForm.addComponent(labelBioprojectSampleScope);

//</editor-fold>
                }
                if (extdbid.startsWith("GSE")) {
                    //<editor-fold defaultstate="collapsed" desc="if GSE">
                    tempContainer = createMySQLContainer("study_gse_details", extdbid);
                    String gse_accesion = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("gse").getValue().toString();
                    String gse_summary = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("summary").getValue().toString();
                    String gse_design = tempContainer.getItem(tempContainer.getIdByIndex(0)).getItemProperty("overall_design").getValue().toString();
                    System.out.println(gse_summary);
                    Label labelGSE = new Label("<b>GEO Series Accession : </b>" + gse_accesion, ContentMode.HTML);
                    rightTopForm.addComponent(labelGSE);
                    Label labelGseSummary = new Label("<b>GSE Summary : </b>" + gse_summary, ContentMode.HTML);
                    rightTopForm.addComponent(labelGseSummary);
                    Label labelGseDesign = new Label("<b>GSE Design : </b>" + gse_design, ContentMode.HTML);
                    rightTopForm.addComponent(labelGseDesign);
//</editor-fold>

                }
//</editor-fold>
                //SQLContainer tempContainer = createMySQLContainer("sra_rnaseq", selectedStudy);
                tempContainer = createMySQLContainer("sra_rnaseq", selectedStudy);
                //  List<String> list = new ArrayList<String>();
                Map expMap = new HashMap();
                Map<String, String[]> expDetailMap = new HashMap<>();
                Map<String, String[]> platformMap = new HashMap<>();
                Map runMap = new HashMap();
                HashSet<String> biosampleSet = new HashSet<>();
                for (int i = 0; i < tempContainer.getItemIds().size(); i++) {
                    //<editor-fold defaultstate="collapsed" desc="for loop">

                    String docid = tempContainer.getIdByIndex(i).toString();
                    String biosample = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Biosample_Acc_Id_SampleId").getValue().toString();
                    biosampleSet.add(biosample);
                    String[] b_parts = biosample.split("\\|");

                    String exp = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Experiment_Acc_Ver_Status_Name").getValue().toString();
                    String[] exp_parts = exp.split("\\|");
                    String exp_acc = exp_parts[0];
                    expMap.put(exp_acc, b_parts[0]);
                    expDetailMap.put(exp_acc, exp_parts);

                    String sra_plaforms = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Platform_InstrumentModel").getValue().toString();
                    String[] sra_plaforms_parts = sra_plaforms.split("\\|");
                    platformMap.put(exp_acc, sra_plaforms_parts);

                    String run = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Runs").getValue().toString();
                    String[] run_parts = run.split("\\|");
                    if (run_parts.length > 0) {
                        for (int j = 0; j < run_parts.length; j++) {
                            String temprun = run_parts[j];
                            String[] temprun_parts = temprun.split("\\,");
                            runMap.put(temprun_parts[0], exp_acc);
                        }
                    }
//</editor-fold>
                }

                //<editor-fold defaultstate="collapsed" desc="Manual Annotaion">
                tempContainer = createMySQLContainer("biosample_with_studyacc", selectedStudy);
                int count_cell_line = 0;
                int count_organism_part = 0;
                int count_tissue = 0;
                int count_disease = 0;
                Set<String> cell_lines_set = new HashSet();
                Set<String> organism_part_set = new HashSet();
                Set<String> tissue_set = new HashSet();
                Set<String> disease_set = new HashSet();
                Map<String, Integer> cell_line_stat_map = new HashMap<>();
                Map<String, Integer> organism_part_stat_map = new HashMap<>();
                Map<String, Integer> tissue_stat_map = new HashMap<>();
                Map<String, Integer> disease_stat_map = new HashMap<>();
                for (int i = 0; i < tempContainer.getItemIds().size(); i++) {
                    //<editor-fold defaultstate="collapsed" desc="for biosample attributes">
                    String biosample_attr = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Attributes").getValue().toString();
                    String[] biosample_attr_parts = biosample_attr.split("\\:-");
                    if (biosample_attr_parts.length > 0) {
                        String[] attr_names = biosample_attr_parts[0].split("\\|");
                        String[] attr_values = new String[]{};
                        if (biosample_attr_parts.length > 1) {
                            attr_values = biosample_attr_parts[1].split("\\|");
                        }
                        int atn_index = 0;
                        for (String atn : attr_names) {
                            if (atn.equalsIgnoreCase("cell line")) {
                                count_cell_line = count_cell_line + 1;
                                if (attr_values.length >= atn_index) {
                                    String cell_line_value = attr_values[atn_index];
                                    cell_lines_set.add(cell_line_value);
                                    if (cell_line_stat_map.containsKey(cell_line_value)) {
                                        cell_line_stat_map.put(cell_line_value, cell_line_stat_map.get(cell_line_value) + 1);
                                    } else {
                                        cell_line_stat_map.put(cell_line_value, 1);
                                    }
                                }

                            }
                            if (atn.equalsIgnoreCase("organism part")) {
                                count_organism_part = count_organism_part + 1;
                                if (attr_values.length >= atn_index) {
                                    String organism_part_value = attr_values[atn_index];
                                    organism_part_set.add(organism_part_value);
                                    if (organism_part_stat_map.containsKey(organism_part_value)) {
                                        organism_part_stat_map.put(organism_part_value, organism_part_stat_map.get(organism_part_value) + 1);
                                    } else {
                                        organism_part_stat_map.put(organism_part_value, 1);
                                    }
                                }

                            }

                            if (atn.contains("disease")) {
                                count_disease = count_disease + 1;
                                if (attr_values.length >= atn_index) {
                                    String disease_value = attr_values[atn_index];
                                    disease_set.add(disease_value);
                                    if (disease_stat_map.containsKey(disease_value)) {
                                        disease_stat_map.put(disease_value, disease_stat_map.get(disease_value) + 1);
                                    } else {
                                        disease_stat_map.put(disease_value, 1);
                                    }
                                }

                            }
                            if (atn.equalsIgnoreCase("tissue")) {
                                count_tissue = count_tissue + 1;
                                if (attr_values.length >= atn_index) {
                                    String tissue_value = attr_values[atn_index];
                                    tissue_set.add(tissue_value);
                                    if (tissue_stat_map.containsKey(tissue_value)) {
                                        tissue_stat_map.put(tissue_value, tissue_stat_map.get(tissue_value) + 1);
                                    } else {
                                        tissue_stat_map.put(tissue_value, 1);
                                    }
                                }

                            }
                            atn_index = atn_index + 1;
                        }
                    }
//</editor-fold>

                }

                String suggestion_cell_line = "";
                String suggestion_organism_part = "";
                String suggestion_tissue = "";
                String suggestion_disease = "";
                String samplesType_from_sra = "";
                String cell_line_confidence = "";
                String organism_part_confidence = "";
                String disease_confidence = "";
                String tissue_confidence = "";
                //<editor-fold defaultstate="collapsed" desc="if else biosmaple attr has cell line">
                if (count_cell_line > 0) {
                    if (Integer.parseInt(studyNumsample) == count_cell_line) {
                        //good. all samples are from cell lines
                        suggestion_cell_line = "Yes:";
                        String cell_line_value = cell_lines_set.iterator().next();
                        suggestion_cell_line = suggestion_cell_line + cell_line_value;
                        cell_line_confidence = "100%. All " + cell_line_stat_map.get(cell_line_value).toString() + " Samples";
                    } else {
                        suggestion_cell_line = "Yes:";
                        Map.Entry<String, Integer> maxEntry = null;
                        for (Map.Entry<String, Integer> entry : cell_line_stat_map.entrySet()) {
                            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                                maxEntry = entry;
                            }
                        }
                        suggestion_cell_line = suggestion_cell_line + maxEntry.getKey() + "(" + maxEntry.getValue().toString() + " samples)";
                        int confidence_percent = (int) (maxEntry.getValue() * 100.0f) / (Integer.parseInt(studyNumsample));
                        cell_line_confidence = confidence_percent + "%";
                        for (Iterator<String> it = cell_lines_set.iterator(); it.hasNext();) {
                            String cellLine = it.next();
                            if (cellLine.equals(maxEntry.getKey())) {
                                //do nothing
                            } else {
                                suggestion_cell_line = suggestion_cell_line + ", " + cellLine + "(" + cell_line_stat_map.get(cellLine).toString() + ")";
                            }
                        }

                    }
                } else {
                    suggestion_cell_line = " No";
                }
//</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="if else biosmaple attr has organism part">
                if (count_organism_part > 0) {
                    if (Integer.parseInt(studyNumsample) == count_organism_part) {
                        //good. all samples are from cell lines
                        suggestion_organism_part = "Yes:";
                        String organism_part_value = organism_part_set.iterator().next();
                        suggestion_organism_part = suggestion_organism_part + organism_part_value;
                        organism_part_confidence = "100%. All " + organism_part_stat_map.get(organism_part_value).toString() + " Samples";
                    } else {
                        suggestion_organism_part = "Yes:";
                        Map.Entry<String, Integer> maxEntry = null;
                        for (Map.Entry<String, Integer> entry : organism_part_stat_map.entrySet()) {
                            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                                maxEntry = entry;
                            }
                        }
                        suggestion_organism_part = suggestion_organism_part + maxEntry.getKey() + "(" + maxEntry.getValue().toString() + " samples)";
                        int confidence_percent = (int) (maxEntry.getValue() * 100.0f) / (Integer.parseInt(studyNumsample));
                        organism_part_confidence = confidence_percent + "%";
                        for (Iterator<String> it = organism_part_set.iterator(); it.hasNext();) {
                            String organismPart = it.next();
                            if (organismPart.equals(maxEntry.getKey())) {
                                //do nothing
                            } else {
                                suggestion_organism_part = suggestion_organism_part + ", " + organismPart + "(" + organism_part_stat_map.get(organismPart).toString() + ")";
                            }
                        }

                    }
                } else {
                    suggestion_organism_part = " No";
                }
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="if else biosmaple attr has disease">
                if (count_disease > 0) {
                    if (Integer.parseInt(studyNumsample) == count_disease) {
                        //good. all samples are from cell lines
                        suggestion_disease = "Yes:";
                        String disease_value = disease_set.iterator().next();
                        suggestion_disease = suggestion_disease + disease_value;
                        disease_confidence = "100%. All " + disease_stat_map.get(disease_value).toString() + " Samples";
                    } else {
                        suggestion_disease = "Yes:";
                        Map.Entry<String, Integer> maxEntry = null;
                        for (Map.Entry<String, Integer> entry : disease_stat_map.entrySet()) {
                            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                                maxEntry = entry;
                            }
                        }
                        suggestion_disease = suggestion_disease + maxEntry.getKey() + "(" + maxEntry.getValue().toString() + " samples)";
                        int confidence_percent = (int) (maxEntry.getValue() * 100.0f) / (Integer.parseInt(studyNumsample));
                        disease_confidence = confidence_percent + "%";
                        for (Iterator<String> it = disease_set.iterator(); it.hasNext();) {
                            String diseasePart = it.next();
                            if (diseasePart.equals(maxEntry.getKey())) {
                                //do nothing
                            } else {
                                suggestion_disease = suggestion_disease + ", " + diseasePart + "(" + disease_stat_map.get(diseasePart).toString() + ")";
                            }
                        }

                    }
                } else {
                    suggestion_disease = " No";
                }
//</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="if else biosmaple attr has tissue">
                if (count_tissue > 0) {
                    if (Integer.parseInt(studyNumsample) == count_tissue) {
                        //good. all samples are from cell lines
                        suggestion_tissue = "Yes:";
                        String tissue_value = tissue_set.iterator().next();
                        suggestion_tissue = suggestion_tissue + tissue_value;
                        tissue_confidence = "100%. All " + tissue_stat_map.get(tissue_value).toString() + " Samples";
                    } else {
                        suggestion_tissue = "Yes:";
                        Map.Entry<String, Integer> maxEntry = null;
                        for (Map.Entry<String, Integer> entry : tissue_stat_map.entrySet()) {
                            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                                maxEntry = entry;
                            }
                        }
                        suggestion_tissue = suggestion_tissue + maxEntry.getKey() + "(" + maxEntry.getValue().toString() + " samples)";
                        int confidence_percent = (int) (maxEntry.getValue() * 100.0f) / (Integer.parseInt(studyNumsample));
                        tissue_confidence = confidence_percent + "%";
                        for (Iterator<String> it = disease_set.iterator(); it.hasNext();) {
                            String tissuePart = it.next();
                            if (tissuePart.equals(maxEntry.getKey())) {
                                //do nothing
                            } else {
                                suggestion_tissue = suggestion_tissue + ", " + tissuePart + "(" + tissue_stat_map.get(tissuePart).toString() + ")";
                            }
                        }

                    }
                } else {
                    suggestion_tissue = " No";
                }

//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="filling right top manual annotation">
                HorizontalLayout diseaseLayout = new HorizontalLayout();
                CheckBox checkboxDiseaseYes = new CheckBox("Yes");
                CheckBox checkboxDiseaseNo = new CheckBox("No");

                String disease_from_biosample_attribute = "";
                if (suggestion_disease.startsWith("Yes")) {
                    disease_from_biosample_attribute = "From Biosample: " + suggestion_disease;
                }
                String disease_found = "";
                String disease_text_parsed_confidence = "";
                for (String disease : list_of_diseases) {
                    if (StringUtils.containsIgnoreCase(studyName, disease) || StringUtils.containsIgnoreCase(studyTitle, disease)) {
                        checkboxDiseaseYes.setValue(true);
                        disease_found = disease_found + disease + " ";
                        disease_text_parsed_confidence = "keyword found in Study or Title";
                    }
                }
                String diseaseLabelString = "";
                if (disease_text_parsed_confidence.length() > 1) {
                    diseaseLabelString = "<b><i>Suggestion: </i></b>" + disease_found + " <b> <i> Confidence: <i></b>  " + disease_text_parsed_confidence;
                }
                if (disease_from_biosample_attribute.length() > 1) {
                    diseaseLabelString = diseaseLabelString + "<b><i>Suggestion: </i></b>" + disease_from_biosample_attribute + " <b> <i> Confidence: <i></b>  " + disease_confidence;
                }

                Label diseaseLabel = new Label(diseaseLabelString, ContentMode.HTML);
                Label diseaseTitle = new Label("<b>Disease: </b>", ContentMode.HTML);
                diseaseLayout.addComponent(diseaseTitle);
                diseaseLayout.addComponent(checkboxDiseaseNo);
                diseaseLayout.addComponent(checkboxDiseaseYes);
                diseaseLayout.addComponent(diseaseLabel);

                HorizontalLayout caseControlLayout = new HorizontalLayout();
                CheckBox checkboxCaseControlYes = new CheckBox("Yes");
                CheckBox checkboxCaseControlNo = new CheckBox("No");
                checkboxDiseaseYes.setValue(true);
                Label caseControlTitle = new Label("<b>Case-Control: </b>", ContentMode.HTML);
                caseControlLayout.addComponent(caseControlTitle);
                caseControlLayout.addComponent(checkboxCaseControlYes);
                caseControlLayout.addComponent(checkboxCaseControlNo);

                HorizontalLayout timeSeriesLayout = new HorizontalLayout();
                CheckBox checkboxTimeSerieslYes = new CheckBox("Yes");
                CheckBox checkboxTimeSeriesNo = new CheckBox("No");
                checkboxDiseaseYes.setValue(true);
                Label timeSeriesTitle = new Label("<b>Time Series: </b>", ContentMode.HTML);
                timeSeriesLayout.addComponent(timeSeriesTitle);
                timeSeriesLayout.addComponent(checkboxTimeSerieslYes);
                timeSeriesLayout.addComponent(checkboxTimeSeriesNo);

                HorizontalLayout diseaseCategoriesLayout = new HorizontalLayout();
                Label diseaseCategoryLabel = new Label("<b><i>Suggestion: </i></b>" + disease_found + " <b> <i> Confidence: <i></b>  " + disease_confidence, ContentMode.HTML);
                String[] diseaseCategories = new String[]{"Complex Disease", "Rare Disease", "Other", "Not Sure"};
                List<String> diseaseCategoriesList = Arrays.asList(diseaseCategories);
                ComboBox diseaseCategoryComboBox = new ComboBox("Disease Category", diseaseCategoriesList);
                diseaseCategoriesLayout.addComponent(diseaseCategoryComboBox);
                diseaseCategoriesLayout.addComponent(diseaseCategoryLabel);

                String[] platforms = new String[]{"Illumina", "SOLID", "Roche 454", "PacBio", "Helicos", "Complete Genomics"};
                List<String> platformsList = Arrays.asList(platforms);
                ComboBox platformsListComboBox = new ComboBox("Sequencing Platform", platformsList);
                Set<String> matchedPlatformSet = new HashSet();
                String platform_from_sra = "";
                String platorm_confidence = "";
                int matchPlatformCount = 0;
                for (String[] val : platformMap.values()) {
                    if (val.length > 1) {
                        for (String pf : platforms) {
                            if (val[0].equalsIgnoreCase(pf)) {
                                matchedPlatformSet.add(pf);
                                matchPlatformCount = matchPlatformCount + 1;
                            }
                        }
                    } else {

                    }
                }
                if (matchedPlatformSet.isEmpty()) {
                    platform_from_sra = "Match Not Found";
                } else {
                    if (matchedPlatformSet.size() == matchPlatformCount) {
                        //Ideally this shouldn't be the case. 
                        // This suggests that all experiments of this project are run on different platforms or
                        // it has only one expriment
                        if (matchedPlatformSet.size() == 1) {
                            platform_from_sra = matchedPlatformSet.iterator().next().toString();
                            platformsListComboBox.select(platform_from_sra);
                        } else {
                            platform_from_sra = "Can't predict. All experiment on different Platforms";
                        }
                    } else {
                        if (matchedPlatformSet.size() == 1) {
                            // Perfect 
                            platform_from_sra = matchedPlatformSet.iterator().next().toString();
                            platformsListComboBox.select(platform_from_sra);
                            platorm_confidence = "100%";
                        } else {

                        }
                    }
                }

                HorizontalLayout platformLayout = new HorizontalLayout();
                Label suggestedPlatformLabel = new Label("<b><i>Suggestion: </i></b>" + platform_from_sra + "<b> <i> Confidence: <i></b>  " + platorm_confidence, ContentMode.HTML);
                platformLayout.addComponent(platformsListComboBox);
                platformLayout.addComponent(suggestedPlatformLabel);

                VerticalLayout sampleTypesLayout = new VerticalLayout();
                CheckBox checkboxSampleTypeCellLine = new CheckBox("Cell Line");
                CheckBox checkboxSampleTypeTissue = new CheckBox("Tissue");
                CheckBox checkboxSampleTypeStemCells = new CheckBox("Stem Cells");
                CheckBox checkboxSampleTypeWholeBlood = new CheckBox("Whole Blood");
                CheckBox checkboxSampleTypePlasma = new CheckBox("Plasma");
                String suggestedCellTypeLabelString = "";
                if (suggestion_cell_line.startsWith("Yes")) {
                    //sampleTypesListComboBox.select("Cell Lines");
                    checkboxSampleTypeCellLine.setValue(true);
                    suggestedCellTypeLabelString = "<b><i>Suggestion: </i></b>" + "Cell Lines --> " + suggestion_cell_line + " <b> <i> Confidence: <i></b>   " + cell_line_confidence;
                }
                Label suggestedCellLineLabel = new Label(suggestedCellTypeLabelString, ContentMode.HTML);
                String suggestedTissueLabelString = "";
                if (suggestion_organism_part.startsWith("Yes")) {
                    checkboxSampleTypeTissue.setValue(true);
                    suggestedTissueLabelString = suggestedTissueLabelString + "<b><i>Suggestion: </i></b>" + "Organism part --> " + suggestion_organism_part + " <b> <i> Confidence: <i></b>   " + organism_part_confidence;
                }

                if (suggestion_tissue.startsWith("Yes")) {
                    checkboxSampleTypeTissue.setValue(true);
                    suggestedTissueLabelString = suggestedTissueLabelString + "<b><i>Suggestion: </i></b>" + "Tissue --> " + suggestion_tissue + " <b> <i> Confidence: <i></b>   " + tissue_confidence;
                }
                Label suggestedTissueLabel = new Label(suggestedTissueLabelString, ContentMode.HTML);

                String suggestedStemCellString = "";
                /*
                 if (suggestion_stem_cell.startsWith("Yes")) {                   
                 checkboxSampleTypeStemCells.setValue(true);
                 suggestedCellTypeLabelString = "<b><i>Suggestion: </i></b>" + "Cell Lines --> " + suggestion_stem_cell + " <b> <i> Confidence: <i></b>   " + stem_cell_confidence;
                 }
                 */
                Label suggestedStemCellLabel = new Label(suggestedStemCellString, ContentMode.HTML);

                String suggestedWholeBloodLabelString = "";
                /*
                 if (suggestion_whole_blood.startsWith("Yes")) {                   
                 checkboxSampleTypeWholeBlood.setValue(true);
                 suggestedWholeBloodLabelString = "<b><i>Suggestion: </i></b>" + "Whole Blood --> " + suggestion_whole_blood + " <b> <i> Confidence: <i></b>   " + whole_blood_confidence;
                 }
                 */
                Label suggestedWholeBloodLabel = new Label(suggestedWholeBloodLabelString, ContentMode.HTML);

                String suggestedPlasmaLabelString = "";
                /*
                 if (suggestion_plasma.startsWith("Yes")) {                   
                 checkboxSampleTypePlasma.setValue(true);
                 suggestedPlasmaLabelString = "<b><i>Suggestion: </i></b>" + "Cell Lines --> " + suggestion_plasma + " <b> <i> Confidence: <i></b>   " + plasma_confidence;
                 }
                 */
                Label suggestedPlasmaLabel = new Label(suggestedPlasmaLabelString, ContentMode.HTML);

                HorizontalLayout CellLineLayout = new HorizontalLayout();
                CellLineLayout.addComponent(checkboxSampleTypeCellLine);
                CellLineLayout.addComponent(suggestedCellLineLabel);

                HorizontalLayout TissueLayout = new HorizontalLayout();
                TissueLayout.addComponent(checkboxSampleTypeTissue);
                TissueLayout.addComponent(suggestedTissueLabel);

                HorizontalLayout WholeBloodLayout = new HorizontalLayout();
                WholeBloodLayout.addComponent(checkboxSampleTypeWholeBlood);
                WholeBloodLayout.addComponent(suggestedWholeBloodLabel);

                HorizontalLayout PlasmaLayout = new HorizontalLayout();
                PlasmaLayout.addComponent(checkboxSampleTypePlasma);
                PlasmaLayout.addComponent(suggestedPlasmaLabel);

                sampleTypesLayout.addComponent(CellLineLayout);
                sampleTypesLayout.addComponent(TissueLayout);
                sampleTypesLayout.addComponent(WholeBloodLayout);
                sampleTypesLayout.addComponent(PlasmaLayout);

                Button addCustomAnnoButton = new Button("++ Custom Annotation");
                addCustomAnnoButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        custom_annotation_counter = custom_annotation_counter + 1;
                        HorizontalLayout customLayout = new HorizontalLayout();
                        TextField customAnnoName = new TextField("Custom Name");
                        customAnnoName.setId("customAnnoName" + custom_annotation_counter);
                        TextField customAnnoValue = new TextField("Custom Value");
                        customLayout.addComponent(customAnnoName);
                        customLayout.addComponent(customAnnoValue);
                        customAnnoValue.setId("customAnnoValue" + custom_annotation_counter);
                        int addCustomAnnoButtonIndex = rightTopAnnotationForm.getComponentIndex(addCustomAnnoButton);
                        rightTopAnnotationForm.addComponent(customLayout, addCustomAnnoButtonIndex);

                    }
                });

                //<editor-fold defaultstate="collapsed" desc="Replicate Type">
                HorizontalLayout replicatTypesLayout = new HorizontalLayout();
                String replicateType_from_sra = "";
                String replicateType_confidence = "";
                Label suggestedreplicatTypeLabel = new Label("<b><i>Suggestion: </i></b>" + replicateType_from_sra + " <b> <i> Confidence: <i></b>  " + replicateType_confidence, ContentMode.HTML);
                String[] replicatTypes = new String[]{"Biological -- different individuals", "Biological -- same individual but severe treatment to RNA", "Semi Biological/Technical -- mild treatment", "Technical -- machine parameter or buffer (very mild)"};
                List<String> replicatTypesList = Arrays.asList(replicatTypes);
                ComboBox replicatTypesListComboBox = new ComboBox("Replicates Type ", replicatTypesList);
                replicatTypesLayout.addComponent(replicatTypesListComboBox);
                replicatTypesLayout.addComponent(suggestedreplicatTypeLabel);

                //</editor-fold>
                checkboxDiseaseYes.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        checkboxDiseaseNo.setValue(!checkboxDiseaseYes.getValue());
                        diseaseCategoryComboBox.setVisible(true);
                    }
                }
                );
                checkboxDiseaseNo.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        checkboxDiseaseYes.setValue(!checkboxDiseaseNo.getValue());
                        diseaseCategoryComboBox.setVisible(false);
                    }
                }
                );

                //<editor-fold defaultstate="collapsed" desc="CheckBox Annotation Ongoing or Completed ">
                CheckBox checkboxAnnotaionCompleted = new CheckBox("Annotaion Completed");
                CheckBox checkboxAnnotaionOngoing = new CheckBox("Annotaion Ongoing");
                checkboxAnnotaionOngoing.setValue(true);
                checkboxAnnotaionCompleted.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        checkboxAnnotaionOngoing.setValue(!checkboxAnnotaionCompleted.getValue());
                    }
                }
                );
                checkboxAnnotaionOngoing.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        checkboxAnnotaionCompleted.setValue(!checkboxAnnotaionOngoing.getValue());
                    }
                }
                );
                //</editor-fold>

                HorizontalLayout annotationStatusLayout = new HorizontalLayout();
                annotationStatusLayout.addComponent(checkboxAnnotaionOngoing);
                annotationStatusLayout.addComponent(checkboxAnnotaionCompleted);

                Button submitButton = new Button("Submit");
                submitButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        int diseaseBoolean = 0;
                        if (checkboxDiseaseYes.getValue() && !checkboxDiseaseNo.getValue()) {
                            diseaseBoolean = 1;
                        }
                        int caseControlBoolean = 0;
                        if (checkboxCaseControlYes.getValue() && !checkboxCaseControlNo.getValue()) {
                            caseControlBoolean = 1;
                        }
                        for (int j = 1; j <= custom_annotation_counter; j++) {
                            if (findById(rightTopAnnotationForm, "customAnnoName" + j) instanceof TextField) {
                                TextField tempTFname = (TextField) findById(rightTopAnnotationForm, "customAnnoName" + j);
                                System.out.println(j + "custom name: " + tempTFname.getValue());
                            }
                             if (findById(rightTopAnnotationForm, "customAnnoValue" + j) instanceof TextField) {
                                TextField tempTFvalue = (TextField) findById(rightTopAnnotationForm, "customAnnoValue" + j);
                                System.out.println(j + "custom value: "  + tempTFvalue.getValue());
                            }
                        }

//Notification.show("Do not press this button again");
                    }
                });

                rightTopAnnotationForm.addComponent(diseaseLayout);
                rightTopAnnotationForm.addComponent(caseControlLayout);
                rightTopAnnotationForm.addComponent(timeSeriesLayout);
                rightTopAnnotationForm.addComponent(diseaseCategoriesLayout);
                rightTopAnnotationForm.addComponent(platformLayout);
                rightTopAnnotationForm.addComponent(sampleTypesLayout);
                rightTopAnnotationForm.addComponent(replicatTypesLayout);
                rightTopAnnotationForm.addComponent(addCustomAnnoButton);
                rightTopAnnotationForm.addComponent(annotationStatusLayout);
                rightTopAnnotationForm.addComponent(submitButton);
              //</editor-fold>
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="fill tree in right bottom">
                Iterator iterator = biosampleSet.iterator();

                // check values
                while (iterator.hasNext()) {
                    //  System.out.println("Value: "+ iterator.next() + " ");
                    String bsample = iterator.next().toString();
                    String[] biosample_parts = bsample.split("\\|");
                    String biosample_acc = biosample_parts[0];
                    tree.addItem(biosample_acc);
                    tree.setParent(biosample_acc, selectedStudy);
                    tree.setItemCaption(biosample_acc, "BioSample: " + biosample_acc);
                }
                Set expMap_entrset = expMap.entrySet();
                Iterator it = expMap_entrset.iterator();
                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    String[] expParts = expDetailMap.get(me.getKey());
                    String expTitle = "";
                    if (expParts.length > 3) {
                        expTitle = expParts[3];
                    }
                    tree.addItem(me.getKey());
                    tree.setParent(me.getKey(), me.getValue());
                    tree.setItemCaption(me.getKey(), "Experiment: " + me.getKey() + ": " + expTitle);
                }

                Set runMap_entrset = runMap.entrySet();
                Iterator runit = runMap_entrset.iterator();
                while (runit.hasNext()) {
                    Map.Entry runEntry = (Map.Entry) runit.next();
                    tree.addItem(runEntry.getKey());
                    tree.setParent(runEntry.getKey(), runEntry.getValue());
                    tree.setItemCaption(runEntry.getKey(), "Run: " + runEntry.getKey());
                }

                tree.expandItemsRecursively(selectedStudy);

//</editor-fold>
            }
        });

        tree.setSelectable(true);
        tree.setImmediate(true);
        tree.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Object id = event.getProperty().getValue();
                if (id != null) {
                    String selectedTreeId = id.toString();
                    String selectedTreeItem = tree.getItemCaption(id).toString();
                    System.out.println("Tree event is fired: " + selectedTreeItem);
                    if (selectedTreeItem.startsWith("BioSample")) {

                        myform.removeAllComponents();
                        SQLContainer tempContainer = createMySQLContainer("biosample", selectedTreeId);
                        for (int i = 0; i < tempContainer.getItemIds().size(); i++) {
                            String accessType = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("AccessType").getValue().toString();
                            String publicationDate = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("PublicationDate").getValue().toString();
                            String lastUpdate = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("LastUpdate").getValue().toString();
                            String accession = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Accession").getValue().toString();
                            String title = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Title").getValue().toString();
                            String taxonomy_id = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("TaxonomyId").getValue().toString();
                            String taxonomy_name = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("TaxonomyName").getValue().toString();
                            String samplePackage = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Package").getValue().toString();
                            String status = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Status").getValue().toString();
                            String statusTime = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("StatusTime").getValue().toString();
                            String sampleIds = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Ids").getValue().toString();
                            String attributes = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Attributes").getValue().toString();
                            String models = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Models").getValue().toString();
                            String submissionDate = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("SubmissionDate").getValue().toString();
                            String owner = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Owner").getValue().toString();

                            String[] myfields = new String[]{accessType, publicationDate, lastUpdate, accession, title, taxonomy_id, taxonomy_name, samplePackage,
                                status, statusTime, sampleIds, attributes, models, submissionDate, owner};
                            /*
                             for (String fieldName : myfields) {
                             Label tempLabl = new Label("");
                             }
                             */
                            Label labelAccessType = new Label("Access Type: " + accessType);
                            myform.addComponent(labelAccessType);
                            Label labelPublicationDate = new Label("Publication Date: " + publicationDate);
                            myform.addComponent(labelPublicationDate);
                            Label labelLastUpdate = new Label("Last Update: " + lastUpdate);
                            myform.addComponent(labelLastUpdate);
                            Label labelAccession = new Label("Accession: " + accession);
                            myform.addComponent(labelAccession);
                            Label labelTitle = new Label("<b>Title: </b>" + title, ContentMode.HTML);
                            myform.addComponent(labelTitle);
                            Label labelTaxonomyId = new Label("Taxonomy Id: " + taxonomy_id);
                            myform.addComponent(labelTaxonomyId);
                            Label labelTaxonomyName = new Label("Taxonomy Name: " + taxonomy_name);
                            myform.addComponent(labelTaxonomyName);
                            Label labelPackage = new Label("Package: " + samplePackage);
                            myform.addComponent(labelPackage);
                            Label labelStatus = new Label("Status: " + status);
                            myform.addComponent(labelStatus);
                            Label labelStatusTime = new Label("Status Time: " + statusTime);
                            myform.addComponent(labelStatusTime);
                            Label labelOwner = new Label("Owner: " + owner);
                            myform.addComponent(labelOwner);

                            String[] id_parts = sampleIds.split("\\:\\-");
                            if (id_parts.length > 1) {
                                String[] id_dbs = id_parts[0].split("\\|");
                                String[] id_values = id_parts[1].split("\\|");
                                for (int j = 0; j < id_dbs.length; j++) {
                                    Label tempLabel = new Label(id_dbs[j] + ": " + id_values[j]);
                                    myform.addComponent(tempLabel);

                                }
                            }

                            String[] attribute_parts = attributes.split("\\:\\-");
                            if (attribute_parts.length > 1) {
                                String[] attribute_names = attribute_parts[0].split("\\|");
                                String[] attribute_values = attribute_parts[1].split("\\|");
                                for (int j = 0; j < attribute_names.length; j++) {
                                    Label tempLabel = new Label("<b>" + attribute_names[j] + ": </b>" + attribute_values[j], ContentMode.HTML);
                                    myform.addComponent(tempLabel);
                                    System.out.println("inside attributes ");

                                }
                            }

                            //   TextField field = new TextField(lastDpdate);
                            // myform.addComponent(field);
                            //field.setWidth("100%");
                        }
                    }
                    if (selectedTreeItem.startsWith("Experiment")) {
                        SQLContainer tempContainer = createMySQLContainer("sra_rnaseq_exp", selectedTreeId);
                        myform.removeAllComponents();
                        Map expMap = new HashMap();
                        Map<String, String[]> expDetailMap = new HashMap<>();
                        Map<String, String[]> libraryDetailMap = new HashMap<>();
                        Map<String, String[]> platformMap = new HashMap<>();
                        Map runMap = new HashMap();
                        HashSet<String> biosampleSet = new HashSet<>();
                        for (int i = 0; i < tempContainer.getItemIds().size(); i++) {
                            String docid = tempContainer.getIdByIndex(i).toString();
                            String biosample = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Biosample_Acc_Id_SampleId").getValue().toString();
                            biosampleSet.add(biosample);
                            String[] b_parts = biosample.split("\\|");

                            String exp = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Experiment_Acc_Ver_Status_Name").getValue().toString();
                            String[] exp_parts = exp.split("\\|");
                            String exp_acc = exp_parts[0];
                            expMap.put(exp_acc, b_parts[0]);
                            expDetailMap.put(exp_acc, exp_parts);

                            String library_string = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Library_Name_Strategy_Source_Selection_Layout").getValue().toString();
                            String[] library_parts = library_string.split("\\|");
                            String library_name = "";
                            String library_strategy = "";
                            String library_source = "";
                            String library_selection = "";
                            String library_layout = "";
                            if (library_parts.length > 0) {
                                library_name = library_parts[0];
                            }
                            if (library_parts.length > 1) {
                                library_strategy = "<b> <i> Strategy </i> </b>" + library_parts[1];
                            }
                            if (library_parts.length > 2) {
                                library_source = "<b> <i> Source </i> </b>" + library_parts[2];
                            }
                            if (library_parts.length > 3) {
                                library_selection = "<b> <i> Selection </i> </b>" + library_parts[3];
                            }
                            if (library_parts.length > 4) {
                                String tempLayout = library_parts[4];
                                tempLayout = tempLayout.replaceAll("<", "");
                                tempLayout = tempLayout.replaceAll("/>", "");
                                library_layout = "<b> <i> Layout </i> </b>" + tempLayout;
                            }
                            String[] tempStringArray = new String[]{library_strategy, library_source, library_selection, library_layout};
                            if (library_name.length() > 1) {
                                libraryDetailMap.put(library_name, tempStringArray);
                            }

                            String sra_plaforms = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Platform_InstrumentModel").getValue().toString();
                            String[] sra_plaforms_parts = sra_plaforms.split("\\|");
                            platformMap.put(exp_acc, sra_plaforms_parts);

                            String run = tempContainer.getItem(tempContainer.getIdByIndex(i)).getItemProperty("Runs").getValue().toString();
                            String[] run_parts = run.split("\\|");
                            if (run_parts.length > 0) {
                                for (int j = 0; j < run_parts.length; j++) {
                                    String temprun = run_parts[j];
                                    String[] temprun_parts = temprun.split("\\,");
                                    runMap.put(temprun_parts[0], exp_acc);
                                }
                            }
                        }
                        Label labelExperimentAcc = new Label("<b>Experiment Accession: </b>" + selectedTreeId, ContentMode.HTML);
                        myform.addComponent(labelExperimentAcc);
                        String libraryDetails = "";
                        for (Map.Entry<String, String[]> entry : libraryDetailMap.entrySet()) {
                            libraryDetails = libraryDetails + entry.getKey();
                            String[] temp_library_details = entry.getValue();
                            for (String dt : temp_library_details) {
                                libraryDetails = libraryDetails + dt;
                            }
                        }
                        Label labelLibraryDetails = new Label("<b>Library Details: </b>" + libraryDetails, ContentMode.HTML);
                        myform.addComponent(labelLibraryDetails);

                    }
                } else {
                    System.out.println("id is null");
                }
            }

        });

        bioprojectSummaryTable.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Object contactId = bioprojectSummaryTable.getValue();

//Binding data
//When a contact is selected from the list, we want to show that in our editor on the right. This is nicely done by the FieldGroup that binds all the fields to the corresponding Properties in our contact at once.                                if (contactId != null)
                //                           editorFields.setItemDataSource(bioprojectSummaryTable.getItem(contactId));
                //                  editorLayout.setVisible(contactId != null);
            }
        });

    }

    private void initSearch() {

        searchField.setInputPrompt("Search Project");
        /*
         //Attempted to do autosuggestion but it require Indexcontainer for attached to vaadin table. Currently vaadin table is 
         // attached to SqlContainer. It's possble to store fetch table data and use it for creating Indexcontainer and then attach it to vaadin table 
        
         searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);

         searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
         public void textChange(final FieldEvents.TextChangeEvent event) {
              
         rnaseqContainer.removeAllContainerFilters();
         //	rnaseqContainer.addContainerFilter(new AnnotateRNAseqSQL.ContactFilter(event
         //			.getText()));
         }
         });
         */

        searchButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                /*
                 * Rows in the Container data model are called Item. Here we add
                 * a new row in the beginning of the list.
                 */
                String search_value = searchField.getValue();
                rnaseqContainer = createMySQLContainer("study_filter", search_value);
                rnaseqContainer.removeAllContainerFilters();

                bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
                //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
                bioprojectSummaryTable.setSelectable(true);
                bioprojectSummaryTable.setImmediate(true);
                bioprojectSummaryTable.setColumnReorderingAllowed(true);
                bioprojectSummaryTable.setSortEnabled(true);
                bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "name", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases"});
                //bioprojectSummaryTable.setVisibleColumns(new Object[] { "firstName", "lastName", "department", "phoneNumber", "street", "city", "zipCode" });
                // studyName.setValue(rnaseqContainer.firstItemId().toString());
            }
        });

    }

    public Component findById(HasComponents root, String id) {
        System.out.println("findById called on " + root);

        Iterator<Component> iterate = root.iterator();
        while (iterate.hasNext()) {
            Component c = iterate.next();
            if (id.equals(c.getId())) {
                return c;
            }
            if (c instanceof HasComponents) {
                Component cc = findById((HasComponents) c, id);
                if (cc != null) {
                    return cc;
                }
            }
        }

        return null;
    }

    private static SQLContainer createMySQLContainer(String dataTable, String bioargument) {
        //  TableQuery query = null;
        SQLContainer container = null;
        try {
            System.out.println("1. Trying to connect to Mysql RNA database");
            SimpleJDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost:3306/rna", "rnaseq",
                    "rna", 2, 100);
          //  query = new TableQuery("customers", connectionPool);
            // query.setVersionColumn("id");

            // temp = new SQLContainer(query);
            /*
             container = new SQLContainer(new FreeformQuery(
             "SELECT * FROM bioproject_summary",
             connectionPool, "Bioproject"));
            
             */
            String bioproject_detail_query = "SELECT * FROM sra_rnaseq where BioprojectId  = " + "'" + bioargument + "'";
            String biosample_detail_query = "SELECT * FROM biosample where Accession  = " + "'" + bioargument + "'";
            //      System.out.println("SQL query is: " + biosample_detail_query);

            switch (dataTable) {
                case "study_summary":
                    FreeformQuery query = new FreeformQuery(
                            "SELECT * FROM study_summary",
                            connectionPool, "Study");
                    query.setDelegate(new StudyFreeFormQueryDelegate());
                    container = new SQLContainer(query);
                    System.out.println("SQL query is: SELECT * FROM study_summary ");
                    break;
                case "study_filter":
                    String study_filter_query = "SELECT * FROM study_summary "
                            + "where Title like  " + "'%" + bioargument + "%' or "
                            + "Name like " + "'%" + bioargument + "%' or "
                            + " Study like " + "'%" + bioargument + "%'";
                    System.out.println("SQL query is: " + study_filter_query);
                    container = new SQLContainer(new FreeformQuery(
                            study_filter_query,
                            connectionPool, "Study"));

                    break;
                case "sra_rnaseq":
                    container = new SQLContainer(new FreeformQuery(
                            bioproject_detail_query,
                            connectionPool, "DocId"));
                    System.out.println("SQL query is: " + bioproject_detail_query);
                    break;
                case "sra_rnaseq_exp":
                    String individual_exp_detail_query = "SELECT * FROM sra_rnaseq where Experiment_Acc_Ver_Status_Name  like " + "'" + bioargument + "%'";
                    container = new SQLContainer(new FreeformQuery(
                            individual_exp_detail_query,
                            connectionPool, "DocId"));
                    System.out.println("SQL query is: " + individual_exp_detail_query);
                    break;
                case "biosample":
                    container = new SQLContainer(new FreeformQuery(
                            biosample_detail_query,
                            connectionPool, "Accession"));
                    System.out.println("SQL query is: " + biosample_detail_query);
                    break;
                case "biosample_with_studyacc":
                    String biosample_study_detail_query = "SELECT * FROM biosample_with_studyacc where studyacc  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            biosample_study_detail_query,
                            connectionPool, "Accession"));
                    System.out.println("SQL query is: " + biosample_study_detail_query);
                    break;
                case "study_extdb":
                    String study_extdb_query = "SELECT * FROM study_extdb where study_id  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            study_extdb_query,
                            connectionPool, "study_id"));
                    System.out.println("SQL query is: " + study_extdb_query);
                    break;
                case "bioproject_details":
                    String bioproject_details_query = "SELECT * FROM bioproject_details where BioprojectAccession  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            bioproject_details_query,
                            connectionPool, "BioprojectAccession"));
                    System.out.println("SQL query is: " + bioproject_details_query);
                    break;
                case "study_gse_details":
                    String gse_details_query = "SELECT * FROM study_gse_details where gse  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            gse_details_query,
                            connectionPool, "gse"));
                    System.out.println("SQL query is: " + gse_details_query);
                    break;
                case "bioproject_summary":
                    System.out.println("SQL query is: accessing wrong table");
                    break;

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
