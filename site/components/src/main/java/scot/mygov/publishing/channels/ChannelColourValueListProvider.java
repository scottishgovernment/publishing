package scot.mygov.publishing.channels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hippoecm.hst.core.parameters.ValueListProvider;
import scot.mygov.publishing.channels.WebsiteInfo;

public class ChannelColourValueListProvider implements ValueListProvider {
    private static Map<String, String> colourValuesMap = new LinkedHashMap<>();
    private static List<String> colourValuesList;

    private List getValueListForLocale(Locale locale) {
        colourValuesMap.put("teal","Teal");
        colourValuesMap.put("darkteal","Dark teal");
        colourValuesMap.put("green","Green");
        colourValuesMap.put("darkgreen","Dark green");
        colourValuesMap.put("orange","Orange");
        colourValuesMap.put("red","Red");
        colourValuesMap.put("pink","Pink");
        colourValuesMap.put("purple","Purple");
        colourValuesMap.put("brown","Brown");
        colourValuesMap.put("black","Black");

        return colourValuesList = Collections.unmodifiableList(new LinkedList<>(colourValuesMap.keySet()));
    }

    @Override
    public List<String> getValues() {
        List list = getValueListForLocale(null);
        if (list != null) {
            return list;
        }

        // if no color list found, return empty list.
        return new ArrayList<String>();
    }

    @Override
    public String getDisplayValue(String value) {
        return getDisplayValue(value, null);
    }

    @Override
    public String getDisplayValue(String value, Locale locale) {
        String displayValue = colourValuesMap.get(value);
        return (displayValue != null) ? displayValue : value;
    }
}
