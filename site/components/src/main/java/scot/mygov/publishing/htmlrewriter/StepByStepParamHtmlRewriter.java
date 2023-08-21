package scot.mygov.publishing.htmlrewriter;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.rewriter.impl.SimpleContentRewriter;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;

public class StepByStepParamHtmlRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(StepByStepParamHtmlRewriter.class);

    Pattern pattern = Pattern.compile("<div\\s+class=\"ds_step-navigation\">.*?</div>", Pattern.DOTALL);

    @Override
    public String rewrite(String html, Node node, HstRequestContext requestContext, Mount targetMount) {

        HippoBean bean = requestContext.getContentBean();
        Matcher matcher = pattern.matcher(html);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String divBlock = matcher.group();
            String modifiedDivBlock = divBlock.replaceAll("<a\\s+href=\"(.*?)\"", "<a href=\"$1?slug=blah\"");
            matcher.appendReplacement(result, Matcher.quoteReplacement(modifiedDivBlock));
        }
        matcher.appendTail(result);


        return super.rewrite(result.toString(), node, requestContext, targetMount);
    }


}
