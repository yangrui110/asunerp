
package com.hanlin.fadp.base.conversion;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.lang.JSON;
import com.hanlin.fadp.base.util.UtilGenerics;

/** JSON Converter classes. */
public class JSONConverters implements ConverterLoader {

    public static class JSONToList extends AbstractConverter<JSON, List<Object>> {
        public JSONToList() {
            super(JSON.class, List.class);
        }

        public List<Object> convert(JSON obj) throws ConversionException {
            try {
                return UtilGenerics.<List<Object>>cast(obj.toObject(List.class));
            } catch (IOException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class JSONToMap extends AbstractConverter<JSON, Map<String, Object>> {
        public JSONToMap() {
            super(JSON.class, Map.class);
        }

        public Map<String, Object> convert(JSON obj) throws ConversionException {
            try {
                return UtilGenerics.<Map<String, Object>>cast(obj.toObject(Map.class));
            } catch (IOException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class ListToJSON extends AbstractConverter<List<Object>, JSON> {
        public ListToJSON() {
            super(List.class, JSON.class);
        }

        public JSON convert(List<Object> obj) throws ConversionException {
            try {
                return JSON.from(obj);
            } catch (IOException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class MapToJSON extends AbstractConverter<Map<String, Object>, JSON> {
        public MapToJSON() {
            super(Map.class, JSON.class);
        }

        public JSON convert(Map<String, Object> obj) throws ConversionException {
            try {
                return JSON.from(obj);
            } catch (IOException e) {
                throw new ConversionException(e);
            }
        }
    }

    public void loadConverters() {
        Converters.loadContainedConverters(JSONConverters.class);
    }
}
