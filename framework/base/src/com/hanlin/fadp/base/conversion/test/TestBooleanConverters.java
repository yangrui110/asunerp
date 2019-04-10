
package com.hanlin.fadp.base.conversion.test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.hanlin.fadp.base.conversion.BooleanConverters;
import com.hanlin.fadp.base.conversion.Converter;
import com.hanlin.fadp.base.conversion.ConverterLoader;
import com.hanlin.fadp.base.conversion.Converters;

public class TestBooleanConverters extends TestCase {

    public TestBooleanConverters(String name) {
        super(name);
    }

    public static <T> void assertFromBoolean(String label, Converter<Boolean, T> converter, T trueResult, T falseResult) throws Exception {
        assertTrue(label + " can convert", converter.canConvert(Boolean.class, trueResult.getClass()));
        assertEquals(label + " registered", converter.getClass(), Converters.getConverter(Boolean.class, trueResult.getClass()).getClass());
        assertEquals(label + " converted", trueResult, converter.convert(true));
        assertEquals(label + " converted", falseResult, converter.convert(false));
    }

    public static <S> void assertToBoolean(String label, Converter<S, Boolean> converter, S trueSource, S falseSource) throws Exception {
        assertTrue(label + " can convert", converter.canConvert(trueSource.getClass(), Boolean.class));
        assertEquals(label + " registered", converter.getClass(), Converters.getConverter(trueSource.getClass(), Boolean.class).getClass());
        assertEquals(label + " converted", Boolean.TRUE, converter.convert(trueSource));
        assertEquals(label + " converted", Boolean.FALSE, converter.convert(falseSource));
    }

    @SuppressWarnings("unchecked")
    public static <S> void assertToCollection(String label, S source) throws Exception {
        Converter<S, ? extends Collection> toList = (Converter<S, ? extends Collection>) Converters.getConverter(source.getClass(), List.class);
        Collection<S> listResult = toList.convert(source);
        assertEquals(label + " converted to List", source, listResult.toArray()[0]);
        Converter<S, ? extends Collection> toSet = (Converter<S, ? extends Collection>) Converters.getConverter(source.getClass(), Set.class);
        Collection<S> setResult = toSet.convert(source);
        assertEquals(label + " converted to Set", source, setResult.toArray()[0]);
    }

    public void testBooleanConverters() throws Exception {
        ConverterLoader loader = new BooleanConverters();
        loader.loadConverters();
        assertFromBoolean("BooleanToInteger", new BooleanConverters.BooleanToInteger(), 1, 0);
        assertFromBoolean("BooleanToString", new BooleanConverters.BooleanToString(), "true", "false");
        assertToBoolean("IntegerToBoolean", new BooleanConverters.IntegerToBoolean(), 1, 0);
        assertToBoolean("StringToBoolean", new BooleanConverters.StringToBoolean(), "true", "false");
        assertToCollection("BooleanToCollection", Boolean.TRUE);
    }
}
