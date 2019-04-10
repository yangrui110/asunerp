
package com.hanlin.fadp.service;

import java.sql.Timestamp;

import com.hanlin.fadp.base.util.UtilDateTime;

public class RunningService {

    protected ModelService model;
    protected String name;
    protected int mode;

    protected Timestamp startStamp;
    protected Timestamp endStamp;

    private RunningService() {
        this.startStamp = UtilDateTime.nowTimestamp();
        this.endStamp = null;
    }

    public RunningService(String localName, ModelService model, int mode) {
        this();
        this.name = localName;
        this.model = model;
        this.mode = mode;
    }

    public ModelService getModelService() {
        return this.model;
    }

    public String getLocalName() {
        return this.name;
    }

    public int getMode() {
        return mode;
    }

    public Timestamp getStartStamp() {
        return this.startStamp;
    }

    public Timestamp getEndStamp() {
        return this.endStamp;
    }

    public void setEndStamp() {
        this.endStamp = UtilDateTime.nowTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof RunningService) {
            RunningService x = (RunningService) o;
            if (this.model.equals(x.getModelService()) && this.mode == x.getMode() && this.startStamp.equals(x.getStartStamp())) {
                return true;
            }
        }
        return false;
    }
}
