package com.inopek.duvana.sink.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.inopek.duvana.sink.beans.SinkBean;
import com.inopek.duvana.sink.beans.UserBean;
import com.inopek.duvana.sink.constants.SinkConstants;
import com.inopek.duvana.sink.utils.ImageUtils;
import com.inopek.duvana.sink.utils.PropertiesUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHttpRequestTask<T> extends AsyncTask<Void, Void, T> {

    protected final Context context;
    private UserBean userBean;

    protected abstract T post(RestTemplate restTemplate, String url, Map<String, String> mapVariables);

    protected abstract String getNameRequest();

    public AbstractHttpRequestTask(Context context, UserBean userBean) {
        super();
        this.context = context;
        this.userBean = userBean;
    }

    @Override
    protected T doInBackground(Void... params) {
        try {
            final String url = SinkConstants.HTTP_PREFIX + PropertiesUtils.getProperty(SinkConstants.ADDRESS_HOST, context) + ":" + PropertiesUtils.getProperty(SinkConstants.PORT_HOST, context) + getNameRequest() + "{userImi}";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            Map<String, String> mapVariables = new HashMap<>();
            mapVariables.put("userImi", userBean.getImiNumber());
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(SinkConstants.CONNECT_TIMEOUT);
            return post(restTemplate, url, mapVariables);
        } catch (Exception e) {
            Log.e("HttpRequestTask ", e.getMessage(), e);
            return null;
        }
    }

    protected void encodePhotoFile(SinkBean sinkBean) {
        if(StringUtils.isNotEmpty(sinkBean.getImageBeforePath())) {
            sinkBean.setImageBefore(getEncodeImage(sinkBean.getImageBeforePath()));
        } else if(StringUtils.isNotEmpty(sinkBean.getImageAfterPath())) {
            sinkBean.setImageAfter(getEncodeImage(sinkBean.getImageAfterPath()));
        } else  {
            FirebaseCrash.log("Error before encoding image. Bean has not photo does not exist " + sinkBean.getReference());
        }
    }

    private String getEncodeImage(String path) {
        File file = new File(path);
        if (file.exists()) {
            return ImageUtils.convertBitmapToSmallerSizetoString(path);
        }
        FirebaseCrash.log("Error while encoding image. File does not exist " + path);
        return null;
    }

}
