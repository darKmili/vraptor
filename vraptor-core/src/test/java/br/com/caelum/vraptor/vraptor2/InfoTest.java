package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.resource.Resource;

public class InfoTest {

    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }

    @Component("customized")
    class CustomComponent {

    }

    @Test
    public void shouldReadAComponentAnnotatedName() {
        assertThat(Info.getComponentName(CustomComponent.class), is(equalTo("customized")));
    }

    @Component
    class DefaultComponents {
        public void noAnnotation() {
        }

        @Logic
        public void emptyAnnotation() {
        }

        @Logic("value")
        public void full() {
        }
    }

    @Test
    public void shouldReadAComponentDefaultName() {
        assertThat(Info.getComponentName(DefaultComponents.class), is(equalTo("DefaultComponents")));
    }

    @Component
    class DefaultController {
    }

    @Test
    public void shouldReadAComponentDefaultNameWithSuffixRemoval() {
        assertThat(Info.getComponentName(DefaultController.class), is(equalTo("default")));
    }

    @Test
    public void shouldDetectAnOldComponent() {
        Resource resource = mockery.resource(CustomComponent.class);
        assertThat(Info.isOldComponent(resource), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    class DefaultResource {

    }

    @Test
    public void shouldDetectANewComponent() {
        Resource resource = mockery.resource(DefaultResource.class);
        assertThat(Info.isOldComponent(resource), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldDetectTheDefaultLogicNameIfNoAnnotationPresent() throws SecurityException, NoSuchMethodException {
        assertThat(Info.getLogicName(DefaultComponents.class.getMethod("noAnnotation")), is(equalTo("noAnnotation")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldDetectTheDefaultLogicNameIfAnnotationWithoutValue() throws SecurityException,
            NoSuchMethodException {
        assertThat(Info.getLogicName(DefaultComponents.class.getMethod("emptyAnnotation")),
                is(equalTo("emptyAnnotation")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldDetectTheDefaultLogicNameIfAnnotationWithName() throws SecurityException, NoSuchMethodException {
        assertThat(Info.getLogicName(DefaultComponents.class.getMethod("full")), is(equalTo("value")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToCapitalizeASingleCharacter() {
        assertThat(Info.capitalize("v"), is(equalTo("V")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToCapitalizeAStringLongerThanOneCharacter() {
        assertThat(Info.capitalize("value"), is(equalTo("Value")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThreatViewParameterAsAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue(""));
                one(request).getParameter("view"); will(returnValue("ajax"));
            }
        });
        assertThat(Info.isAjax(request), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThreatViewURIAsAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue("somethig.ajax.logic"));
            }
        });
        assertThat(Info.isAjax(request), is(equalTo(true)));
        mockery.assertIsSatisfied();
    }


    @Test
    public void shouldNormalURIAsNotAjax() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestURI(); will(returnValue("somethig.non-ajax.logic"));
                one(request).getParameter("view"); will(returnValue("xml"));
            }
        });
        assertThat(Info.isAjax(request), is(equalTo(false)));
        mockery.assertIsSatisfied();
    }

}
