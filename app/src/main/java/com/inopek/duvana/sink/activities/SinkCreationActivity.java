package com.inopek.duvana.sink.activities;

import com.inopek.duvana.sink.beans.SinkBean;

import java.util.Date;

public class SinkCreationActivity extends AbstractInputActivity {

    @Override
    protected void addSendListenerAction() {
        SinkBean sinkBean = new SinkBean();
        if (createSinkBean(sinkBean)) {
            runTask(sinkBean, true, false);
        }
    }

    @Override
    protected void populateFromExtras(String extra) {

    }

    @Override
    protected void updateCustomValues(SinkBean sinkBean) {
        sinkBean.setSinkCreationDate(new Date());
    }

}
