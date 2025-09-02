package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder;
import org.hippoecm.repository.util.DateTools;
import scot.gov.publishing.hippo.funnelback.component.Search;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;
import static org.onehippo.repository.util.JcrConstants.JCR_PRIMARY_TYPE;

public class ConstraintUtils {

    private static final String PUBLICATION_DATE = "publishing:publicationDate";

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    static {
        Collections.addAll(FIELD_NAMES, "publishing:title",
                "publishing:summary",
                "hippostd:tags",
                "publishing:contentBlocks/publishing:content/hippostd:content",
                "publishing:contentBlocks/publishing:document/hippo:filename",
                "publishing:contentBlocks/publishing:title");
    }

    private ConstraintUtils() {
        // hide public constructor
    }

    public static Constraint [] fieldConstraints(String term) {
        List<Constraint> constraints = FIELD_NAMES
                .stream()
                .map(field -> ConstraintBuilder.constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    public static Constraint topicsConstraint(Search search) {
        if (search.getTopics().isEmpty()) {
            return null;
        }

        Constraint [] constraintsArray = search.getTopics().keySet()
                .stream().map(ConstraintUtils::topicConstraint).map(ConstraintUtils::orConstraint)
                .toArray(Constraint[]::new);
        return ConstraintBuilder.or(constraintsArray);
    }

    public static Constraint publicationTypesConstraint(Search search) {
        if (search.getPublicationTypes().isEmpty()) {
            return null;
        }

        List<Constraint> constraints = new ArrayList<>();

        // convert the publication type 'news' to query for documents with the type 'govscot:News'
        if (search.getPublicationTypes().containsKey("news")) {
            constraints.add(isType("publishing:News"));
        }

        search.getPublicationTypes().keySet()
                .stream().map(ConstraintUtils::publicationTypeConstraint).map(ConstraintUtils::orConstraint)
                .forEach(constraints::add);
        return ConstraintBuilder.or(constraints.toArray(new Constraint[0]));
    }

    static Constraint isType(String type) {
        return constraint(JCR_PRIMARY_TYPE).equalTo(type);
    }

    static Constraint orConstraint(Constraint constraint) {
        return or(constraint);
    }

    static Constraint topicConstraint(String topic) {
        return constraint("publishing:topics").equalTo(topic);
    }

    static Constraint publicationTypeConstraint(String type) {
        return constraint("publishing:publicationType").equalTo(type);
    }

    public static List<Constraint> dateConstraint(Search search) {
        List<Constraint> constraints = new ArrayList<>();
        if (search.getToDate() != null && search.getFromDate() != null) {
            Calendar beginCal = getCalendar(search.getFromDate());
            Calendar endCal = getCalendar(search.getToDate());
            return singletonList(constraint(PUBLICATION_DATE).between(beginCal, endCal, DateTools.Resolution.DAY));
        } else {
            constraints.add(beginFilter(search));
            constraints.add(endFilter(search));
        }
        return constraints;
    }

    static Constraint beginFilter(Search search) {
        if (search.getFromDate() == null ) {
            return null;
        }

        Calendar calendar = getCalendar(search.getFromDate());
        return constraint(PUBLICATION_DATE).greaterOrEqualThan(calendar, DateTools.Resolution.DAY);
    }

    static Constraint endFilter(Search search) {
        if (search.getToDate() == null ) {
            return null;
        }

        Calendar calendar = getCalendar(search.getToDate());
        return constraint(PUBLICATION_DATE).lessOrEqualThan(calendar, DateTools.Resolution.DAY);
    }

    static  Calendar getCalendar(LocalDate localDate) {
        return GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
    }
}
