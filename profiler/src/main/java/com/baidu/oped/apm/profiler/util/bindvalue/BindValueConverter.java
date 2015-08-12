
package com.baidu.oped.apm.profiler.util.bindvalue;

import java.util.HashMap;
import java.util.Map;

import com.baidu.oped.apm.profiler.util.bindvalue.converter.*;

/**
 * class BindValueConverter 
 *
 * @author meidongxu@baidu.com
 */
public class BindValueConverter {
    private static final BindValueConverter converter;
    static {
        converter = new BindValueConverter();
        converter.register();
    }

    public final Map<String, Converter> convertermap = new HashMap<String, Converter>() ;

    private void register() {
        simpleType();
        classNameType();

        // There also is method with 3 parameters.
        convertermap.put("setNull", new NullTypeConverter());

        BytesConverter bytesConverter = new BytesConverter();
        convertermap.put("setBytes", bytesConverter);

        convertermap.put("setObject", new ObjectConverter());
    }

    private void classNameType() {
        // replace with class name if we don't want to (or can't) read the value
        ClassNameConverter classNameConverter = new ClassNameConverter();
        
     // There also is method with 3 parameters.
        convertermap.put("setAsciiStream", classNameConverter);
        convertermap.put("setUnicodeStream", classNameConverter);
        convertermap.put("setBinaryStream", classNameConverter);

     // There also is method with 3 parameters.
        convertermap.put("setBlob", classNameConverter);
     // There also is method with 3 parameters.
        convertermap.put("setClob", classNameConverter);
        convertermap.put("setArray", classNameConverter);
        convertermap.put("setNCharacterStream", classNameConverter);

     // There also is method with 3 parameters.
        convertermap.put("setNClob", classNameConverter);

        convertermap.put("setCharacterStream", classNameConverter);
        convertermap.put("setSQLXML", classNameConverter);
    }

    private void simpleType() {

        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
        convertermap.put("setByte", simpleTypeConverter);
        convertermap.put("setBoolean", simpleTypeConverter);
        convertermap.put("setShort", simpleTypeConverter);
        convertermap.put("setInt", simpleTypeConverter);
        convertermap.put("setLong", simpleTypeConverter);
        convertermap.put("setFloat", simpleTypeConverter);
        convertermap.put("setDouble", simpleTypeConverter);
        convertermap.put("setBigDecimal", simpleTypeConverter);
        convertermap.put("setString", simpleTypeConverter);
        convertermap.put("setDate", simpleTypeConverter);

     // There also is method with 3 parameters.
        convertermap.put("setTime", simpleTypeConverter);
        //convertermap.put("setTime", simpleTypeConverter);

     // There also is method with 3 parameters.
        convertermap.put("setTimestamp", simpleTypeConverter);
        //convertermap.put("setTimestamp", simpleTypeConverter);


        // could be replaced with string
        convertermap.put("setURL", simpleTypeConverter);
        // could be replaced with string
        convertermap.put("setRef", simpleTypeConverter);
        convertermap.put("setNString", simpleTypeConverter);
    }

    public String convert0(String methodName, Object[] args) {
        Converter converter = this.convertermap.get(methodName);
        if (converter == null) {
            return "";
        }
        return converter.convert(args);
    }


    public static String convert(String methodName, Object[] args) {
        return converter.convert0(methodName, args);
    }

}
