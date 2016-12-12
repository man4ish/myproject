/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pak2013
 */
@Entity
@Table(name = "sra_rnaseq")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SraRnaseq.findAll", query = "SELECT s FROM SraRnaseq s"),
    @NamedQuery(name = "SraRnaseq.findByDocId", query = "SELECT s FROM SraRnaseq s WHERE s.docId = :docId"),
    @NamedQuery(name = "SraRnaseq.findByBioprojectId", query = "SELECT s FROM SraRnaseq s WHERE s.bioprojectId = :bioprojectId"),
    @NamedQuery(name = "SraRnaseq.findByTitle", query = "SELECT s FROM SraRnaseq s WHERE s.title = :title"),
    @NamedQuery(name = "SraRnaseq.findByCreateDate", query = "SELECT s FROM SraRnaseq s WHERE s.createDate = :createDate"),
    @NamedQuery(name = "SraRnaseq.findByUpdateDate", query = "SELECT s FROM SraRnaseq s WHERE s.updateDate = :updateDate"),
    @NamedQuery(name = "SraRnaseq.findByStudyAccName", query = "SELECT s FROM SraRnaseq s WHERE s.studyAccName = :studyAccName"),
    @NamedQuery(name = "SraRnaseq.findByBiosampleAccIdSampleId", query = "SELECT s FROM SraRnaseq s WHERE s.biosampleAccIdSampleId = :biosampleAccIdSampleId"),
    @NamedQuery(name = "SraRnaseq.findByExperimentAccVerStatusName", query = "SELECT s FROM SraRnaseq s WHERE s.experimentAccVerStatusName = :experimentAccVerStatusName"),
    @NamedQuery(name = "SraRnaseq.findBySubmitterAccContactCenterLab", query = "SELECT s FROM SraRnaseq s WHERE s.submitterAccContactCenterLab = :submitterAccContactCenterLab"),
    @NamedQuery(name = "SraRnaseq.findByStatisticTotalRunsTotalSpotsTotalBasesTotalSize", query = "SELECT s FROM SraRnaseq s WHERE s.statisticTotalRunsTotalSpotsTotalBasesTotalSize = :statisticTotalRunsTotalSpotsTotalBasesTotalSize"),
    @NamedQuery(name = "SraRnaseq.findByPlatformInstrumentModel", query = "SELECT s FROM SraRnaseq s WHERE s.platformInstrumentModel = :platformInstrumentModel"),
    @NamedQuery(name = "SraRnaseq.findByLibraryNameStrategySourceSelectionLayout", query = "SELECT s FROM SraRnaseq s WHERE s.libraryNameStrategySourceSelectionLayout = :libraryNameStrategySourceSelectionLayout"),
    @NamedQuery(name = "SraRnaseq.findByRuns", query = "SELECT s FROM SraRnaseq s WHERE s.runs = :runs")})
public class SraRnaseq implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "DocId")
    private String docId;
    @Size(max = 15)
    @Column(name = "BioprojectId")
    private String bioprojectId;
    @Size(max = 200)
    @Column(name = "Title")
    private String title;
    @Size(max = 10)
    @Column(name = "CreateDate")
    private String createDate;
    @Size(max = 10)
    @Column(name = "UpdateDate")
    private String updateDate;
    @Size(max = 1000)
    @Column(name = "Study_Acc_Name")
    private String studyAccName;
    @Size(max = 100)
    @Column(name = "Biosample_Acc_Id_SampleId")
    private String biosampleAccIdSampleId;
    @Size(max = 200)
    @Column(name = "Experiment_Acc_Ver_Status_Name")
    private String experimentAccVerStatusName;
    @Size(max = 200)
    @Column(name = "Submitter_Acc_Contact_Center_Lab")
    private String submitterAccContactCenterLab;
    @Size(max = 200)
    @Column(name = "Statistic_TotalRuns_TotalSpots_TotalBases_TotalSize")
    private String statisticTotalRunsTotalSpotsTotalBasesTotalSize;
    @Size(max = 200)
    @Column(name = "Platform_InstrumentModel")
    private String platformInstrumentModel;
    @Size(max = 200)
    @Column(name = "Library_Name_Strategy_Source_Selection_Layout")
    private String libraryNameStrategySourceSelectionLayout;
    @Size(max = 4000)
    @Column(name = "Runs")
    private String runs;

    public SraRnaseq() {
    }

    public SraRnaseq(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getBioprojectId() {
        return bioprojectId;
    }

    public void setBioprojectId(String bioprojectId) {
        this.bioprojectId = bioprojectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getStudyAccName() {
        return studyAccName;
    }

    public void setStudyAccName(String studyAccName) {
        this.studyAccName = studyAccName;
    }

    public String getBiosampleAccIdSampleId() {
        return biosampleAccIdSampleId;
    }

    public void setBiosampleAccIdSampleId(String biosampleAccIdSampleId) {
        this.biosampleAccIdSampleId = biosampleAccIdSampleId;
    }

    public String getExperimentAccVerStatusName() {
        return experimentAccVerStatusName;
    }

    public void setExperimentAccVerStatusName(String experimentAccVerStatusName) {
        this.experimentAccVerStatusName = experimentAccVerStatusName;
    }

    public String getSubmitterAccContactCenterLab() {
        return submitterAccContactCenterLab;
    }

    public void setSubmitterAccContactCenterLab(String submitterAccContactCenterLab) {
        this.submitterAccContactCenterLab = submitterAccContactCenterLab;
    }

    public String getStatisticTotalRunsTotalSpotsTotalBasesTotalSize() {
        return statisticTotalRunsTotalSpotsTotalBasesTotalSize;
    }

    public void setStatisticTotalRunsTotalSpotsTotalBasesTotalSize(String statisticTotalRunsTotalSpotsTotalBasesTotalSize) {
        this.statisticTotalRunsTotalSpotsTotalBasesTotalSize = statisticTotalRunsTotalSpotsTotalBasesTotalSize;
    }

    public String getPlatformInstrumentModel() {
        return platformInstrumentModel;
    }

    public void setPlatformInstrumentModel(String platformInstrumentModel) {
        this.platformInstrumentModel = platformInstrumentModel;
    }

    public String getLibraryNameStrategySourceSelectionLayout() {
        return libraryNameStrategySourceSelectionLayout;
    }

    public void setLibraryNameStrategySourceSelectionLayout(String libraryNameStrategySourceSelectionLayout) {
        this.libraryNameStrategySourceSelectionLayout = libraryNameStrategySourceSelectionLayout;
    }

    public String getRuns() {
        return runs;
    }

    public void setRuns(String runs) {
        this.runs = runs;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (docId != null ? docId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SraRnaseq)) {
            return false;
        }
        SraRnaseq other = (SraRnaseq) object;
        if ((this.docId == null && other.docId != null) || (this.docId != null && !this.docId.equals(other.docId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.cornell.qatarmed.entities.SraRnaseq[ docId=" + docId + " ]";
    }
    
}
