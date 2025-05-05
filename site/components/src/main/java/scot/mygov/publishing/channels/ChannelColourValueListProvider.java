package scot.mygov.publishing.channels;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hippoecm.hst.core.parameters.ValueListProvider;

public class ChannelColourValueListProvider implements ValueListProvider {

    public static final Map<String, String> COLOURS = new LinkedHashMap<>();

    static {
        COLOURS.put("sg-brand","SG brand");
        COLOURS.put("dark-blue","Dark blue");
        COLOURS.put("green","Green");
        COLOURS.put("purple","Purple");
        COLOURS.put("teal","Teal");
        COLOURS.put("neutral","Neutral");
        COLOURS.put("designsystem","Design System");
    }

    private List getValueListForLocale(Locale locale) {
        return Collections.unmodifiableList(new LinkedList<>(COLOURS.keySet()));
    }

    @Override
    public List<String> getValues() {
        return getValueListForLocale(null);
    }

    @Override
    public String getDisplayValue(String value) {
        return getDisplayValue(value, null);
    }

    @Override
    public String getDisplayValue(String value, Locale locale) {
        String displayValue = COLOURS.get(value);
        return (displayValue != null) ? displayValue : value;
    }
}
