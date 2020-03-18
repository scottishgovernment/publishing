package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.repository.modules.DaemonModule;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Register any event listeners.
 */
public class ListenersModule implements DaemonModule {

    AddCategoryEventListener folderTypesEventListener;

    MirrorNameEventListener updateMirrorNameEventListener;

    @Override
    public void initialize(Session session) throws RepositoryException {
        folderTypesEventListener = new AddCategoryEventListener(session);
        updateMirrorNameEventListener = new MirrorNameEventListener(session);

        HippoEventListenerRegistry.get().register(folderTypesEventListener);
        HippoEventListenerRegistry.get().register(updateMirrorNameEventListener);
    }

    @Override
    public void shutdown() {
        HippoEventListenerRegistry.get().unregister(folderTypesEventListener);
        HippoEventListenerRegistry.get().unregister(updateMirrorNameEventListener);
    }
}
