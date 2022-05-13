package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.RuntimeQueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.request.HstRequestContext;

/**
 * Created by z418868 on 18/05/2022.
 */
public class DavidsHstQueryBuilder extends HstQueryBuilder {

    private HstQueryBuilder builder;

    public static HstQueryBuilder create(HippoBean... scopeBeans) throws RuntimeQueryException {

        HstQueryBuilder builder = HstQueryBuilder.create(scopeBeans);
        return builder;

    }

    DavidsHstQueryBuilder(HstQueryBuilder builder) {
        this.builder = builder;
    }

    @Override
    public HstQuery build(HstQueryManager hstQueryManager) throws RuntimeQueryException {

        return builder.build(hstQueryManager);
    }


}
