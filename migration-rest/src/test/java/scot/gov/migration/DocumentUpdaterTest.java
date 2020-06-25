package scot.gov.migration;

import org.junit.Test;
import org.mockito.Mockito;
import org.onehippo.forge.content.exim.core.DocumentManager;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by z418868 on 25/06/2020.
 */
public class DocumentUpdaterTest {

    @Test
    public void publishesDocumentIfPublishTrue() {
        DocumentManager manager = Mockito.mock(DocumentManager.class);
        DocumentUpdater.publish(manager, true, "location");
        verify(manager).publishDocument("location");
    }

    @Test
    public void doesNotPublishDocumentIfPublishFalse() {
        DocumentManager manager = Mockito.mock(DocumentManager.class);
        DocumentUpdater.publish(manager, false, "location");
        verify(manager, never()).publishDocument("location");
    }

}
