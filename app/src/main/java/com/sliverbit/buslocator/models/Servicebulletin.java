package com.sliverbit.buslocator.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root
public class Servicebulletin {

    @Element
    private String nm0020;

    @Element
    private String sbj;

    @Element
    private String dtl;

    @Element
    private String brf;

    @Element
    private String prty;

    @Element
    private List<Affectedservice> srvc;

    public String getNm_0020() {
        return nm0020;
    }

    public void setNm_0020(String value) {
        this.nm0020 = value;
    }

    public String getSbj() {
        return sbj;
    }

    public void setSbj(String value) {
        this.sbj = value;
    }

    public String getDtl() {
        return dtl;
    }

    public void setDtl(String value) {
        this.dtl = value;
    }

    public String getBrf() {
        return brf;
    }

    public void setBrf(String value) {
        this.brf = value;
    }

    public String getPrty() {
        return prty;
    }

    public void setPrty(String value) {
        this.prty = value;
    }

    public List<Affectedservice> getSrvc() {
        if (srvc == null) {
            srvc = new ArrayList<Affectedservice>();
        }
        return this.srvc;
    }

}
