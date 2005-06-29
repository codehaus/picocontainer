package org.nanocontainer.nanowar.nanoweb.example.bookmarker;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.nanowar.nanoweb.ActionFactory;
import org.nanocontainer.nanowar.nanoweb.ConverterComponentAdapter;
import org.nanocontainer.nanowar.nanoweb.NanoWebServletComponent;
import org.nanocontainer.nanowar.nanoweb.defaults.GroovyActionFactory;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.hb3.BookmarkDAOImpl;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi.Bookmark;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi.BookmarkDAO;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.webaplication.BookmarkConverter;
import org.nanocontainer.persistence.hibernate.FailoverSessionDelegator;
import org.nanocontainer.persistence.hibernate.SessionFactoryDelegator;
import org.nanocontainer.persistence.hibernate.SessionFactoryLifecycle;
import org.nanocontainer.persistence.hibernate.SessionLifecycle;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;

public class NanoWarContainerComposer implements ContainerComposer {

    // It supports two kind of scope value for each scope to help on testing.
    public static final String APPLICATION_SCOPE = "APPLICATION_SCOPE";
    public static final String REQUEST_SCOPE = "REQUEST_SCOPE";

    public void composeContainer(MutablePicoContainer pico, Object scope) {
        if ((scope instanceof ServletContext) || (APPLICATION_SCOPE.equals(scope))) {
            pico.registerComponentInstance(new HSQLDBDatabaseComponent());

            // Nanoweb
            pico.registerComponentImplementation(NanoWebServletComponent.class, NanoWebServletComponent.class);
            pico.registerComponentImplementation(ActionFactory.class, GroovyActionFactory.class, new Parameter[] { new ConstantParameter(((ServletContext) scope).getRealPath("/")) });

            // Hibernate stufs
            pico.registerComponentImplementation(Configuration.class, ConstructableAnnotationConfiguration.class);
            pico.registerComponentImplementation(SessionFactory.class, SessionFactoryDelegator.class);
            pico.registerComponentImplementation(SessionFactoryLifecycle.class, SessionFactoryLifecycle.class);

        } else if ((scope instanceof ServletRequest) || (REQUEST_SCOPE.equals(scope))) {
            // Hibernate stuffs
            pico.registerComponentImplementation(Session.class, FailoverSessionDelegator.class);
            pico.registerComponentImplementation(SessionLifecycle.class, SessionLifecycle.class);

            // Daos
            pico.registerComponentImplementation(BookmarkDAO.class, BookmarkDAOImpl.class);
            pico.registerComponent(new ConverterComponentAdapter(Bookmark.class, new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(BookmarkConverter.class, BookmarkConverter.class))));
        }
    }
}
