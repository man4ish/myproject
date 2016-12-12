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
@Table(name = "bioproject_summary")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BioprojectSummary.findAll", query = "SELECT b FROM BioprojectSummary b"),
    @NamedQuery(name = "BioprojectSummary.findByBioproject", query = "SELECT b FROM BioprojectSummary b WHERE b.bioproject = :bioproject"),
    @NamedQuery(name = "BioprojectSummary.findByNumsample", query = "SELECT b FROM BioprojectSummary b WHERE b.numsample = :numsample"),
    @NamedQuery(name = "BioprojectSummary.findByNumexp", query = "SELECT b FROM BioprojectSummary b WHERE b.numexp = :numexp"),
    @NamedQuery(name = "BioprojectSummary.findByNumrun", query = "SELECT b FROM BioprojectSummary b WHERE b.numrun = :numrun"),
    @NamedQuery(name = "BioprojectSummary.findByActualruns", query = "SELECT b FROM BioprojectSummary b WHERE b.actualruns = :actualruns"),
    @NamedQuery(name = "BioprojectSummary.findByEmptyruns", query = "SELECT b FROM BioprojectSummary b WHERE b.emptyruns = :emptyruns"),
    @NamedQuery(name = "BioprojectSummary.findByAvgspots", query = "SELECT b FROM BioprojectSummary b WHERE b.avgspots = :avgspots"),
    @NamedQuery(name = "BioprojectSummary.findByStdspots", query = "SELECT b FROM BioprojectSummary b WHERE b.stdspots = :stdspots"),
    @NamedQuery(name = "BioprojectSummary.findByAvgbases", query = "SELECT b FROM BioprojectSummary b WHERE b.avgbases = :avgbases"),
    @NamedQuery(name = "BioprojectSummary.findByStdbases", query = "SELECT b FROM BioprojectSummary b WHERE b.stdbases = :stdbases")})
public class BioprojectSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "Bioproject")
    private String bioproject;
    @Column(name = "Numsample")
    private Integer numsample;
    @Column(name = "Numexp")
    private Integer numexp;
    @Column(name = "Numrun")
    private Integer numrun;
    @Column(name = "Actualruns")
    private Integer actualruns;
    @Column(name = "Emptyruns")
    private Integer emptyruns;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Avgspots")
    private Double avgspots;
    @Column(name = "stdspots")
    private Double stdspots;
    @Column(name = "avgbases")
    private Double avgbases;
    @Column(name = "stdbases")
    private Double stdbases;

    public BioprojectSummary() {
    }

    public BioprojectSummary(String bioproject) {
        this.bioproject = bioproject;
    }

    public String getBioproject() {
        return bioproject;
    }

    public void setBioproject(String bioproject) {
        this.bioproject = bioproject;
    }

    public Integer getNumsample() {
        return numsample;
    }

    public void setNumsample(Integer numsample) {
        this.numsample = numsample;
    }

    public Integer getNumexp() {
        return numexp;
    }

    public void setNumexp(Integer numexp) {
        this.numexp = numexp;
    }

    public Integer getNumrun() {
        return numrun;
    }

    public void setNumrun(Integer numrun) {
        this.numrun = numrun;
    }

    public Integer getActualruns() {
        return actualruns;
    }

    public void setActualruns(Integer actualruns) {
        this.actualruns = actualruns;
    }

    public Integer getEmptyruns() {
        return emptyruns;
    }

    public void setEmptyruns(Integer emptyruns) {
        this.emptyruns = emptyruns;
    }

    public Double getAvgspots() {
        return avgspots;
    }

    public void setAvgspots(Double avgspots) {
        this.avgspots = avgspots;
    }

    public Double getStdspots() {
        return stdspots;
    }

    public void setStdspots(Double stdspots) {
        this.stdspots = stdspots;
    }

    public Double getAvgbases() {
        return avgbases;
    }

    public void setAvgbases(Double avgbases) {
        this.avgbases = avgbases;
    }

    public Double getStdbases() {
        return stdbases;
    }

    public void setStdbases(Double stdbases) {
        this.stdbases = stdbases;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bioproject != null ? bioproject.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BioprojectSummary)) {
            return false;
        }
        BioprojectSummary other = (BioprojectSummary) object;
        if ((this.bioproject == null && other.bioproject != null) || (this.bioproject != null && !this.bioproject.equals(other.bioproject))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.cornell.qatarmed.entities.BioprojectSummary[ bioproject=" + bioproject + " ]";
    }
    
}
