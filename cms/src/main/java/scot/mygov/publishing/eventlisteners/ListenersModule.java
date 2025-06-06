package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.services.eventbus.HippoEventListenerRegistry;
import org.onehippo.repository.modules.DaemonModule;
import scot.mygov.publishing.HippoUtils;

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

    GuidePageReviewDateEventsListener guidePageLifeEventsListener;

    ContentBlocksOldVersionListener contentBlocksOldVersionListener;

    PublicationTypeEventListener publicationTypeEventListener;

    ServiceFinderOrderListener serviceFinderOrderListener;

    @Override
    public void initialize(Session session) throws RepositoryException {
        slugMaintainenceListener = new SlugMaintenanceListener(session);
        folderTypesEventListener = new AddEventListener(session, new HippoUtils());
        updateMirrorNameEventListener = new MirrorEventListener(session);
        thumbnailsEventListener = new ThumbnailsEventListener(session);
        guidePageLifeEventsListener = new GuidePageReviewDateEventsListener(session);
        contentBlocksOldVersionListener = new ContentBlocksOldVersionListener(session);
        publicationTypeEventListener = new PublicationTypeEventListener(session);
        serviceFinderOrderListener = new ServiceFinderOrderListener(session);

        HippoEventListenerRegistry.get().register(slugMaintainenceListener);
        HippoEventListenerRegistry.get().register(folderTypesEventListener);
        HippoEventListenerRegistry.get().register(updateMirrorNameEventListener);
        HippoEventListenerRegistry.get().register(thumbnailsEventListener);
        HippoEventListenerRegistry.get().register(guidePageLifeEventsListener);
        HippoEventListenerRegistry.get().register(contentBlocksOldVersionListener);
        HippoEventListenerRegistry.get().register(publicationTypeEventListener);
        HippoEventListenerRegistry.get().register(serviceFinderOrderListener);
    }

    @Override
    public void shutdown() {
        HippoEventListenerRegistry.get().unregister(slugMaintainenceListener);
        HippoEventListenerRegistry.get().unregister(folderTypesEventListener);
        HippoEventListenerRegistry.get().unregister(updateMirrorNameEventListener);
        HippoEventListenerRegistry.get().unregister(thumbnailsEventListener);
        HippoEventListenerRegistry.get().unregister(guidePageLifeEventsListener);
        HippoEventListenerRegistry.get().unregister(contentBlocksOldVersionListener);
        HippoEventListenerRegistry.get().unregister(publicationTypeEventListener);
    }
}
