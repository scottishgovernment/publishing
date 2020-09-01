import org.hippoecm.repository.util.JcrUtils
import org.onehippo.repository.update.BaseNodeUpdateVisitor

import javax.jcr.Node
import javax.jcr.Session

class PreviewLinksCleaner extends BaseNodeUpdateVisitor {

    String defaultMode = "all";
    int defaultTime = 30;

    String mode;
    int time;

    void initialize(Session session) {
        mode = parametersMap.get("mode", defaultMode)
        time = parametersMap.get("period", defaultTime)

        if (!mode.equals("all") && !mode.equals("invalid")) {
            mode = defaultMode
        }
        if (time < 0) {
            time = defaultTime
        }
        log.info "PreviewLinksCleaner initialized with parameters: { mode: ${mode}, time: ${time} }"

    }

    boolean skipCheckoutNodes() {
        return true;
    }

    boolean doUpdate(Node node) {

        boolean shouldRemove = false
        Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null)
        expirationCalendar == null || Calendar.getInstance().before(expirationCalendar)

        if("all".equals(mode) || ("invalid".equals(mode) && expirationCalendar.after(Calendar.getInstance()))){
            shouldRemove = true
        }

        if(shouldRemove) {
            node.remove()
            return true
        } else {
            return false
        }
    }

    boolean undoUpdate(Node node) {
        throw new UnsupportedOperationException('Updater does not implement undoUpdate method')
    }

}