package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.repository.modules.DaemonModule;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Register any event listeners.
 */
public class ListenersModule implements DaemonModule {

    SlugMaintenanceListener slugMaintainenceListener;

    AddEventListener folderTypesEventListener;

    MirrorEventListener updateMirrorNameEventListener;

    ThumbnailsEventListener thumbnailsEventListener;

    @Override
    public void initialize(Session session) throws RepositoryException {
        slugMaintainenceListener = new SlugMaintenanceListener(session);
        folderTypesEventListener = new AddEventListener(session);
        updateMirrorNameEventListener = new MirrorEventListener(session);
        thumbnailsEventListener = new ThumbnailsEventListener(session);

        HippoEventListenerRegistry.get().register(slugMaintainenceListener);
        HippoEventListenerRegistry.get().register(folderTypesEventListener);
        HippoEventListenerRegistry.get().register(updateMirrorNameEventListener);
        HippoEventListenerRegistry.get().register(thumbnailsEventListener);
    }

    @Override
    public void shutdown() {
        HippoEventListenerRegistry.get().unregister(slugMaintainenceListener);
        HippoEventListenerRegistry.get().unregister(folderTypesEventListener);
        HippoEventListenerRegistry.get().unregister(updateMirrorNameEventListener);
        HippoEventListenerRegistry.get().unregister(thumbnailsEventListener);
    }
}
