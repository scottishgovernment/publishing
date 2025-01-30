package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.ValueListProvider;

import java.util.List;
import java.util.Locale;

public class LatestCountValueListProvider implements ValueListProvider {
    @Override
    public List<String> getValues() {
        return List.of("2", "3");
    }

    @Override
    public String getDisplayValue(String s) {
        switch (s) {
            case "2": return "Two cards";
            case "3": return "Three cards";
            default: return "";
        }
    }

    @Override
    public String getDisplayValue(String s, Locale locale) {
        return getDisplayValue(s);
    }
}
