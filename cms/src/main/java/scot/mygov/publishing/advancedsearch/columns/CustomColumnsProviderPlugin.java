package scot.mygov.publishing.advancedsearch.columns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.gallery.compare.CalendarComparator;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.hippoecm.frontend.plugins.standards.datetime.DateTimePrinter;
import org.hippoecm.frontend.plugins.standards.list.AbstractListColumnProviderPlugin;
import org.hippoecm.frontend.plugins.standards.list.ListColumn;
import org.hippoecm.frontend.skin.DocumentListColumn;

public class CustomColumnsProviderPlugin extends AbstractListColumnProviderPlugin {

    public CustomColumnsProviderPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    public List<ListColumn<Node>> getColumns() {
        return Collections.emptyList();
    }

    @Override
    public List<ListColumn<Node>> getExpandedColumns() {
        List<ListColumn<Node>> columns = new ArrayList<>();
        columns.add(createReviewDateColumn());
        columns.add(createLastOfficialUpdateDateColumn());
        return columns;
    }

    private ListColumn<Node> createReviewDateColumn() {
        final ClassResourceModel displayModel = new ClassResourceModel("doclisting-preview-date", getClass());
        final ListColumn<Node> column = new ListColumn<>(displayModel, "review-date");

        column.setCssClass(DocumentListColumn.DATE.getCssClass());
        column.setComparator(new CalendarComparator("publishing:reviewDate"));
        column.setRenderer(new CustomDocumentAttributeRenderer(){
            @Override
            protected String getObject(final CustomAttributes atts) {
                Calendar reviewDate = atts.getReviewDate();
                return DateTimePrinter.of(reviewDate).print();
            }
        });

        return column;
    }

    private ListColumn<Node> createLastOfficialUpdateDateColumn() {
        final ClassResourceModel displayModel = new ClassResourceModel("doclisting-last-official-update-date", getClass());
        final ListColumn<Node> column = new ListColumn<>(displayModel, "lastofficialupdate-date");

        column.setCssClass(DocumentListColumn.DATE.getCssClass());
        column.setComparator(new CalendarComparator("publishing:lastUpdatedDate"));
        column.setRenderer(new CustomDocumentAttributeRenderer(){
            @Override
            protected String getObject(final CustomAttributes atts) {
                Calendar lastOfficialUpdateDate = atts.getLastOfficialUpdateDate();
                return DateTimePrinter.of(lastOfficialUpdateDate).print();
            }
        });

        return column;
    }

}
