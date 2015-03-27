/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.planrnaseq;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
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
import com.vaadin.ui.Link;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
@Title("AnnotateView")
@Theme("mytheme")
public class BrowseAndAnnotate extends UI {

    Label userWelcome = new Label("");

    private TextField user;

    private PasswordField password;

    private Button loginSubmitButton;

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
    Button searchButton = new Button("Quick Search");
    Button slowSearchButton = new Button("General Search");
    Button guidedSearchButton = new Button("Guided Search");
    SQLContainer rnaseqContainer;
    String[] list_of_diseases = new String[]{"cancer", "diabetes", "obesity", "cardiovascular", "arthritis",
        "alzheimer"};
    String[] complexDiseaseArray = new String[]{"Cancer", "-- Any", "-- Pancreatic", "-- Breast", "-- Lung", "-- Brain", "-- Neuroblastoma",
        "-- Ovarian", "-- Gynecological", "-- Intestinal", "-- Colon", "-- Liver", "-- Leukemia", "-- Lymphoma", "-- Bladder", "-- Kidney", "-- Melanoma",
        "-- Prostate", "-- Thyroid", "-- Head and neck", "-- Bone",
        "Neurological", "-- Any", "-- Parkinson", "-- Alzheimer", "-- Bipolar", "-- Schizophrenia", "-- Dementia",
        "Cardiovascular", "-- Coronary artery", "-- Cardiomyopathy",
        "Diabetes", "-- Type1", "-- Type2",
        "Respiratory", "-- Tuberculosis", "-- Idiopathic pulmonary fibrosis", "-- Asthama"};
    String[] rareDiseaseArray = new String[]{"Rare", "-- Any",
        "-- Arthrogryposis", "-- Cystic fibrosis", "-- Intersex and medicine",
        "-- Mesothelioma", "-- People with caudal regression syndrome",
        "-- People with tetra-amelia syndrome", "-- Progeroid syndromes",
        "-- Rare cancers", "-- Rare infectious diseases", "-- Supernumerary body parts",
        "-- Tay–Sachs disease",
        "Inborn Errors of Metabolism", "-- Any",
        "-- Amino acid metabolism associated", "-- Carbohydrate metabolism associated",
        "-- Fatty acid metabolism associated", "-- Glycoprotein metabolism associated",
        "-- Heme metabolism associated", "-- Metal metabolism associated",
        "-- Purine & pyrimidine metabolism associated", "-- Lipid metabolism associated",
        "-- Phospholipid metabolism associated", "-- Proteoglycan metabolism associated",
        "-- Proteoglycan metabolism associated", "-- coenzyme, cofactor & vitamin metabolism associated"
    };
    String[] otherDiseaseArray = new String[]{"Ebola", "AIDS/HIV", "HBV", "HCV", "Trisomy", "Others"};
    String[] platforms = new String[]{"Illumina", "SOLID", "Roche 454", "PacBio", "Helicos", "Complete Genomics", "Ion Torrent"};
    int submit_manual_count = 0;

    VerticalLayout leftLayout = new VerticalLayout();
    HorizontalLayout leftTopLayout = new HorizontalLayout();
    VerticalLayout startHelpLayout = new VerticalLayout();

    @Override
    protected void init(VaadinRequest request) {

        initLayout();
        initSearch();
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void initLayout() {

        /* Root of the user interface component tree is set */
        VerticalLayout mainLayout = new VerticalLayout();
        Label titleLabel = new Label("<span style=\"color:rgb(255,255,255)\">MetaRNA-Seq: An interactive "
                + "tool to browse and annotate RNA-Seq meta-data</span>", ContentMode.HTML);
        titleLabel.addStyleName("maintitle");
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();

        mainLayout.addComponent(titleLabel);
        mainLayout.addComponent(splitPanel);

        setContent(mainLayout);
        splitPanel.setSizeFull();
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(splitPanel, 1);


        /* Build the component tree */
        //  VerticalLayout leftLayout = new VerticalLayout(); // moved this to class level as it is been accessed by other function
        VerticalSplitPanel rightSplitPanel = new VerticalSplitPanel();
        //  VerticalSplitPanel leftSplitPanel = new VerticalSplitPanel();

        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(rightSplitPanel);

        VerticalLayout rightTopLayout = new VerticalLayout();
        // rightTopLayout.addComponent(rightTopForm);
        rightTopTabsheet.setSizeFull();
        rightTopLayout.addComponent(rightTopTabsheet);
        rightTopTabsheet.addTab(startHelpLayout, "Start Help");
        StartHelp sh = new StartHelp();
        startHelpLayout.addComponent(sh);
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

        //HorizontalLayout leftTopLayout = new HorizontalLayout(); // moved this to class level as it is been accessed by other function
        leftLayout.addComponent(leftTopLayout);
        leftTopLayout.addComponent(searchField);
        leftTopLayout.addComponent(searchButton);
        leftTopLayout.addComponent(slowSearchButton);
        leftTopLayout.addComponent(guidedSearchButton);
        leftTopLayout.setWidth("100%");
        searchField.setWidth("100%");

        leftTopLayout.setExpandRatio(searchField, 1);
        leftLayout.addComponent(bioprojectSummaryTable);
        // leftLayout.setExpandRatio(searchField, 0);
        leftLayout.setExpandRatio(bioprojectSummaryTable, 1);
        bioprojectSummaryTable.setSizeFull();
        /* Set the contents in the left of the split panel to use all the space */
        leftLayout.setSizeFull();

        rightBottomLeftLayout.addComponent(tree);

        rightBottomRightLayout.addComponent(rightBottomTabsheet);
        rightBottomTabsheet.addTab(myform, "Details of selected Item");
        myform.setSizeFull();
        VerticalLayout rbTabBiosampleSummaryLayout = new VerticalLayout(); // Right bottom Biosample Summary
      //  rightBottomTabsheet.addTab(rbTabBiosampleSummaryLayout, "All Biosamples");
        rbTabBiosampleSummaryLayout.addComponent(biosampleSummaryTable);
        rbTabBiosampleSummaryLayout.setSizeFull();

        initDataAndSubcomponent();
        rightTopLayout.setSizeFull();
        rightBottomRightLayout.setSizeFull();

    }

    private void initDataAndSubcomponent() {
        //<editor-fold defaultstate="collapsed" desc="populating project/study summary table">      

        rnaseqContainer = createMySQLContainer("study_summary", "dummy");
        bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
        //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
        bioprojectSummaryTable.setCurrentPageFirstItemIndex(300);
        bioprojectSummaryTable.setSelectable(true);
        bioprojectSummaryTable.setImmediate(true);
        bioprojectSummaryTable.setColumnReorderingAllowed(true);
        bioprojectSummaryTable.setSortEnabled(true);
        bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases", "name"});
        //bioprojectSummaryTable.setVisibleColumns(new Object[] { "firstName", "lastName", "department", "phoneNumber", "street", "city", "zipCode" });
        studyName.setValue(rnaseqContainer.firstItemId().toString());

        bioprojectSummaryTable.setCellStyleGenerator(new Table.CellStyleGenerator() {
            @Override
            public String getStyle(Table table, Object itemId, Object propertyId) {
                String mynullreturn = "";
                if (propertyId == null) {
                    // Styling for row
                    //  Item item = bioprojectSummaryTable.getItem(itemId);
                    Item item = table.getItem(itemId);

                    String annotatus_status = "";

                    if (item == null) {    // checking this is important in lazy loading table. Otherwise it produces null pointer exception while scrolling down the table.
                        // System.out.println("It's null");
                        return mynullreturn;
                    } else {
                        if (item.getItemProperty("annotation_status").getValue() != null) {
                            annotatus_status = (String) item.getItemProperty("annotation_status").getValue();
                        }
                    }

//               String   annotatus_status =  (String) item.getItemProperty("annotation_status").getValue();
                    if (annotatus_status.toLowerCase().startsWith("ongoing")) {
                        // System.out.println(annotatus_status);
                        return "highlight-orange";
                    } else if (annotatus_status.toLowerCase().startsWith("completed")) {
                        return "highlight-green";
                    } else {
                        return mynullreturn;
                    }

                } else {
                    // styling for column propertyId
                    return mynullreturn;
                }

            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Upon clicking any project from the project/study summary table">

        bioprojectSummaryTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            int custom_annotation_counter = 0;

            public void itemClick(ItemClickEvent event) {
                rightTopTabsheet.setSelectedTab(rightTopForm);
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

                HorizontalLayout studyAccLinkLayout = new HorizontalLayout();
                Label labelStudyAcc = new Label("<b>SRA Study Accession : </b>" + selectedStudy + "&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                String ncbi_sra_study_link = "http://www.ncbi.nlm.nih.gov/Traces/sra/?study=" + selectedStudy;
                Link link = new Link("NCBI SRA Link", new ExternalResource(ncbi_sra_study_link));
                // Open the URL in a new window/tab
                link.setTargetName("_blank");
                // Indicate visually that it opens in a new window/tab
                link.setIcon(new ThemeResource("icons/external-link.png"));
                link.addStyleName("icon-after-caption");
                studyAccLinkLayout.addComponent(labelStudyAcc);
                studyAccLinkLayout.addComponent(link);
                rightTopForm.addComponent(studyAccLinkLayout);
                Label labelStudyTitle = new Label("<b>Study Title: </b>" + studyTitle, ContentMode.HTML);

                rightTopForm.addComponent(labelStudyTitle);
                Label labelStudyName = new Label("<b>Study Name: </b>" + studyName, ContentMode.HTML);
                rightTopForm.addComponent(labelStudyName);
                tempContainer = null;
                tempContainer = createMySQLContainer("study_abstracts", selectedStudy);  // In this table I will chaeck for the manual annotation status
                if (tempContainer.size() > 0) {
                    Item tempItem = tempContainer.getItem(tempContainer.getIdByIndex(0));
                    if (!(tempItem.getItemProperty("abstract").getValue() == null)) {
                        String abstr = tempItem.getItemProperty("abstract").getValue().toString();
                        String xref = tempItem.getItemProperty("xref").getValue().toString();
                        if (abstr.length() > 3) {
                            Label labelStudyAbstract = new Label("<b>Abstract: </b>" + abstr, ContentMode.HTML);
                            rightTopForm.addComponent(labelStudyAbstract);
                        }
                        if ((xref.length() > 3) & (xref.contains("pubmed"))) {
                            HorizontalLayout pubLinkLayout = new HorizontalLayout();
                            Label labelPubmed = new Label("<b>Pubmed Id : </b>" + "&nbsp;&nbsp;", ContentMode.HTML);
                            pubLinkLayout.addComponent(labelPubmed);
                            String[] pub = xref.split("\\|");
                            if (pub.length > 0) {
                                for (String p : pub) {
                                    String[] pid = p.split("\\:-");
                                    if (pid[0].startsWith("pubmed")) {
                                        Label tempPubLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);

                                        String pubmed_link = "http://www.ncbi.nlm.nih.gov/pubmed/" + pid[1];
                                        Link linkpub = new Link(pid[1], new ExternalResource(pubmed_link));
                                        linkpub.setTargetName("_blank");
                                        linkpub.setIcon(new ThemeResource("icons/external-link.png"));
                                        linkpub.addStyleName("icon-after-caption");
                                        pubLinkLayout.addComponent(tempPubLabel);
                                        pubLinkLayout.addComponent(linkpub);
                                    }
                                }
                            }

                            rightTopForm.addComponent(pubLinkLayout);
                        }
                    }
                }

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
                    // System.out.println(gse_summary);
                    Label labelGSE = new Label("<b>GEO Series Accession : </b>" + gse_accesion, ContentMode.HTML);
                    rightTopForm.addComponent(labelGSE);
                    Label labelGseSummary = new Label("<b>GSE Summary : </b>" + gse_summary, ContentMode.HTML);
                    rightTopForm.addComponent(labelGseSummary);
                    Label labelGseDesign = new Label("<b>GSE Design : </b>" + gse_design, ContentMode.HTML);
                    rightTopForm.addComponent(labelGseDesign);
//</editor-fold>

                }

                try {
                    String search_query = " SELECT * FROM manual_annotation "
                            + "where annotation_status = 'completed' "
                            + " AND studyid =  '" + selectedStudy + "'";

                    rnaseqContainer = createMySQLContainer("suggestion_by_manual_annotation", search_query);
                    if (rnaseqContainer.getItemIds().size() > 0) {
                        Label ManualAnnotationLabelStart = new Label("<b>Manual Annotaion </b>", ContentMode.HTML);
                        rightTopForm.addComponent(ManualAnnotationLabelStart);

                        for (int i = 0; i < rnaseqContainer.getItemIds().size(); i++) {
                            Item tempItem = rnaseqContainer.getItem(rnaseqContainer.getIdByIndex(i));
                            int annotation_count = i + 1;
                            String stringManualAnnotationDetails = "------------ Manual Annotaion " + annotation_count + "  ------------";
                            System.out.println("Item is " + tempItem);

                            if (!(tempItem.getItemProperty("isDisease").getValue() == null)) {
                                if (tempItem.getItemProperty("isDisease").getValue().toString().equals("1")) {
                                    stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> Disease = Yes ";
                                }
                            }
                            String stringStudyTypes = "";
                            if (!(tempItem.getItemProperty("isCaseControl").getValue() == null)) {
                                if (tempItem.getItemProperty("isCaseControl").getValue().toString().equals("1")) {
                                    stringStudyTypes = stringStudyTypes + "<br> Case-Control = Yes ";

                                }
                            }

                            if (!(tempItem.getItemProperty("isTimeSeries").getValue() == null)) {
                                if (tempItem.getItemProperty("isTimeSeries").getValue().toString().equals("1")) {
                                    stringStudyTypes = stringStudyTypes + "<br> Time Series = Yes ";

                                }
                            }

                            if (!(tempItem.getItemProperty("isTreatment").getValue() == null)) {
                                if (tempItem.getItemProperty("isTreatment").getValue().toString().equals("1")) {
                                    stringStudyTypes = stringStudyTypes + "<br> Treatment = Yes ";

                                }
                            }
                            if (stringStudyTypes.length() > 2) {
                                stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> <i> **** Study Types ****</i> " + stringStudyTypes;

                            }
                            String stringSampleTypes = "";
                            if (!(tempItem.getItemProperty("isCellLine").getValue() == null)) {
                                if (tempItem.getItemProperty("isCellLine").getValue().toString().equals("1")) {
                                    stringSampleTypes = stringSampleTypes + "<br> Cell Line = Yes ";

                                }
                            }
                            if (!(tempItem.getItemProperty("isPrimaryCells").getValue() == null)) {
                                if (tempItem.getItemProperty("isPrimaryCells").getValue().toString().equals("1")) {
                                    stringSampleTypes = stringSampleTypes + "<br> Primary Cells = Yes ";

                                }
                            }

                            if (!(tempItem.getItemProperty("isTissue").getValue() == null)) {
                                if (tempItem.getItemProperty("isTissue").getValue().toString().equals("1")) {
                                    stringSampleTypes = stringSampleTypes + "<br> Tissue = Yes ";

                                }
                            }
                            if (!(tempItem.getItemProperty("isWholeBlood").getValue() == null)) {
                                if (tempItem.getItemProperty("isWholeBlood").getValue().toString().equals("1")) {
                                    stringSampleTypes = stringSampleTypes + "<br> Blood = Yes ";
                                }
                            }

                            if (!(tempItem.getItemProperty("isPlasma").getValue() == null)) {
                                if (tempItem.getItemProperty("isPlasma").getValue().toString().equals("1")) {
                                    stringSampleTypes = stringSampleTypes + "<br> Plasma = Yes ";
                                }
                            }
                            if (stringSampleTypes.length() > 2) {
                                stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> <i>**** Sample Types ****</i> " + stringSampleTypes;

                            }
                            if (!(tempItem.getItemProperty("sequencing_platform").getValue() == null)) {
                                String sequencing_platform = tempItem.getItemProperty("sequencing_platform").getValue().toString();
                                String[] annotated_platforms = sequencing_platform.split("\\;");
                                stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> Sequencing Platform = " + sequencing_platform;
                            }

                            if (!(tempItem.getItemProperty("replicate_type").getValue() == null)) {
                                String replicate_type = tempItem.getItemProperty("replicate_type").getValue().toString();
                                stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> Replicate Type = " + replicate_type;
                            }

                            if (!(tempItem.getItemProperty("disease_category").getValue() == null)) {
                                String annotated_disease_category = tempItem.getItemProperty("disease_category").getValue().toString();
                                String[] annotated_disease_categories = annotated_disease_category.split("\\;");
                                String string_disease_cat = "";
                                for (String cat : annotated_disease_categories) {
                                    if (cat.startsWith("complex_disease")) {
                                        String[] cat_parts = cat.split("\\|");
                                        if (string_disease_cat.contains("complex_disease")) {
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(50) + " ---- " + cat_parts[1];
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(57) + " ---- " + cat_parts[2];
                                        } else {
                                            string_disease_cat = string_disease_cat + "&nbsp;&nbsp;Complex Disease ";
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(50) + " ---- " + cat_parts[1];
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(57) + " ---- " + cat_parts[2];
                                        }
                                    }
                                    if (cat.startsWith("rare_disease")) {
                                        String[] cat_parts = cat.split("\\|");
                                        if (string_disease_cat.contains("rare_disease")) {
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(50) + " ---- " + cat_parts[1];
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(57) + " ---- " + cat_parts[2];
                                        } else {
                                            if (string_disease_cat.contains("complex_disease")) {
                                                string_disease_cat = string_disease_cat + "<br> ";
                                            } else {
                                                string_disease_cat = string_disease_cat + "&nbsp;&nbsp;Rare Disease ";
                                            }
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(50) + " ---- " + cat_parts[1];
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(57) + " ---- " + cat_parts[2];
                                        }
                                    }
                                    if (cat.startsWith("other_disease")) {
                                        String[] cat_parts = cat.split("\\|");
                                        if (string_disease_cat.contains("other_disease")) {
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(50) + " ---- " + cat_parts[1];
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(57) + " ---- " + cat_parts[2];
                                        } else {
                                            if (string_disease_cat.contains("complex_disease") || string_disease_cat.contains("rare_disease")) {
                                                string_disease_cat = string_disease_cat + "<br> ";
                                            } else {
                                                string_disease_cat = string_disease_cat + "&nbsp;&nbsp;Other Disease ";
                                            }

                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(50) + " ---- " + cat_parts[1];
                                            string_disease_cat = string_disease_cat + "<br> " + createHTMLspaces(57) + " ---- " + cat_parts[2];
                                        }
                                    }
                                }
                                stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> Disease Category = " + string_disease_cat;
                            }

                            if (!(tempItem.getItemProperty("annotator").getValue() == null)) {
                                String annotator = tempItem.getItemProperty("annotator").getValue().toString();
                                stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br> Annotator = " + annotator;
                            }

                            stringManualAnnotationDetails = stringManualAnnotationDetails + "<br></br>-----------------------------------";
                            Label tempManualAnnotationDetails = new Label(stringManualAnnotationDetails, ContentMode.HTML);

                            rightTopForm.addComponent(tempManualAnnotationDetails);

                        }
                        //  Label ManualAnnotationLabelEnd = new Label("<b>Manual Annotaion </b>", ContentMode.HTML);
                        //    rightTopForm.addComponent(ManualAnnotationLabelEnd);

                    }
                } catch (Exception e) {
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
                        for (Iterator<String> it = tissue_set.iterator(); it.hasNext();) {
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
                //<editor-fold defaultstate="collapsed" desc="disease layout (Manual Annotation">
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

                Label diseaseLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + diseaseLabelString, ContentMode.HTML);
                Label diseaseTitle = new Label("<b>Disease: </b>", ContentMode.HTML);
                diseaseLayout.addComponent(diseaseTitle);
                diseaseLayout.addComponent(checkboxDiseaseNo);
                diseaseLayout.addComponent(checkboxDiseaseYes);
                diseaseLayout.addComponent(diseaseLabel);
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="study types layout (Manual Annotation)">
                Panel studyTypesPanel = new Panel("Sample Types");
                HorizontalLayout studyTypesLayout = new HorizontalLayout();

                HorizontalLayout caseControlLayout = new HorizontalLayout();
                CheckBox checkboxCaseControlYes = new CheckBox("Yes");
                CheckBox checkboxCaseControlNo = new CheckBox("No");
                // checkboxCaseControlYes.setValue(true);
                Label caseControlTitle = new Label("<b>Case-Control: </b>", ContentMode.HTML);
                caseControlLayout.addComponent(caseControlTitle);
                caseControlLayout.addComponent(checkboxCaseControlYes);
                caseControlLayout.addComponent(checkboxCaseControlNo);

                HorizontalLayout timeSeriesLayout = new HorizontalLayout();
                CheckBox checkboxTimeSeriesYes = new CheckBox("Yes");
                CheckBox checkboxTimeSeriesNo = new CheckBox("No");
                //checkboxTimeSeriesYes.setValue(true);
                Label timeSeriesTitle = new Label("<b>Time Series: </b>", ContentMode.HTML);
                timeSeriesLayout.addComponent(timeSeriesTitle);
                timeSeriesLayout.addComponent(checkboxTimeSeriesYes);
                timeSeriesLayout.addComponent(checkboxTimeSeriesNo);

                HorizontalLayout treatementLayout = new HorizontalLayout();
                CheckBox checkboxTreatmentYes = new CheckBox("Yes");
                CheckBox checkboxTreatmentNo = new CheckBox("No");
                //  checkboxTreatmentYes.setValue(true);
                Label treatmentTitle = new Label("<b>Treatment: </b>", ContentMode.HTML);
                treatementLayout.addComponent(treatmentTitle);
                treatementLayout.addComponent(checkboxTreatmentYes);
                treatementLayout.addComponent(checkboxTreatmentNo);

                studyTypesLayout.addComponent(caseControlLayout);
                Label emptyLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                studyTypesLayout.addComponent(emptyLabel);
                studyTypesLayout.addComponent(timeSeriesLayout);
                Label emptyLabel0 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                studyTypesLayout.addComponent(emptyLabel0);
                studyTypesLayout.addComponent(treatementLayout);
                studyTypesLayout.setSizeFull();
                studyTypesPanel.setContent(studyTypesLayout);
                studyTypesPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                studyTypesPanel.addStyleName("panelborder");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Disease Category Layout">
                Panel diseaseCategoryPanel = new Panel("Disease Category");
                HorizontalLayout diseaseCategoriesLayout = new HorizontalLayout();
                //Complex Disease
                ListSelect complexDisease = new ListSelect("Complex Disease");
                complexDisease.setMultiSelect(true);

                for (String disease : complexDiseaseArray) {
                    complexDisease.addItem(disease);

                }
                diseaseCategoriesLayout.addComponent(complexDisease);
                Label emptyLabel2 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                diseaseCategoriesLayout.addComponent(emptyLabel2);
                // Rare disease
                ListSelect rareDisease = new ListSelect("Rare Diseases");
                rareDisease.setMultiSelect(true);
                for (String disease : rareDiseaseArray) {
                    rareDisease.addItem(disease);
                }
                diseaseCategoriesLayout.addComponent(rareDisease);
                Label emptyLabel3 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                diseaseCategoriesLayout.addComponent(emptyLabel3);
                // Other diseases
                ListSelect otherDisease = new ListSelect("Other Diseases");
                otherDisease.setMultiSelect(true);
                for (String disease : otherDiseaseArray) {
                    otherDisease.addItem(disease);
                }
                diseaseCategoriesLayout.addComponent(otherDisease);
                //   Label emptyLabel4 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                //  diseaseCategoriesLayout.addComponent(emptyLabel4);

                diseaseCategoryPanel.setContent(diseaseCategoriesLayout);
                diseaseCategoryPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                diseaseCategoryPanel.addStyleName("panelborder");

                /*
                 HorizontalLayout diseaseCategoriesLayout = new HorizontalLayout();
                 Label diseaseCategoryLabel = new Label("<b><i>Suggestion: </i></b>" + disease_found + " <b> <i> Confidence: <i></b>  " + disease_confidence, ContentMode.HTML);
                 String[] diseaseCategories = new String[]{"Complex Disease", "Rare Disease", "Other", "Not Sure"};
                 List<String> diseaseCategoriesList = Arrays.asList(diseaseCategories);
                 ComboBox diseaseCategoryComboBox = new ComboBox("Disease Category", diseaseCategoriesList);
                 diseaseCategoriesLayout.addComponent(diseaseCategoryComboBox);
                 diseaseCategoriesLayout.addComponent(diseaseCategoryLabel);
                 */
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="platform layout (manual annotation)">
                //  List<String> platformsList = Arrays.asList(platforms);
                //  ComboBox platformsListSelect = new ComboBox("Sequencing Platform", platformsList);
                ListSelect platformsListSelect = new ListSelect("Sequencing Platform");
                platformsListSelect.setMultiSelect(true);
                for (String platform : platforms) {
                    platformsListSelect.addItem(platform);
                }
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
                        if (matchedPlatformSet.size() == 1) {
                            platform_from_sra = matchedPlatformSet.iterator().next().toString();
                            for (Iterator i = platformsListSelect.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (platform_from_sra.equalsIgnoreCase(temp)) {
                                    platformsListSelect.select(iid);
                                }
                            }

                        } else {
                            platform_from_sra = "Can't predict. All experiment on different Platforms";
                        }
                    } else {
                        if (matchedPlatformSet.size() == 1) {
                            // Perfect 
                            platform_from_sra = matchedPlatformSet.iterator().next().toString();
                            for (Iterator i = platformsListSelect.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (platform_from_sra.equalsIgnoreCase(temp)) {
                                    platformsListSelect.select(iid);
                                }
                            }
                            platorm_confidence = "100%";
                        } else {

                        }
                    }
                }

                HorizontalLayout platformLayout = new HorizontalLayout();
                Label suggestedPlatformLabel = new Label("<b><i>Suggestion: </i></b>" + platform_from_sra + "<b> <i> Confidence: <i></b>  " + platorm_confidence, ContentMode.HTML);
                platformsListSelect.setHeight(platformsListSelect.size() + 2, Unit.EM);
                platformLayout.addComponent(platformsListSelect);
                platformLayout.addComponent(suggestedPlatformLabel);
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Sample types Panel (Manual Annotation)">
                Panel sampleTypesPanel = new Panel("Sample Types");
                VerticalLayout sampleTypesLayout = new VerticalLayout();
                CheckBox checkboxSampleTypeCellLine = new CheckBox("Cell Line");
                CheckBox checkboxSampleTypeTissue = new CheckBox("Tissue");
                CheckBox checkboxSampleTypePrimaryCells = new CheckBox("Primary Cells");
                CheckBox checkboxSampleTypeWholeBlood = new CheckBox("Blood");
                CheckBox checkboxSampleTypePlasma = new CheckBox("Plasma");
                String suggestedCellTypeLabelString = "";
                if (suggestion_cell_line.startsWith("Yes")) {
                    //sampleTypesListComboBox.select("Cell Lines");
                    checkboxSampleTypeCellLine.setValue(true);
                    suggestedCellTypeLabelString = "<b><i>&nbsp;&nbsp;&nbsp;&nbsp;Suggestion: </i></b>" + "Cell Lines --> " + suggestion_cell_line + " <b> <i> Confidence: <i></b>   " + cell_line_confidence;
                }
                Label suggestedCellLineLabel = new Label(suggestedCellTypeLabelString, ContentMode.HTML);
                String suggestedTissueLabelString = "";
                if (suggestion_organism_part.startsWith("Yes")) {
                    checkboxSampleTypeTissue.setValue(true);
                    suggestedTissueLabelString = suggestedTissueLabelString + "<b><i>&nbsp;&nbsp;&nbsp;&nbsp;Suggestion: </i></b>" + "Organism part --> " + suggestion_organism_part + " <b> <i> Confidence: <i></b>   " + organism_part_confidence;
                }

                if (suggestion_tissue.startsWith("Yes")) {
                    checkboxSampleTypeTissue.setValue(true);
                    suggestedTissueLabelString = suggestedTissueLabelString + "<b><i>&nbsp;&nbsp;&nbsp;&nbsp;Suggestion: </i></b>" + "Tissue --> " + suggestion_tissue + " <b> <i> Confidence: <i></b>   " + tissue_confidence;
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

                HorizontalLayout PrimaryCellsLayout = new HorizontalLayout();
                PrimaryCellsLayout.addComponent(checkboxSampleTypePrimaryCells);
                //  PrimaryCellsLayout.addComponent(suggestedPrimaryCellsLabel);

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
                sampleTypesLayout.addComponent(PrimaryCellsLayout);
                sampleTypesLayout.addComponent(TissueLayout);
                sampleTypesLayout.addComponent(WholeBloodLayout);
                sampleTypesLayout.addComponent(PlasmaLayout);

                sampleTypesPanel.setContent(sampleTypesLayout);
                sampleTypesPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                sampleTypesPanel.addStyleName("panelborder");

                //</editor-fold>
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
                checkboxCaseControlYes.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (checkboxCaseControlYes.getValue()) {
                            checkboxCaseControlNo.setValue(!checkboxCaseControlYes.getValue());
                        }

                    }
                }
                );
                checkboxCaseControlNo.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (checkboxCaseControlNo.getValue()) {
                            checkboxCaseControlYes.setValue(!checkboxCaseControlNo.getValue());
                        }
                    }
                }
                );

                checkboxTimeSeriesYes.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (checkboxTimeSeriesYes.getValue()) {
                            checkboxTimeSeriesNo.setValue(!checkboxTimeSeriesYes.getValue());
                        }

                    }
                }
                );
                checkboxTimeSeriesNo.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (checkboxTimeSeriesNo.getValue()) {
                            checkboxTimeSeriesYes.setValue(!checkboxTimeSeriesNo.getValue());
                        }
                    }
                }
                );

                checkboxTreatmentYes.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (checkboxTreatmentYes.getValue()) {
                            checkboxTreatmentNo.setValue(!checkboxTreatmentYes.getValue());
                        }

                    }
                }
                );
                checkboxTreatmentNo.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (checkboxTreatmentNo.getValue()) {
                            checkboxTreatmentYes.setValue(!checkboxTreatmentNo.getValue());
                        }
                    }
                }
                );

                checkboxDiseaseYes.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        checkboxDiseaseNo.setValue(!checkboxDiseaseYes.getValue());
                        diseaseCategoryPanel.setVisible(true);
                    }
                }
                );
                checkboxDiseaseNo.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        checkboxDiseaseYes.setValue(!checkboxDiseaseNo.getValue());
                        diseaseCategoryPanel.setVisible(false);
                    }
                }
                );

                //<editor-fold defaultstate="collapsed" desc="Annotation Status Ongoing or Completed ">
                Panel annotationStatusPanel = new Panel("Annotation Status");
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

                HorizontalLayout annotationStatusLayout = new HorizontalLayout();
                annotationStatusLayout.addComponent(checkboxAnnotaionOngoing);
                Label emptyLabel_1 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                annotationStatusLayout.addComponent(emptyLabel_1);
                annotationStatusLayout.addComponent(checkboxAnnotaionCompleted);
                annotationStatusPanel.setContent(annotationStatusLayout);
                annotationStatusPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                annotationStatusPanel.addStyleName("panelborder");
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="login, sign in, register, and suggestion by manual annotation if logged ">
                Button loginButton = new Button("Sign In");
                Button registerButton = new Button("Register");

                HorizontalLayout requestLoginLayout = new HorizontalLayout();
                requestLoginLayout.addComponent(userWelcome);
                requestLoginLayout.addComponent(loginButton);
                requestLoginLayout.addComponent(registerButton);
                rightTopAnnotationForm.addComponent(requestLoginLayout);
                VerticalLayout userPasswordLayout = new VerticalLayout();
                VerticalLayout registerLayout = new VerticalLayout();

                loginButton.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (event.getButton() == loginButton) {
                            if (loginButton.getCaption() == "Sign In") {

                                // Create the user input field
                                user = new TextField("User:");
                                user.setWidth("300px");
                                user.setRequired(true);
                                user.setInputPrompt("Your username (eg. joe@email.com)");
                                user.addValidator(new EmailValidator(
                                        "Username must be an email address"));
                                user.setInvalidAllowed(false);

                                // Create the password input field
                                password = new PasswordField("Password:");
                                password.setWidth("300px");
                                password.addValidator(new PasswordValidator());
                                password.setRequired(true);
                                password.setValue("");
                                password.setNullRepresentation("");

                                // Create login button
                                loginSubmitButton = new Button("Login", this);
                                loginSubmitButton.addClickListener(new Button.ClickListener() {

                                    @Override
                                    public void buttonClick(ClickEvent event) {
                                        if (event.getButton() == loginSubmitButton) {
                                            if (!user.isValid() || !password.isValid()) {
                                                return;
                                            }
                                            String username = user.getValue();
                                            String entered_password = password.getValue();

                                            boolean isChecked = false;
                                            try {
                                                // check user details in the database
                                                SQLContainer checkContainer = createMySQLContainer("annotation_users", "dummy");
                                                Item id = checkContainer.getItem(new RowId(new Object[]{username}));
                                                if (id != null) {
                                                    user.setCaption("User");
                                                    if (entered_password.equals(id.getItemProperty("password").getValue().toString())) {
                                                        isChecked = true;
                                                    } else {
                                                        password.setCaption("Password (wrong password entered)");
                                                        System.out.println("Password can not be validated. Please re-enter or Register");
                                                        System.out.println("Fetched password for user : " + id.toString() + " is " + id.getItemProperty("password").getValue().toString());
                                                    }
                                                } else {
                                                    user.setCaption("User (wrong username entered. Enter your email again or register)");
                                                    password.setCaption("Password");
                                                    System.out.println("User name can not be validated. Please re-enter or Register");
                                                }

                                            } catch (Exception e) {
                                                isChecked = false;
                                                System.out.println("Problem in validating login details using database. Either user or password is wrong");
                                            }
                                            boolean isValid = username.equals("test@test.com")
                                                    && entered_password.equals("passw0rd");

                                            if (isChecked) {
                                                // System.out.println("User name and passoword : Both are correct");
                                                // Store the current user in the service session
                                                getSession().setAttribute("user", username);
                                                userWelcome.setValue("Hello " + username);
                                                int userPasswordLayoutIndex = rightTopAnnotationForm.getComponentIndex(userPasswordLayout);
                                                requestLoginLayout.removeComponent(registerButton);
                                                rightTopAnnotationForm.addComponent(requestLoginLayout, userPasswordLayoutIndex);
                                                rightTopAnnotationForm.removeComponent(userPasswordLayout);
                                                userPasswordLayout.removeAllComponents();
                                                loginButton.setCaption("Logout");
                                            } else {
                                                // Wrong password clear the password field and refocuses it
                                                password.setValue(null);
                                                password.focus();

                                            }
                                            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                        }
                                    }
                                });

                                // Add both to a panel
                                VerticalLayout fields = new VerticalLayout(user, password, loginSubmitButton);
                                fields.setCaption("Please login to access the application. (test@test.com/passw0rd)");
                                fields.setSpacing(true);
                                fields.setMargin(new MarginInfo(true, true, true, false));
                                fields.setSizeUndefined();

                                // The view root layout
                                userPasswordLayout.addComponent(fields);
                                int addUserLayoutIndex = rightTopAnnotationForm.getComponentIndex(requestLoginLayout);
                                rightTopAnnotationForm.addComponent(userPasswordLayout, addUserLayoutIndex);
                                rightTopAnnotationForm.removeComponent(requestLoginLayout);

                            } else if (loginButton.getCaption() == "Logout") {
                                getSession().setAttribute("user", null);
                                userWelcome.setValue("Anonymous ");
                                loginButton.setCaption("Sign In");
                                requestLoginLayout.addComponent(registerButton);

                            }
                        }

                    }
                });

                registerButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (event.getButton() == registerButton) {

                            // Create the user input field
                            TextField newUser = new TextField("User:");
                            newUser.setWidth("300px");
                            newUser.setRequired(true);
                            newUser.setInputPrompt("Your username (eg. joe@email.com)");
                            newUser.addValidator(new EmailValidator(
                                    "Username must be an email address"));
                            newUser.setInvalidAllowed(false);

                            // Create the password input field
                            PasswordField setPassword = new PasswordField("Set Password:");
                            setPassword.setWidth("300px");
                            setPassword.addValidator(new PasswordValidator());
                            setPassword.setRequired(true);
                            setPassword.setValue("");
                            setPassword.setNullRepresentation("");

                            // Create login button
                            Button registerSubmitButton = new Button("Register me", this);
                            registerSubmitButton.addClickListener(new Button.ClickListener() {

                                @Override
                                public void buttonClick(ClickEvent event) {
                                    if (event.getButton() == registerSubmitButton) {
                                        if (!newUser.isValid() || !setPassword.isValid()) {
                                            return;
                                        }
                                        String username = newUser.getValue();
                                        String entered_password = setPassword.getValue();
                                        String insert_user = "'" + username + "' , '" + entered_password + "'";

                                        boolean isInserted = true;
                                        try {
                                            // Insert new user details in the database
                                            SQLContainer insertContainer = createMySQLContainer("annotation_users", insert_user);
                                            Object id = insertContainer.addItem();
                                            insertContainer.getContainerProperty(id, "user").setValue(username);
                                            insertContainer.getContainerProperty(id, "password").setValue(entered_password);
                                            insertContainer.commit();
                                        } catch (Exception e) {
                                            isInserted = false;
                                            System.out.println("Problem in registering new user while inserting into the database");
                                        }

                                        if (isInserted) {
                                            userWelcome.setValue("Registered Successfulle. Please Sign in to annotate. ");
                                            int registerLayoutIndex = rightTopAnnotationForm.getComponentIndex(registerLayout);
                                            rightTopAnnotationForm.addComponent(requestLoginLayout, registerLayoutIndex);
                                            rightTopAnnotationForm.removeComponent(registerLayout);
                                            registerLayout.removeAllComponents();
                                            requestLoginLayout.removeComponent(registerButton);
                                        } else {
                                            // Wrong password clear the password field and refocuses it
                                            password.setValue(null);
                                            password.focus();

                                        }
                                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                                    }
                                }
                            });

                            // Add both to a panel
                            VerticalLayout registerFields = new VerticalLayout(newUser, setPassword, registerSubmitButton);
                            registerFields.setCaption("Please login to access the application. (test@test.com/passw0rd)");
                            registerFields.setSpacing(true);
                            registerFields.setMargin(new MarginInfo(true, true, true, false));
                            registerFields.setSizeUndefined();

                            registerLayout.addComponent(registerFields);
                            int requestLoginLayoutIndex = rightTopAnnotationForm.getComponentIndex(requestLoginLayout);
                            rightTopAnnotationForm.addComponent(registerLayout, requestLoginLayoutIndex);
                            rightTopAnnotationForm.removeComponent(requestLoginLayout);

                        }
                    }
                });
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                if (isLoggedIn) {
                    // System.out.println("user attribute" + String.valueOf(getSession().getAttribute("user")));
                    String logged_user = String.valueOf(getSession().getAttribute("user"));
                    userWelcome.setValue("Hello " + logged_user);
                    loginButton.setCaption("Logout");
                    requestLoginLayout.removeComponent(registerButton);
                    try {

                        String search_query = " SELECT * FROM manual_annotation "
                                + "where annotator = '" + logged_user + "'"
                                + " AND studyid =  '" + selectedStudy + "'";

                        rnaseqContainer = createMySQLContainer("suggestion_by_manual_annotation", search_query);
                        //rnaseqContainer.removeAllContainerFilters();
                        Item lastItem = rnaseqContainer.getItem(rnaseqContainer.lastItemId());
                        System.out.println("Item is " + lastItem);
                        //   String annotated_disease_category = lastItem.getItemProperty("disease_category").getValue().toString();
                        String isDisease = "1";
                        if (!(lastItem.getItemProperty("isDisease").getValue() == null)) {
                            isDisease = lastItem.getItemProperty("isDisease").getValue().toString();
                            // System.out.println("disease is not null" +  isDisease);
                            if (isDisease.equals("1")) {
                                checkboxDiseaseYes.setValue(true);
                            } else if (isDisease.equals("0")) {
                                checkboxDiseaseNo.setValue(true);
                            }
                        } else {
                            checkboxDiseaseYes.setValue(true);
                            //  System.out.println("disease is null");
                        }

                        if (!(lastItem.getItemProperty("isCaseControl").getValue() == null)) {
                            if (lastItem.getItemProperty("isCaseControl").getValue().toString().equals("1")) {
                                checkboxCaseControlYes.setValue(true);
                            } else if (lastItem.getItemProperty("isCaseControl").getValue().toString().equals("0")) {
                                checkboxCaseControlNo.setValue(true);
                            }
                        }

                        if (!(lastItem.getItemProperty("isTimeSeries").getValue() == null)) {
                            if (lastItem.getItemProperty("isTimeSeries").getValue().toString().equals("1")) {
                                checkboxTimeSeriesYes.setValue(true);
                            } else if (lastItem.getItemProperty("isTimeSeries").getValue().toString().equals("0")) {
                                checkboxTimeSeriesNo.setValue(true);
                            }
                        }

                        if (!(lastItem.getItemProperty("isTreatment").getValue() == null)) {
                            if (lastItem.getItemProperty("isTreatment").getValue().toString().equals("1")) {
                                checkboxTreatmentYes.setValue(true);
                            } else if (lastItem.getItemProperty("isTreatment").getValue().toString().equals("0")) {
                                checkboxTreatmentNo.setValue(true);
                            }
                        }

                        if (!(lastItem.getItemProperty("isCellLine").getValue() == null)) {
                            if (lastItem.getItemProperty("isCellLine").getValue().toString().equals("1")) {
                                checkboxSampleTypeCellLine.setValue(true);
                            } else if (lastItem.getItemProperty("isCellLine").getValue().toString().equals("0")) {
                                checkboxSampleTypeCellLine.setValue(false);
                            }
                        }
                        if (!(lastItem.getItemProperty("isPrimaryCells").getValue() == null)) {
                            if (lastItem.getItemProperty("isPrimaryCells").getValue().toString().equals("1")) {
                                checkboxSampleTypePrimaryCells.setValue(true);
                            } else if (lastItem.getItemProperty("isPrimaryCells").getValue().toString().equals("0")) {
                                checkboxSampleTypePrimaryCells.setValue(false);
                            }
                        }

                        if (!(lastItem.getItemProperty("isTissue").getValue() == null)) {
                            if (lastItem.getItemProperty("isTissue").getValue().toString().equals("1")) {
                                checkboxSampleTypeTissue.setValue(true);
                            } else if (lastItem.getItemProperty("isTissue").getValue().toString().equals("0")) {
                                checkboxSampleTypeTissue.setValue(false);
                            }
                        }
                        if (!(lastItem.getItemProperty("isWholeBlood").getValue() == null)) {
                            if (lastItem.getItemProperty("isWholeBlood").getValue().toString().equals("1")) {
                                checkboxSampleTypeWholeBlood.setValue(true);
                            } else if (lastItem.getItemProperty("isWholeBlood").getValue().toString().equals("0")) {
                                checkboxSampleTypeWholeBlood.setValue(false);
                            }
                        }

                        if (!(lastItem.getItemProperty("isPlasma").getValue() == null)) {
                            if (lastItem.getItemProperty("isPlasma").getValue().toString().equals("1")) {
                                checkboxSampleTypePlasma.setValue(true);
                            } else if (lastItem.getItemProperty("isPlasma").getValue().toString().equals("0")) {
                                checkboxSampleTypePlasma.setValue(false);
                            }
                        }

                        if (!(lastItem.getItemProperty("sequencing_platform").getValue() == null)) {
                            String sequencing_platform = lastItem.getItemProperty("sequencing_platform").getValue().toString();
                            String[] annotated_platforms = sequencing_platform.split("\\;");
                            for (Iterator i = platformsListSelect.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (annotated_platforms.length > 0) {
                                    for (String pf : annotated_platforms) {
                                        if (pf.equalsIgnoreCase(temp)) {
                                            platformsListSelect.select(iid);
                                        }
                                    }
                                }

                            }
                        }

                        if (!(lastItem.getItemProperty("replicate_type").getValue() == null)) {
                            String replicate_type = lastItem.getItemProperty("replicate_type").getValue().toString();
                            for (Iterator i = replicatTypesListComboBox.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (replicate_type.equalsIgnoreCase(temp)) {
                                    replicatTypesListComboBox.select(iid);
                                }
                            }

                        }

                        if (!(lastItem.getItemProperty("annotation_status").getValue() == null)) {
                            if (lastItem.getItemProperty("annotation_status").getValue().toString().equals("ongoing")) {
                                checkboxAnnotaionOngoing.setValue(true);
                            } else if (lastItem.getItemProperty("annotation_status").getValue().toString().equals("completed")) {
                                checkboxAnnotaionCompleted.setValue(true);
                            }
                        }

                        String annotated_disease_category = lastItem.getItemProperty("disease_category").getValue().toString();
                        String[] annotated_disease_categories = annotated_disease_category.split("\\;");

                        String main_disease = "";
                        for (Iterator i = complexDisease.getItemIds().iterator(); i.hasNext();) {
                            Object iid = (Object) i.next();
                            String selected_disease_category = "";
                            String temp = iid.toString();
                            if (!temp.startsWith("--")) {
                                main_disease = temp;
                            }

                            if (iid.toString().startsWith("--")) { // sub disease is selected
                                if (selected_disease_category.isEmpty()) {
                                    selected_disease_category = "complex_disease|" + main_disease + "|" + temp;
                                } else {
                                    selected_disease_category = selected_disease_category + ","
                                            + "complex_disease|" + main_disease + "|" + temp;
                                }

                            } else {
                                if (selected_disease_category.isEmpty()) {
                                    selected_disease_category = "complex_disease|" + main_disease + "|" + "-- Any";
                                } else {
                                    selected_disease_category = selected_disease_category + ","
                                            + "complex_disease|" + main_disease + "|" + "-- Any";
                                }

                            }

                            if (annotated_disease_categories.length > 0) {
                                for (String cat : annotated_disease_categories) {
                                    //  System.out.println(cat + selected_disease_category);
                                    if (cat.equals(selected_disease_category)) {
                                        complexDisease.select(iid);
                                    }
                                }
                            }
                        }

                        main_disease = "";
                        for (Iterator i = rareDisease.getItemIds().iterator(); i.hasNext();) {
                            Object iid = (Object) i.next();
                            String selected_disease_category = "";
                            String temp = iid.toString();
                            if (!temp.startsWith("--")) {
                                main_disease = temp;
                            }

                            if (iid.toString().startsWith("--")) { // sub disease is selected
                                if (selected_disease_category.isEmpty()) {
                                    selected_disease_category = "rare_disease|" + main_disease + "|" + temp;
                                } else {
                                    selected_disease_category = selected_disease_category + ","
                                            + "rare_disease|" + main_disease + "|" + temp;
                                }

                            } else {
                                if (selected_disease_category.isEmpty()) {
                                    selected_disease_category = "rare_disease|" + main_disease + "|" + "-- Any";
                                } else {
                                    selected_disease_category = selected_disease_category + ","
                                            + "rare_disease|" + main_disease + "|" + "-- Any";
                                }

                            }

                            if (annotated_disease_categories.length > 0) {
                                for (String cat : annotated_disease_categories) {
                                    //  System.out.println(cat + selected_disease_category);
                                    if (cat.equals(selected_disease_category)) {
                                        rareDisease.select(iid);
                                    }
                                }
                            }
                        }

                        main_disease = "";
                        for (Iterator i = otherDisease.getItemIds().iterator(); i.hasNext();) {
                            Object iid = (Object) i.next();
                            String selected_disease_category = "";
                            String temp = iid.toString();
                            if (!temp.startsWith("--")) {
                                main_disease = temp;
                            }

                            if (iid.toString().startsWith("--")) { // sub disease is selected
                                if (selected_disease_category.isEmpty()) {
                                    selected_disease_category = "other_disease|" + main_disease + "|" + temp;
                                } else {
                                    selected_disease_category = selected_disease_category + ","
                                            + "other_disease|" + main_disease + "|" + temp;
                                }

                            } else {
                                if (selected_disease_category.isEmpty()) {
                                    selected_disease_category = "other_disease|" + main_disease + "|" + "-- Any";
                                } else {
                                    selected_disease_category = selected_disease_category + ","
                                            + "other_disease|" + main_disease + "|" + "-- Any";
                                }

                            }

                            if (annotated_disease_categories.length > 0) {
                                for (String cat : annotated_disease_categories) {
                                    // System.out.println(cat + selected_disease_category);
                                    if (cat.equals(selected_disease_category)) {
                                        otherDisease.select(iid);
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                    }

                }
                //</editor-fold>

                Button submitButton = new Button("Submit");
                submit_manual_count = 0;
                Label manualSubmitStatus = new Label("");
                submitButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (event.getButton() == submitButton) {
                            submit_manual_count = submit_manual_count + 1;
                            int isDisease = 0;
                            if (checkboxDiseaseYes.getValue() && !checkboxDiseaseNo.getValue()) {
                                isDisease = 1;
                            }
                            String selected_disease_category = "";
                            String main_disease = "";
                            for (Iterator i = complexDisease.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (!temp.startsWith("--")) {
                                    main_disease = temp;
                                }
                                if (complexDisease.isSelected(iid)) {
                                    //  System.out.println("Selected" + temp);

                                    if (iid.toString().startsWith("--")) { // sub disease is selected
                                        if (selected_disease_category.isEmpty()) {
                                            selected_disease_category = "complex_disease|" + main_disease + "|" + temp;
                                        } else {
                                            selected_disease_category = selected_disease_category + ";"
                                                    + "complex_disease|" + main_disease + "|" + temp;
                                        }

                                    } else { //main disease is selected
                                        //     selected_disease_main = main_disease;
                                        if (selected_disease_category.isEmpty()) {
                                            selected_disease_category = "complex_disease|" + main_disease + "|" + "Any";
                                        } else {
                                            selected_disease_category = selected_disease_category + ";"
                                                    + "complex_disease|" + main_disease + "|" + "Any";
                                        }

                                    }
                                }
                            }
                            main_disease = "";
                            for (Iterator i = rareDisease.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (!temp.startsWith("--")) {
                                    main_disease = temp;
                                }
                                if (rareDisease.isSelected(iid)) {
                                    //  System.out.println("Selected" + temp);

                                    if (iid.toString().startsWith("--")) { // sub disease is selected
                                        if (selected_disease_category.isEmpty()) {
                                            selected_disease_category = "rare_disease|" + main_disease + "|" + temp;
                                        } else {
                                            selected_disease_category = selected_disease_category + ";"
                                                    + "rare_disease|" + main_disease + "|" + temp;
                                        }

                                    } else { //main disease is selected
                                        //     selected_disease_main = main_disease;
                                        if (selected_disease_category.isEmpty()) {
                                            selected_disease_category = "rare_disease|" + main_disease + "|" + "Any";
                                        } else {
                                            selected_disease_category = selected_disease_category + ";"
                                                    + "rare_disease|" + main_disease + "|" + "Any";
                                        }

                                    }
                                }
                            }
                            main_disease = "";
                            for (Iterator i = otherDisease.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();
                                if (!temp.startsWith("--")) {
                                    main_disease = temp;
                                }
                                if (otherDisease.isSelected(iid)) {
                                    //  System.out.println("Selected" + temp);

                                    if (iid.toString().startsWith("--")) { // sub disease is selected
                                        if (selected_disease_category.isEmpty()) {
                                            selected_disease_category = "other_disease|" + main_disease + "|" + temp;
                                        } else {
                                            selected_disease_category = selected_disease_category + ";"
                                                    + "other_disease|" + main_disease + "|" + temp;
                                        }

                                    } else {
                                        if (selected_disease_category.isEmpty()) {
                                            selected_disease_category = "other_disease|" + main_disease + "|" + "Any";
                                        } else {
                                            selected_disease_category = selected_disease_category + ";"
                                                    + "other_disease|" + main_disease + "|" + "Any";
                                        }

                                    }
                                }
                            }

                            int isCaseControl = 0;
                            if (checkboxCaseControlYes.getValue() && !checkboxCaseControlNo.getValue()) {
                                isCaseControl = 1;
                            }
                            int isTimeSeries = 0;
                            if (checkboxTimeSeriesYes.getValue() && !checkboxTimeSeriesNo.getValue()) {
                                isTimeSeries = 1;
                            }
                            int isTreatment = 0;
                            if (checkboxTreatmentYes.getValue() && !checkboxTreatmentNo.getValue()) {
                                isTreatment = 1;
                            }
                            int isTissue = 0;
                            if (checkboxSampleTypeTissue.getValue()) {
                                isTissue = 1;
                            }
                            int isCellLine = 0;
                            if (checkboxSampleTypeCellLine.getValue()) {
                                isCellLine = 1;
                            }
                            int isPrimaryCells = 0;
                            if (checkboxSampleTypePrimaryCells.getValue()) {
                                isPrimaryCells = 1;
                            }
                            int isWholeBlood = 0;
                            if (checkboxSampleTypeWholeBlood.getValue()) {
                                isWholeBlood = 1;
                            }
                            int isPlasma = 0;
                            if (checkboxSampleTypePlasma.getValue()) {
                                isPlasma = 1;
                            }

                            String selected_sequencing_platforms = "";
                            for (Iterator i = platformsListSelect.getItemIds().iterator(); i.hasNext();) {
                                Object iid = (Object) i.next();
                                String temp = iid.toString();

                                if (platformsListSelect.isSelected(iid)) {
                                    if (selected_sequencing_platforms.isEmpty()) {
                                        selected_sequencing_platforms = temp;
                                    } else {
                                        selected_sequencing_platforms = selected_sequencing_platforms + ";" + temp;
                                    }

                                }
                            }

                            String annotation_status = "ongoing";
                            if (checkboxAnnotaionCompleted.getValue() && !checkboxAnnotaionOngoing.getValue()) {
                                annotation_status = "completed";
                            }

                            String custom_annotation_value = "";
                            for (int j = 1; j <= custom_annotation_counter; j++) {
                                if (findById(rightTopAnnotationForm, "customAnnoName" + j) instanceof TextField) {
                                    TextField tempTFname = (TextField) findById(rightTopAnnotationForm, "customAnnoName" + j);
                                    custom_annotation_value = custom_annotation_value + tempTFname.getValue() + ":::";
                                    //  System.out.println(j + "custom name: " + tempTFname.getValue());
                                }
                                if (findById(rightTopAnnotationForm, "customAnnoValue" + j) instanceof TextField) {
                                    TextField tempTFvalue = (TextField) findById(rightTopAnnotationForm, "customAnnoValue" + j);
                                    custom_annotation_value = custom_annotation_value + tempTFvalue.getValue() + "###";
                                    //  System.out.println(j + "custom value: " + tempTFvalue.getValue());
                                }
                            }
                            String annotator = "Anonymous";
                            boolean isLoggedIn = getSession().getAttribute("user") != null;
                            if (isLoggedIn) {
                                annotator = String.valueOf(getSession().getAttribute("user"));
                                //  System.out.println("annotator is " + annotator);
                            }
                            Boolean isInserted = true;
                            try {
                                // Insert new user details in the database
                                SQLContainer insertContainer = createMySQLContainer("manual_annotation", "dummy");
                                Object id = insertContainer.addItem();
                                insertContainer.getContainerProperty(id, "studyId").setValue(selectedStudy);
                                insertContainer.getContainerProperty(id, "isDisease").setValue(isDisease);
                                insertContainer.getContainerProperty(id, "isTimeSeries").setValue(isTimeSeries);
                                insertContainer.getContainerProperty(id, "isTreatment").setValue(isTreatment);
                                insertContainer.getContainerProperty(id, "isCellLine").setValue(isCellLine);
                                insertContainer.getContainerProperty(id, "isPrimaryCells").setValue(isPrimaryCells);
                                insertContainer.getContainerProperty(id, "isCaseControl").setValue(isCaseControl);
                                insertContainer.getContainerProperty(id, "isTissue").setValue(isTissue);
                                insertContainer.getContainerProperty(id, "disease_category").setValue(selected_disease_category);
                                insertContainer.getContainerProperty(id, "sequencing_platform").setValue(selected_sequencing_platforms);
                                insertContainer.getContainerProperty(id, "replicate_type").setValue(replicatTypesListComboBox.getValue());
                                insertContainer.getContainerProperty(id, "isWholeBlood").setValue(isWholeBlood);
                                insertContainer.getContainerProperty(id, "isPlasma").setValue(isPlasma);
                                insertContainer.getContainerProperty(id, "custom_annotation").setValue(custom_annotation_value);
                                insertContainer.getContainerProperty(id, "annotation_status").setValue(annotation_status);
                                insertContainer.getContainerProperty(id, "annotator").setValue(annotator);
                                insertContainer.commit();
                                manualSubmitStatus.setCaption("Submitted Manual Annotation Successfully");
                                SQLContainer dummyStudyContainer = null;
                                if (annotation_status.equalsIgnoreCase("ongoing")) {
                                    dummyStudyContainer = createMySQLContainer("study_summary_ongoing", selectedStudy);
                                } else if (annotation_status.equalsIgnoreCase("completed")) {
                                    dummyStudyContainer = createMySQLContainer("study_summary_completed", selectedStudy);
                                }

                                //  Object studyid = insertStudyContainer.getItem(new RowId(new Object[]{selectedStudy}));
                                //  System.out.println("selected study is " + selectedStudy);
                                //   System.out.println("selected study id is  " + studyid.toString());
                                //  insertStudyContainer.getContainerProperty(studyid, "annotation_status").setValue(annotation_status);
                                //   insertStudyContainer.commit();
                                if (submit_manual_count > 1) {
                                    manualSubmitStatus.setCaption("Submitted Manual Annotation Successfully " + submit_manual_count + " times");
                                }
                            } catch (Exception e) {
                                isInserted = false;
                                manualSubmitStatus.setCaption("Problem occured during submission of Manual Annotation to the database");
                                System.out.println("Problem in inserting manual annotation into the database");
                                //  e.printStackTrace();
                            }
                        }
                    }
                });

                rightTopAnnotationForm.addComponent(diseaseLayout);
                rightTopAnnotationForm.addComponent(studyTypesPanel);
                rightTopAnnotationForm.addComponent(diseaseCategoryPanel);
                rightTopAnnotationForm.addComponent(platformLayout);
                rightTopAnnotationForm.addComponent(sampleTypesPanel);
                rightTopAnnotationForm.addComponent(replicatTypesLayout);
                rightTopAnnotationForm.addComponent(addCustomAnnoButton);
                rightTopAnnotationForm.addComponent(annotationStatusPanel);
                rightTopAnnotationForm.addComponent(submitButton);
                rightTopAnnotationForm.addComponent(manualSubmitStatus);
              //</editor-fold>
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="fill tree in right bottom">
                Iterator iterator = biosampleSet.iterator();

                // check values
                int treeBiosampleCount = 0;
                while (iterator.hasNext()) {
                    treeBiosampleCount = treeBiosampleCount + 1;
                    //  System.out.println("Value: "+ iterator.next() + " ");
                    //    if(treeBiosampleCount < 500){
                    String bsample = iterator.next().toString();
                    String[] biosample_parts = bsample.split("\\|");
                    String biosample_acc = biosample_parts[0];
                    tree.addItem(biosample_acc);
                    tree.setParent(biosample_acc, selectedStudy);
                    tree.setItemCaption(biosample_acc, "BioSample: " + biosample_acc);
                    //    }
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
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Events associated with the Tree (study/Biosample/Experiment/Runs">
        tree.setSelectable(true);
        tree.setImmediate(true);
        tree.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Object id = event.getProperty().getValue();
                if (id != null) {
                    String selectedTreeId = id.toString();
                    String selectedTreeItem = tree.getItemCaption(id).toString();
                    // System.out.println("Tree event is fired: " + selectedTreeItem);
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
                                    //  System.out.println("inside attributes ");

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
                     //   Label labelExperimentAcc = new Label("<b>Experiment Accession: </b>" + selectedTreeId, ContentMode.HTML);
                        // myform.addComponent(labelExperimentAcc);
                        String libraryDetails = "<b>Experiment Accession: </b>" + selectedTreeId + "<br><br>";
                        for (Map.Entry<String, String[]> entry : libraryDetailMap.entrySet()) {
                            libraryDetails = libraryDetails + "<b>Library Details: </b>" + entry.getKey() + "<br><br>";
                            String[] temp_library_details = entry.getValue();
                            for (String dt : temp_library_details) {
                                libraryDetails = libraryDetails + dt + "<br><br>";
                            }
                        }
                        Label labelLibraryDetails = new Label(libraryDetails, ContentMode.HTML);
                        myform.addComponent(labelLibraryDetails);
                        //   myform.setComponentAlignment(labelExperimentAcc, Alignment.TOP_LEFT);
                        myform.setComponentAlignment(labelLibraryDetails, Alignment.TOP_LEFT);

                    }
                    if (selectedTreeItem.startsWith("Run")) {
                        myform.removeAllComponents();
                        SQLContainer tempContainer = createMySQLContainer("sra_rnaseq_run", selectedTreeId);
                        if (tempContainer.size() > 0) {
                            Item tempItem = tempContainer.getItem(tempContainer.firstItemId());
                            String runs = tempItem.getItemProperty("Runs").getValue().toString();
                            String[] runs_parts = runs.split("\\|");
                            for (String run : runs_parts) {
                                if (run.contains(selectedTreeId)) {
                                    String[] run_details = run.split("\\,");
                                    String stringRun = "";
                                    if (run_details.length > 0) {
                                        stringRun = stringRun + "<b>Run Accession: </b>" + run_details[0] + "<br><br>";
                                    }
                                    if (run_details.length > 1) {
                                        stringRun = stringRun + "<b>Number of spots or reads: </b>" + run_details[1] + "<br><br>";
                                    }
                                    if (run_details.length > 1) {
                                        stringRun = stringRun + "<b>Number of bases: </b>" + run_details[2] + "<br><br>";
                                    }
                                     Label labelRunDetails = new Label(stringRun, ContentMode.HTML);
                                   myform.addComponent(labelRunDetails);
                                }

                            }
                        }
                    }
                } else {
                    System.out.println("id is null");
                }
            }

        });
        //</editor-fold>

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
        searchButton.setClickShortcut(KeyCode.ENTER);
        searchButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton() == searchButton) {
                    Component cc = findById(leftLayout, "GuidedSearch");
                    if (cc != null) {
                        leftLayout.removeComponent(cc);
                    }

                    String search_value = searchField.getValue();
                    rnaseqContainer = createMySQLContainer("study_filter", search_value);
                    rnaseqContainer.removeAllContainerFilters();

                    bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
                    //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
                    bioprojectSummaryTable.setSelectable(true);
                    bioprojectSummaryTable.setImmediate(true);
                    bioprojectSummaryTable.setColumnReorderingAllowed(true);
                    bioprojectSummaryTable.setSortEnabled(true);
                    bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases", "name"});
                }
            }
        });

        slowSearchButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton() == slowSearchButton) {
                    Component cc = findById(leftLayout, "GuidedSearch");
                    if (cc != null) {
                        leftLayout.removeComponent(cc);
                    }
                    String search_value = searchField.getValue();
                    rnaseqContainer = createMySQLContainer("study_filter_slow", search_value);
                    rnaseqContainer.removeAllContainerFilters();

                    bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
                    //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
                    bioprojectSummaryTable.setSelectable(true);
                    bioprojectSummaryTable.setImmediate(true);
                    bioprojectSummaryTable.setColumnReorderingAllowed(true);
                    bioprojectSummaryTable.setSortEnabled(true);
                    bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases", "name"});
                }
            }
        });

        guidedSearchButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton() == guidedSearchButton) {
                    VerticalLayout guidedSearchLayout = new VerticalLayout();
                    guidedSearchLayout.setId("GuidedSearch");
                    //<editor-fold defaultstate="collapsed" desc="Guided search layout">
                    HorizontalLayout diseaseLayout = new HorizontalLayout();
                    CheckBox checkboxDiseaseYes = new CheckBox("Yes");
                    CheckBox checkboxDiseaseNo = new CheckBox("No");

                    Label diseaseTitle = new Label("<b>Disease: </b>", ContentMode.HTML);
                    diseaseLayout.addComponent(diseaseTitle);
                    diseaseLayout.addComponent(checkboxDiseaseNo);
                    diseaseLayout.addComponent(checkboxDiseaseYes);

                    //<editor-fold defaultstate="collapsed" desc="Study types panel (guided search)">
                    Panel studyTypesPanel = new Panel("Sample Types");
                    HorizontalLayout studyTypesLayout = new HorizontalLayout();

                    HorizontalLayout caseControlLayout = new HorizontalLayout();
                    CheckBox checkboxCaseControlYes = new CheckBox("Yes");
                    CheckBox checkboxCaseControlNo = new CheckBox("No");

                    Label caseControlTitle = new Label("<b>Case-Control: </b>", ContentMode.HTML);
                    caseControlLayout.addComponent(caseControlTitle);
                    caseControlLayout.addComponent(checkboxCaseControlYes);
                    caseControlLayout.addComponent(checkboxCaseControlNo);
                    HorizontalLayout timeSeriesLayout = new HorizontalLayout();
                    CheckBox checkboxTimeSeriesYes = new CheckBox("Yes");
                    CheckBox checkboxTimeSeriesNo = new CheckBox("No");

                    Label timeSeriesTitle = new Label("<b>Time Series: </b>", ContentMode.HTML);
                    timeSeriesLayout.addComponent(timeSeriesTitle);
                    timeSeriesLayout.addComponent(checkboxTimeSeriesYes);
                    timeSeriesLayout.addComponent(checkboxTimeSeriesNo);
                    HorizontalLayout treatementLayout = new HorizontalLayout();
                    CheckBox checkboxTreatmentYes = new CheckBox("Yes");
                    CheckBox checkboxTreatmentNo = new CheckBox("No");
                    //  checkboxTreatmentYes.setValue(true);
                    Label treatmentTitle = new Label("<b>Treatment: </b>", ContentMode.HTML);
                    treatementLayout.addComponent(treatmentTitle);
                    treatementLayout.addComponent(checkboxTreatmentYes);
                    treatementLayout.addComponent(checkboxTreatmentNo);

                    studyTypesLayout.addComponent(caseControlLayout);
                    Label emptyLabel = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    studyTypesLayout.addComponent(emptyLabel);
                    studyTypesLayout.addComponent(timeSeriesLayout);
                    Label emptyLabel_2 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    studyTypesLayout.addComponent(emptyLabel_2);
                    studyTypesLayout.addComponent(treatementLayout);
                    studyTypesLayout.setSizeFull();
                    studyTypesPanel.setContent(studyTypesLayout);
                    studyTypesPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                    studyTypesPanel.addStyleName("panelborder");

                    //</editor-fold>`
                    //<editor-fold defaultstate="collapsed" desc="Disease Category Panel (guided search)">
                    Panel diseaseCategoryPanel = new Panel("Disease Category");
                    HorizontalLayout diseaseCategoriesLayout = new HorizontalLayout();
                    //Complex Disease
                    ListSelect complexDisease = new ListSelect("Complex Disease");
                    complexDisease.setMultiSelect(true);

                    for (String disease : complexDiseaseArray) {
                        complexDisease.addItem(disease);
                    }
                    diseaseCategoriesLayout.addComponent(complexDisease);
                    Label emptyLabel2 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    diseaseCategoriesLayout.addComponent(emptyLabel2);
                    // Rare disease
                    ListSelect rareDisease = new ListSelect("Rare Diseases");
                    rareDisease.setMultiSelect(true);

                    for (String disease : rareDiseaseArray) {
                        rareDisease.addItem(disease);
                    }
                    diseaseCategoriesLayout.addComponent(rareDisease);
                    Label emptyLabel3 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    diseaseCategoriesLayout.addComponent(emptyLabel3);
                    // Other diseases
                    ListSelect otherDisease = new ListSelect("Other Diseases");
                    otherDisease.setMultiSelect(true);
                    for (String disease : otherDiseaseArray) {
                        otherDisease.addItem(disease);
                    }
                    diseaseCategoriesLayout.addComponent(otherDisease);
                    //   Label emptyLabel4 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    //  diseaseCategoriesLayout.addComponent(emptyLabel4);

                    diseaseCategoryPanel.setContent(diseaseCategoriesLayout);
                    diseaseCategoryPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                    diseaseCategoryPanel.addStyleName("panelborder");

                    //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Platform (guided search)">
                    ListSelect platformsListSelect = new ListSelect("Sequencing Platform");
                    platformsListSelect.setMultiSelect(true);
                    for (String platform : platforms) {
                        platformsListSelect.addItem(platform);
                    }

                    HorizontalLayout platformLayout = new HorizontalLayout();
                    platformsListSelect.setHeight(platformsListSelect.size() + 2, Unit.EM);
                    platformLayout.addComponent(platformsListSelect);
                    //</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="Sample types panel (guided search)">
                    Panel sampleTypesPanel = new Panel("Sample Types");
                    HorizontalLayout sampleTypesLayout = new HorizontalLayout();
                    CheckBox checkboxSampleTypeCellLine = new CheckBox("Cell Line");
                    CheckBox checkboxSampleTypeTissue = new CheckBox("Tissue");
                    CheckBox checkboxSampleTypePrimaryCells = new CheckBox("Primary Cells");
                    CheckBox checkboxSampleTypeWholeBlood = new CheckBox("Whole Blood");
                    CheckBox checkboxSampleTypePlasma = new CheckBox("Plasma");

                    HorizontalLayout CellLineLayout = new HorizontalLayout();
                    CellLineLayout.addComponent(checkboxSampleTypeCellLine);

                    HorizontalLayout PrimaryCellsLayout = new HorizontalLayout();
                    PrimaryCellsLayout.addComponent(checkboxSampleTypePrimaryCells);

                    HorizontalLayout TissueLayout = new HorizontalLayout();
                    TissueLayout.addComponent(checkboxSampleTypeTissue);

                    HorizontalLayout WholeBloodLayout = new HorizontalLayout();
                    WholeBloodLayout.addComponent(checkboxSampleTypeWholeBlood);

                    HorizontalLayout PlasmaLayout = new HorizontalLayout();
                    PlasmaLayout.addComponent(checkboxSampleTypePlasma);

                    sampleTypesLayout.addComponent(CellLineLayout);
                    Label emptyLabel8 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    sampleTypesLayout.addComponent(emptyLabel8);
                    sampleTypesLayout.addComponent(PrimaryCellsLayout);
                    Label emptyLabel5 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    sampleTypesLayout.addComponent(emptyLabel5);
                    sampleTypesLayout.addComponent(TissueLayout);
                    Label emptyLabel6 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    sampleTypesLayout.addComponent(emptyLabel6);
                    sampleTypesLayout.addComponent(WholeBloodLayout);
                    Label emptyLabel7 = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML);
                    sampleTypesLayout.addComponent(emptyLabel7);
                    sampleTypesLayout.addComponent(PlasmaLayout);

                    sampleTypesPanel.setContent(sampleTypesLayout);
                    sampleTypesPanel.setWidth(Sizeable.SIZE_UNDEFINED, Unit.PERCENTAGE);
                    sampleTypesPanel.addStyleName("panelborder");

                    //</editor-fold>
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
                    checkboxCaseControlYes.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxCaseControlYes.getValue()) {
                                checkboxCaseControlNo.setValue(!checkboxCaseControlYes.getValue());
                            }

                        }
                    }
                    );
                    checkboxCaseControlNo.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxCaseControlNo.getValue()) {
                                checkboxCaseControlYes.setValue(!checkboxCaseControlNo.getValue());
                            }
                        }
                    }
                    );

                    checkboxTimeSeriesYes.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxTimeSeriesYes.getValue()) {
                                checkboxTimeSeriesNo.setValue(!checkboxTimeSeriesYes.getValue());
                            }

                        }
                    }
                    );
                    checkboxTimeSeriesNo.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxTimeSeriesNo.getValue()) {
                                checkboxTimeSeriesYes.setValue(!checkboxTimeSeriesNo.getValue());
                            }
                        }
                    }
                    );

                    checkboxTreatmentYes.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxTreatmentYes.getValue()) {
                                checkboxTreatmentNo.setValue(!checkboxTreatmentYes.getValue());
                            }

                        }
                    }
                    );
                    checkboxTreatmentNo.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxTreatmentNo.getValue()) {
                                checkboxTreatmentYes.setValue(!checkboxTreatmentNo.getValue());
                            }
                        }
                    }
                    );

                    checkboxDiseaseYes.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxDiseaseYes.getValue()) {
                                checkboxDiseaseNo.setValue(!checkboxDiseaseYes.getValue());
                                diseaseCategoryPanel.setVisible(true);
                            }

                        }
                    }
                    );
                    checkboxDiseaseNo.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxDiseaseNo.getValue()) {
                                checkboxDiseaseYes.setValue(!checkboxDiseaseNo.getValue());
                                diseaseCategoryPanel.setVisible(false);
                            }
                        }
                    }
                    );

                    //<editor-fold defaultstate="collapsed" desc="CheckBox Annotation Ongoing or Completed ">
                    CheckBox checkboxAnnotaionCompleted = new CheckBox("Annotaion Completed");
                    CheckBox checkboxAnnotaionOngoing = new CheckBox("Annotaion Ongoing");
                    // checkboxAnnotaionOngoing.setValue(true);
                    checkboxAnnotaionCompleted.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxAnnotaionCompleted.getValue()) {
                                checkboxAnnotaionOngoing.setValue(!checkboxAnnotaionCompleted.getValue());
                            }
                        }
                    }
                    );
                    checkboxAnnotaionOngoing.addValueChangeListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (checkboxAnnotaionOngoing.getValue()) {
                                checkboxAnnotaionCompleted.setValue(!checkboxAnnotaionOngoing.getValue());
                            }
                        }
                    }
                    );
                    //</editor-fold>

                    HorizontalLayout annotationStatusLayout = new HorizontalLayout();
                    annotationStatusLayout.addComponent(checkboxAnnotaionOngoing);
                    annotationStatusLayout.addComponent(checkboxAnnotaionCompleted);

                    Button guidedSearchSubmitButton = new Button("Search Meeting ALL Criteria");
                    guidedSearchSubmitButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {
                            if (event.getButton() == guidedSearchSubmitButton) {
                                String query_part3 = "";
                                if (checkboxDiseaseYes.getValue() && !checkboxDiseaseNo.getValue()) {
                                    query_part3 = query_part3 + " AND ( isDisease = 1) ";
                                } else if (!checkboxDiseaseYes.getValue() && checkboxDiseaseNo.getValue()) {
                                    query_part3 = query_part3 + " AND (isDisease =  null  or isDisease = 0 ) ";
                                }
                                String selected_disease_category = "";
                                String main_disease = "";
                                for (Iterator i = complexDisease.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();
                                    if (!temp.startsWith("--")) {
                                        main_disease = temp;
                                    }
                                    if (complexDisease.isSelected(iid)) {
                                        // System.out.println("Selected" + temp);

                                        if (iid.toString().startsWith("--")) { // sub disease is selected
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "complex_disease|" + main_disease + "|" + temp;
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "complex_disease|" + main_disease + "|" + temp;
                                            }

                                        } else { //main disease is selected
                                            //     selected_disease_main = main_disease;
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "complex_disease|" + main_disease + "|" + "Any";
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "complex_disease|" + main_disease + "|" + "Any";
                                            }

                                        }
                                    }
                                }
                                main_disease = "";
                                for (Iterator i = rareDisease.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();
                                    if (!temp.startsWith("--")) {
                                        main_disease = temp;
                                    }
                                    if (rareDisease.isSelected(iid)) {
                                        //  System.out.println("Selected" + temp);

                                        if (iid.toString().startsWith("--")) { // sub disease is selected
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "rare_disease|" + main_disease + "|" + temp;
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "rare_disease|" + main_disease + "|" + temp;
                                            }

                                        } else {
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "rare_disease|" + main_disease + "|" + "Any";
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "rare_disease|" + main_disease + "|" + "Any";
                                            }

                                        }
                                    }
                                }
                                main_disease = "";
                                for (Iterator i = otherDisease.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();
                                    if (!temp.startsWith("--")) {
                                        main_disease = temp;
                                    }
                                    if (otherDisease.isSelected(iid)) {
                                        //  System.out.println("Selected" + temp);

                                        if (iid.toString().startsWith("--")) { // sub disease is selected
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "other_disease|" + main_disease + "|" + temp;
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "other_disease|" + main_disease + "|" + temp;
                                            }

                                        } else {
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "other_disease|" + main_disease + "|" + "Any";
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "other_disease|" + main_disease + "|" + "Any";
                                            }

                                        }
                                    }
                                }

                                if (checkboxCaseControlYes.getValue() && !checkboxCaseControlNo.getValue()) {
                                    query_part3 = query_part3 + " AND ( isCaseControl = 1) ";
                                } else if (!checkboxCaseControlYes.getValue() && checkboxCaseControlNo.getValue()) {
                                    query_part3 = query_part3 + " AND (isCaseControl =  null  or isCaseControl = 0 ) ";
                                }

                                if (checkboxTimeSeriesYes.getValue() && !checkboxTimeSeriesNo.getValue()) {
                                    query_part3 = query_part3 + " AND ( isTimeSeries = 1) ";
                                } else if (!checkboxTimeSeriesYes.getValue() && checkboxTimeSeriesNo.getValue()) {
                                    query_part3 = query_part3 + " AND (isTimeSeries =  null  or isTimeSeries = 0 ) ";
                                }

                                if (checkboxTreatmentYes.getValue() && !checkboxTreatmentNo.getValue()) {
                                    query_part3 = query_part3 + " AND ( isTreatment = 1) ";
                                } else if (!checkboxTreatmentYes.getValue() && checkboxTreatmentNo.getValue()) {
                                    query_part3 = query_part3 + " AND (isTreatment =  null  or isTreatment = 0 ) ";
                                }

                                if (checkboxSampleTypeTissue.getValue()) {
                                    query_part3 = query_part3 + " AND ( isTissue = 1) ";
                                } else {
                                    //  query_part3 = query_part3  + " AND (isTissue =  null  or isTissue = 0 ) " ; 
                                }

                                if (checkboxSampleTypeCellLine.getValue()) {
                                    query_part3 = query_part3 + " AND ( isCellLine = 1) ";
                                } else {
                                    //  query_part3 = query_part3  + " AND (isCellLine =  null  or isCellLine = 0 ) " ; 
                                }

                                if (checkboxSampleTypePrimaryCells.getValue()) {
                                    query_part3 = query_part3 + " AND ( isPrimaryCells = 1) ";
                                } else {
                                    //   query_part3 = query_part3  + " AND (isPrimaryCells =  null  or isPrimaryCells = 0 ) " ; 
                                }

                                if (checkboxSampleTypeWholeBlood.getValue()) {
                                    query_part3 = query_part3 + " AND ( isWholeBlood = 1) ";
                                } else {
                                    //     query_part3 = query_part3  + " AND (isWholeBlood =  null  or isWholeBlood = 0 ) " ; 
                                }

                                if (checkboxSampleTypePlasma.getValue()) {
                                    query_part3 = query_part3 + " AND ( isPlasma = 1) ";
                                } else {
                                    //  query_part3 = query_part3  + " AND (isPlasma =  null  or isPlasma = 0 ) " ; 
                                }

                                String selected_sequencing_platforms = "";
                                for (Iterator i = platformsListSelect.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();

                                    if (platformsListSelect.isSelected(iid)) {
                                        if (selected_sequencing_platforms.isEmpty()) {
                                            selected_sequencing_platforms = temp;
                                        } else {
                                            selected_sequencing_platforms = selected_sequencing_platforms + ";" + temp;
                                        }

                                    }
                                }

                                String replicate_type = "";
                                if (!(replicatTypesListComboBox.getValue() == null)) {
                                    replicate_type = replicatTypesListComboBox.getValue().toString();
                                }
                                String annotation_status = "";
                                if (checkboxAnnotaionCompleted.getValue() && !checkboxAnnotaionOngoing.getValue()) {
                                    annotation_status = "completed";
                                } else if (!checkboxAnnotaionCompleted.getValue() && checkboxAnnotaionOngoing.getValue()) {
                                    annotation_status = "completed";
                                }
                                try {
                                    String query_part1 = "SELECT * FROM study_summary ";

                                    String query_part2 = " SELECT studyid FROM manual_annotation ";

                                    if (replicate_type.length() > 1) {
                                        query_part3 = query_part3 + " AND ( replicate_type = '" + replicate_type + "' ) ";
                                    }
                                    if (annotation_status.length() > 1) {
                                        query_part3 = query_part3 + " AND ( annotation_status = '" + annotation_status + "' ) ";
                                    }
                                    if (selected_disease_category.length() > 1) {
                                        query_part3 = query_part3 + " AND disease_category like  " + "'%" + selected_disease_category + "%'";
                                    }
                                    
                                     if (selected_sequencing_platforms.length() > 1) {
                                        query_part3 = query_part3 + " AND sequencing_platform like  " + "'%" + selected_sequencing_platforms + "%'";
                                    }

                                    query_part3 = query_part3.trim();
                                    if (query_part3.startsWith("AND")) {
                                        query_part3 = query_part3.substring(3);
                                    }

                                    String search_query = query_part1 + " where Study in (" + query_part2 + " WHERE " + query_part3 + ")";

                                    rnaseqContainer = createMySQLContainer("search_manual_annotation", search_query);
                                    rnaseqContainer.removeAllContainerFilters();

                                    bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
                                    //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
                                    bioprojectSummaryTable.setSelectable(true);
                                    bioprojectSummaryTable.setImmediate(true);
                                    bioprojectSummaryTable.setColumnReorderingAllowed(true);
                                    bioprojectSummaryTable.setSortEnabled(true);
                                    bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases", "name"});

                                } catch (Exception e) {
                                }

                            }
                        }
                    });

                    Button guidedSearchANYSubmitButton = new Button("Search Meeting ANY Criteria");
                    guidedSearchANYSubmitButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {
                            if (event.getButton() == guidedSearchANYSubmitButton) {
                                String query_part3 = "";
                                if (checkboxDiseaseYes.getValue() && !checkboxDiseaseNo.getValue()) {
                                    query_part3 = query_part3 + " OR ( isDisease = 1) ";
                                } else if (!checkboxDiseaseYes.getValue() && checkboxDiseaseNo.getValue()) {
                                    query_part3 = query_part3 + " OR (isDisease =  null  or isDisease = 0 ) ";
                                }
                                String selected_disease_category = "";
                                String main_disease = "";
                                for (Iterator i = complexDisease.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();
                                    if (!temp.startsWith("--")) {
                                        main_disease = temp;
                                    }
                                    if (complexDisease.isSelected(iid)) {
                                        //System.out.println("Selected" + temp);

                                        if (iid.toString().startsWith("--")) { // sub disease is selected
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "complex_disease|" + main_disease + "|" + temp;
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "complex_disease|" + main_disease + "|" + temp;
                                            }

                                        } else { //main disease is selected
                                            //     selected_disease_main = main_disease;
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "complex_disease|" + main_disease + "|" + "Any";
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "complex_disease|" + main_disease + "|" + "Any";
                                            }

                                        }
                                    }
                                }
                                main_disease = "";
                                for (Iterator i = rareDisease.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();
                                    if (!temp.startsWith("--")) {
                                        main_disease = temp;
                                    }
                                    if (rareDisease.isSelected(iid)) {
                                        //  System.out.println("Selected" + temp);

                                        if (iid.toString().startsWith("--")) { // sub disease is selected
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "rare_disease|" + main_disease + "|" + temp;
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "rare_disease|" + main_disease + "|" + temp;
                                            }

                                        } else {
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "rare_disease|" + main_disease + "|" + "Any";
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "rare_disease|" + main_disease + "|" + "Any";
                                            }

                                        }
                                    }
                                }
                                main_disease = "";
                                for (Iterator i = otherDisease.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();
                                    if (!temp.startsWith("--")) {
                                        main_disease = temp;
                                    }
                                    if (otherDisease.isSelected(iid)) {
                                        // System.out.println("Selected" + temp);

                                        if (iid.toString().startsWith("--")) { // sub disease is selected
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "other_disease|" + main_disease + "|" + temp;
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "other_disease|" + main_disease + "|" + temp;
                                            }

                                        } else {
                                            if (selected_disease_category.isEmpty()) {
                                                selected_disease_category = "other_disease|" + main_disease + "|" + "Any";
                                            } else {
                                                selected_disease_category = selected_disease_category + ";"
                                                        + "other_disease|" + main_disease + "|" + "Any";
                                            }

                                        }
                                    }
                                }

                                if (checkboxCaseControlYes.getValue() && !checkboxCaseControlNo.getValue()) {
                                    query_part3 = query_part3 + " OR ( isCaseControl = 1) ";
                                } else if (!checkboxCaseControlYes.getValue() && checkboxCaseControlNo.getValue()) {
                                    query_part3 = query_part3 + " OR (isCaseControl =  null  or isCaseControl = 0 ) ";
                                }

                                if (checkboxTimeSeriesYes.getValue() && !checkboxTimeSeriesNo.getValue()) {
                                    query_part3 = query_part3 + " OR ( isTimeSeries = 1) ";
                                } else if (!checkboxTimeSeriesYes.getValue() && checkboxTimeSeriesNo.getValue()) {
                                    query_part3 = query_part3 + " OR (isTimeSeries =  null  or isTimeSeries = 0 ) ";
                                }

                                if (checkboxTreatmentYes.getValue() && !checkboxTreatmentNo.getValue()) {
                                    query_part3 = query_part3 + " OR ( isTreatment = 1) ";
                                } else if (!checkboxTreatmentYes.getValue() && checkboxTreatmentNo.getValue()) {
                                    query_part3 = query_part3 + " OR (isTreatment =  null  or isTreatment = 0 ) ";
                                }

                                if (checkboxSampleTypeTissue.getValue()) {
                                    query_part3 = query_part3 + " OR ( isTissue = 1) ";
                                } else {
                                    //  query_part3 = query_part3  + " AND (isTissue =  null  or isTissue = 0 ) " ; 
                                }

                                if (checkboxSampleTypeCellLine.getValue()) {
                                    query_part3 = query_part3 + " OR ( isCellLine = 1) ";
                                } else {
                                    //  query_part3 = query_part3  + " AND (isCellLine =  null  or isCellLine = 0 ) " ; 
                                }

                                if (checkboxSampleTypePrimaryCells.getValue()) {
                                    query_part3 = query_part3 + " OR ( isPrimaryCells = 1) ";
                                } else {
                                    //   query_part3 = query_part3  + " AND (isPrimaryCells =  null  or isPrimaryCells = 0 ) " ; 
                                }

                                if (checkboxSampleTypeWholeBlood.getValue()) {
                                    query_part3 = query_part3 + " OR ( isWholeBlood = 1) ";
                                } else {
                                    //     query_part3 = query_part3  + " AND (isWholeBlood =  null  or isWholeBlood = 0 ) " ; 
                                }

                                if (checkboxSampleTypePlasma.getValue()) {
                                    query_part3 = query_part3 + " OR ( isPlasma = 1) ";
                                } else {
                                    //  query_part3 = query_part3  + " AND (isPlasma =  null  or isPlasma = 0 ) " ; 
                                }

                                String selected_sequencing_platforms = "";
                                for (Iterator i = platformsListSelect.getItemIds().iterator(); i.hasNext();) {
                                    Object iid = (Object) i.next();
                                    String temp = iid.toString();

                                    if (platformsListSelect.isSelected(iid)) {
                                        if (selected_sequencing_platforms.isEmpty()) {
                                            selected_sequencing_platforms = temp;
                                        } else {
                                            selected_sequencing_platforms = selected_sequencing_platforms + ";" + temp;
                                        }

                                    }
                                }

                                String replicate_type = "";
                                if (!(replicatTypesListComboBox.getValue() == null)) {
                                    replicate_type = replicatTypesListComboBox.getValue().toString();
                                }
                                String annotation_status = "";
                                if (checkboxAnnotaionCompleted.getValue() && !checkboxAnnotaionOngoing.getValue()) {
                                    annotation_status = "completed";
                                } else if (!checkboxAnnotaionCompleted.getValue() && checkboxAnnotaionOngoing.getValue()) {
                                    annotation_status = "completed";
                                }
                                try {
                                    String query_part1 = "SELECT * FROM study_summary ";

                                    String query_part2 = " SELECT studyid FROM manual_annotation ";

                                    if (replicate_type.length() > 1) {
                                        query_part3 = query_part3 + " OR ( replicate_type = '" + replicate_type + "' ) ";
                                    }
                                    if (annotation_status.length() > 1) {
                                        query_part3 = query_part3 + " OR ( annotation_status = '" + annotation_status + "' ) ";
                                    }
                                    if (selected_disease_category.length() > 1) {
                                        query_part3 = query_part3 + " OR disease_category like  " + "'%" + selected_disease_category + "%'";
                                    }
                                      
                                     if (selected_sequencing_platforms.length() > 1) {
                                        query_part3 = query_part3 + " AND sequencing_platform like  " + "'%" + selected_sequencing_platforms + "%'";
                                    }


                                    query_part3 = query_part3.trim();
                                    if (query_part3.startsWith("OR")) {
                                        query_part3 = query_part3.substring(2);
                                    }

                                    String search_query = query_part1 + " where Study in (" + query_part2 + " WHERE " + query_part3 + ")";

                                    rnaseqContainer = createMySQLContainer("search_manual_annotation", search_query);
                                    rnaseqContainer.removeAllContainerFilters();

                                    bioprojectSummaryTable.setContainerDataSource(rnaseqContainer);
                                    //   bioprojectSummaryTable.setVisibleColumns(new String[] { studyName });
                                    bioprojectSummaryTable.setSelectable(true);
                                    bioprojectSummaryTable.setImmediate(true);
                                    bioprojectSummaryTable.setColumnReorderingAllowed(true);
                                    bioprojectSummaryTable.setSortEnabled(true);
                                    bioprojectSummaryTable.setVisibleColumns(new Object[]{"Study", "title", "Numsample", "Numexp", "Numrun", "Avgspots", "avgbases", "name"});

                                } catch (Exception e) {
                                }

                            }
                        }
                    });

                    HorizontalLayout guidedSearchButtonLayout = new HorizontalLayout();
                    guidedSearchButtonLayout.addComponent(guidedSearchSubmitButton);
                    guidedSearchButtonLayout.addComponent(guidedSearchANYSubmitButton);

                    guidedSearchLayout.addComponent(diseaseLayout);
                    //  guidedSearchLayout.addComponent(caseControlLayout);
                    //  guidedSearchLayout.addComponent(timeSeriesLayout);
                    guidedSearchLayout.addComponent(studyTypesPanel);
                    guidedSearchLayout.addComponent(diseaseCategoryPanel);
                    guidedSearchLayout.addComponent(platformLayout);
                    guidedSearchLayout.addComponent(sampleTypesPanel);
                    guidedSearchLayout.addComponent(replicatTypesLayout);
                    guidedSearchLayout.addComponent(annotationStatusLayout);
                    guidedSearchLayout.addComponent(guidedSearchButtonLayout);
                    guidedSearchLayout.setComponentAlignment(guidedSearchButtonLayout, Alignment.MIDDLE_CENTER);
                    int leftTopLayoutIndex = leftLayout.getComponentIndex(leftTopLayout);
                    leftLayout.addComponent(guidedSearchLayout, leftTopLayoutIndex + 1);

                    //</editor-fold>
                }
            }
        });

    }

    // Validator for validating the passwords
    private static final class PasswordValidator extends
            AbstractValidator<String> {

        public PasswordValidator() {
            super("The password provided is not valid");
        }

        @Override
        protected boolean isValidValue(String value) {
            //
            // Password must be at least 3 characters long and contain at least
            // one number
            //
            //     if (value != null && (value.length() < 3 || !value.matches(".*\\d.*"))) {
            //         return false;
            //     }
            if (value != null && (value.length() < 3)) {
                return false;
            }
            return true;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    private String createHTMLspaces(int numSpaces) {
        String spaces = "";
        for (int i = 0; i < numSpaces; i++) {
            spaces = spaces + "&nbsp;";
        }
        return spaces;
    }

    public Component findById(HasComponents root, String id) {
        // System.out.println("findById called on " + root);

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

            String bioproject_detail_query = "SELECT * FROM sra_rnaseq where BioprojectId  = " + "'" + bioargument + "'";
            String biosample_detail_query = "SELECT * FROM biosample where Accession  = " + "'" + bioargument + "'";
            //      System.out.println("SQL query is: " + biosample_detail_query);
            String query_part1 = "";
            String query_part2 = "";
            String query_part3 = "";
            String query_part4 = "";
            String query_part5 = "";
            String study_filter_query = "";
            Connection conn = null;
            Statement statement;
            String updateQuery = "";

            switch (dataTable) {
                case "study_summary":
                    FreeformQuery query = new FreeformQuery(
                            "SELECT * FROM study_summary",
                            ConnectionPool.getConnectionPool(), "Study");
                    //   query.setDelegate(new StudyFreeFormQueryDelegateOld());
                    query.setDelegate(new StudyFreeFormQueryDelegate("SELECT * FROM study_summary"));
                    container = new SQLContainer(query);
                    //  System.out.println("SQL query is: SELECT * FROM study_summary ");
                    break;
                case "study_summary_ongoing":
                    // container = new SQLContainer(new TableQuery("study_summary", connectionPool));

                    conn = (Connection) ConnectionPool.getConnectionPool().reserveConnection();
                    statement = (Statement) conn.createStatement();
                    updateQuery = "update study_summary set annotation_status = 'ongoing' where Study = "
                            + "'" + bioargument + "'";
                    //System.out.println(updateQuery);
                    statement.executeUpdate(updateQuery);

                    statement.close();
                    conn.commit();
                    ConnectionPool.getConnectionPool().releaseConnection(conn);
                    break;
                case "study_summary_completed":
                    // container = new SQLContainer(new TableQuery("study_summary", ConnectionPool.getConnectionPool()));

                    conn = (Connection) ConnectionPool.getConnectionPool().reserveConnection();
                    statement = (Statement) conn.createStatement();
                    updateQuery = "update study_summary set annotation_status = 'completed' where Study = "
                            + "'" + bioargument + "'";
                    // System.out.println(updateQuery);
                    statement.executeUpdate(updateQuery);

                    statement.close();
                    conn.commit();
                    ConnectionPool.getConnectionPool().releaseConnection(conn);
                    break;
                case "study_filter_slow":
                    query_part1 = "SELECT * FROM study_summary "
                            + "where Title like  " + "'%" + bioargument + "%' or "
                            + "Name like " + "'%" + bioargument + "%' or "
                            + " Study like " + "'%" + bioargument + "%'";
                    query_part2 = " SELECT studyacc FROM biosample_with_studyacc "
                            + "where Attributes like  " + "'%" + bioargument + "%'";
                    query_part3 = " SELECT studyacc FROM sra_rnaseq "
                            + " where Platform_InstrumentModel like  " + "'%" + bioargument + "%' or "
                            + " Library_Name_Strategy_Source_Selection_Layout like " + "'%" + bioargument + "%'  ";
                    query_part4 = " SELECT studyacc FROM gse_details "
                            + " where summary like  " + "'%" + bioargument + "%' or "
                            + " design like " + "'%" + bioargument + "%'  ";
                    query_part5 = " SELECT studyacc FROM bioproject_details "
                            + " where Title like  " + "'%" + bioargument + "%' or "
                            + " Capture like " + "'%" + bioargument + "%' or "
                            + " Material like " + "'%" + bioargument + "%' or "
                            + " SampleScope like " + "'%" + bioargument + "%' or "
                            + " MethodType like " + "'%" + bioargument + "%' or "
                            + " description like " + "'%" + bioargument + "%' or "
                            + " DataType like " + "'%" + bioargument + "%'  ";
                    study_filter_query = query_part1 + " or Study in (" + query_part2 + ")"
                            + " or Study in (" + query_part3 + ")"
                            + " or Study in (" + query_part4 + ")" // This is slow
                            + " or Study in (" + query_part5 + ")"; // This also makes the search process slow
                    // System.out.println("SQL query is: " + study_filter_query);
                    FreeformQuery study_filter_query_slow_fq = new FreeformQuery(
                            study_filter_query,
                            ConnectionPool.getConnectionPool(), "Study");
                    study_filter_query_slow_fq.setDelegate(new StudySearchFreeFormQueryDelegate(study_filter_query));

                    container = new SQLContainer(study_filter_query_slow_fq);
                    break;
                case "study_filter":
                    query_part1 = "SELECT * FROM study_summary "
                            + "where Title like  " + "'%" + bioargument + "%' or "
                            + "Name like " + "'%" + bioargument + "%' or "
                            + " Study like " + "'%" + bioargument + "%'";
                    query_part2 = " SELECT studyacc FROM biosample_with_studyacc "
                            + "where Attributes like  " + "'%" + bioargument + "%'";
                    query_part3 = " SELECT studyacc FROM sra_rnaseq "
                            + " where Platform_InstrumentModel like  " + "'%" + bioargument + "%' or "
                            + " Library_Name_Strategy_Source_Selection_Layout like " + "'%" + bioargument + "%'  ";
                    query_part5 = " SELECT studyacc FROM bioproject_details "
                            + " where Title like  " + "'%" + bioargument + "%' or "
                            + " Capture like " + "'%" + bioargument + "%' or "
                            + " Material like " + "'%" + bioargument + "%' or "
                            + " SampleScope like " + "'%" + bioargument + "%' or "
                            + " MethodType like " + "'%" + bioargument + "%' or "
                            //  + " description like " + "'%" + bioargument + "%' or "
                            + " DataType like " + "'%" + bioargument + "%'  ";
                    study_filter_query = query_part1 + " or Study in (" + query_part2 + ")"
                            + " or Study in (" + query_part3 + ")"
                            //   + " or Study in (" + query_part4 + ")" // This is slow
                            + " or Study in (" + query_part5 + ")"; // This also makes the search process slow
                    // System.out.println("SQL query is: " + study_filter_query);
                    FreeformQuery study_filter_query_fq = new FreeformQuery(
                            study_filter_query,
                            ConnectionPool.getConnectionPool(), "Study");
                    study_filter_query_fq.setDelegate(new StudySearchFreeFormQueryDelegate(study_filter_query));
                    container = new SQLContainer(study_filter_query_fq);
                    break;
                case "search_manual_annotation":
                    // System.out.println("SQL query is: " + bioargument);
                    FreeformQuery study_filter_manual_annotation = new FreeformQuery(
                            bioargument,
                            ConnectionPool.getConnectionPool(), "Study");
                    study_filter_manual_annotation.setDelegate(new StudySearchFreeFormQueryDelegate(bioargument));
                    container = new SQLContainer(study_filter_manual_annotation);
                    break;
                case "suggestion_by_manual_annotation":
                    //System.out.println("SQL query is: " + bioargument);
                    container = new SQLContainer(new FreeformQuery(
                            bioargument,
                            ConnectionPool.getConnectionPool(), "studyid"));
                    break;
                case "sra_rnaseq":
                    container = new SQLContainer(new FreeformQuery(
                            bioproject_detail_query,
                            ConnectionPool.getConnectionPool(), "DocId"));
                    // System.out.println("SQL query is: " + bioproject_detail_query);
                    break;
                case "sra_rnaseq_exp":
                    String individual_exp_detail_query = "SELECT * FROM sra_rnaseq where Experiment_Acc_Ver_Status_Name  like " + "'" + bioargument + "%'";
                    container = new SQLContainer(new FreeformQuery(
                            individual_exp_detail_query,
                            ConnectionPool.getConnectionPool(), "DocId"));
                    // System.out.println("SQL query is: " + individual_exp_detail_query);
                    break;
                case "sra_rnaseq_run":
                    String individual_run_detail_query = "SELECT * FROM sra_rnaseq where Runs  like " + "'" + bioargument + "%'";
                    container = new SQLContainer(new FreeformQuery(
                            individual_run_detail_query,
                            ConnectionPool.getConnectionPool(), "DocId"));
                    // System.out.println("SQL query is: " + individual_run_detail_query);
                    break;
                case "biosample":
                    container = new SQLContainer(new FreeformQuery(
                            biosample_detail_query,
                            ConnectionPool.getConnectionPool(), "Accession"));
                    // System.out.println("SQL query is: " + biosample_detail_query);
                    break;
                case "biosample_with_studyacc":
                    String biosample_study_detail_query = "SELECT * FROM biosample_with_studyacc where studyacc  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            biosample_study_detail_query,
                            ConnectionPool.getConnectionPool(), "Accession"));
                    //System.out.println("SQL query is: " + biosample_study_detail_query);
                    break;
                case "study_extdb":
                    String study_extdb_query = "SELECT * FROM study_extdb where study_id  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            study_extdb_query,
                            ConnectionPool.getConnectionPool(), "study_id"));
                    //System.out.println("SQL query is: " + study_extdb_query);
                    break;
                case "study_abstracts":
                    String study_abstracts_query = "SELECT * FROM study_abstracts where studyacc  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            study_abstracts_query,
                            ConnectionPool.getConnectionPool(), "studyacc"));
                    //System.out.println("SQL query is: " + study_extdb_query);
                    break;
                case "bioproject_details":
                    String bioproject_details_query = "SELECT * FROM bioproject_details where BioprojectAccession  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            bioproject_details_query,
                            ConnectionPool.getConnectionPool(), "BioprojectAccession"));
                    //  System.out.println("SQL query is: " + bioproject_details_query);
                    break;
                case "study_gse_details":
                    String gse_details_query = "SELECT * FROM study_gse_details where gse  = " + "'" + bioargument + "'";
                    container = new SQLContainer(new FreeformQuery(
                            gse_details_query,
                            ConnectionPool.getConnectionPool(), "gse"));
                    // System.out.println("SQL query is: " + gse_details_query);
                    break;
                case "annotation_users":
                    // String insert_annotation_users_statement = "insert into annotation_users (user, password) values ( "  + bioargument + ")";
                    //  FreeformQuery insert_query = new FreeformQuery( insert_annotation_users_statement, ConnectionPool.getConnectionPool(), "annotation_users");
                    //    insert_query.setDelegate(new AnnotationUserFormQueryDelegate());
                    container = new SQLContainer(new TableQuery("annotation_users", ConnectionPool.getConnectionPool()));

                    //   System.out.println("SQL query is: " + insert_annotation_users_statement);
                    break;
                case "manual_annotation":
                    container = new SQLContainer(new TableQuery("manual_annotation", ConnectionPool.getConnectionPool()));
                    break;
                case "bioproject_summary":
                    //  System.out.println("SQL query is: accessing wrong table");
                    break;

            }

            //   System.out.println("my " + container.firstItemId());
            // System.out.println("2. Trying to connect to Mysql RNA database");
        } catch (SQLException e) {
            // System.out.println("some problem");
            e.printStackTrace();
        }
        return container;
    }
}
