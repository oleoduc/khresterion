package com.khresterion.due.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.khresterion.web.kbuilder.jsonmapping.RestModule;

public class JacksonObjectMapper extends ObjectMapper {

    /**
     * 
     */
    private static final long serialVersionUID = -6231090579143739642L;

    public JacksonObjectMapper() {

        super();
        configure(SerializationFeature.INDENT_OUTPUT, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        setSerializationInclusion(Include.ALWAYS);

        final DateFormat df = new SimpleDateFormat("mm-dd-yy'T'HH:mm:sss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        setDateFormat(df);
        registerModules(new RestModule());
    }

}
